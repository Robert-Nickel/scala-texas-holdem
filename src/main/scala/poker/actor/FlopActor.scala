package poker.actor

import java.util.UUID.randomUUID

import akka.actor.{Actor, ActorRef, Props}
import poker.evaluator.Evaluation
import poker.getDeck
import poker.model.Card

// TODO: zufaellige Reihenfolge beim erstellen der Aktoren

case class Start()

case class Result()

case class RemoveAnActor(actor: ActorRef)

case class FlopActor(handAndBoard: List[Card]) extends Actor {
  var value = 0
  var count = 0
  var waitingFor = Set.empty[ActorRef]

  override def receive: PartialFunction[Any, Unit] = {
    case Start => {
      val remainingDeck = getDeck.filter(card => !handAndBoard.contains(card))
      remainingDeck.foreach(card => {
        val turnActorRef = context.actorOf(Props(TurnActor(handAndBoard :+ card)), s"turnActor${card.toLetterNotation}" + randomUUID().toString)
        turnActorRef ! Start
        waitingFor += turnActorRef
      })
    }

    case RemoveAnActor(actor: ActorRef) => {
      waitingFor -= actor
      if (waitingFor.size == 0) self ! Result
    }

    case evaluation: Evaluation => {
      value += evaluation.value
      count += 1
    }

    case Result => {
      context.parent ! value / count
    }

  }
}
