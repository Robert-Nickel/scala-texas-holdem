package poker.actor

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import poker.model.Card

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.math.abs

object TestActors extends App {
  val system = ActorSystem("FlopEvaluatorExample")
  // Example. In the actAsBot function, it will be replaced by the actual hand and table.board
  val handAndBoard = List(Card('A', '♥'), Card('K', '♥'), Card('Q', '♥'), Card('2', '♥'), Card('4', '♣'))

  val flopActor = system.actorOf(Props(FlopActor(handAndBoard)), "FlopActor")
  flopActor ! Start
  Thread.sleep(210) // This value defines how long we wait for the actors to calc a value
  implicit val timeout = Timeout(250 milliseconds)
  val result = Await.result(flopActor ? Result, timeout.duration).asInstanceOf[Integer]
  println(s"Exact result would be 15007. This calculation's result is: ${result}.\nThe difference is ${abs(15_007 - result)}.")

  system.terminate()
}
