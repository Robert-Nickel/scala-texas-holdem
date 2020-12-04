package poker.actor

import akka.actor.{Actor, ActorRef}

import scala.collection.mutable.ListBuffer

trait PokerActor extends Actor {
  var value = 0
  var count = 0
  var workingChildren: ListBuffer[ActorRef] = ListBuffer()
  var askActor: Option[ActorRef] = None

  def emitResult(): Unit = {
    askActor.get ! value / count
  }
}
