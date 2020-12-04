package poker.actor

import poker.evaluator.{Evaluation, Evaluator}
import poker.model.Card

case class RiverActor(handAndBoard: List[Card]) extends PokerActor {

  override def receive: Receive = {
    case Start => start
  }

  private def start = {
    askActor = Some(context.sender())
    handleEvaluation(Evaluator.eval(handAndBoard))
  }

  def handleEvaluation(evaluation: Evaluation): Unit = {
    askActor.get ! evaluation.value
  }
}