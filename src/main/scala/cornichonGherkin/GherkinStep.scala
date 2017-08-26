package cornichonGherkin

import java.util.regex.Pattern

import com.github.agourlay.cornichon.core.{CornichonError, Step}
import com.github.agourlay.cornichon.dsl.BodyElementCollector
import com.github.agourlay.cornichon.steps.regular.assertStep.{AssertStep, Assertion}
import gherkin.ast.{DocString, DataTable => GDataTable, Step => GStep}

import scala.collection.JavaConverters._

sealed trait GherkinStep

object GherkinStep extends GherkinStepHelper {
  def errorStep(title: String, error: CornichonError): Step =
    AssertStep(title, _ ⇒ Assertion.failWith(error))
}

case class StepDefinition(parts: List[String], extractors: Seq[RegExpExtractor[_]], stepFn: (GStep, Vector[String]) ⇒ Either[CornichonError, Step]) extends GherkinStep {
  val pattern = {
    val fullPattern =
      parts.zipWithIndex.foldLeft("") {
        case (acc, (p, idx)) if idx < extractors.size ⇒
          val ex = extractors(idx)

          acc + (if (ex.optional) optEscape(p) else escape(p)) + "(" + ex.regexp.toString + ")" + (if (ex.optional) "?" else "")
        case (acc, (p, _)) ⇒ acc + p
      }

    Pattern.compile(fullPattern)
  }

  private def escape(s: String) = if (s.isEmpty) s else "\\Q" + s + "\\E"
  private def optEscape(s: String) = {
    val mat = "^(.*?)\\s+$".r.pattern.matcher(s)

    if (mat.matches())
      escape(mat.group(1)) + "\\s*"
    else
      escape(s)
  }

  def steps(step: GStep): Option[Step] = {
    val mat = pattern.matcher(step.getText)

    if (mat.matches()) {
      val validated = stepFn(step, extractors.indices.toVector.map(idx ⇒ Option(mat.group(idx + 1)) getOrElse ""))

      validated.fold(
        error ⇒ Some(GherkinStep.errorStep("Can't create step definition", error)),
        v ⇒ Some(v))
    } else None
  }
}

object StepDefinition {
  def extractTable[T](step: GStep, e: TableExtractor[T]): Either[CornichonError, T] = {
    Option(step.getArgument).collect {case t: GDataTable ⇒ t} match {
      case Some(t) ⇒ e.transformFn(t.getRows.asScala.toList.map(row ⇒ row.getCells.asScala.toList.map(cell ⇒ cell.getValue)))
      case None if e.optional ⇒ e.transformFn(Nil)
      case None ⇒ Left(GherkinError("Data table is not provided"))
    }
  }

  def extractDocString[T](step: GStep, e: DocStringExtractor[T]): Either[CornichonError, T] = {
    Option(step.getArgument).collect {case t: DocString ⇒ t} match {
      case Some(t) ⇒ e.transformFn(t.getContent)
      case None if e.optional ⇒ e.transformFn("")
      case None ⇒ Left(GherkinError("Doc string is not provided"))
    }
  }
}

case class After(tags: Set[String], steps: String ⇒ List[Step]) extends GherkinStep

object After {
  def apply(tags: String*): BodyElementCollector[Step, After] =
    BodyElementCollector[Step, After](steps ⇒ After(tags.toSet, _ ⇒ steps))
}

case class Before(tags: Set[String], steps: String ⇒ List[Step]) extends GherkinStep

object Before {
  def apply(tags: String*): BodyElementCollector[Step, Before] =
    BodyElementCollector[Step, Before](steps ⇒ Before(tags.toSet, _ ⇒ steps))
}

case class Around(tags: Set[String], steps: (String, List[Step]) ⇒ List[Step]) extends GherkinStep

object Around {
  def apply(tags: String*)(step: List[Step] ⇒ List[Step]): Around = Around(tags.toSet, (_, s) ⇒ step(s))
}