package poker.evaluator

import poker.model.Card

object FastPostFlopEvaluator extends PostFlopEvaluator {

  override def getPostFlopEvaluation(cards: List[Card]): Int = {
    Evaluator.eval(cards).value
  }
}
