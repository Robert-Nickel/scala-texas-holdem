package poker.actor

import java.util.UUID.randomUUID

import akka.actor.Props
import poker.evaluator.Evaluation
import poker.getDeck
import poker.model.Card

case class FlopActor(handAndBoard: List[Card]) extends PokerActor {

  override def receive: Receive = {
    case StartCommand => handleStartCommand
    case evaluation: Evaluation => handleEvaluation(evaluation)
    case GetResultCommand => handleGetResultCommand
  }

  override def handleEvaluation(evaluation: Evaluation) = {
    value += evaluation.value
    count += 1
  }

  private def handleStartCommand = {
    getDeck
      .filter(card => !handAndBoard.contains(card))
      .foreach(card => {
        val turnActorRef = context.actorOf(Props(TurnActor(handAndBoard :+ card)), s"turnActor${card.toLetterNotation}" + randomUUID().toString)
        turnActorRef ! StartCommand
      })
  }
}
