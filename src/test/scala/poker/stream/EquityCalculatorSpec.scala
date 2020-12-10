package poker.stream

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import poker.model.Card
import poker.stream.EquityCalculator.{calculatePostflopEquity, calculatePreflopEquity, countWins, getFilteredDeck}

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class EquityCalculatorSpec extends AnyWordSpec {
  val flop = List(Card('K', '♥'), Card('Q', '♠'), Card('Q', '♥'))
  val turn: List[Card] = flop :+ Card('A', '♣')
  val river: List[Card] = turn :+ Card('A', '♦')
  val hands = List(
    Some((Card('A', '♥'), Card('A', '♠'))),
    None,
    Some((Card('K', '♦'), Card('Q', '♣'))),
    Some((Card('T', '♦'), Card('T', '♣'))),
    Some((Card('2', '♦'), Card('4', '♣'))),
    Some((Card('J', '♦'), Card('7', '♣'))))

  "Given hands with Aces" should {
    "return deck without Aces" in {
      val filteredDeck = getFilteredDeck(List(Card('A', '♥'), Card('A', '♠'), Card('A', '♦'), Card('A', '♣')))
      filteredDeck.contains(Card('A', '♥')) should be(false)
      filteredDeck.contains(Card('A', '♠')) should be(false)
      filteredDeck.contains(Card('A', '♦')) should be(false)
      filteredDeck.contains(Card('A', '♣')) should be(false)
    }
  }

  "Given hands for different players" should {
    "return the win count for each player" in {
      val eventualDone = countWins(hands)
      val tolerance = 1_000 // 1_000 appears to be safe, can be in- or decreased based on required precision
      val result = Await.result(eventualDone, 30 seconds)
      result(0) should be(10_680 +- tolerance)
      result(1) should be(0 +- tolerance)
      result(2) should be(2_164 +- tolerance)
      result(3) should be(3_255 +- tolerance)
      result(4) should be(2_366 +- tolerance)
      result(5) should be(2_182 +- tolerance)
    }

    "calculate preflop equity" in {
      val result = calculatePreflopEquity(hands)
      val tolerance = 1
      result(0) should be(50.2 +- tolerance)
      result(1) should be(0.0 +- tolerance)
      result(2) should be(11.0 +- tolerance)
      result(3) should be(16.1 +- tolerance)
      result(4) should be(11.8 +- tolerance)
      result(5) should be(10.9 +- tolerance)
      result.sum should be(100.0 +- tolerance)
    }

    "calculate flop equity" in {
      val result = calculatePostflopEquity(hands, flop)
      val tolerance = 1
      result(0) should be(10.1 +- tolerance)
      result(1) should be(0.0 +- tolerance)
      result(2) should be(90.0 +- tolerance)
      result(3) should be(0.1 +- tolerance)
      result(4) should be(0.0 +- tolerance)
      result(5) should be(0.0 +- tolerance)
      result.sum should be(100.0 +- tolerance)
    }

    "calculate turn equity" in {
      val result = calculatePostflopEquity(hands, turn)
      val tolerance = 1
      result(0) should be(97.4 +- tolerance)
      result(1) should be(0.0 +- tolerance)
      result(2) should be(2.6 +- tolerance)
      result(3) should be(0.0 +- tolerance)
      result(4) should be(0.0 +- tolerance)
      result(5) should be(0.0 +- tolerance)
      result.sum should be(100.0 +- tolerance)
    }

    "calculate river equity" in {
      val result = calculatePostflopEquity(hands, river)
      val tolerance = 1
      result(0) should be(100.0 +- tolerance)
      result(1) should be(0.0 +- tolerance)
      result(2) should be(0.0 +- tolerance)
      result(3) should be(0.0 +- tolerance)
      result(4) should be(0.0 +- tolerance)
      result(5) should be(0.0 +- tolerance)
      result.sum should be(100.0 +- tolerance)
    }
  }
}

