package poker.actor

import java.util.UUID.randomUUID

import akka.actor.{ActorRef, Props}
import poker.evaluator.Evaluation
import poker.getDeck
import poker.model.Card

case class TurnActor(handAndBoard: List[Card],
                     shouldEmitEvaluation: Boolean = false,
                     shouldEmitResult: Boolean = false) extends PokerActor {


  override def receive: Receive = {
    case Start => start
    case evaluation: Evaluation => {
      handleEvaluation(evaluation, shouldEmitEvaluation, shouldEmitResult, Some(context.sender))
    }
  }

  private def start = {
    askActor = Some(context.sender())
    val remainingDeck = getDeck.filter(card => !handAndBoard.contains(card))
    remainingDeck.foreach(card => {
      val riverActorRef = context.actorOf(Props(RiverActor(handAndBoard :+ card, shouldEmitEvaluation = true)),
        s"riverActor" + randomUUID())
      workingChildren = workingChildren :+ riverActorRef
      riverActorRef ! Start
    })
  }
}

