package poker.stream

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

    // a source element that "emits" integers
    val source = Source(boardStream)
    val flow = Flow[List[Card]].map(board => {
      println(board.toString())
      println(holeCards)
      holeCards.map(holeCardTuple => {
        if (holeCardTuple.isDefined) {
          // FIXME: why is map not evaluated multiple times?
          println("returning eval..")
          Evaluator.eval(board :+ holeCardTuple.get._1 :+ holeCardTuple.get._2).value
        } else {
          0
        }
      }).zipWithIndex.maxBy(_._1)._2
    })

    val sink = Sink.foreach[Int](println); // a sink that receives integers and prints each to the console
    val graph = source.via(flow).to(sink) // combine all components together in a static graph
    graph.run() // start the graph = "materialize" it
  }

  def getFilteredDeck(holeCards: List[Option[(Card, Card)]]): List[Card] = {
    val holeCardsList = holeCards.filter(_.isDefined).flatMap((cardTuple) => Seq(cardTuple.get._1, cardTuple.get._2))
    getDeck.filter(card => !holeCardsList.contains(card))
  }


}
