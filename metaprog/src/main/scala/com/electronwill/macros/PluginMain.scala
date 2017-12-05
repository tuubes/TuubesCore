package com.electronwill.macros

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.collection.mutable.ArrayBuffer
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

/**
 * Generates a companion object of type PluginInfosCompanion with getters for the plugin's
 * informations: name, version, requiredDependencies, optionalDependencies.
 * <p>
 * This companion is required to load the plugins in the correct order without problems. It is
 * not correct to create an instance of the plugin and then get the informations, because the
 * plugin's constructor may need some classes that are found in the dependencies.
 *
 * @author TheElectronWill
 */
@compileTimeOnly("This macro annotation must be expanded by the paradise compiler plugin")
class PluginMain extends StaticAnnotation {
	def macroTransform(annottees: Any*): Any = macro PluginMain.impl
}

object PluginMain {
	final val Values = Seq("name", "version", "requiredDependencies", "optionalDependencies")

	def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
		import c.universe._
		val picTypeName = TypeName("org.mcphoton.plugin.PluginInfosCompanion")
		/** Modifies the class to ensure that it has a companion with the plugin's infos */
		def createResult(cls: ClassDef, optionalCompanion: Option[ModuleDef]): c.Expr[Any] = {
			val q"class ${clsName: TypeName} extends {..$e} with ..$clsParents {..$clsBody}" = cls
			val (newClsBody, getters) = extractValues(clsName, clsBody.asInstanceOf[Seq[Tree]], Values: _*)
			/* If the companion exists, adds the getters to it. Otherwise, create it with the
			getters. */
			val companion = optionalCompanion.map { comp =>
				val q"object $obj extends {..$e} with ..$parents { ..$companionBody }" = comp
				val newParents = ensureImplementPIC(parents.asInstanceOf[Seq[Tree]])
				q"""object $obj extends {..$e} with ..$newParents {
						..${(companionBody ++ getters).toList}
					}
				 """
			} getOrElse {
				val parent = Ident(picTypeName)
				q"""object ${clsName.toTermName} extends $parent {
						..${getters.toList}
	 				}
				 """
			}
			val newCls = q"class $clsName extends {..$e} with ..$clsParents {..$newClsBody}"
			c.Expr[Any](q"$newCls; $companion")
		}

		/** Ensures that `parents` contains an Ident for PluginInfosCompanion. */
		def ensureImplementPIC(parents: Seq[Tree]): Seq[Tree] = {
			if (parents.exists(p => p.isInstanceOf[Ident]
				&& p.asInstanceOf[Ident].name.decodedName == picTypeName)) {
				parents
			} else {
				parents :+ Ident(picTypeName)
			}
		}

		/** Gathers the plugin's informations and creates final val for them. */
		def extractValues(clsName: TypeName, clsBody: Seq[Tree], vals: String*): (Seq[Tree], Seq[Tree]) = {
			val newClsBody = new ArrayBuffer[Tree](clsBody.length) // The modified class body
			val constants = new ArrayBuffer[Tree](vals.length) // Constants for the companion
			// Scans the class' body:
			for (tree <- clsBody) {
				if (tree.isInstanceOf[ValDef]) { // Only inspect values
					val q"$mods val ${id: TermName} = ${expr: Tree}" = tree
					val name = id.decodedName.toString
					val constantName = TermName(name.capitalize) // Follows naming conventions
					if (vals.contains(name)) {
						// Adds a getter that will end up in the companion object
						constants += q"final val $constantName = $expr"

						/* Modifies the class to use the companion's getter instead of the value,
						  to avoid duplicating the value (the duplication is unlikely with Strings
						  or AnyVals but it will happen with other types) */
						newClsBody += q"$mods val $id = ${clsName.toTermName}.$constantName"
					}
				} else {
					newClsBody += tree
				}
			}
			(newClsBody, constants)
		}

		annottees.map(_.tree) match {
			case (cls: ClassDef) :: Nil => createResult(cls, None)
			case (cls: ClassDef) :: (comp: ModuleDef) :: Nil => createResult(cls, Some(comp))
			case _ => c.abort(c.enclosingPosition, "@PluginMain can only be used with classes")
		}
	}
}