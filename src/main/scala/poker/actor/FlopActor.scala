package poker.actor

import java.util.UUID.randomUUID

import akka.actor.Props
import poker.evaluator.Evaluation
import poker.getDeck
import poker.model.Card

case class FlopActor(handAndBoard: List[Card]) extends PokerActor {

  override def receive: Receive = {
    case StartCommand => handleStartCommand
    case evaluation: Evaluation => handleEvaluation(evaluation, shouldEmit = false)
    case GetResultCommand => handleGetResultCommand
  }

  private def handleStartCommand = {
    getDeck
      .filter(card => !handAndBoard.contains(card))
      .foreach(card => {
        val turnActorRef = context.actorOf(Props(TurnActor(handAndBoard :+ card, shouldEmit=true)), s"turnActor${card.toLetterNotation}" + randomUUID().toString)
        turnActorRef ! StartCommand
      })
  }
}
