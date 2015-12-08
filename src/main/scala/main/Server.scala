package main

import akka.actor.ActorSystem
import spray.routing.SimpleRoutingApp

import play.api.libs.json._

import logic._


object Server extends App with SimpleRoutingApp {

  implicit val system = ActorSystem("my-system")

  val address = "0.0.0.0"
  val port: Int = 9000

  startServer(interface = address, port = port) {

    println("Server started @ " + address + ":" + port)

    path("order") {
      post {
        entity(as[String]) {
          order =>
            complete {
              processRequest(order)
            }
        }
      }
    } ~
      path("feedback") {
        post {
          entity(as[String]) {
            feedback =>
              complete {
                println("Feedback received : " + feedback)
                ""
              }
          }
        }
      }

  }
}
