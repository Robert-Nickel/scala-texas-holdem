package poker.stream

import java.util.Properties

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import poker.evaluator.Evaluator
import poker.getDeck
import poker.kafka.Producer.{KEY, TOPIC, producer, props}
import poker.model.Card

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt
import scala.util.Random

object EquityCalculator {
  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  val TOPIC = "equity"
  val KEY = "calculated"

  def calculatePreflopEquity(holeCards: List[Option[(Card, Card)]]) = {
    calculateEquity(holeCards)
  }

  def calculatePostflopEquity(holeCards: List[Option[(Card, Card)]], board: List[Card]) = {
    calculateEquity(holeCards, board)
  }

  def calculateEquity(holeCards: List[Option[(Card, Card)]], board: List[Card] = List()) = {
    val producer = new KafkaProducer[String, String](props)
    val result = Await.result(countWins(holeCards, board), 10 seconds)
    val equity = result.map(winCount => BigDecimal(winCount.toFloat / result.sum * 100).setScale(1, BigDecimal.RoundingMode.HALF_UP).toDouble)
    producer.send(new ProducerRecord(TOPIC, KEY, equity.toString))
    Thread.sleep(1000)
    producer.close()
    equity
  }

  def countWins(holeCards: List[Option[(Card, Card)]], board: List[Card] = List()): Future[List[Int]] = {
    val holeCardsList = holeCards
      .filter(_.isDefined)
      .flatMap(cardTuple => Seq(cardTuple.get._1, cardTuple.get._2))

    implicit val system = ActorSystem()
    val evaluator = Evaluator // "thread pool"

    val evalFlow = Flow[List[Card]].map(board => {
      holeCards.map(cardTuple => {
        if (cardTuple.isDefined) {
          val result = evaluator.eval(board :+ cardTuple.get._1 :+ cardTuple.get._2).value
          result
        } else {
          0
        }
      }).zipWithIndex.maxBy(_._1)._2
    })

    Source(randomCommunityCards(board, getFilteredDeck(holeCardsList ::: board)))
      .take(20000)
      .via(evalFlow)
      .runWith(Sink.fold(List(0, 0, 0, 0, 0, 0))((accList: List[Int], elementIdx: Int) => accList.updated(elementIdx, accList(elementIdx) + 1)))
  }

  def randomCommunityCards(board: List[Card], deck: List[Card]): Stream[List[Card]] = { // 5, 2 or 1
    val shuffledDeck = Random.shuffle(deck)

    val amount = 5 - board.size
    val result: List[Card] = amount match {
      case 5 => List(
        shuffledDeck.head,
        shuffledDeck.tail.head,
        shuffledDeck.tail.tail.head,
        shuffledDeck.tail.tail.tail.head,
        shuffledDeck.tail.tail.tail.tail.head
      )
      case 2 => List(
        shuffledDeck.head,
        shuffledDeck.tail.head) ::: board
      case 1 => List(shuffledDeck.head) ::: board
      case _ => board
    }
    result #:: randomCommunityCards(board, deck)
  }

  def getFilteredDeck(handAndBoard: List[Card]): List[Card] = {
    getDeck.filter(card => !handAndBoard.contains(card))
  }
}
