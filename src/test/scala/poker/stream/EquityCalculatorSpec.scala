package poker.stream

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import poker.model.Card

class EquityCalculatorSpec extends AnyWordSpec {

  val equityCalculator = EquityCalculator();
  val hands = List(Some((Card('A', '♥'), Card('A', '♠'))),
    None,
    Some((Card('A', '♦'), Card('A', '♣'))))

  "Given hands with Aces" should {
    "return deck without Aces" in {
      val filteredDeck = equityCalculator.getFilteredDeck(hands)
      filteredDeck.contains(Card('A', '♥')) should be(false)
      filteredDeck.contains(Card('A', '♠')) should be(false)
      filteredDeck.contains(Card('A', '♦')) should be(false)
      filteredDeck.contains(Card('A', '♣')) should be(false)
    }
  }

  "Given hands with Aces" should {
    "return flop equity" in {
      val equityCalculator = EquityCalculator()
      equityCalculator.calculateFlopEquity(hands)
    }
  }
}

