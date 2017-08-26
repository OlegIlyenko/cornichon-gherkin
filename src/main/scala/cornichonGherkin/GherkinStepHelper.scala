package cornichonGherkin

import com.github.agourlay.cornichon.core.Step

import scala.language.experimental.{macros => `scalac, please just let me do it!`}
import com.github.agourlay.cornichon.dsl.BodyElementCollector
import cornichonGherkin.macros._

trait GherkinStepHelper {
  implicit class GherkinStringContext(val sc: StringContext) {
    def step(): StepBuilder0 = macro GherkinStepMacro.step0Impl
    def step[S](e1: DocStringExtractor[S]): StepBuilder1[S] = macro GherkinStepMacro.step1Impl
    def step[T](e1: TableExtractor[T]): StepBuilder1[T] = macro GherkinStepMacro.step1Impl
                                                      
    def step[E1   ](e1: RegExpExtractor[E1]): StepBuilder1[E1] = macro GherkinStepMacro.step1Impl
    def step[E1, S](e1: RegExpExtractor[E1], e2: DocStringExtractor[S]): StepBuilder2[E1, S] = macro GherkinStepMacro.step2Impl
    def step[E1, T](e1: RegExpExtractor[E1], e2: TableExtractor[T]): StepBuilder2[E1, T] = macro GherkinStepMacro.step2Impl

    def step[E1, E2   ](e1: RegExpExtractor[E1], e2: RegExpExtractor[E2]): StepBuilder2[E1, E2] = macro GherkinStepMacro.step2Impl
    def step[E1, E2, S](e1: RegExpExtractor[E1], e2: RegExpExtractor[E2], e3: DocStringExtractor[S]): StepBuilder3[E1, E2, S] = macro GherkinStepMacro.step3Impl
    def step[E1, E2, T](e1: RegExpExtractor[E1], e2: RegExpExtractor[E2], e3: TableExtractor[T]): StepBuilder3[E1, E2, T] = macro GherkinStepMacro.step3Impl

    def step[E1, E2, E3   ](e1: RegExpExtractor[E1], e2: RegExpExtractor[E2], e3: RegExpExtractor[E3]): StepBuilder3[E1, E2, E3] = macro GherkinStepMacro.step3Impl
    def step[E1, E2, E3, S](e1: RegExpExtractor[E1], e2: RegExpExtractor[E2], e3: RegExpExtractor[E3], e4: DocStringExtractor[S]): StepBuilder4[E1, E2, E3, S] = macro GherkinStepMacro.step4Impl
    def step[E1, E2, E3, T](e1: RegExpExtractor[E1], e2: RegExpExtractor[E2], e3: RegExpExtractor[E3], e4: TableExtractor[T]): StepBuilder4[E1, E2, E3, T] = macro GherkinStepMacro.step4Impl

    def step[E1, E2, E3, E4   ](e1: RegExpExtractor[E1], e2: RegExpExtractor[E2], e3: RegExpExtractor[E3], e4: RegExpExtractor[E4]): StepBuilder4[E1, E2, E3, E4] = macro GherkinStepMacro.step4Impl
    def step[E1, E2, E3, E4, S](e1: RegExpExtractor[E1], e2: RegExpExtractor[E2], e3: RegExpExtractor[E3], e4: RegExpExtractor[E4], e5: DocStringExtractor[S]): StepBuilder5[E1, E2, E3, E4, S] = macro GherkinStepMacro.step5Impl
    def step[E1, E2, E3, E4, T](e1: RegExpExtractor[E1], e2: RegExpExtractor[E2], e3: RegExpExtractor[E3], e4: RegExpExtractor[E4], e5: TableExtractor[T]): StepBuilder5[E1, E2, E3, E4, T] = macro GherkinStepMacro.step5Impl

    def step[E1, E2, E3, E4, E5   ](e1: RegExpExtractor[E1], e2: RegExpExtractor[E2], e3: RegExpExtractor[E3], e4: RegExpExtractor[E4], e5: RegExpExtractor[E5]): StepBuilder5[E1, E2, E3, E4, E5] = macro GherkinStepMacro.step5Impl
    def step[E1, E2, E3, E4, E5, S](e1: RegExpExtractor[E1], e2: RegExpExtractor[E2], e3: RegExpExtractor[E3], e4: RegExpExtractor[E4], e5: RegExpExtractor[E5], e6: DocStringExtractor[S]): StepBuilder6[E1, E2, E3, E4, E5, S] = macro GherkinStepMacro.step6Impl
    def step[E1, E2, E3, E4, E5, T](e1: RegExpExtractor[E1], e2: RegExpExtractor[E2], e3: RegExpExtractor[E3], e4: RegExpExtractor[E4], e5: RegExpExtractor[E5], e6: TableExtractor[T]): StepBuilder6[E1, E2, E3, E4, E5, T] = macro GherkinStepMacro.step6Impl

    def step[E1, E2, E3, E4, E5, E6   ](e1: RegExpExtractor[E1], e2: RegExpExtractor[E2], e3: RegExpExtractor[E3], e4: RegExpExtractor[E4], e5: RegExpExtractor[E5], e6: RegExpExtractor[E6]): StepBuilder6[E1, E2, E3, E4, E5, E6] = macro GherkinStepMacro.step6Impl
    def step[E1, E2, E3, E4, E5, E6, S](e1: RegExpExtractor[E1], e2: RegExpExtractor[E2], e3: RegExpExtractor[E3], e4: RegExpExtractor[E4], e5: RegExpExtractor[E5], e6: RegExpExtractor[E6], e7: DocStringExtractor[S]): StepBuilder7[E1, E2, E3, E4, E5, E6, S] = macro GherkinStepMacro.step7Impl
    def step[E1, E2, E3, E4, E5, E6, T](e1: RegExpExtractor[E1], e2: RegExpExtractor[E2], e3: RegExpExtractor[E3], e4: RegExpExtractor[E4], e5: RegExpExtractor[E5], e6: RegExpExtractor[E6], e7: TableExtractor[T]): StepBuilder7[E1, E2, E3, E4, E5, E6, T] = macro GherkinStepMacro.step7Impl
  }

  def Steps: BodyElementCollector[GherkinStep, List[GherkinStep]] =
    BodyElementCollector[GherkinStep, List[GherkinStep]](identity)

  def StepList: BodyElementCollector[Step, List[Step]] =
    BodyElementCollector[Step, List[Step]](identity)


}
