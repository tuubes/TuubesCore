package com.electronwill.macros

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

/**
 * Ensures that the method is executed in the right ExecutionGroup. This is useful for
 * non-thread-safe Entity methods (among others) since it will ensure that the method is always
 * executed from the same thread as the Entity's updates.
 * <p>
 * <p1>How it works</p1>
 * <ol>
 * <li>If not already present, an implicit parameter `callerGroup` of type `ExecutionGroup` is
 * added to the declaration of the method.</li>
 * <li>The content of the method is modified such that:
 * <ul>
 * <li>If `(callerGroup eq execGroup)` then the method is executed normally.</li>
 * <li>Otherwise the method is submitted to `execGroup` and will be executed as soon as possible.</li>
 * </ul>
 * </li>
 * </ol>
 * <p>
 * <p1>Requirements to make it work</p1>
 * <ul>
 * <li>The method must explicitely return Unit.</li>
 * <li>A variable or parameterless method `execGroup` of type `ExecutionGroup` must be in scope.
 * This is always the case if the method belongs to an instance of Updatable.
 * </li>
 * </ul>
 *
 * @author TheElectronWill
 */
@compileTimeOnly("This macro annotation must be expanded by the paradise compiler plugin")
class EnsureInGroup extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro EnsureInGroup.impl
}

object EnsureInGroup {
  def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    def wrapMethodContent(method: DefDef) = {
      val q"$mods def $name[..$t](...$paramss)(implicit ..$iParams): $result = $expr" =
        method
      val implicitParams = iParams.asInstanceOf[List[ValDef]]
      val normalParams = paramss.asInstanceOf[List[List[ValDef]]]
      val safeResult = ensureSafeResult(result.asInstanceOf[Tree])
      val (newImplicitParams, iParam) = addImplicitParam(implicitParams)
      q"""$mods def $name[..$t](...$paramss)(implicit ..$newImplicitParams): $safeResult = {
				if($iParam eq execGroup) {
					$expr
				} else {
					execGroup.execute(() => $name(...${normalParams.map(_.map(_.name))})(..${newImplicitParams
        .map(
          v =>
            if (iParam.decodedName.toString == v.name.decodedName.toString) iParam
            else v.name) // Pass execGroup as the caller group
      }))
				}
			}
			 """
    }

    def addImplicitParam(implicitParams: Seq[ValDef]): (Seq[ValDef], TermName) = {
      val execGroupType = TypeName("org.mcphoton.server.ExecutionGroup")
      val intParam =
        implicitParams.find(_.tpt.asInstanceOf[Ident].name == execGroupType)
      intParam match {
        case Some(valDef) => (implicitParams, valDef.name)
        case None =>
          val newParams = implicitParams :+ q"implicit val callerGroup: $execGroupType"
            .asInstanceOf[ValDef]
          (newParams, TermName("callerGroup"))
      }
    }

    def ensureSafeResult(result: Tree): Ident = {
      val isUnit = result match {
        case (id: Ident) => id.name == TypeName("Unit")
        case _           => false
      }
      if (!isUnit) {
        c.abort(result.pos, "Methods annotated with @EnsureInGroup must return Unit")
      }
      Ident(TypeName("Unit"))
    }

    annottees.map(_.tree) match {
      case (m: DefDef) :: Nil => c.Expr[Any](wrapMethodContent(m))
      case _ =>
        c.abort(c.enclosingPosition, "@EnsureInGroup can only be used with methods")
    }
  }
}
