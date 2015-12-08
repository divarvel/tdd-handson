package main

import scala.util.Try
import play.api.libs.json._

object logic {
  case class Order(prices: Seq[Float], quantities: Seq[Int], country: String, reduction: String)
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
      withDiscount <- applyDiscount(withTaxes, order.reduction)
    } yield Answer(withDiscount)
  }

  def getTotalPrice(order: Order): Option[Float] = ???
  def applyTaxes(totalPrice: Float, country: String): Option[Float] = ???
  def applyDiscount(withTaxes: Float, reduction: String): Option[Float] = ???
}
