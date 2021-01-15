package poker.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._


class CardSpec extends AnyWordSpec:

  "Given the card A♠" should {
    val card = Card('A', '♠')
    "return correct string representation" in {
      card.toString shouldBe("[A♠]")
    }
  }