package cornichonGherkin

import com.github.agourlay.cornichon.core.{CornichonError, IllegalKey, Session}
import cats.syntax.either._

sealed trait Argument[T] {
  def resolve(s: Session): scala.Either[CornichonError, T]
}

case class Value[T](value: T) extends Argument[T] {
  override def resolve(s: Session) = Right(value)
  override def toString = value.toString
}

case class Placeholder[T](placeholder: String, mapFn: String ⇒ Either[CornichonError, T]) extends Argument[T] {
  override def resolve(s: Session) = {
    val mat = RegExpExtractor.placeholderInnerPattern.matcher(placeholder)

    if (mat.matches()) s.get(mat.group(1), Option(mat.group(3)).map(_.toInt)).flatMap(mapFn)
    else Left(IllegalKey(placeholder))
  }

  override def toString = placeholder
}
