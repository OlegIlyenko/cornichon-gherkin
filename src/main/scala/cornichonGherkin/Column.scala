package cornichonGherkin

import com.github.agourlay.cornichon.core.CornichonError
import com.github.agourlay.cornichon.json.CornichonJson
import cats.syntax.traverse._
import cats.syntax.either._
import cats.instances.list._
import cats.instances.either._
import sangria.util.StringUtil

import scala.util.{Failure, Success, Try}

trait Column[T] {
  def extract(row: Map[String, String], idx: Int): Either[CornichonError, T]
}

object Column extends ColumnHelper

trait ColumnHelper {
  def stringBasedColumn[T](name: String)(extractFn: (String, Int) ⇒ Either[CornichonError, T]) =
    StandardColumn[T](name, (json, idx) ⇒ json.get(name) match {
      case Some(s) if s.trim.nonEmpty ⇒ extractFn(s, idx)
      case _ ⇒ Left(TableParseError(s"Column '$name' is not provided. (at index $idx)"))
    })

  def intBasedColumn[T](name: String)(extractFn: (Int, Int) ⇒ Either[CornichonError, T]) =
    stringBasedColumn(name) { (s, idx) ⇒
      Try(s.toInt) match {
        case Success(i) ⇒ extractFn(i, idx)
        case Failure(_) ⇒ Left(TableParseError(s"Column '$name' is not an integer number. (at index $idx)"))
      }
    }

  def stringColumn(name: String): StandardColumn[String] =
    stringBasedColumn(name)((s, _) ⇒ Right(StringUtil.escapeString(s)))

  def intColumn(name: String): StandardColumn[Int] =
    intBasedColumn(name)((i, _) ⇒ Right(i))

  def permyriadColumn(name: String): StandardColumn[Int] =
    stringBasedColumn(name) { (s, idx) ⇒
      val mat = """(\d{1,3})\s*%""".r.pattern.matcher(s)

      if (mat.matches()) Right(mat.group(1).toInt * 100)
      else Left(TableParseError(s"Column '$name' (at row index $idx) had invalid permyriad value. (valid example: '42%')"))
    }

  def parseTable[T](table: String, column: Column[Option[T]]): Either[CornichonError, List[T]] =
    CornichonJson.parseDataTableRaw(table) flatMap (parseTable(_, column))

  def parseTable[T](table: List[Map[String, String]], column: Column[Option[T]]): Either[CornichonError, List[T]] = {
    def pt(rowWithIndex: (Map[String, String], Int)): Either[CornichonError, T] = {
      val (row, idx) = rowWithIndex

      column.extract(row, idx).flatMap(_.map(Right(_)) getOrElse Left(TableParseError(s"Row $idx is absent!")))
    }

    table.zipWithIndex traverseU pt
  }

  def matchAll = ColumnGroup.matchAll
}

case class ColumnOpt[T](name: String, column: Column[T]) extends Column[Option[T]] {
  def extract(row: Map[String, String], idx: Int) =
    row.get(name) match {
      case Some(_) ⇒ column.extract(row, idx).map(Some(_))
      case None ⇒ Right(None)
    }
}

case class ColumnDefault[T](name: String, column: Column[T], default: T) extends Column[T] {
  def extract(row: Map[String, String], idx: Int) =
    row.get(name) match {
      case Some(_) ⇒ column.extract(row, idx)
      case None ⇒ Right(default)
    }
}

case class StandardColumn[T](name: String, extractFn: (Map[String, String], Int) ⇒ Either[CornichonError, T]) extends Column[T] {
  def extract(row: Map[String, String], idx: Int) = extractFn(row, idx)
  def opt = ColumnOpt(name, this)
  def withDefault(default: T) = ColumnDefault(name, this, default)
}

case class TableParseError(baseErrorMessage: String) extends CornichonError