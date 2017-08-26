package cornichonGherkin

import com.github.agourlay.cornichon.core.CornichonError

object ColumnGroup {
  val matchAll: Map[String, String] ⇒ Boolean = Function.const(true)

  def apply[T, C1](matchFn: Map[String, String] ⇒ Boolean, c1: Column[C1])(extractFn: (Int, C1) ⇒ Either[CornichonError, T]) =
    ColumnGroup1(matchFn, c1)(extractFn)

  def apply[T, C1, C2](matchFn: Map[String, String] ⇒ Boolean, c1: Column[C1], c2: Column[C2])(extractFn: (Int, C1, C2) ⇒ Either[CornichonError, T]) =
    ColumnGroup2(matchFn, c1, c2)(extractFn)

  def apply[T, C1, C2, C3](matchFn: Map[String, String] ⇒ Boolean, c1: Column[C1], c2: Column[C2], c3: Column[C3])(extractFn: (Int, C1, C2, C3) ⇒ Either[CornichonError, T]) =
    ColumnGroup3(matchFn, c1, c2, c3)(extractFn)

  def apply[T, C1, C2, C3, C4](matchFn: Map[String, String] ⇒ Boolean, c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4])(extractFn: (Int, C1, C2, C3, C4) ⇒ Either[CornichonError, T]) =
    ColumnGroup4(matchFn, c1, c2, c3, c4)(extractFn)

  def apply[T, C1, C2, C3, C4, C5](matchFn: Map[String, String] ⇒ Boolean, c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5])(extractFn: (Int, C1, C2, C3, C4, C5) ⇒ Either[CornichonError, T]) =
    ColumnGroup5(matchFn, c1, c2, c3, c4, c5)(extractFn)

  def apply[T, C1, C2, C3, C4, C5, C6](matchFn: Map[String, String] ⇒ Boolean, c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6])(extractFn: (Int, C1, C2, C3, C4, C5, C6) ⇒ Either[CornichonError, T]) =
    ColumnGroup6(matchFn, c1, c2, c3, c4, c5, c6)(extractFn)

  def apply[T, C1, C2, C3, C4, C5, C6, C7](matchFn: Map[String, String] ⇒ Boolean, c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7])(extractFn: (Int, C1, C2, C3, C4, C5, C6, C7) ⇒ Either[CornichonError, T]) =
    ColumnGroup7(matchFn, c1, c2, c3, c4, c5, c6, c7)(extractFn)
}

case class ColumnGroup1[T, C1](matchFn: Map[String, String] ⇒ Boolean, c1: Column[C1])(extractFn: (Int, C1) ⇒ Either[CornichonError, T]) extends Column[Option[T]] {
  def extract(row: Map[String, String], idx: Int) =
    if (matchFn(row))
      for {
        v1 ← c1.extract(row, idx)
        res ← extractFn(idx, v1)
      } yield Some(res)
    else
      Right(None)
}

case class ColumnGroup2[T, C1, C2](matchFn: Map[String, String] ⇒ Boolean, c1: Column[C1], c2: Column[C2])(extractFn: (Int, C1, C2) ⇒ Either[CornichonError, T]) extends Column[Option[T]] {
  def extract(row: Map[String, String], idx: Int) =
    if (matchFn(row))
      for {
        v1 ← c1.extract(row, idx)
        v2 ← c2.extract(row, idx)
        res ← extractFn(idx, v1, v2)
      } yield Some(res)
    else
      Right(None)
}

case class ColumnGroup3[T, C1, C2, C3](matchFn: Map[String, String] ⇒ Boolean, c1: Column[C1], c2: Column[C2], c3: Column[C3])(extractFn: (Int, C1, C2, C3) ⇒ Either[CornichonError, T]) extends Column[Option[T]] {
  def extract(row: Map[String, String], idx: Int) =
    if (matchFn(row))
      for {
        v1 ← c1.extract(row, idx)
        v2 ← c2.extract(row, idx)
        v3 ← c3.extract(row, idx)
        res ← extractFn(idx, v1, v2, v3)
      } yield Some(res)
    else
      Right(None)
}

case class ColumnGroup4[T, C1, C2, C3, C4](matchFn: Map[String, String] ⇒ Boolean, c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4])(extractFn: (Int, C1, C2, C3, C4) ⇒ Either[CornichonError, T]) extends Column[Option[T]] {
  def extract(row: Map[String, String], idx: Int) =
    if (matchFn(row))
      for {
        v1 ← c1.extract(row, idx)
        v2 ← c2.extract(row, idx)
        v3 ← c3.extract(row, idx)
        v4 ← c4.extract(row, idx)
        res ← extractFn(idx, v1, v2, v3, v4)
      } yield Some(res)
    else
      Right(None)
}

case class ColumnGroup5[T, C1, C2, C3, C4, C5](matchFn: Map[String, String] ⇒ Boolean, c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5])(extractFn: (Int, C1, C2, C3, C4, C5) ⇒ Either[CornichonError, T]) extends Column[Option[T]] {
  def extract(row: Map[String, String], idx: Int) =
    if (matchFn(row))
      for {
        v1 ← c1.extract(row, idx)
        v2 ← c2.extract(row, idx)
        v3 ← c3.extract(row, idx)
        v4 ← c4.extract(row, idx)
        v5 ← c5.extract(row, idx)
        res ← extractFn(idx, v1, v2, v3, v4, v5)
      } yield Some(res)
    else
      Right(None)
}

case class ColumnGroup6[T, C1, C2, C3, C4, C5, C6](matchFn: Map[String, String] ⇒ Boolean, c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6])(extractFn: (Int, C1, C2, C3, C4, C5, C6) ⇒ Either[CornichonError, T]) extends Column[Option[T]] {
  def extract(row: Map[String, String], idx: Int) =
    if (matchFn(row))
      for {
        v1 ← c1.extract(row, idx)
        v2 ← c2.extract(row, idx)
        v3 ← c3.extract(row, idx)
        v4 ← c4.extract(row, idx)
        v5 ← c5.extract(row, idx)
        v6 ← c6.extract(row, idx)
        res ← extractFn(idx, v1, v2, v3, v4, v5, v6)
      } yield Some(res)
    else
      Right(None)
}

case class ColumnGroup7[T, C1, C2, C3, C4, C5, C6, C7](matchFn: Map[String, String] ⇒ Boolean, c1: Column[C1], c2: Column[C2], c3: Column[C3], c4: Column[C4], c5: Column[C5], c6: Column[C6], c7: Column[C7])(extractFn: (Int, C1, C2, C3, C4, C5, C6, C7) ⇒ Either[CornichonError, T]) extends Column[Option[T]] {
  def extract(row: Map[String, String], idx: Int) =
    if (matchFn(row))
      for {
        v1 ← c1.extract(row, idx)
        v2 ← c2.extract(row, idx)
        v3 ← c3.extract(row, idx)
        v4 ← c4.extract(row, idx)
        v5 ← c5.extract(row, idx)
        v6 ← c6.extract(row, idx)
        v7 ← c7.extract(row, idx)
        res ← extractFn(idx, v1, v2, v3, v4, v5, v6, v7)
      } yield Some(res)
    else
      Right(None)
}
