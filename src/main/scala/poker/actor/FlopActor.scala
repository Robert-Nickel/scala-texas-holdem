package poker.actor

import java.util.UUID.randomUUID

import akka.actor.{Actor, ActorRef, Props}
import poker.evaluator.Evaluation
import poker.getDeck
import poker.model.Card

// TODO: random order of deck to increase statistical significance for incomplete evaluations

case class Start()

case class Result()

case class RemoveAnActor(actor: ActorRef)

case class FlopActor(handAndBoard: List[Card]) extends Actor {
  var value = 0
  var count = 0
  // var waitingFor = Set.empty[ActorRef]

  override def receive: PartialFunction[Any, Unit] = {
    case Start =>
      getDeck
        .filter(card => !handAndBoard.contains(card))
        .foreach(card => {
          val turnActorRef = context.actorOf(Props(TurnActor(handAndBoard :+ card)), s"turnActor${card.toLetterNotation}" + randomUUID().toString)
          turnActorRef ! Start
          // waitingFor += turnActorRef
        })

    //case RemoveAnActor(actor: ActorRef) => {
    //  waitingFor -= actor
    //  if (waitingFor.size == 0) self ! Result
    //}

    case evaluation: Integer => {
      value += evaluation
      count += 1
    }

    case Result => {
      val result = if (count != 0)
        value / count
      else
        -1
      context.sender() ! result
    }
  }
}
