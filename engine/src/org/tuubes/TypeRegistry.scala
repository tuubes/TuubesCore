package org.tuubes

/** A registry for game types.
  * @tparam A the type of things to register, for instance `Block`
  * @tparam T a class that "materializes" the game type of those things,
              for instance if A is `Block` then T should be `BlockType`
  */
abstract class TypeRegistry[A, T <: RegisteredType[A]] {
  private val classMap = HashMap[Class[?], T]()
  private val idMap = IntMap(initialCapacity=256)
  private var currentId = -1

  /** Registers a simple "thing" that has no state. */
  def register(singleton: A): T = {
    val provider = ()=>singleton
    val runtimeClass = singleton.getClass
    val tag = ClassTag[A](runtimeClass)
    classMap.getOrElseUpdate(runtimeClass, internalNewType(provider, tag))
  }

  /** Registers an advanced "thing" that may have an internal state (hence "stateful").
    * It is guaranteed that each thing of that type will be a separate instance.
    * Each instance stores its own data independently.
    */
  def registerStateful[B <: A](builder: () => B)(given tag: ClassTag[B]): T = {
    classMap.getOrElseUpdate(tag.runtimeClass, internalNewType(builder, tag))
  }

  /** Creates a "thing" of some precisely known type.
    * @param B the type of thing to create
    * @param tag tag a `ClassTag` that contains information about the class `B`
                 (automatically provided by the compiler)
      @return a new thing of type `B`
    */
  def create[B <: A](given tag: ClassTag[B]): B = {
    classMap(tag.runtimeClass).build()
  }

  /** Creates a "thing" of some unprecise type.
    * @param tpe the type of thing to create
    * @return a new thing
    */
  def create(tpe: T): A = tpe.build()

  /** Materializes a type. */
  def typeOf[B <: A](given tag: ClassTag[B]): T = classMap(tag.runtimeClass)

  /** Materializes a type. */
  def typeOf(runtimeClass: Class[_<:A]): T = classMap(runtimeClass)

  // --- Tuubes only ---
  /** Searches a type by internal id. */
  private[tuubes] def typeWithId(id: Int): T = idMap(id)

  // --- Registry Internals ---
  /** Creates a new instance of `T` (a "type of thing") for a `B <: A` (the "thing" to be typed).
    * @param provider a function that creates instances of `B` (it provides "things")
    * @param tag a `ClassTag` that contains information about the class `B`
    * @param id the internal id to associate to the new instance of `T`
    * @return a new registered type
    */
  protected def newType[B <: A](provider: ()=>B, tag: ClassTag[B], id: Int): T

  /** Generates a new internal id and registers a new type with that id. */
  protected final def internalNewType[B <: A](provider: ()=>B, tag: ClassTag[B]): T = {
    val id = nextId
    val tpe = newType(provider, tag, id)
    idMap(id) = tpe
    tpe
  }

  /** Generates a new internal type id. */
  private def nextId = {
    currentId += 1
    currentId
  }
}
/** A type of "thing" that has been registered to a registry */
abstract class RegisteredType[B<:A](val build: ()=>B, val tag: ClassTag[B], private[tuubes] val id: Int)
