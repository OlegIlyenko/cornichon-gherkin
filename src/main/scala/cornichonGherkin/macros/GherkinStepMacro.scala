package cornichonGherkin.macros

import cornichonGherkin.{DocStringExtractor, Extractor, RegExpExtractor, TableExtractor}
import sangria.parser.{QueryParser, SyntaxError}

import scala.reflect.macros.blackbox

class GherkinStepMacro(context: blackbox.Context) extends {
  val c = context
  val universe: c.universe.type = c.universe

  import c.universe._

  def step0Impl() = stepCode()
  def step1Impl(e1: Tree) = stepCode(e1)
  def step2Impl(e1: Tree, e2: Tree) = stepCode(e1, e2)
  def step3Impl(e1: Tree, e2: Tree, e3: Tree) = stepCode(e1, e2, e3)
  def step4Impl(e1: Tree, e2: Tree, e3: Tree, e4: Tree) = stepCode(e1, e2, e3, e4)
  def step5Impl(e1: Tree, e2: Tree, e3: Tree, e4: Tree, e5: Tree) = stepCode(e1, e2, e3, e4, e5)
  def step6Impl(e1: Tree, e2: Tree, e3: Tree, e4: Tree, e5: Tree, e6: Tree) = stepCode(e1, e2, e3, e4, e5, e6)
  def step7Impl(e1: Tree, e2: Tree, e3: Tree, e4: Tree, e5: Tree, e6: Tree, e7: Tree) = stepCode(e1, e2, e3, e4, e5, e6, e7)

  private def stepCode(args: Tree*) = {
    val (withArg, parts) = extractParts(args)

    val errors = validateArgs(withArg, args)
    val arity = args.size

    if (errors.isEmpty) {
      val vals =
        args.zipWithIndex.map {
          case (arg, idx) if withArg && idx == args.size - 1 ⇒
            if (checkType[TableExtractor[_]](arg))
              fq"${TermName("v" + idx)} ← _root_.cornichonGherkin.StepDefinition.extractTable(step, $arg)"
            else if (checkType[DocStringExtractor[_]](arg))
              fq"${TermName("v" + idx)} ← _root_.cornichonGherkin.StepDefinition.extractDocString(step, $arg)"
            else
              fq"${TermName("v" + idx)} ← $arg.transformFn(matches($idx))"

          case (arg, idx) ⇒
            fq"${TermName("v" + idx)} ← $arg.transformFn(matches($idx))"
        }

      val impl =
        if (vals.nonEmpty)
          q"""
            for (..$vals) yield fn(..${args.indices.map(i ⇒ TermName("v" + i))})
          """
        else
          q"_root_.scala.Right(fn())"

      val regextractors = if (withArg) args.dropRight(1) else args

      q"""
         import cats.syntax.either._
         
         _root_.cornichonGherkin.${TermName("StepBuilder" + arity)}(fn ⇒ _root_.cornichonGherkin.StepDefinition($parts, ${regextractors.toList}, (step, matches) ⇒ $impl))
      """
    } else reportErrors(errors)
  }

  private def validateArgs(withArg: Boolean, args: Seq[Tree]) = {
    val errors =
      (if (withArg) args.dropRight(1) else args).flatMap(typeCheck[RegExpExtractor[_]](_))

    val lastErrors =
      if (withArg) {
        val last = args.last
        val t = typeCheck[TableExtractor[_]](last)
        val s = typeCheck[DocStringExtractor[_]](last)
        val r = typeCheck[RegExpExtractor[_]](last)

        if (t.isDefined && s.isDefined && r.isDefined) t.toList else Nil
      } else Nil

    errors ++ lastErrors
  }

  private def checkType[T: WeakTypeTag](tree: Tree): Boolean =
    checkType[T](tree.tpe)

  private def checkType[T: WeakTypeTag](tpe: Type): Boolean =
    tpe <:< weakTypeTag[T].tpe

  private def extractParts(args: Seq[Tree]): (Boolean, List[String]) = {
    val parts = extractStringContext

    val withArg = parts.size >= 2 && parts.last.matches("\\s*") && parts.dropRight(1).last.matches("^.*:\\s*$")

    if (withArg) {
      val lastTpe = args.last.tpe
      
      if (checkType[TableExtractor[_]](lastTpe) || checkType[DocStringExtractor[_]](lastTpe))
        true → parts.dropRight(1).updated(parts.size - 2, parts(parts.size - 2).replaceAll("(.*):\\s*$", "$1"))
      else false → parts
    } else false → parts
  }

  private def typeCheck[T: WeakTypeTag](tree: Tree): Option[(Position, String)] = try {
    c.typecheck(tree, pt = weakTypeTag[T].tpe)

    None
  } catch {
    case e: c.TypecheckException ⇒ Some(e.pos.asInstanceOf[Position] → e.msg)
  }

  private def extractStringContext: List[String] = c.prefix.tree match {
    case Apply(_, List(Apply(_, parts))) ⇒
      parts.collect {case Literal((Constant(s: String))) ⇒ s}
  }

  private def reportErrors(errors: Seq[(Position, String)]) = {
    require(errors.nonEmpty)

    val (lastPos, lastError) = errors.last

    errors.dropRight(1).foreach{case (pos, error) ⇒ c.error(pos, error)}

    c.abort(lastPos, lastError)
  }
}
