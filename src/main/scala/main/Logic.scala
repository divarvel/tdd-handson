package main

import scala.util.Try
import play.api.libs.json._

object logic {

  case class Order(prices: Seq[Float], quantities: Seq[Int], country: String, reduction: String)
  implicit val orderFormat = Json.format[Order]
  case class Answer(total: Float)
  implicit val answerFormat = Json.format[Answer]

  def processRequest(body: String): String = ???
}
