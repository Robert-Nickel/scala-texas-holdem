package poker.evaluator

import java.util.UUID.randomUUID

import akka.actor.{ActorRef, Props}
import akka.pattern.ask
import poker.actor.{FlopActor, RiverActor, Start, TurnActor}
import poker.actorSystem
import poker.model.Card

import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, Future}

object ActorPostFlopEvaluator extends PostFlopEvaluator {

  override def getPostFlopEvaluation(cards: List[Card]): Int = {
    val actor: ActorRef = if (cards.size == 5) {
      actorSystem.actorOf(Props(FlopActor(cards)), "FlopActor" + randomUUID())
    } else if (cards.size == 6) {
      actorSystem.actorOf(Props(TurnActor(cards)), "TurnActor" + randomUUID())
    } else {
      actorSystem.actorOf(Props(RiverActor(cards)), "RiverActor" + randomUUID())
    }
    val result: Future[Any] = actor.ask(Start)(3 seconds)
    Await.result(result, Duration.Inf).asInstanceOf[java.lang.Integer]
  }
}
