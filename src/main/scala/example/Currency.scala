package example

import java.math.{MathContext, RoundingMode}

sealed abstract class Currency extends Product with Serializable

object Currency {
  case object USD extends Currency
  case object EUR extends Currency
  case object RUB extends Currency
}

object implicits {
  import Money._

  implicit class BigDecimalOps(val value: BigDecimal) extends AnyVal {
    def apply[T <: Currency](t: T): Money[T] = MoneyValue[T](value)
  }

  implicit class IntOps(val value: Int) extends AnyVal {
    def apply[T <: Currency](t: T): Money[T] = BigDecimal(value).apply(t)
  }
}

sealed abstract class Money[T <: Currency] {
  import Money._
  def +(right: Money[T]): Money[T] = MoneyAdd(this, right)
  def -(right: Money[T]): Money[T] = MoneySub(this, right)
  def *(right: BigDecimal): Money[T] = MoneyMul(this, right)
  def /(right: BigDecimal): Money[T] = MoneyMul(this, right)

  def eval: BigDecimal = this match {
    case MoneyValue(amount) => amount
    case MoneyAdd(left, right) => left.eval + right.eval
    case MoneySub(left, right) => left.eval - right.eval
    case MoneyMul(left, right) => left.eval * right
    case MoneyDiv(left, right) => left.eval / right
  }

  def print: String = this match {
    case MoneyValue(amount) => amount.toString
    case MoneyAdd(l, r) => "(" + l.print + " + " + r.print + ")"
    case MoneySub(l, r) => "(" + l.print + " - " + r.print + ")"
    case MoneyMul(l, r) => "(" + l.print + " * " + r.toString + ")"
    case MoneyDiv(l, r) => "(" + l.print + " / " + r.toString + ")"
  }
}

object Money {
  case class MoneyValue[T <: Currency](amount: BigDecimal) extends Money[T]
  case class MoneyAdd[T <: Currency](left: Money[T], right: Money[T]) extends Money[T]
  case class MoneySub[T <: Currency](left: Money[T], right: Money[T]) extends Money[T]
  case class MoneyMul[T <: Currency](left: Money[T], right: BigDecimal) extends Money[T]
  case class MoneyDiv[T <: Currency](left: Money[T], right: BigDecimal) extends Money[T]

  val mc = new MathContext(3, RoundingMode.HALF_EVEN)
}

object Main extends App {
  import Currency._
  import implicits._

  val money1 = 40(USD) + 40(USD) * 2

  println(money1)
  println(money1.eval)
  println(money1.print)
}
