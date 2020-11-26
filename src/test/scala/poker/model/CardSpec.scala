package poker.model

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class CardSpec extends AnyWordSpec {
  
  "A Card" when {
    "given value is A and symbol is s" should {
      val card = Card('A', 's')
      "return correct string representation" in {
        card.toString() should be("[As]")
      }
    }
  }
}
