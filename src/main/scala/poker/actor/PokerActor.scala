package poker.actor

import akka.actor.{Actor, ActorRef}
import poker.evaluator.Evaluation

import scala.collection.mutable.ListBuffer

trait PokerActor extends Actor {
  var value = 0
  var count = 0
  var workingChildren: ListBuffer[ActorRef] = ListBuffer()
  var askActor: Option[ActorRef] = None

  def handleEvaluation(evaluation: Evaluation,
                       shouldEmitEvaluation: Boolean, // false
                       shouldEmitResult: Boolean, // true
                       child: Option[ActorRef]): Unit = {
    value += evaluation.value
    count += 1

    if (shouldEmitEvaluation) {
      context.parent ! evaluation
    }
    if (child.isDefined) {
      workingChildren -= child.get
    }
    if (workingChildren.isEmpty && shouldEmitResult) {
      emitResult()
    }
  }

  private def emitResult(): Unit = {
    val result = if (count != 0)
      value / count
    else
      -1
    askActor.get ! result
  }
}
