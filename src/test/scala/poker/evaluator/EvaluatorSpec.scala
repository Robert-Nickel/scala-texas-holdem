package poker.evaluator

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import poker.model.Card

class EvaluatorSpec extends AnyWordSpec{
  val handTypes = List(
    "invalid hand",
    "high card",
    "one pair",
    "two pairs",
    "three of a kind",
    "straight",
    "flush",
    "full house",
    "four of a kind",
    "straight flush"
  )

  "Given a straight flush " should {
    val straightFlush = List(
      Card('A', '♠'), Card('K','♠'), Card('Q','♠'), Card('J', '♠'), Card('T', '♠'), Card('3', '♦'), Card('5', '♥'));
    "evaluate" in {
      Evaluator.eval(straightFlush).value should be(36_874)
      Evaluator.eval(straightFlush).handType should be(9)
      Evaluator.eval(straightFlush).handRank should be(10)
      Evaluator.eval(straightFlush).handName should be("straight flush")
    }
  }

  "Given a heart straight flush " should {
    val straightFlush = List(
      Card('9', '♥'), Card('K','♥'), Card('Q','♥'), Card('J', '♥'), Card('T', '♥'), Card('3', '♥'), Card('5', '♥'));
    "evaluate" in {
      Evaluator.eval(straightFlush).value should be(36_873)
      Evaluator.eval(straightFlush).handType should be(9)
      Evaluator.eval(straightFlush).handRank should be(9)
      Evaluator.eval(straightFlush).handName should be("straight flush")
    }
  }

  "Given four of a kind " should {
    val quads = List(
      Card('K', '♥'), Card('K','♦'), Card('K','♣'), Card('K', '♠'), Card('T', '♥'), Card('3', '♥'), Card('5', '♥'));
    "evaluate" in {
      Evaluator.eval(quads).value should be(32_909)
      Evaluator.eval(quads).handType should be(8)
      Evaluator.eval(quads).handRank should be(141)
      Evaluator.eval(quads).handName should be("four of a kind")
    }
  }

  "Given a full house " should {
    val fullhouse = List(
      Card('K', '♥'), Card('K','♦'), Card('K','♣'), Card('A', '♠'), Card('A', '♥'), Card('3', '♥'), Card('5', '♥'));
    "evaluate" in {
      Evaluator.eval(fullhouse).value should be(28_816)
      Evaluator.eval(fullhouse).handType should be(7)
      Evaluator.eval(fullhouse).handRank should be(144)
      Evaluator.eval(fullhouse).handName should be("full house")
    }
  }

  "Given a flush " should {
    val flush = List(
      Card('3', '♥'), Card('4', '♥'), Card('5', '♥'), Card('A', '♥'), Card('J', '♥'), Card('3', '♠'), Card('4', '♠'))
    "evaluate" in {
      Evaluator.eval(flush).value should be(25_489)
      Evaluator.eval(flush).handType should be(6)
      Evaluator.eval(flush).handRank should be(913)
      Evaluator.eval(flush).handName should be("flush")
    }
  }

  "Given a straight " should {
    val flush = List(
      Card('3', '♥'), Card('4', '♦'), Card('5', '♠'), Card('6', '♥'), Card('7', '♥'), Card('3', '♠'), Card('4', '♠'))
    "evaluate" in {
      Evaluator.eval(flush).value should be(20_483)
      Evaluator.eval(flush).handType should be(5)
      Evaluator.eval(flush).handRank should be(3)
      Evaluator.eval(flush).handName should be("straight")
    }
  }

  "Given three of a kind" should {
    val set = List(
      Card('3', '♥'), Card('3', '♦'), Card('3', '♠'), Card('6', '♥'), Card('8', '♥'), Card('T', '♠'), Card('A', '♠'))
    "evaluate" in {
      Evaluator.eval(set).value should be(16_513)
      Evaluator.eval(set).handType should be(4)
      Evaluator.eval(set).handRank should be(129)
      Evaluator.eval(set).handName should be("three of a kind")
    }
  }


  "Given two pair" should {
    val twoPair = List(
      Card('3', '♥'), Card('3', '♦'), Card('K', '♠'), Card('K', '♥'), Card('8', '♥'), Card('T', '♠'), Card('A', '♠'))
    "evaluate" in {
      Evaluator.eval(twoPair).value should be(12_915)
      Evaluator.eval(twoPair).handType should be(3)
      Evaluator.eval(twoPair).handRank should be(627)
      Evaluator.eval(twoPair).handName should be("two pairs")
    }
  }

  "Given a pair" should {
    val pair = List(
      Card('3', '♥'), Card('3', '♦'), Card('7', '♠'), Card('K', '♥'), Card('8', '♥'), Card('T', '♠'), Card('A', '♠'))
    "evaluate" in {
      Evaluator.eval(pair).value should be(8_630)
      Evaluator.eval(pair).handType should be(2)
      Evaluator.eval(pair).handRank should be(438)
      Evaluator.eval(pair).handName should be("one pair")
    }
  }

  "Given high card" should {
    val highCard = List(
      Card('3', '♥'), Card('4', '♦'), Card('7', '♠'), Card('K', '♥'), Card('8', '♥'), Card('T', '♠'), Card('A', '♠'))
    "evaluate" in {
      Evaluator.eval(highCard).value should be(5_286)
      Evaluator.eval(highCard).handType should be(1)
      Evaluator.eval(highCard).handRank should be(1_190)
      Evaluator.eval(highCard).handName should be("high card")
    }
  }

  "Given high card A with 5 cards" should {
    val highCard = List(
      Card('2', '♦'), Card('5', '♣'), Card('8', '♥'), Card('T', '♠'), Card('A', '♠'))
    "evaluate" in {
      Evaluator.eval(highCard).value should be(6_445_701)
      Evaluator.eval(highCard).handRank should be(2_693)
    }
  }

  "Given high card K with 5 cards" should {
    val highCard = List(
      Card('2', '♦'), Card('5', '♣'), Card('8', '♥'), Card('T', '♠'), Card('K', '♠'))
    "evaluate" in {
      Evaluator.eval(highCard).value should be(6_445_648)
      Evaluator.eval(highCard).handName should be("invalid hand")
    }
  }

  "Given a full house with 5 cards" should {
    val highCard = List(
      Card('K', '♦'), Card('K', '♣'), Card('A', '♥'), Card('A', '♠'), Card('K', '♠'))
    "evaluate" in {
      Evaluator.eval(highCard).value should be(13_808_196)
    }
  }

  "Given a full house with 6 cards" should {
    val highCard = List(
      Card('K', '♦'), Card('K', '♣'), Card('A', '♥'), Card('A', '♠'), Card('K', '♠'), Card('3', '♠'))
    "evaluate" in {
      Evaluator.eval(highCard).value should be(23_100_792)
      Evaluator.eval(highCard).handName should be("invalid hand")
    }
  }

}