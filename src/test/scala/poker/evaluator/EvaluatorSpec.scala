package poker.evaluator

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import poker.model.Card

class EvaluatorSpec extends AnyWordSpec {
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
      Card('A', '♠'),
      Card('K', '♠'),
      Card('Q', '♠'),
      Card('J', '♠'),
      Card('T', '♠'),
      Card('3', '♦'),
      Card('5', '♥')
    );
    "evaluate" in {
      Evaluator.eval(straightFlush).value shouldBe (36_874)
      Evaluator.eval(straightFlush).handType shouldBe (9)
      Evaluator.eval(straightFlush).handRank shouldBe (10)
      Evaluator.eval(straightFlush).handName shouldBe ("straight flush")
    }
  }

  "Given Ah Kh and Board: As 3d 4c" should {
    val handAndBoard = List(
      Card('A', '♥'),
      Card('K', '♥'),
      Card('A', '♠'),
      Card('3', '♦'),
      Card('4', '♣')
    )
  }

  "Given a heart straight flush " should {
    val straightFlush = List(
      Card('9', '♥'),
      Card('K', '♥'),
      Card('Q', '♥'),
      Card('J', '♥'),
      Card('T', '♥'),
      Card('3', '♥'),
      Card('5', '♥')
    );
    "evaluate" in {
      Evaluator.eval(straightFlush).value shouldBe (36_873)
      Evaluator.eval(straightFlush).handType shouldBe (9)
      Evaluator.eval(straightFlush).handRank shouldBe (9)
      Evaluator.eval(straightFlush).handName shouldBe ("straight flush")
    }
  }

  "Given four of a kind " should {
    val quads = List(
      Card('K', '♥'),
      Card('K', '♦'),
      Card('K', '♣'),
      Card('K', '♠'),
      Card('T', '♥'),
      Card('3', '♥'),
      Card('5', '♥')
    );
    "evaluate" in {
      Evaluator.eval(quads).value shouldBe (32_909)
      Evaluator.eval(quads).handType shouldBe (8)
      Evaluator.eval(quads).handRank shouldBe (141)
      Evaluator.eval(quads).handName shouldBe ("four of a kind")
    }
  }

  "Given a full house " should {
    val fullhouse = List(
      Card('K', '♥'),
      Card('K', '♦'),
      Card('K', '♣'),
      Card('A', '♠'),
      Card('A', '♥'),
      Card('3', '♥'),
      Card('5', '♥')
    );
    "evaluate" in {
      Evaluator.eval(fullhouse).value shouldBe (28_816)
      Evaluator.eval(fullhouse).handType shouldBe (7)
      Evaluator.eval(fullhouse).handRank shouldBe (144)
      Evaluator.eval(fullhouse).handName shouldBe ("full house")
    }
  }

  "Given a flush " should {
    val flush = List(
      Card('3', '♥'),
      Card('4', '♥'),
      Card('5', '♥'),
      Card('A', '♥'),
      Card('J', '♥'),
      Card('3', '♠'),
      Card('4', '♠')
    )
    "evaluate" in {
      Evaluator.eval(flush).value shouldBe (25_489)
      Evaluator.eval(flush).handType shouldBe (6)
      Evaluator.eval(flush).handRank shouldBe (913)
      Evaluator.eval(flush).handName shouldBe ("flush")
    }
  }

  "Given a straight " should {
    val flush = List(
      Card('3', '♥'),
      Card('4', '♦'),
      Card('5', '♠'),
      Card('6', '♥'),
      Card('7', '♥'),
      Card('3', '♠'),
      Card('4', '♠')
    )
    "evaluate" in {
      Evaluator.eval(flush).value shouldBe (20_483)
      Evaluator.eval(flush).handType shouldBe (5)
      Evaluator.eval(flush).handRank shouldBe (3)
      Evaluator.eval(flush).handName shouldBe ("straight")
    }
  }

  "Given three of a kind" should {
    val set = List(
      Card('3', '♥'),
      Card('3', '♦'),
      Card('3', '♠'),
      Card('6', '♥'),
      Card('8', '♥'),
      Card('T', '♠'),
      Card('A', '♠')
    )
    "evaluate" in {
      Evaluator.eval(set).value shouldBe (16_513)
      Evaluator.eval(set).handType shouldBe (4)
      Evaluator.eval(set).handRank shouldBe (129)
      Evaluator.eval(set).handName shouldBe ("three of a kind")
    }
  }

  "Given two pair" should {
    val twoPair = List(
      Card('3', '♥'),
      Card('3', '♦'),
      Card('K', '♠'),
      Card('K', '♥'),
      Card('8', '♥'),
      Card('T', '♠'),
      Card('A', '♠')
    )
    "evaluate" in {
      Evaluator.eval(twoPair).value shouldBe (12_915)
      Evaluator.eval(twoPair).handType shouldBe (3)
      Evaluator.eval(twoPair).handRank shouldBe (627)
      Evaluator.eval(twoPair).handName shouldBe ("two pairs")
    }
  }

  "Given a pair" should {
    val pair = List(
      Card('3', '♥'),
      Card('3', '♦'),
      Card('7', '♠'),
      Card('K', '♥'),
      Card('8', '♥'),
      Card('T', '♠'),
      Card('A', '♠')
    )
    "evaluate" in {
      Evaluator.eval(pair).value shouldBe (8_630)
      Evaluator.eval(pair).handType shouldBe (2)
      Evaluator.eval(pair).handRank shouldBe (438)
      Evaluator.eval(pair).handName shouldBe ("one pair")
    }
  }

  "Given high card" should {
    val highCard = List(
      Card('3', '♥'),
      Card('4', '♦'),
      Card('7', '♠'),
      Card('K', '♥'),
      Card('8', '♥'),
      Card('T', '♠'),
      Card('A', '♠')
    )
    "evaluate" in {
      Evaluator.eval(highCard).value shouldBe (5_286)
      Evaluator.eval(highCard).handType shouldBe (1)
      Evaluator.eval(highCard).handRank shouldBe (1_190)
      Evaluator.eval(highCard).handName shouldBe ("high card")
    }
  }

  "Given high card A with 5 cards" should {
    val highCard = List(
      Card('2', '♦'),
      Card('5', '♣'),
      Card('8', '♥'),
      Card('T', '♠'),
      Card('A', '♠')
    )
    "evaluate" in {
      Evaluator.eval(highCard).value shouldBe (4_973)
      Evaluator.eval(highCard).handRank shouldBe (877)
    }
  }

  "Given high card K with 5 cards" should {
    val highCard = List(
      Card('2', '♦'),
      Card('5', '♣'),
      Card('8', '♥'),
      Card('T', '♠'),
      Card('K', '♠')
    )
    "evaluate" in {
      Evaluator.eval(highCard).value shouldBe (4_645)
      Evaluator.eval(highCard).handName shouldBe ("high card")
    }
  }

  "Given a full house with 5 cards" should {
    val fullHouse = List(
      Card('K', '♦'),
      Card('K', '♣'),
      Card('A', '♥'),
      Card('A', '♠'),
      Card('K', '♠')
    )
    "evaluate" in {
      Evaluator.eval(fullHouse).value shouldBe (28_816)
    }
  }

  "Given a full house with 6 cards" should {
    val fullHouse = List(
      Card('K', '♦'),
      Card('K', '♣'),
      Card('A', '♥'),
      Card('A', '♠'),
      Card('K', '♠'),
      Card('3', '♠')
    )
    "evaluate" in {
      val evaluation = Evaluator.eval(fullHouse)
      evaluation.value shouldBe (28_816)
      evaluation.handName shouldBe ("full house")
      evaluation.handType shouldBe (7)
    }
  }
  "Given a full house with Queens And Aces" should {
    val fullHouse = List(
      Card('Q', '♦'),
      Card('Q', '♣'),
      Card('A', '♥'),
      Card('A', '♠'),
      Card('Q', '♠'),
      Card('3', '♠')
    )
    "evaluate Queens of Aces" in {
      val evaluation = Evaluator.eval(fullHouse)
      evaluation.handType shouldBe (7)
    }
  }
  "Given a high card Q" should {
    val highCard = List(
      Card('Q', '♦'),
      Card('J', '♣'),
      Card('2', '♥'),
      Card('3', '♠'),
      Card('5', '♠'),
      Card('7', '♠')
    )
    "evaluate Queens of Aces" in {
      val evaluation = Evaluator.eval(highCard)
      evaluation.handType shouldBe (1)
    }
  }

  "Given a pair" should {
    val pair = List(
      Card('K', '♦'),
      Card('K', '♣'),
      Card('2', '♥'),
      Card('3', '♠'),
      Card('5', '♠'),
      Card('7', '♠')
    )
    "evaluate Queens of Aces" in {
      val evaluation = Evaluator.eval(pair)
      evaluation.handName shouldBe ("one pair")
    }
  }

  "Given int representations of cards" should {
    val intCards = List(40, 47, 21, 13, 23, 44, 1)
    "be 9457 after evalCards" in {
      var p = 53
      intCards.foreach(card => p = Evaluator.evalCard(p + card))
      p shouldBe (9457)
    }
  }

  "Given int card 25 - [8♣]" should {
    "evalCard" in {
      val p = 53
      Evaluator.evalCard(p + 25) shouldBe (1378)
    }
    "evalCard next int card 34 - [T♦]" in {
      val p = 1378
      Evaluator.evalCard(p + 34) shouldBe (53530)
    }
    "evalCard next int card 20 - [6♠]" in {
      val p = 53530
      Evaluator.evalCard(p + 20) shouldBe (961950)
    }
    "evalCard next int card 47 - [K♥]" in {
      val p = 961950
      Evaluator.evalCard(p + 47) shouldBe (4806782)
    }
    "evalCard next int card 30 - [9♦]" in {
      val p = 4806782
      Evaluator.evalCard(p + 30) shouldBe (12819958)
    }
    "evalCard next int card 8 - [3♠]" in {
      val p = 12819958
      Evaluator.evalCard(p + 8) shouldBe (22802402)
    }
    "evalCard next int card 15 - [5♥]" in {
      val p = 22802402
      Evaluator.evalCard(p + 15) shouldBe (4676)
    }
  }
}
