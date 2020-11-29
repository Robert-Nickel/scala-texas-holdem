package poker.actor

import akka.actor.{Actor, ActorSystem, Props}
import poker.evaluator.Evaluator
import poker.model.Card

object PostflopEvaluator extends App {



  case class Start(BoardAndHand: List[Card])
  case class Stop()
  case class Result(val invalidHand: Int, val highCard: Int, val onePair: Int, val twoPair: Int)

  class PostflopActor extends Actor {

    var invalidHand = 0;
    var highCard = 0;
    var onePair = 0;
    var twoPair = 0;

    override def receive: Receive = {
      case Start(handAndBoard: List[Card]) => {
        val newHandAndBoard = handAndBoard :+ Card('5', '♣') :+ Card('8', '♦')
        val childRef = context.actorOf(Props[EvaluateActor], "5c8d")
        childRef ! PossibleFutureCombination(newHandAndBoard)
        val childRef2 = context.actorOf(Props[EvaluateActor], "4c8d")
        childRef2 ! PossibleFutureCombination(handAndBoard :+ Card('4', '♣') :+ Card('8', '♦'))

        // TODO: add all other combinations
      }
      case HandResult(handValue) => {
        // add 1 to calculated Hands
        handValue match {
          case "invalid hand" => invalidHand += 1
          case "high card" => highCard += 1
          case "one pair" => onePair += 1
          case "two pairs" => twoPair += 1
        }
      }
      case Stop => {
        println(Result(invalidHand = invalidHand, highCard = highCard, onePair = onePair, twoPair = twoPair))
      }
    }
  }

  case class PossibleFutureCombination(handAndBoard: List[Card]) // a list of 7 cards
  case class HandResult(val handType: String) // can be highcard, fullhouse, etc.

  // evaluates 1 possible combination
  class EvaluateActor extends Actor {
    override def receive: Receive = {
      case PossibleFutureCombination(handAndBoard: List[Card]) => context.sender() ! HandResult(Evaluator.eval(handAndBoard).handName)
    }
  }

  val system = ActorSystem("PostflopEvaluatorExample")
  val postflopActor = system.actorOf(Props[PostflopActor], "postflopActor")

  postflopActor ! Start(List(Card('A', '♥'), Card('K', '♥'),Card('A', '♠'), Card('3', '♦'), Card('4', '♣')))
  Thread.sleep(1000)
  postflopActor ! Stop
}

// Den code werden wir dann vemrutlich innerhalb actAsBot aufrufen oder weiter oben (Falls wir dem spieler auch zeigen wollen wie wahrscheinlich
// die jeweilige Kombination ist ?

/* Waiting for a result (ausserhalb von aktoren). Nachdem die Aktoren gearbeitet haben, warten wir auf einen Wert!
https://stackoverflow.com/questions/29825276/scala-akka-consumer-producer-return-value
* implicit val timeout = Timeout(5 seconds)
val wCount = system.actorOf(Props(run.actor()), "WordCount")
val answer = Await.result(wCount ? "sent.txt", timeout.duration)
* */