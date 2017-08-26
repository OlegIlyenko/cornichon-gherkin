package cornichonGherkin

import com.github.agourlay.cornichon.core.CornichonError

case class GherkinError(baseErrorMessage: String) extends CornichonError
