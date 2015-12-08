package main

import scala.util.Try
import play.api.libs.json._

import scalaz._; import Scalaz._

object logic {
  sealed trait DiscountType
  case object STANDARD extends DiscountType
  val discountTypes = Seq(STANDARD)

  implicit val discountTypeFormat = new Format[DiscountType] {
    def reads(js: JsValue): JsResult[DiscountType] = js match {
      case JsString(s) => discountTypes.find(_.toString == s) map { dt =>
        JsSuccess(dt)
      } getOrElse JsError("Invalid discount type: " + js.toString)
      case _ => JsError("Invalid discount type: " + js.toString)
    }
    def writes(dt: DiscountType): JsValue = JsString(dt.toString)
  }

  case class Order(prices: Seq[Float], quantities: Seq[Int], country: String, reduction: DiscountType)
  implicit val orderFormat = Json.format[Order]
  case class Answer(total: Float)
  implicit val answerFormat = Json.format[Answer]

  def processRequest(body: String): String = {
    val answer = for {
      jsonBody <- Try(Json.parse(body)).toOption
      order    <- Json.fromJson[Order](jsonBody).asOpt
      answer   <- processOrder(order)
    } yield Json.toJson(answer).toString

    answer getOrElse "{}"
  }

  def processOrder(order: Order): Option[Answer] = {
    for {
      totalPrice <- getTotalPrice(order)
      withTaxes <- applyTaxes(totalPrice, order.country)
    } yield Answer(applyDiscount(withTaxes, order.reduction))
  }

  def getTotalPrice(order: Order): Option[Float] = {
    val total = order.prices.zip(order.quantities).map({case (p,q) => p * q}).sum

    Some(total).filter(_ => order.prices.length == order.quantities.length)
  }

  sealed trait Taxed
  def applyTaxes(totalPrice: Float, country: String): Option[Float @@ Taxed] = {
    ratesByCountry.get(country) map { rate =>
      Tag[Float,Taxed](totalPrice * (1 + rate))
    }
  }
  def applyDiscount(withTaxes: Float @@ Taxed, reduction: DiscountType): Float = {
    val price = Tag.unwrap(withTaxes)
    val rate = getDiscountRate(reduction, price)

    price * (1 - rate)
  }

  def getDiscountRate(discount: DiscountType, price: Float): Float = discount match {
    case STANDARD => getStandardDiscount(price)
  }

  def getStandardDiscount(price: Float): Float = price match {
    case p if p >= 50000 => 0.15f
    case p if p >= 10000 => 0.10f
    case p if p >= 7000  => 0.07f
    case p if p >= 5000  => 0.05f
    case p if p >= 1000  => 0.03f
    case _               => 0f
  }

  val ratesByCountry = Map(
    "DE" -> 0.20f,
    "UK" -> 0.21f,
    "FR" -> 0.20f,
    "IT" -> 0.25f,
    "ES" -> 0.19f,
    "PL" -> 0.21f,
    "RO" -> 0.20f,
    "NL" -> 0.20f,
    "BE" -> 0.24f,
    "EL" -> 0.20f,
    "CZ" -> 0.19f,
    "PT" -> 0.23f,
    "HU" -> 0.27f,
    "SE" -> 0.23f,
    "AT" -> 0.22f,
    "BG" -> 0.21f,
    "DK" -> 0.21f,
    "FI" -> 0.17f,
    "SK" -> 0.18f,
    "IE" -> 0.21f,
    "HR" -> 0.23f,
    "LT" -> 0.23f,
    "SI" -> 0.24f,
    "LV" -> 0.20f,
    "EE" -> 0.22f,
    "CY" -> 0.21f,
    "LU" -> 0.25f,
    "MT" -> 0.20f
  )
}
