package poker.model

import scala.util.Failure
import main.scala.poker.model.{Card, Player}
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

import scala.util.Failure

class PlayerSpec extends AnyWordSpec with Matchers {

  "A Player" when {
    "given the name 'You' and hole cards Ah, As" should {
      val player = Player("You", 200, Some(Card('A', 'h'), Card('A', 's')))

      "return correct string representation for hole cards" in {
        player.getHoleCardsString() should be("[Ah][As]")
      }
    }
    "given any name except 'You' and hole cards Ah, As" should {
      val player = Player("Bob", 200, Some(Card('A', 'h'), Card('A', 's')))
      "return correct string representation for hole cards" in {
        player.getHoleCardsString() should be("[xx][xx]")
      }
    }
    "given hole cards" should {
      val player = Player("Bob", 200, Some(Card('A', 'h'), Card('A', 's')))
      "return he is in round" in {
        player.isInRound should be(true)
      }
    }
    "given hole cards and triggered fold action" should {
      val player = Player("Bob", 200, None).fold()
      "return he is not in round" in {
        player.isInRound should be(false)
      }
    }
    "given player is in round and has a stack of 200 and calls 50" should {
      val player = Player("Bill", 200, Some(Card('A', 'h'), Card('A', 's')))
      "return player with stack of 150" in {
        val newPlayer = player.call(50)
        newPlayer.stack should be(150)
      }
    }
    "given player is in round and has a stack of 50 and calls where the highest overall bet is 100" should {
      val player = Player("Bill", 50, Some(Card('A', 'h'), Card('A', 's')))
      "return player with stack of 0" in {
        val newPlayer = player.call(50)
        newPlayer.stack should be(0)
      }
    }
    "given player is in round and has a stack of 200, the highest overall bet is 20 and raises 50" should {
      val player = Player("Bill", 200, Some(Card('A', 'h'), Card('A', 's')))
      "return player with stack of 150" in {
        val newPlayer = player.raise(50, 20)
        newPlayer.get.stack should be(150)
      }
    }
    "given player is in round and has a stack of 120, the highest overall bet is 80 and raises 120" should {
      val player = Player("Bill", 120, Some(Card('A', 'h'), Card('A', 's')), 20)
      "return player with stack of 0" in {
        val newPlayer = player.raise(120, 80)
        newPlayer.get.stack should be(0)
      }
    }
    "given player is in round and has a stack of 120, the highest overall bet is 80 and raises 100" should {
      val player = Player("Bill", 120, Some(Card('A', 'h'), Card('A', 's')), 20)
      "return player with stack of 0" in {
        val newPlayer = player.raise(100, 80)
        newPlayer shouldBe a[Failure[_]]
      }
    }
  }
}