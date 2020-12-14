package poker.evaluator

import poker.model.Card

trait PostFlopEvaluator {
  def getPostFlopEvaluation(board: List[Card]): Int
}
