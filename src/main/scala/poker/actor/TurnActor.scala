package poker.actor

import akka.actor.{Actor, ActorRef, Props}
import poker.evaluator.Evaluation
import poker.getDeck
import poker.model.Card

case class TurnActor(handAndBoard: List[Card]) extends Actor {
  var waitingFor = Set.empty[ActorRef]
  var value = 0
  var count = 0

  override def receive: Receive = {
    case Start => {
      val remainingDeck = getDeck.filter(card => !handAndBoard.contains(card))
      remainingDeck.foreach(card => {
        val riverActorRef = context.actorOf(Props(RiverActor(handAndBoard :+ card)), s"riverActor${card.toLetterNotation}")
        riverActorRef ! Evaluate()
        waitingFor += riverActorRef
      })
    }

    case evalAndActor: (Evaluation, ActorRef) => {
      waitingFor -= evalAndActor._2
      value += evalAndActor._1.value
      count += 1
      if (waitingFor.isEmpty) {
        val result = if (count != 0) {
          value / count
        }
        else -1
        context.parent ! result
      }
    }
  }
}

