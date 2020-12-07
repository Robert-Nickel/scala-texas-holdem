package poker.stream

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import poker.evaluator.Evaluator
import poker.getDeck
import poker.model.Card

import scala.::
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.Random

// Preflop Equity
case class EquityCalculator() {
  // shuffle deck, Build stream with

  def calculateFlopEquity(holeCards: List[Option[(Card, Card)]]): Future[List[Int]] = {
    val filteredDeck = getFilteredDeck(holeCards)
    implicit val system = ActorSystem()
    val evaluator = Evaluator
    import system.dispatcher // "thread pool"

    def boardStream: LazyList[List[Card]] = {
      val shuffledDeck = Random.shuffle(filteredDeck)
      List(
        shuffledDeck.head,
        shuffledDeck.tail.head,
        shuffledDeck.tail.tail.head,
        shuffledDeck.tail.tail.tail.head,
        shuffledDeck.tail.tail.tail.tail.head
      ) #:: boardStream
    }

    val evalFlow = Flow[List[Card]].map(board => {
      println("Board: ")
      println(board)
      holeCards.map(cardTuple => {
        if(cardTuple.isDefined) {
          val result = evaluator.eval(board :+ cardTuple.get._1 :+ cardTuple.get._2 ).value
          println(result)
          result
        } else {
          0
        }
      }).zipWithIndex.maxBy(_._1)._2
    })

    // val sink = Sink.foreach[Int](println);
    val sink = Sink.collection
//    val sink = Sink.fold(ListBuffer(0,0,0,0,0,0))((acc: ListBuffer[Int], winnerIndex: Int) =>  winnerIndex match {
//      case 0 => player0Wins += 1
//    })
    // Source(boardStream).via(evalFlow).to(sink).run()

    // Source(boardStream).via(evalFlow).runWith(sink)
    val sum: Future[List[Int]] = Source(boardStream).via(evalFlow).toMat(sink)(Keep.right).run()
    sum
  }

  def getFilteredDeck(holeCards: List[Option[(Card, Card)]]): List[Card] = {
    val holeCardsList = holeCards.filter(_.isDefined).flatMap((cardTuple) => Seq(cardTuple.get._1, cardTuple.get._2))
    getDeck.filter(card => !holeCardsList.contains(card))
  }
}
