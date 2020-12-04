package poker.actor

import java.util.UUID.randomUUID

import akka.actor.Props
import poker.evaluator.Evaluation
import poker.getDeck
import poker.model.Card

case class FlopActor(handAndBoard: List[Card]) extends PokerActor {

  override def receive: Receive = {
    case Start => start
    case evaluation: Evaluation => handleEvaluation(evaluation, shouldEmitEvaluation = false, shouldEmitResult = true, Some(context.sender()))
  }

  private def start = {
    askActor = Some(context.sender())
    getDeck
      .filter(card => !handAndBoard.contains(card))
      .foreach(card => {
        val turnActorRef = context.actorOf(Props(TurnActor(handAndBoard :+ card, shouldEmitEvaluation = true)),
          s"turnActor" + randomUUID().toString)
        workingChildren = workingChildren :+ turnActorRef
        turnActorRef ! Start
      })
  }
}
