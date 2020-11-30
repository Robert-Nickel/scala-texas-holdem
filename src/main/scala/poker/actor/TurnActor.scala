package poker.actor

import akka.actor.{Actor, ActorRef, Props}
import poker.evaluator.Evaluation
import poker.getDeck
import poker.model.Card

case class TurnActor(handAndBoard: List[Card]) extends Actor {
  var value = 0
  var count = 0

  override def receive: Receive = {
    case Start => {
      val remainingDeck = getDeck.filter(card => !handAndBoard.contains(card))
      remainingDeck.foreach(card => {
        val riverActorRef = context.actorOf(Props(RiverActor(handAndBoard :+ card)), s"riverActor${card.toLetterNotation}")
        riverActorRef ! Evaluate()
      })
    }

    case evaluation: Evaluation => {
      context.parent ! evaluation
    }
  }
}

