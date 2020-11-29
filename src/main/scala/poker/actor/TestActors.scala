package poker.actor

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import poker.model.Card

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, DurationInt}
import scala.language.postfixOps

object TestActors extends App {
  val system = ActorSystem("FlopEvaluatorExample")
  implicit val timeout = Timeout(5 seconds)
  val handAndBoard = List(Card('A', '♥'), Card('K', '♥'),Card('Q', '♥'),Card('2', '♥'),Card('4', '♣'))
  val flopActor = system.actorOf(Props(FlopActor(handAndBoard)), "FlopActor")
  val future = flopActor ? Start
  val result = Await.result(future, timeout.duration).asInstanceOf[java.lang.Integer]
  println("result: " + result)
  system.terminate()
}
