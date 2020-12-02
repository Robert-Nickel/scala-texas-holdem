package poker.actor

import akka.actor.Actor
import poker.evaluator.Evaluation

case class StartCommand()

case class GetResultCommand()

trait PokerActor extends Actor {
  var value = 0
  var count = 0

  def handleEvaluation(evaluation: Evaluation) = {
    value += evaluation.value
    count += 1
    context.parent ! evaluation
  }

  def handleGetResultCommand = {
    val result = if (count != 0)
      value / count
    else
      -1
    context.sender() ! result
  }
}
