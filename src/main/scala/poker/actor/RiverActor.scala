package poker.actor

import poker.evaluator.{Evaluation, Evaluator}
import poker.model.Card

case class Evaluate()

case class RiverActor(handAndBoard: List[Card]) extends PokerActor {

  override def receive: Receive = {
    case Evaluate() => handleEvaluateCommand
    case GetResultCommand => handleGetResultCommand
  }

  private def handleEvaluateCommand = {
    handleEvaluation(Evaluator.eval(handAndBoard))
  }
}