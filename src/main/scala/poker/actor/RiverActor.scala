package poker.actor

import poker.evaluator.Evaluator
import poker.model.Card


case class RiverActor(handAndBoard: List[Card],
                      shouldEmitEvaluation: Boolean = false,
                      shouldEmitResult: Boolean = false) extends PokerActor {

  override def receive: Receive = {
    case Start => start
  }

  private def start = {
    askActor = Some(context.sender())
    handleEvaluation(Evaluator.eval(handAndBoard), shouldEmitEvaluation, shouldEmitResult, None)
  }
}