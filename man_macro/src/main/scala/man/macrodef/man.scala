package man.macrodef

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros

/** This class has the following responsibilities.
 */

class help(helpString: String) extends StaticAnnotation

class man(classHelp: String) extends StaticAnnotation {
    def macroTransform(annottees: Any*): Any = macro ManPageGenerator.generate
}

private object ManPageGenerator {
    def generate(c: scala.reflect.macros.whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
        import c.universe._
        val inputs = annottees.map(_.tree).toList

            def genValOrDefHelp(implDef: ImplDef) = {
                val valOrDefWithHelpAnn = implDef.impl.children.collect {
                    case valOrDefDef: ValOrDefDef if hasHelpAnnotation(valOrDefDef) => valOrDefDef
                }
                valOrDefWithHelpAnn.foreach { vOrD =>
                    if (vOrD.mods.hasFlag(Flag.PRIVATE) || vOrD.mods.hasFlag(Flag.PROTECTED)) {
                        c.abort(vOrD.pos, "@help cannot be annotated on private or protected members.")
                    }
                }
                val valDefHelp = valOrDefWithHelpAnn.map(helpAnnotatorGenerator)
                valDefHelp
            }
            def hasHelpAnnotation(valOrDefDef: ValOrDefDef): Boolean = {
                valOrDefDef.mods.annotations.collectFirst {
                    case q"new help(${ Literal(Constant(helpString: String)) },..$rest)" =>
                        true
                    case q"new help(${ Select(Literal(Constant(hs: String)), TermName("stripMargin")) },..$rest)" =>
                        true
                }.isDefined
            }

            def helpAnnotatorGenerator(valOrDefDef: ValOrDefDef): String = {
                val hString = valOrDefDef.mods.annotations.collectFirst {
                    case q"new help(${ Literal(Constant(helpString: String)) },..$rest)" if helpString.nonEmpty =>
                        helpString
                    case q"new help(${ Select(Literal(Constant(helpString: String)), TermName("stripMargin")) },..$rest)" if helpString.nonEmpty => helpString
                    case _ => c.abort(valOrDefDef.pos, "@help annotation cannot have a non empty help string")
                }.get.trim
                val definitionStr = valOrDefDef match {
                    case q"${ mods: Modifiers } val $tname: ${ tpt: Tree } = $expr" =>
                        if (tpt.isEmpty) { c.abort(valOrDefDef.pos, "@help annotated vals should have type explicitly defined") }
                        else { showCode(q"val $tname: $tpt") }

                    case q"${ mods: Modifiers } val $pat = $expr" =>
                        showCode(q"val $pat = $expr")

                    case q"${ mods: Modifiers } var $tname: ${ tpt: Tree } = $expr" =>
                        if (tpt.isEmpty) { c.abort(valOrDefDef.pos, "@help annotated vars should have type explicitly defined") }
                        else { showCode(q"var $tname: $tpt") }

                    case q"${ mods: Modifiers } val $pat = ${ expr: Tree }" =>
                        showCode(q"val $pat = $expr")

                    case q"$mods def $tname[..$tparams](...$paramss): ${ tpt: Tree }= $expr" =>
                        if (tpt.isEmpty) c.abort(valOrDefDef.pos, "@help annotated defs should have reurn type explicitly defined.")
                        showCode(q"def $tname[..$tparams](...$paramss): $tpt ")
                    case _ => c.abort(valOrDefDef.pos, "unknown")

                }

                s"""$definitionStr
                  |$hString
                """.stripMargin
            }
            def genClassHelp(classDef: ClassDef) = {
                val classHelp = c.prefix.tree match {
                    case q"new man(${ Literal(Constant(classHelpString: String)) },..$rest)" if classHelpString.nonEmpty => classHelpString
                    case q"new man(${ Select(Literal(Constant(classHelpString: String)), TermName("stripMargin")) },..$rest)" if classHelpString.stripMargin.nonEmpty => classHelpString
                    case _ => c.abort(c.enclosingPosition, "@man annotation should have non empty help string")
                }
                val definition = classDef match {
                    case q"$mods class $tpname[..$tparams] $ctorMods(...${ paramss: List[List[ValDef]] }) extends { ..$earlydefns } with ..$parents { $self => ..$stats }" =>
                        showCode(q"$mods class $tpname[..$tparams] $ctorMods(...${paramss: List[List[ValDef]]}) extends { ..$earlydefns } with ..$parents")
                    case q"$mods trait $tpname[..$tparams] extends { ..$earlydefns } with ..$parents { $self => ..$stats }" =>
                        showCode(q"$mods trait $tpname[..$tparams] extends { ..$earlydefns } with ..$parents")
                    case _ => c.abort(classDef.pos, "unknown")

                }

                val valDefHelp = genValOrDefHelp(classDef)
                (classHelp, definition.trim, valDefHelp)
            }

            def genObjectHelp(moduleDef: ModuleDef) = {
                val objHelp = c.prefix.tree match {
                    case q"new man(${ Literal(Constant(classHelpString: String)) },..$rest)" if classHelpString.nonEmpty => classHelpString
                    case q"new man(${ Select(Literal(Constant(classHelpString: String)), TermName("stripMargin")) },..$rest)" if classHelpString.stripMargin.nonEmpty => classHelpString
                    case _ => c.abort(c.enclosingPosition, "@man annotation should have non empty help string")
                }

                val definition = moduleDef match {
                    case q"$mods object $tname extends { ..$earlydefns } with ..$parents { $self => ..$body }" =>
                        showCode(q"$mods object $tname extends { ..$earlydefns } with ..$parents")
                }

                val valDefHelp = genValOrDefHelp(moduleDef)

                (objHelp, definition, valDefHelp)

            }

            def addHelpToCompanion(help: String, methodName: String, comp: Tree) = {
                val q"$mods object $tname extends { ..$earlydefns } with ..$parents { $self => ..$body }" = comp

                val helpMethod = q"def ${TermName(methodName)}():Unit = println($help)"
                q"""
                     $mods object $tname extends { ..$earlydefns } with ..$parents { $self =>
                        ..$body
                        $helpMethod
                     }
                 """

            }

            def classHelpGenerator(classDef: ClassDef, companion: Option[ModuleDef] = None) = {
                val (classHelp, definition, fhelps) = genClassHelp(classDef)
                val helpString = ManPageCreator.create(classDef.name.toString, classHelp, definition, fhelps)
                val comp = companion.getOrElse(q"object ${classDef.name.toTermName} {}")
                val companionObject = addHelpToCompanion(helpString, "help", comp)
                val result = c.Expr(
                    q"""
                       $classDef
                       $companionObject
                     """)

                //                println(s"Generated Help for class ${classDef.name}:\n" + showCode(result.tree))
                result

            }

            def objectHelpGenerator(moduleDef: ModuleDef) = {
                val (classHelp, definition, fhelps) = genObjectHelp(moduleDef)
                val helpString = ManPageCreator.create(moduleDef.name.toString, classHelp, definition, fhelps)

                val newObj = addHelpToCompanion(helpString, "help_", moduleDef)
                val result = c.Expr(q"""$newObj""")
                //                println(s"Generated Help for object ${moduleDef.name}:\n" + showCode(result.tree))
                result
            }

        inputs match {
            case (manClass: ClassDef) :: Nil => classHelpGenerator(manClass)
            case (manClass: ClassDef) :: (companion: ModuleDef) :: Nil => classHelpGenerator(manClass, Some(companion))
            case (justObject: ModuleDef) :: Nil => objectHelpGenerator(justObject)
            case _ => c.abort(c.enclosingPosition, "@man can only be applied to traits/class/object/abstract class.")

        }
        //        c.Expr[Any](Block(inputs, Literal(Constant(()))))
    }

}