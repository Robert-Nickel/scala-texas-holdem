package poker.actor

import java.util.UUID.randomUUID

import akka.actor.Props
import poker.getDeck
import poker.model.Card

case class TurnActor(handAndBoard: List[Card]) extends PokerActor {
  override def receive: Receive = {
    case Start => start
    case result: Int => handleResult(result)
  }

  def handleResult(result: Int) = {
    value += result
    count += 1
    workingChildren -= context.sender

    if (workingChildren.isEmpty) {
      emitResult()
    }
  }

  private def start = {
    askActor = Some(context.sender())
    val remainingDeck = getDeck.filter(card => !handAndBoard.contains(card))
    remainingDeck.foreach(card => {
      val riverActorRef = context.actorOf(Props(RiverActor(handAndBoard :+ card)),
        s"riverActor" + randomUUID())
      workingChildren = workingChildren :+ riverActorRef
      riverActorRef ! Start
    })
  }
}

