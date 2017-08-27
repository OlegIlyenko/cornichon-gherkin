package cornichonGherkin

import java.util.regex.Pattern

import com.github.agourlay.cornichon.core.CornichonError
import com.github.agourlay.cornichon.json.CornichonJson
import gherkin.ast.{DataTable => GDataTable}

trait Extractor[T]
trait ArgumentExtractor[T] extends Extractor[T]

import cats.instances.string._
import cats.syntax.either._

object Extractor extends ExtractorHelper

trait ExtractorHelper {
  def regExpArg[T](r: String, transform: String ⇒ Either[CornichonError, T]) = RegExpExtractor(Pattern.compile(r), transform)
  def strArg(pattern: String) = regExpArg(pattern, s ⇒ Right(s))
  def docStrArg[T](transformFn: String ⇒ Either[CornichonError, T]) = DocStringExtractor(transformFn)
  def jsonArg = docStrArg(CornichonJson.parseJson(_))

  def tableArg[T](transformFn: List[Map[String, String]] ⇒ Either[CornichonError, T]): TableExtractor[T] =
    TableExtractor { raw ⇒
      if (raw.isEmpty)
        Left(GherkinError("Table must contain at least a header row"))
      else {
        val headers = raw.head

        transformFn(raw.tail.map(row ⇒ headers.zip(row).toMap))
      }
    }

  lazy val strTableArg = TableExtractor { rows ⇒
    val colSizes = rows.map(_ map (_.length)).transpose.map(_.max)

    Right(rows.map(_ zip colSizes map {case (v, size) ⇒ s"%-${size}s" format v} mkString ("| ", " | ", " |")) mkString ("\n", "\n", "\n"))
  }

  lazy val tableArg: TableExtractor[List[Map[String, String]]] = tableArg(t ⇒ Right(t))
  lazy val docStrArg: DocStringExtractor[String] = docStrArg(s ⇒ Right(s))
  lazy val strArg = regExpArg(".*?", s ⇒ Right(s))
  lazy val intArg = regExpArg("\\d+", s ⇒ Right(s.toInt))
}

case class RegExpExtractor[T](regexp: Pattern, transformFn: String ⇒ Either[CornichonError, T], optional: Boolean = false) extends Extractor[T] {
  def transform[R](fn: T ⇒ Either[CornichonError, R]): RegExpExtractor[R] =
    copy(transformFn = transformFn(_).flatMap(fn))

  /** allows a placeholder in place of this argument */
  def $: RegExpExtractor[Argument[T]] = ph

  /** allows a placeholder in place of this argument */
  def ph: RegExpExtractor[Argument[T]] = copy(
    regexp = Pattern.compile(RegExpExtractor.PlaceholderRegexp + "|" + regexp.toString),
    transformFn = s ⇒ if (s.startsWith("<")) Right(Placeholder(s, transformFn)) else transformFn(s).map(Value(_)))

  def opt: RegExpExtractor[Option[T]] = copy(
    optional = true,
    transformFn = s ⇒ if (s.trim.isEmpty) Right(None) else transformFn(s).map(Some(_)))
}

object RegExpExtractor {
  val PlaceholderRegexp = "<[^>]+>"
  val placeholderInnerPattern = """<([^>]+?)(\[(\d+)\])?>""".r.pattern
}

case class TableExtractor[T](transformFn: List[List[String]] ⇒ Either[CornichonError, T], optional: Boolean = false) extends ArgumentExtractor[T] {
  def transform[R](fn: T ⇒ Either[CornichonError, R]): TableExtractor[R] =
    copy(transformFn = transformFn(_).flatMap(fn))

  def opt: TableExtractor[Option[T]] =
    copy(optional = true, transformFn = l ⇒ if (l.isEmpty) Right(None) else transformFn(l).map(Some(_)))
}

case class DocStringExtractor[T](transformFn: String ⇒ Either[CornichonError, T], optional: Boolean = false) extends ArgumentExtractor[T] {
  def transform[R](fn: T ⇒ Either[CornichonError, R]): DocStringExtractor[R] =
    copy(transformFn = transformFn(_).flatMap(fn))

  def opt: DocStringExtractor[Option[T]] =
    copy(optional = true, transformFn = s ⇒ if (s.trim.isEmpty) Right(None) else transformFn(s).map(Some(_)))
}

