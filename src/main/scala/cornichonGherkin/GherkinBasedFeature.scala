package cornichonGherkin

import java.io.InputStreamReader

import com.github.agourlay.cornichon.CornichonFeature
import com.github.agourlay.cornichon.core.{FeatureDef, Step, Scenario => ScenarioDef}
import com.github.agourlay.cornichon.steps.wrapped.AttachStep
import gherkin.ast.{Background, GherkinDocument, Tag, Feature => GFeature, Scenario => GScenario, Step => GStep}
import gherkin.{AstBuilder, Parser}

import scala.collection.JavaConverters._

trait GherkinBasedFeature extends CornichonFeature with GherkinStepHelper with ExtractorHelper with ColumnHelper {
  import GherkinBasedFeature._

  def featureFile: String
  def stepDefinitions: List[GherkinStep]

  lazy val feature = generateFeature(loadFeature(featureFile), stepDefinitions)

  lazy val Before = cornichonGherkin.Before
  lazy val After = cornichonGherkin.After
  lazy val Around = cornichonGherkin.Around
}

object GherkinBasedFeature {
  val FocusTag = "@focus"
  val IgnoreTag = "@ignore"
  val PendingTag = "@pending"

  def loadFeature(fileName: String): GherkinDocument =
    getClass.getResourceAsStream("/" + fileName) match {
      case null ⇒ throw GherkinError("Can't find feature file: " + fileName).toException
      case stream ⇒ new Parser(new AstBuilder).parse(new InputStreamReader(stream))
    }

  def generateFeature(doc: GherkinDocument, stepDefinitions: List[GherkinStep]) = {
    Option(doc.getFeature).fold(FeatureDef("[Empty]", Nil))(f ⇒ defineFeature(f, GherkinStepColl(stepDefinitions, goodTags(f.getTags))))
  }

  def defineFeature(feature: GFeature, stepDefinitions: GherkinStepColl): FeatureDef = {
    val backgroundSteps = feature.getChildren.asScala.flatMap {
      case b: Background ⇒ b.getSteps.asScala
      case _ ⇒ Nil
    }

    val scenarios = feature.getChildren.asScala.collect {case s: GScenario ⇒ s}

    val focused = scenarios filter (s ⇒ stepDefinitions.isFocused(goodTags(s.getTags)))

    val cScenarios = scenarios.map { s ⇒
      val tags = goodTags(s.getTags)
      val ignored = (focused.nonEmpty && !stepDefinitions.isFocused(tags)) || stepDefinitions.isIgnored(tags)
      val pending = stepDefinitions.isPending(tags)

      val steps = (backgroundSteps ++ s.getSteps.asScala).map(defineStep(_, stepDefinitions, tags))

      ScenarioDef(s.getName, steps.toList, ignored = ignored, pending = pending)
    }

    FeatureDef(feature.getName, cScenarios.toList)
  }

  private def goodTags(tags: java.util.List[Tag]) = tags.asScala.map(_.getName).toSet

  def defineStep(step: GStep, stepDefinitions: GherkinStepColl, tags: Set[String]): Step = {
    val foundStep =
      stepDefinitions.stepDefinitions.foldLeft(None: Option[Step]) {
        case (None, s) ⇒ s.steps(step)
        case (s @ Some(_), _) ⇒ s
      }
    
    foundStep match {
      case Some(s) ⇒
        val withTitle = s.setTitle(step.getText)

        val before = stepDefinitions.before(tags)
        val after = stepDefinitions.after(tags)

        val withHooks =
          if (before.nonEmpty || after.nonEmpty)
            AttachStep(step.getText, (before :+ withTitle) ++ after)
          else
            withTitle

        stepDefinitions.around(tags, withHooks)

      case None ⇒
        val message = "Step definition not found for: " + step.getText

        GherkinStep.errorStep(step.getText, GherkinError(message))
    }
  }
}

case class GherkinStepColl(allDefinitions: List[GherkinStep], featureTags: Set[String]) {
  import GherkinBasedFeature.{FocusTag, IgnoreTag, PendingTag}

  lazy val stepDefinitions = allDefinitions.collect {case s: StepDefinition ⇒ s}
  lazy val before = allDefinitions.collect {case s: Before ⇒ s}
  lazy val after = allDefinitions.collect {case s: After ⇒ s}
  lazy val around = allDefinitions.collect {case s: Around ⇒ s}

  def isFocused(tags: Set[String]) = (tags ++ featureTags) contains FocusTag
  def isIgnored(tags: Set[String]) = (tags ++ featureTags) contains IgnoreTag
  def isPending(tags: Set[String]) = (tags ++ featureTags) contains PendingTag

  def before(tags: Set[String]): List[Step] = {
    val allTags = tags ++ featureTags

    before.flatMap { b ⇒
      val inter = b.tags.intersect(allTags)

      if (inter.nonEmpty) Some(b.step(inter.head)) else None
    }
  }

  def after(tags: Set[String]): List[Step] = {
    val allTags = tags ++ featureTags

    after.flatMap { a ⇒
      val inter = a.tags.intersect(allTags)

      if (inter.nonEmpty) Some(a.step(inter.head)) else None
    }
  }

  def around(tags: Set[String], s: Step): Step = {
    val allTags = tags ++ featureTags

    around.foldLeft(s) {
      case (acc, a) ⇒
        val inter = a.tags.intersect(allTags)

        if (inter.nonEmpty) a.step(inter.head, acc) else acc
    }
  }
}

class SimpleGherkinFeature(val featureFile: String, val stepDefinitions: List[GherkinStep] = Nil) extends GherkinBasedFeature