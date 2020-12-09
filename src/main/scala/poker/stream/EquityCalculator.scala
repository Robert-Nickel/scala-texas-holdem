package poker.stream

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source}
import poker.evaluator.Evaluator
import poker.getDeck
import poker.model.Card

import scala.concurrent.Future
import scala.util.Random

// Preflop Equity
case class EquityCalculator() {
  // shuffle deck, Build stream with

  def calculateFlopEquity(holeCards: List[Option[(Card, Card)]]): Future[List[Int]] = {
    val filteredDeck = getFilteredDeck(holeCards)
    implicit val system = ActorSystem()
    val evaluator = Evaluator // "thread pool"

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
      holeCards.map(cardTuple => {
        if(cardTuple.isDefined) {
          val result = evaluator.eval(board :+ cardTuple.get._1 :+ cardTuple.get._2 ).value
          result
        } else {
          0
        }
      }).zipWithIndex.maxBy(_._1)._2
    })

    Source(boardStream)
      .take(100)
      .via(evalFlow)
      .runWith(Sink.fold(List(0,0,0,0,0,0))((accList: List[Int], elementIdx: Int) => accList.updated(elementIdx, accList(elementIdx) + 1)))
  }

  def getFilteredDeck(holeCards: List[Option[(Card, Card)]]): List[Card] = {
    val holeCardsList = holeCards.filter(_.isDefined).flatMap((cardTuple) => Seq(cardTuple.get._1, cardTuple.get._2))
    getDeck.filter(card => !holeCardsList.contains(card))
  }
}
