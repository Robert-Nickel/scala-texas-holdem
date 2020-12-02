package poker.actor

import akka.actor.Props
import poker.evaluator.Evaluation
import poker.getDeck
import poker.model.Card

case class TurnActor(handAndBoard: List[Card]) extends PokerActor {

  override def receive: Receive = {
    case StartCommand => start
    case evaluation: Evaluation => handleEvaluation(evaluation)
    case GetResultCommand => handleGetResultCommand
  }

  private def start = {
    val remainingDeck = getDeck.filter(card => !handAndBoard.contains(card))
    remainingDeck.foreach(card => {
      val riverActorRef = context.actorOf(Props(RiverActor(handAndBoard :+ card)), s"riverActor${card.toLetterNotation}")
      riverActorRef ! Evaluate()
    })
  }
}

