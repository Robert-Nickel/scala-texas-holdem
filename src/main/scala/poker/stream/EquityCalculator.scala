package poker.stream

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source}
import poker.evaluator.Evaluator
import poker.getDeck
import poker.model.Card

import scala.::
import scala.util.Random

// Preflop Equity
case class EquityCalculator() {
  // shuffle deck, Build stream with

  def calculateFlopEquity(holeCards: List[Option[(Card, Card)]]): Unit = {
    val filteredDeck = getFilteredDeck(holeCards)
    implicit val system = ActorSystem()
    import system.dispatcher // "thread pool"
    //def boardList = List("abc", "def", "ijk", "l", "m", "n", "o", "p", "q")
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

    val indexSource = Source.fromIterator(() => (0 until 9).iterator)
    val boardFlow = Flow[Int].map(index => boardStream(index))
    val evalFlow = Flow[List[Card]].map(board => {
      holeCards.map(holeCardTuple => {
        println(holeCardTuple)
        if (holeCardTuple.isDefined) {
          Evaluator.eval(board :+ holeCardTuple.get._1 :+ holeCardTuple.get._2).value
        } else {
          0
        }
      }).zipWithIndex.maxBy(_._1)._2
    })
    val sink = Sink.foreach[Int](println);

    indexSource.via(boardFlow).via(evalFlow).to(sink).run()
  }

  def getFilteredDeck(holeCards: List[Option[(Card, Card)]]): List[Card] = {
    val holeCardsList = holeCards.filter(_.isDefined).flatMap((cardTuple) => Seq(cardTuple.get._1, cardTuple.get._2))
    getDeck.filter(card => !holeCardsList.contains(card))
  }
}
