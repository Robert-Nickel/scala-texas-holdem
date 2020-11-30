package poker.actor

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import poker.model.Card

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object TestActors extends App {
  val system = ActorSystem("FlopEvaluatorExample")
  val handAndBoard = List(Card('A', '♥'), Card('K', '♥'), Card('Q', '♥'), Card('2', '♥'), Card('4', '♣'))

  val flopActor = system.actorOf(Props(FlopActor(handAndBoard)), "FlopActor")
  flopActor ! Start
  Thread.sleep(500)
  implicit val timeout = Timeout(5 seconds)
  val future = flopActor ? Result
  val result = Await.result(future, timeout.duration).asInstanceOf[java.lang.Integer]
  println("result: " + result)
  system.terminate()
}
