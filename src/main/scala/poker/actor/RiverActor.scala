package poker.actor

import akka.actor.Actor
import poker.evaluator.Evaluator
import poker.model.Card

case class Evaluate()

case class RiverActor(handAndBoard: List[Card]) extends Actor {
  override def receive: Receive = {
    case Evaluate() => {
      context.sender() ! Evaluator.eval(handAndBoard)
      context.sender() ! RemoveAnActor(self)
    }
  }
}
