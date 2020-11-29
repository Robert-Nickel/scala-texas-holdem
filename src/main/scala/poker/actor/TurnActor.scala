package poker.actor

import akka.actor.{Actor, ActorRef, Props}
import poker.evaluator.Evaluation
import poker.getDeck
import poker.model.Card

case class TurnActor(handAndBoard: List[Card]) extends Actor {
  var waitingFor = Set.empty[ActorRef]

  override def receive: Receive = {
    case Start => {
      val remainingDeck = getDeck.filter( card => !handAndBoard.contains(card))
      remainingDeck.foreach( card => {
        val riverActorRef = context.actorOf(Props(RiverActor(handAndBoard :+ card)), s"riverActor${card.toLetterNotation}")
        riverActorRef ! Evaluate()
        waitingFor += riverActorRef
      })
    }
    case evaluation: Evaluation =>  context.parent ! evaluation

    case RemoveAnActor(actor: ActorRef) => {
      waitingFor -= actor
      if(waitingFor.size == 0) context.parent ! RemoveAnActor(self)
    }

  }
}

