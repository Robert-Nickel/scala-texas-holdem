package poker.model

import main.scala.poker.model.{Card, Player}
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class PlayerSpec extends AnyWordSpec with Matchers {

  "A Player" when {
    "given the name 'You' and hole cards Ah, As" should {
      val player = Player("You", 200, (Option.apply(Card('A', 'h')), Option.apply(Card('A', 's'))))
      "return correct string representation for hole cards" in {
        player.getHoleCardsString() should be("[Ah][As]")
      }
    }
    "given any name except 'You' and hole cards Ah, As" should {
      val player = Player("Bob", 200, (Option.apply(Card('A', 'h')), Option.apply(Card('A', 's'))))
      "return correct string representation for hole cards" in {
        player.getHoleCardsString() should be("[xx][xx]")
      }
    }
    "given name is Bob and stack is 200 and hole cards are Ah, As" should {
      val player = Player("Bob", 200, (Option.apply(Card('A', 'h')), Option.apply(Card('A', 's'))))
      "return correct string representation" in {
        player.toString() should be("Player: Bob, Stack: 200, Cards: Ah As")
      }
    }
  }
}