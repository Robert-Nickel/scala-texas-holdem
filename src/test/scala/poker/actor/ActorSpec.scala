package poker.actor

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import poker.model.Card

import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, Future}

class ActorSpec extends TestKit(ActorSystem("PokerActors"))
  with AnyWordSpecLike
  with BeforeAndAfterAll
  with ImplicitSender
  with Matchers {

  "Given a flopActor with flopped royal flush" should {
    val flopActor = system.actorOf(
      Props(FlopActor(List(Card('A', '♦'), Card('K', '♦'), Card('Q', '♦'), Card('J', '♦'), Card('T', '♦')))),
      "FlopActor")
    "return result = 36_874" in {
      val result: Future[Any] = flopActor.ask(Start)(10.seconds)
      Await.result(result, Duration.Inf).asInstanceOf[Int] should be(36_874)
    }
  }

  "Given a turnActor with turned two pairs" should {
    val turnActor = system.actorOf(
      Props(TurnActor(List(Card('A', '♦'), Card('K', '♦'), Card('Q', '♦'), Card('A', '♥'), Card('T', '♦'), Card('T', '♥')))),
      "TurnActor")
    "return result = 17_692" in {
      val result: Future[Any] = turnActor.ask(Start)(10.seconds)
      Await.result(result, Duration.Inf).asInstanceOf[Int] should be(17_692)
    }
  }

  "Given a riverActor with rivered high card" should {
    val riverActor = system.actorOf(
      Props(RiverActor(List(Card('2', '♦'), Card('4', '♦'), Card('6', '♦'), Card('8', '♥'), Card('T', '♦'), Card('Q', '♥'), Card('A', '♥')))),
      "RiverActor")
    "return result = 5_165" in {
      val result: Future[Any] = riverActor.ask(Start)(10.seconds)
      Await.result(result, Duration.Inf).asInstanceOf[Int] should be(5_165)
    }
  }

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }
}
