package poker.actor

import java.util.UUID.randomUUID

import akka.actor.Props
import poker.getDeck
import poker.model.Card

case class FlopActor(handAndBoard: List[Card]) extends PokerActor {

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
    getDeck
      .filter(card => !handAndBoard.contains(card))
      .foreach(card => {
        val turnActorRef = context.actorOf(Props(TurnActor(handAndBoard :+ card)),
          s"turnActor" + randomUUID().toString)
        workingChildren = workingChildren :+ turnActorRef
        turnActorRef ! Start
      })
  }
}
