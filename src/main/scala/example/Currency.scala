package example

import java.math.{MathContext, RoundingMode}

sealed abstract class CurrencyType extends Product with Serializable

object CurrencyType {
  abstract class USD extends CurrencyType
  abstract class EUR extends CurrencyType
  abstract class RUB extends CurrencyType
}

sealed abstract class Currency[T <: CurrencyType] extends Product with Serializable {
  import Currency._
  def +(r: Currency[T]): Currency[T] = CurrencyAdd(this, r)
  def -(r: Currency[T]): Currency[T] = CurrencySub(this, r)
  def *(r: Currency[T]): Currency[T] = CurrencyMul(this, r)
  def /(r: Currency[T]): Currency[T] = CurrencyDiv(this, r)

  def eval: BigDecimal = this match {
    case CurrencyValue(amount) => amount
    case CurrencyAdd(l, r) => l.eval + r.eval
    case CurrencySub(l, r) => l.eval - r.eval
    case CurrencyMul(l, r) => l.eval * r.eval
    case CurrencyDiv(l, r) => (l.eval / r.eval).round(mc)
  }

  def print: String = this match {
    case CurrencyValue(amount) => amount.toString
    case CurrencyAdd(l, r) => "(" + l.print + " + " + r.print + ")"
    case CurrencySub(l, r) => "(" + l.print + " - " + r.print + ")"
    case CurrencyMul(l, r) => "(" + l.print + " * " + r.print + ")"
    case CurrencyDiv(l, r) => "(" + l.print + " / " + r.print + ")"
  }
}

object Currency {
  case class CurrencyValue[T <: CurrencyType](amount: BigDecimal) extends Currency[T]
  case class CurrencyAdd[T <: CurrencyType](l: Currency[T], r: Currency[T]) extends Currency[T]
  case class CurrencySub[T <: CurrencyType](l: Currency[T], r: Currency[T]) extends Currency[T]
  case class CurrencyMul[T <: CurrencyType](l: Currency[T], r: Currency[T]) extends Currency[T]
  case class CurrencyDiv[T <: CurrencyType](l: Currency[T], r: Currency[T]) extends Currency[T]

  val mc = new MathContext(3, RoundingMode.HALF_EVEN)
}

object Main extends App {
  import Currency._
  import CurrencyType._
  val expr1: Currency[USD] = CurrencyValue(2) + CurrencyValue(2)
  val expr2: Currency[USD] = expr1 * CurrencyValue(4)
  val expr: Currency[USD] = expr2 / CurrencyValue(3)

  println(expr)
  println(expr.eval)
  println(expr.print)
}
