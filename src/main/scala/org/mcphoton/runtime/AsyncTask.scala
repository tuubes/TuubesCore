package org.mcphoton.runtime

import java.io.File
import java.net.{HttpURLConnection, URL, URLConnection}
import java.nio.channels.FileChannel
import java.nio.file.OpenOption
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger, AtomicReference}
import java.util.concurrent.{ConcurrentLinkedQueue, Executor, TimeUnit}

import org.mcphoton.runtime.TaskStatus._

import scala.annotation.{tailrec, varargs}
import scala.collection.mutable.ArrayBuffer
import scala.util.{Failure, Success, Try}

/**
 * An asynchronous task that can be chained with other tasks.
 *
 * @author TheElectronWill
 */
abstract class AsyncTask[A, B](private[this] val function: A => Try[B],
							   private[this] val executor: Executor) {
	@volatile private[this] var _result: Try[B] = _
	private[this] val _status = new AtomicReference(CREATED)
	private[this] val nextTasks = new ConcurrentLinkedQueue[AsyncTask[B, _]]
	private[this] val failureHandlers = new ConcurrentLinkedQueue[(A, Failure[B]) => Try[B]]

	/** @return the current status of the task */
	def status: TaskStatus = _status.get

	/** @return true if the task is [[COMPLETED]] or [[FAILED]], false otherwise */
	def isDone: Boolean = _status.get == COMPLETED || _status.get == FAILED

	/** @return `Some(result)` if the task is done, `None` otherwise */
	def result: Option[Try[B]] = Option(_result)

	/**
	 * Completes this task with the given result.
	 *
	 * @param result the result
	 */
	def completeNow(result: B): Unit = {
		_result = Success(result)
		_status.set(COMPLETED)
	}

	/**
	 * Completes this task with the given result. If the result is a `Success` the task is
	 * `COMPLETED`, if it is a `Failure` the task is `FAILED`.
	 *
	 * @param result the result
	 */
	def completeNow(result: Try[B]): Unit = {
		_result = result
		_status.set(if (result.isSuccess) COMPLETED else FAILED)
	}

	/**
	 * Fails this task with the given error.
	 *
	 * @param throwable the error
	 */
	def failNow(throwable: Throwable): Unit = {
		_result = Failure(throwable)
		_status.set(FAILED)
	}

	/**
	 * Executes a function on the `TaskSystem` after this task.
	 *
	 * @param f the function
	 * @tparam C the function's return type
	 * @return the newly created CPU task
	 */
	def thenCompute[C](f: ThrowableFunction1[B, C]): AsyncTask[B, C] = {
		addNext(AsyncTask.wrap(f), TaskSystem.executor)
	}

	/**
	 * Executes a function on the `TaskSystem` after this task.
	 *
	 * @param f the function
	 * @tparam C the function's return type
	 * @return the newly created CPU task
	 */
	def thenCompute[C](f: B => Try[C]): AsyncTask[B, C] = {
		addNext(AsyncTask.wrap(f), TaskSystem.executor)
	}

	/**
	 * Executes a function on the `IOSystem` after this task.
	 *
	 * @param f the function
	 * @tparam C the function's return type
	 * @return the newly created IO task
	 */
	def thenIO[C](f: ThrowableFunction1[B, C]): AsyncTask[B, C] = {
		addNext(AsyncTask.wrap(f), IOSystem.executor)
	}

	/**
	 * Executes a function on the `IOSystem` after this task.
	 *
	 * @param f the function
	 * @tparam C the function's return type
	 * @return the newly created IO task
	 */
	def thenIO[C](f: B => Try[C]): AsyncTask[B, C] = {
		addNext(AsyncTask.wrap(f), IOSystem.executor)
	}

	/**
	 * After this task, opens and uses a FileChannel on the `IOSystem`.
	 *
	 * @param file        the file to open
	 * @param f           the function to apply
	 * @param openOptions the options to open the file with
	 * @tparam C the function's return type
	 * @return the newly created IO task
	 */
	@varargs
	def thenIOFile[C](file: File, f: ThrowableFunction1[(B, FileChannel), C],
					  openOptions: OpenOption*): AsyncTask[B, C] = {
		val ioFunction = ioFunctionFile[C](file, f)
		addNext(AsyncTask.wrap(ioFunction), IOSystem.executor)
	}

	/**
	 * After this task, opens and uses a FileChannel on the `IOSystem`.
	 *
	 * @param file        the file to open
	 * @param f           the function to apply
	 * @param openOptions the options to open the file with
	 * @tparam C the function's return type
	 * @return the newly created IO task
	 */
	@varargs
	def thenIOFile[C](file: File, f: (B, FileChannel) => Try[C],
					  openOptions: OpenOption*): AsyncTask[B, C] = {
		val ioFunction = ioFunctionFile[C](file, f)
		addNext(AsyncTask.wrap(ioFunction), IOSystem.executor)
	}

	/**
	 * After this task, opens and uses a URLConnection on the `IOSystem`.
	 *
	 * @param url the url to connect to
	 * @param f   the function to apply
	 * @tparam C the function's return type
	 * @return the newly created IO task
	 */
	def thenIOUrl[C](url: String, f: ThrowableFunction1[(B, URLConnection), C]): AsyncTask[B, C] = {
		val ioFunction = ioFunctionURL[C](url, f)
		addNext(AsyncTask.wrap(ioFunction), IOSystem.executor)
	}

	/**
	 * After this task, opens and uses a URLConnection on the `IOSystem`.
	 *
	 * @param url the url to connect to
	 * @param f   the function to apply
	 * @tparam C the function's return type
	 * @return the newly created IO task
	 */
	def thenIOUrl[C](url: String, f: (B, URLConnection) => Try[C]): AsyncTask[B, C] = {
		val ioFunction = ioFunctionURL[C](url, f)
		addNext(AsyncTask.wrap(ioFunction), IOSystem.executor)
	}

	/**
	 * Creates a function that opens a FileChannel, applies a function `f` to it, closes the channel
	 * and returns the result of `f`.
	 */
	private def ioFunctionFile[R](file: File, f: (B, FileChannel) => R,
								  openOptions: OpenOption*): B => R = {
		(b: B) => {
			var channel: FileChannel = null
			var result: R = null
			try {
				channel = FileChannel.open(file.toPath, openOptions: _*)
				result = f(b, channel)
			} finally {
				if (channel != null) {
					channel.close()
				}
			}
			result
		}
	}

	/**
	 * Creates a function that opens a URLConnection, applies a function `f` to it, closes the
	 * connection if it is of type HttpUrlConnection, and returns the result of `f`.
	 */
	private def ioFunctionURL[R](url: String, f: (B, URLConnection) => R): B => R = {
		(b: B) => {
			var connection: URLConnection = null
			var result: R = null
			try {
				connection = new URL(url).openConnection()
				result = f(b, connection)
			} finally {
				if (connection.isInstanceOf[HttpURLConnection]) {
					connection.asInstanceOf[HttpURLConnection].disconnect()
				}
			}
			result
		}
	}

	/**
	 * Starts (ie launches asynchronously) some tasks after this task.
	 *
	 * @param tasks the tasks to start
	 * @return this task
	 */
	@varargs
	def thenStart(tasks: AsyncTask[_, _]*): this.type = {
		addNext(_ => {tasks.foreach(_.start()); null}, DummyExecutor)
		this
	}

	/**
	 * After this task, waits for one of the given tasks to complete. If all the tasks fail
	 * then the returned task fails too.
	 *
	 * @param tasks the tasks to wait for
	 * @tparam C the tasks' return type
	 * @return a new task that is triggered when the given tasks complete
	 */
	@varargs
	def thenAwaitAny[C](tasks: AsyncTask[B, C]*): AsyncTask[Try[C], C] = {
		val task = new ChildTask[Try[C], C](root, identity, DummyExecutor)

		// See the comments in successConvergenceN and failureConvergenceN
		val onSuccess = AsyncTask.successConvergence1(task)
		val onFailure = AsyncTask.failureConvergence1(task, tasks.length)

		// Runs the completion functions after each specified task:
		for (task <- tasks) {
			task.addNext(c => {onSuccess(c); null}, DummyExecutor)
			task.executeOnFailure(onFailure)
			// Ensures that the tasks are started when this AsyncTask ends:
			this.addNext(b => {task.start(); null}, DummyExecutor)
		}
		task
	}

	/**
	 * After this task, waits for some of the given tasks to complete. If too many tasks fail
	 * then the returned task fails too.
	 *
	 * @param tasks the tasks to wait for
	 * @tparam C the tasks' return type
	 * @return a new task that is triggered when the given tasks complete
	 */
	@varargs
	def thenAwaitSome[C](count: Int, tasks: AsyncTask[B, C]*): AsyncTask[Try[Array[C]], Array[C]] = {
		val task = new ChildTask[Try[Array[C]], Array[C]](root, identity, DummyExecutor)

		// See the comments in successConvergenceN and failureConvergenceN
		val onSuccess = AsyncTask.successConvergenceN(task, count)
		val onFailure = AsyncTask.failureConvergenceN(task, tasks.length - count)

		// Runs the completion functions after each specified task:
		for (task <- tasks) {
			task.addNext(c => {onSuccess(c); null}, DummyExecutor)
			task.executeOnFailure(onFailure)
			// Ensures that the tasks are started when this AsyncTask ends:
			this.addNext(b => {task.start(); null}, DummyExecutor)
		}
		task
	}

	/**
	 * After this task, waits for all the given tasks to complete. If one of the tasks fail
	 * then the returned task fails too.
	 *
	 * @param tasks the tasks to wait for
	 * @tparam C the tasks' return type
	 * @return a new task that is triggered when the given tasks complete
	 */
	@varargs
	def thenAwaitAll[C](tasks: AsyncTask[B, C]*): AsyncTask[Try[Array[C]], Array[C]] = {
		thenAwaitSome(tasks.length, tasks: _*)
	}

	/**
	 * After this task, waits a specific amount of time.
	 *
	 * @param time the time to wait for
	 * @param unit the time's unit
	 * @return a new task that is triggered after the given delay
	 */
	def thenDelay(time: Long, unit: TimeUnit): AsyncTask[B, B] = {
		val after = new ChildTask[B, B](root, Success(_), executor)
		val f = (b: B) => {
			TaskSystem.schedule(() => after.start(), time, unit)
			Success(b)
		}
		addNext(f, executor)
	}

	/**
	 * Executes a function when this task fail.
	 * @param f the function to execute
	 * @return this task
	 */
	def executeOnFailure(f: (A, Throwable) => Unit): this.type = {
		failureHandlers.add((a, fail) => {
			f(a, fail.exception)
			fail
		})
		this
	}

	/**
	 * Executes a task when this task fail.
	 * @param task the task to execute
	 * @return this task
	 */
	def executeOnFailure(task: AsyncTask[(A, Throwable), _]): this.type = {
		failureHandlers.add((a, fail) => {
			task.submit(a, fail.exception)
			fail
		})
		this
	}

	/**
	 * Tries to recover a result when this task fail.
	 *
	 * @param f the recovery function
	 * @return this task
	 */
	def recoverOnFailure(f: ThrowableFunction1[(A, Throwable), B]): this.type = {
		failureHandlers.add((a, fail) => Try(f(a, fail.exception)))
		this
	}

	/**
	 * Tries to recover a result when this task fail.
	 *
	 * @param f the recovery function
	 * @return this task
	 */
	def recoverOnFailure(f: (A, Throwable) => Try[B]): this.type = {
		failureHandlers.add((a, fail) => Try(f(a, fail.exception)).flatten)
		this
	}

	/**
	 * Starts (ie launches asynchronously) some tasks when this task fail.
	 *
	 * @param tasks the tasks to start
	 * @return this task
	 */
	@varargs
	def startOnFailure(tasks: AsyncTask[_, _]*): this.type = {
		failureHandlers.add((_, fail) => {tasks.foreach(_.start()); fail})
		this
	}

	protected final def addNext[C](f: B => Try[C], executor: Executor): AsyncTask[B, C] = {
		val next = new ChildTask[B, C](root, f, executor)
		next.casStatus(CREATED, ROOT_SUBMITTED)
		nextTasks.add(next)
		next
	}

	/** Atomically sets the task's status if it's equal to the expected status */
	protected final def casStatus(expect: TaskStatus, update: TaskStatus): Boolean = {
		_status.compareAndSet(expect, update)
	}

	/** @return an iterator over the tasks that are submitted after this task */
	protected final def nextTasksIterator(): Iterator[AsyncTask[B, _]] = {
		import scala.collection.JavaConverters._
		nextTasks.iterator().asScala
	}

	protected[this] val root: AsyncTask[_, _]

	/**
	 * Starts this task asynchronously, and ensures that its parent tasks are started.
	 */
	def start(): Unit

	/**
	 * Submits this task for execution with the result of the parent task (or Unit if this is a
	 * root task).
	 *
	 * @param previousResult the parent's result
	 */
	protected final def submit(previousResult: A): Unit = {
		_status.set(SUBMITTED)
		executor.execute(() => run(previousResult))
	}

	/**
	 * Runs this task now, in the caller thread, with the result of the parent task (or Unit if
	 * this is a root task).
	 *
	 * @param previousResult the parent's result
	 */
	private final def run(previousResult: A): Unit = {
		_status.set(STARTED)
		_result = function(previousResult)
		_result match {
			case Success(_) => handleSuccess _
			case Failure(_) => handleFailure _
		}
	}

	/**
	 * Handles the successfull completion of this task. The task's status is set to COMPLETED and
	 * the `nextTasks` are submitted for execution.
	 *
	 * @param success the Success result
	 */
	private final def handleSuccess(success: Success[B]): Unit = {
		_result = success
		_status.set(COMPLETED)
		val result = _result.get
		var nextTask: AsyncTask[B, _] = nextTasks.poll()
		while (nextTask ne null) {
			nextTask.submit(result)
			nextTask = nextTasks.poll()
		}
	}

	/**
	 * Handles the failure of this task. The failureHandlers may rectify the situation by
	 * providing a Success with a new result. In that case `handleSuccess` will be called and the
	 * task's status will be COMPLETED. Otherwise the task's status is set to FAILED.
	 *
	 * @param previousResult the result that has been given to submit this task
	 * @param failure        the Failure result
	 */
	private final def handleFailure(previousResult: A, failure: Failure[B]): Unit = {
		var newResult: Try[B] = failure
		var nextHandler: (A, Failure[B]) => Try[B] = failureHandlers.poll()
		while (nextHandler ne null) {
			newResult = nextHandler(previousResult, failure)
			if (newResult.isSuccess) {
				// In case of recovered situation, handle the success and stop here
				handleSuccess(newResult.asInstanceOf[Success[B]])
				return
			}
			nextHandler = failureHandlers.poll()
		}
		_result = newResult
		_status.set(FAILED)
	}
}
object AsyncTask {
	/**
	 * Creates a new IO task that produces a result.
	 *
	 * @param f the function producing the result
	 * @tparam R the result type
	 * @return the task, unstarted
	 */
	def io[R](f: ThrowableFunction0[R]): AsyncTask[Unit, R] = {
		new RootTask[R](wrap(f), IOSystem.executor)
	}

	/**
	 * Creates a new IO task that produces a result.
	 *
	 * @param f the function producing the result
	 * @tparam R the result type
	 * @return the task, unstarted
	 */
	def io[R](f: () => Try[R]): AsyncTask[Unit, R] = {
		new RootTask[R](wrap(f), IOSystem.executor)
	}

	/**
	 * Creates a new CPU task that produces a result.
	 *
	 * @param f the function producing the result
	 * @tparam R the result type
	 * @return the task, unstarted
	 */
	def compute[R](f: ThrowableFunction0[R]): AsyncTask[Unit, R] = {
		new RootTask[R](wrap(f), TaskSystem.executor)
	}

	/**
	 * Creates a new CPU task that produces a result.
	 *
	 * @param f the function producing the result
	 * @tparam R the result type
	 * @return the task, unstarted
	 */
	def compute[R](f: () => Try[R]): AsyncTask[Unit, R] = {
		new RootTask[R](wrap(f), TaskSystem.executor)
	}

	/** Wraps a function in a Try to catch all the non-fatal exceptions */
	private def wrap[T, R](f: ThrowableFunction1[T, R]): T => Try[R] = {
		t => Try(f(t))
	}

	/** Wraps a function in a Try to catch all the non-fatal exceptions */
	private def wrap[R](f: ThrowableFunction0[R]): () => Try[R] = {
		() => Try(f())
	}

	/** Wraps a function in a Try to catch all the non-fatal exceptions */
	private def wrap[T, R](f: (T) => Try[R]): T => Try[R] = {
		t => Try(f(t)).flatten
	}

	/** Wraps a function in a Try to catch all the non-fatal exceptions */
	private def wrap[R](f: () => Try[R]): () => Try[R] = {
		() => Try(f()).flatten
	}

	/**
	 * Creates a new delayed task. When started, the task is not submitted immediately but after
	 * the given delay.
	 *
	 * @param time the delay
	 * @param unit the delay's unit
	 * @return the task, unstarted
	 */
	def delay(time: Long, unit: TimeUnit): AsyncTask[Unit, Unit] = {
		new DelayedRootTask[Unit](time, unit, Success(_), DummyExecutor)
	}

	/**
	 * Creates a new delayed CPU task that produces a result.
	 *
	 * @param time the delay
	 * @param unit the delay's unit
	 * @param f the function producing the result
	 * @tparam R the result type
	 * @return the task, unstarted
	 */
	def delayCompute[R](time: Long, unit: TimeUnit, f: () => Try[R]): AsyncTask[Unit, R] = {
		new DelayedRootTask[R](time, unit, wrap(f), DummyExecutor)
	}

	/**
	 * Creates a new CPU task that produces a result.
	 *
	 * @param time the delay
	 * @param unit the delay's unit
	 * @param f the function producing the result
	 * @tparam R the result type
	 * @return the task, unstarted
	 */
	def delayCompute[R](time: Long, unit: TimeUnit, f: ThrowableFunction0[R]): AsyncTask[Unit, R] = {
		new DelayedRootTask[R](time, unit, wrap(f), DummyExecutor)
	}

	/**
	 * Creates a new (CPU) task that completes when some of the given tasks complete. If too many
	 * tasks fail then the task fails too.
	 *
	 * @param count the number of task that have to be completed to complete the new task
	 * @param tasks the tasks to wait for
	 * @tparam R the result type of the tasks
	 * @return a new task that is triggered when the given tasks complete
	 */
	@varargs
	def awaitSome[R](count: Int, tasks: AsyncTask[_, _ <: R]*): AsyncTask[Try[Array[R]], Array[R]] = {
		val task = new AwaitingRootTask[Array[R], R](tasks: _*)
		val onSuccess = successConvergenceN(task, count)
		val onFailure = failureConvergenceN(task, tasks.length - count)
		// Runs the completion functions after each specified task:
		for (task <- tasks) {
			task.addNext(c => {onSuccess(c); null}, DummyExecutor)
			task.executeOnFailure(onFailure)
		}
		task
	}

	/**
	 * Creates a new (CPU) task that completes when all the given tasks complete. If one of them
	 * fails then the task fails too.
	 *
	 * @param tasks the tasks to wait for
	 * @tparam R the result type of the tasks
	 * @return a new task that is triggered when the given tasks complete
	 */
	@varargs
	def awaitAll[R](tasks: AsyncTask[_, _ <: R]*): AsyncTask[Try[Array[R]], Array[R]] = {
		awaitSome(tasks.length, tasks: _*)
	}

	/**
	 * Creates a new (CPU) task that completes when one of the given tasks complete. If all the
	 * tasks fail then the task fails too.
	 *
	 * @param tasks the tasks to wait for
	 * @tparam R the result type of the tasks
	 * @return a new task that is triggered when the given tasks complete
	 */
	@varargs
	def awaitAny[R](tasks: AsyncTask[_, _ <: R]*): AsyncTask[Try[R], R] = {
		val task = new AwaitingRootTask[R, R](tasks: _*)
		val onSuccess = successConvergence1(task)
		val onFailure = failureConvergence1(task, tasks.length)
		// Runs the completion functions after each specified task:
		for (task <- tasks) {
			task.addNext(c => {onSuccess(c); null}, DummyExecutor)
			task.executeOnFailure(onFailure)
		}
		task
	}

	/** Optimized method that recursively marks the given tasks as ROOT_SUBMITTED. */
	@tailrec
	private[runtime]
	def markRootSubmitted(tasks: TraversableOnce[AsyncTask[_, _]]): Unit = {
		val subTasks = new ArrayBuffer[AsyncTask[_, _]]
		for (task <- tasks) {
			task.casStatus(CREATED, ROOT_SUBMITTED)
			task.nextTasksIterator().foreach(subTasks.append(_))
		}
		markRootSubmitted(subTasks)
	}

	private[runtime]
	def successConvergenceN[A](action: AsyncTask[Try[Array[A]], Array[A]], count: Int): A => Unit = {
		// Counts the number of successes
		val successCounter = new AtomicInteger()

		// Contains the results of the successfull tasks:
		val resultArray = new VolatileArray[A](count)

		// This function will be called by each completed task that we wait for. Its goal is to call
		// the `action` task with a Success when the enough tasks are completed.
		a => {
			val currentCount = successCounter
							   .incrementAndGet() // each thread gets a different count
			if (currentCount <= count) {
				resultArray(currentCount - 1) = a // volatile update with the unique currentCount
				if (currentCount == count) {
					action.submit(Success(resultArray.underlying))
				}
			}
		}
	}

	private[runtime]
	def successConvergence1[A](action: AsyncTask[Try[A], A]): A => Unit = {
		// Becomes true when the first task is completed:
		val completed = new AtomicBoolean()
		// This function will be called by each completed task that we wait for. Its goal is to call
		// the `action` task with a Success when the first task is completed.
		a => {
			if (completed.compareAndSet(false, true)) {
				action.submit(Success(a))
			}
		}
	}

	private[runtime]
	def failureConvergenceN[A](action: AsyncTask[Try[Array[A]], Array[A]],
							   failureTolerance: Int): (Try[Array[A]], Throwable) => Unit = {
		// Counts the number of tolered failures before the `action` fails
		val toleranceCounter = new AtomicInteger(failureTolerance)
		// This function is called by each failed task that we wait for. Its goal is to call the
		// `action` task with a Failure when too many tasks fail.
		(a, fail) => {
			val currentCount = toleranceCounter.decrementAndGet()
			if (currentCount == 0) {
				action.submit(Failure(fail))
			}
		}
	}

	private[runtime]
	def failureConvergence1[A](action: AsyncTask[Try[A], A],
							   failureTolerance: Int): (Try[A], Throwable) => Unit = {
		// Counts the number of tolered failures before the `action` fails
		val toleranceCounter = new AtomicInteger(failureTolerance)
		// This function is called by each failed task that we wait for. Its goal is to call the
		// `action` task with a Failure when too many tasks fail.
		(a, fail) => {
			val currentCount = toleranceCounter.decrementAndGet()
			if (currentCount == 0) {
				action.submit(Failure(fail))
			}
		}
	}
}
/**
 * A simple root task.
 *
 * @param f the function to execute
 * @param e the Executor that will execute the function
 */
private final class RootTask[B](f: () => Try[B], e: Executor)
	extends AsyncTask[Unit, B](_ => f(), e) {

	protected[this] val root = this
	override def start(): Unit = {
		if (casStatus(CREATED, SUBMITTED)) {
			AsyncTask.markRootSubmitted(nextTasksIterator())
			submit(())
		}
	}
}
/**
 * A child task.
 *
 * @param root the root task that this ChildTask is associated with (possibly with intermediary
 *             tasks between the two)
 * @param f    the function to execute
 * @param e    the Executor that will execute the function
 * @tparam A the type of data accepted by this task
 * @tparam B the type of data produced by this task
 */
private final class ChildTask[A, B](protected[this] val root: AsyncTask[_, _],
									f: A => Try[B], e: Executor) extends AsyncTask[A, B](f, e) {

	if (root.status != CREATED) { // The root task has already been submitted
		casStatus(CREATED, ROOT_SUBMITTED)
	}

	override def start(): Unit = {
		if (casStatus(CREATED, ROOT_SUBMITTED)) {
			root.start()
		}
	}
}
/**
 * A root task that is triggered when some other tasks complete.
 */
private final class AwaitingRootTask[R, T](private[this] val tasks: AsyncTask[_, _ <: T]*)
	extends AsyncTask[Try[R], R](identity, TaskSystem.executor) {

	protected[this] val root = this
	override def start(): Unit = {
		if (casStatus(CREATED, SUBMITTED)) {
			AsyncTask.markRootSubmitted(nextTasksIterator())
			tasks.foreach(_.start()) // Ensures that the tasks we wait for are started
		}
	}
}
private final class DelayedRootTask[B](private[this] val delay: Long,
									   private[this] val unit: TimeUnit,
									   f: () => Try[B], e: Executor)
	extends AsyncTask[Unit, B](_ => f(), e) {

	protected[this] val root = this
	override def start(): Unit = {
		if (casStatus(CREATED, SUBMITTED)) {
			AsyncTask.markRootSubmitted(nextTasksIterator())
			TaskSystem.schedule(() => submit(()), delay, unit)
		}
	}
}