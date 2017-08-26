package cornichonGherkin

import com.github.agourlay.cornichon.core.Step

case class StepBuilder0(sfn: (() ⇒ Step) ⇒ GherkinStep) {
  def apply(fn: ⇒ Step): GherkinStep = sfn(() ⇒ fn)
}

case class StepBuilder1[E1](sfn: (E1 ⇒ Step) ⇒ GherkinStep) {
  def apply(fn: E1 ⇒ Step): GherkinStep = sfn(fn)
}

case class StepBuilder2[E1, E2](sfn: ((E1, E2) ⇒ Step) ⇒ GherkinStep) {
  def apply(fn: (E1, E2) ⇒ Step): GherkinStep = sfn(fn)
}

case class StepBuilder3[E1, E2, E3](sfn: ((E1, E2, E3) ⇒ Step) ⇒ GherkinStep) {
  def apply(fn: (E1, E2, E3) ⇒ Step): GherkinStep = sfn(fn)
}

case class StepBuilder4[E1, E2, E3, E4](sfn: ((E1, E2, E3, E4) ⇒ Step) ⇒ GherkinStep) {
  def apply(fn: (E1, E2, E3, E4) ⇒ Step): GherkinStep = sfn(fn)
}

case class StepBuilder5[E1, E2, E3, E4, E5](sfn: ((E1, E2, E3, E4, E5) ⇒ Step) ⇒ GherkinStep) {
  def apply(fn: (E1, E2, E3, E4, E5) ⇒ Step): GherkinStep = sfn(fn)
}

case class StepBuilder6[E1, E2, E3, E4, E5, E6](sfn: ((E1, E2, E3, E4, E5, E6) ⇒ Step) ⇒ GherkinStep) {
  def apply(fn: (E1, E2, E3, E4, E5, E6) ⇒ Step): GherkinStep = sfn(fn)
}

case class StepBuilder7[E1, E2, E3, E4, E5, E6, E7](sfn: ((E1, E2, E3, E4, E5, E6, E7) ⇒ Step) ⇒ GherkinStep) {
  def apply(fn: (E1, E2, E3, E4, E5, E6, E7) ⇒ Step): GherkinStep = sfn(fn)
}