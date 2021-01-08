package poker.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._


class CardSpec extends AnyWordSpec:

  "Given the card A♠" should {
    val card = Card('A', '♠')
    "return correct string representation" in {
      card.toString shouldBe("[A♠]")
    }
    "return letter notation" in {
      card.toLetterNotation shouldBe("As")
    }
  }

  "Given the card A♥" should {
    val card = Card('A', '♥')
    "return letter notation" in {
      card.toLetterNotation shouldBe("Ah")
    }
  }

  "Given the card A♦" should {
    val card = Card('A', '♦')
    "return letter notation" in {
      card.toLetterNotation shouldBe("Ad")
    }
  }

  "Given the card A♣" should {
    val card = Card('A', '♣')
    "return letter notation" in {
      card.toLetterNotation shouldBe("Ac")
    }
  }