package org.tuubes.core.plugins

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * @author TheElectronWill
 */
final class DependencyGraph(e: mutable.Buffer[String] = new ArrayBuffer[String]) {

  /** The errors that occured during build or resolve */
  private[this] val errors = e

  /** The registered nodes */
  private[this] val dataMap = new mutable.HashMap[String, Node] // node.data.name -> node

  def register(data: PluginInfos): Unit = dataMap.put(data.name, new Node(data))

  /** Builds the graph with the registered data */
  def build(): Unit = {
    def isCompatible(provided: Node, req: DependencyRequirement, reqStr: String): Boolean = {
      try {
        req.accepts(provided.data)
      } catch {
        case _: Exception =>
          errors += s"Unable to check the requirement '$reqStr' against the provided " +
            s"version '${provided.data.version}"
          false
      }
    }

    def checkDependency(node: Node, dep: String, optional: Boolean): Unit = {
      if (!node.isValid) {
        return
      }
      val req = DependencyRequirement(dep, optional)
      dataMap.get(req.name) match {
        case Some(providedNode) if providedNode.isValid && isCompatible(providedNode, req, dep) =>
          link(node, providedNode, optional)
        case Some(providedNode) if providedNode.isValid =>
          errors += s"Plugin '${node.data.name}' requires '$dep' but the available " +
            s"version is '${providedNode.data.version}' and isn't compatible."
          invalidate(node)
        case _ =>
          if (!optional) {
            errors += s"Plugin '${node.data.name}' misses dependency: '$dep'"
            invalidate(node)
          }
      }
    }

    for ((_, node) <- dataMap; data = node.data
         if data.requiredDeps.nonEmpty || data.optionalDeps.nonEmpty) {
      data.requiredDeps.foreach(dep => checkDependency(node, dep, optional = false))
      data.optionalDeps.foreach(dep => checkDependency(node, dep, optional = true))
    }
    dataMap.retain((_, node) => node.isValid) // Removes all the invalid nodes
  }

  /**
	 * Resolves the graph.
	 *
	 * @return a Solution containing the resolved plugins in the right order and the
	 *         potential errors that occured.
	 */
  def resolve(): Solution = {
    // Step 1: remove circular dependencies from the graph
    val seenSet = new mutable.LinkedHashSet[Node]
    for (node <- dataMap.values if !node.isChecked) {
      removeCircularDependencies(node, seenSet)
    }

    // Step 3: find the free nodes
    var freeNodes = new ArrayBuffer[Node]
    for (n <- dataMap.values
         if n.hardDependencies.isEmpty && n.softDependencies.isEmpty) {
      freeNodes += n
    }

    // Step 2: compute the order in which the plugins must be loaded
    val resolvedItems = new ArrayBuffer[Resolved](dataMap.size)
    while (dataMap.nonEmpty && freeNodes.nonEmpty) {
      val newFreeNodes = new ArrayBuffer[Node] //To avoid modifying while iterating
      for (freeNode <- freeNodes) {
        resolvedItems += new Resolved(freeNode)
        dataMap.remove(freeNode.data.name)
        for (dependent <- freeNode.hardDependents) {
          dependent.hardDependencies -= freeNode
          dependent.resolvedDependencies += freeNode
          if (dependent.hardDependencies.isEmpty && dependent.softDependencies.isEmpty) {
            newFreeNodes += dependent
          }
        }
        for (dependent <- freeNode.softDependents) {
          dependent.softDependencies -= freeNode
          dependent.resolvedDependencies += freeNode
          if (dependent.hardDependencies.isEmpty && dependent.softDependencies.isEmpty) {
            newFreeNodes += dependent
          }
        }
      }
      freeNodes = newFreeNodes
    }
    // At this point, the remaining nodes in dataMap are the circular dependencies
    new Solution(resolvedItems, errors)
  }

  /**
	 * Detects and remove circular dependencies. This method recursively travels the graph by
	 * following the nodes' dependents. If a node is encountered twice, there is a circular
	 * dependency!
	 */
  private def removeCircularDependencies(n: Node, seen: mutable.LinkedHashSet[Node]): Unit = {
    val alreadySeen = !seen.add(n)
    if (alreadySeen) { // Circular dependency detected!
      // We know exactly which nodes form this circular dependency: they are between this
      // point and the point where `n` has been encountered for the first time.

      // Removes the bad nodes from `seen` and puts them in `circularDeps`:
      val circularDeps = new ArrayBuffer[String]
      var remove = false
      seen.retain { e =>
        if (e eq n) remove = true
        if (remove) {
          circularDeps += s"'${e.data.name}'"
          invalidate(e, addError = false)
        }
        remove
      }
      // Adds an error message for the circular dependency:
      errors += s"Circular dependency between ${circularDeps.mkString(", ")}"
    } else { // Continue exploring the graph
      n.hardDependents.foreach(removeCircularDependencies(_, seen))
      n.softDependents.foreach(removeCircularDependencies(_, seen))
      seen.remove(n)
    }
    n.markChecked()
  }

  /** Links a dependent node with its dependency */
  private def link(dependent: Node, dependency: Node, soft: Boolean): Unit = {
    if (soft) {
      dependent.softDependencies += dependency
      dependency.softDependents += dependent
    } else {
      dependent.hardDependencies += dependency
      dependency.hardDependents += dependent
    }
  }

  /**
	 * Invalidates a node and all its hard dependents. This removes the node's data name
	 * from dataMap, unlinks it from the other nodes, and does the same with all its hard
	 * dependents.
	 *
	 * @param node the node to invalidate
	 */
  private def invalidate(node: Node, addError: Boolean = true): Unit = {
    node.markInvalid()
    node.hardDependencies.foreach(_.hardDependents -= node)
    node.softDependencies.foreach(_.softDependents -= node)
    node.hardDependents.foreach { dependent =>
      invalidate(dependent, addError)
      if (addError) {
        errors += s"Plugin ${dependent.data.name} misses dependency: ${node.data.name}"
      }
    }
    node.softDependents.foreach(_.softDependencies -= node)
  }
}

/** A Node in the graph. Each node is linked to its dependencies and dependents */
final class Node(val data: PluginInfos) {

  /** The nodes that this node depends on */
  val hardDependencies = new ArrayBuffer[Node]

  /** The nodes that this node "softly" depends on, that is, this node can work without
	 * them. */
  val softDependencies = new ArrayBuffer[Node]

  /** The nodes that depend on this node */
  val hardDependents = new ArrayBuffer[Node]

  /** The nodes that "softly" depend on this node, that is, this node isn't strictly
	 * required for them to work. */
  val softDependents = new ArrayBuffer[Node]

  // Hard and soft dependents need to be separated, otherwise an invalidated dependency
  // would prevent its soft dependents to load.

  /** The dependencies that has been resolved */
  val resolvedDependencies = new ArrayBuffer[Node]

  /** The ClassLoader for the plugin (without its dependencies) */
  val singleClassLoader: OpenURLClassLoader = {
    data.urlClassLoader
  }

  /** The ClassLoader for the plugin and its dependencies */
  lazy val fullClassLoader: ClassLoader = {
    if (resolvedDependencies.isEmpty) {
      singleClassLoader
    } else {
      /* Builds the dependency chain: each dependency is associated with a number that
			   shows how far it is from this node (in the graph). Only the farthest occurence
			   of each dependency (ie the most negative number) is kept.

			   The farther a given node is, the more it has dependents nodes between itself
			   and this node, and the sooner it must be loaded. */
      val dependencyMap = new mutable.HashMap[Node, Int]
      buildDependencyChain(dependencyMap, 0)

      /* Gets the nodes, sorted by ascending distance number. Because the farthest node
			   is associated with the most negative number, sortedDeps is in the right order
			   to build the ClassLoader chain with a for loop. */
      val sortedDeps = dependencyMap.toList.sortBy(_._2).map(_._1) //Keys sorted by value
      var parentLoader: ClassLoader = classOf[DependencyGraph].getClassLoader
      for (dependency <- sortedDeps) {
        parentLoader = new DelegateClassLoader(dependency.singleClassLoader, parentLoader)
      }
      new DelegateClassLoader(singleClassLoader, parentLoader)
    }
  }

  /** Recursively builds the dependency chain of this node, without duplicates */
  private def buildDependencyChain(map: mutable.Map[Node, Int], startPos: Int): Int = {
    var pos = startPos
    for (dependency <- resolvedDependencies) {
      map.put(dependency, pos)
      pos = dependency.buildDependencyChain(map, pos - 1)
    }
    pos
  }

  /** False if the node has unresolvable dependencies */
  def isValid: Boolean = valid
  def markInvalid(): Unit = valid = false
  private[this] var valid = true

  /** True if the node has been checked for circular dependencies */
  def isChecked: Boolean = checked
  def markChecked(): Unit = checked = true
  private[this] var checked = false

  override def toString: String =
    s"Node(${data.name}, " +
      s"hardDependencies = ${hardDependencies.map(_.data.name)}, " +
      s"softDependencies = ${softDependencies.map(_.data.name)}, " +
      s"hardDependents = ${hardDependents.map(_.data.name)}, " +
      s"softDependents = ${softDependents.map(_.data.name)})"
}

/** A resolved items: all its dependencies are available. */
final case class Resolved(node: Node, fullLoader: ClassLoader) {
  def this(node: Node) = this(node, node.fullClassLoader)
}

/** Contains the result of `#resolve()` */
final class Solution(val resolvedItems: Seq[Resolved], val errors: Seq[String]) {}
