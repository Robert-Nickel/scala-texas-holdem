package poker.model

import main.scala.poker.model.{Card, Player}
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.immutable.HashMap
import scala.util.Failure

class PlayerSpec extends AnyWordSpec with Matchers {

  val values = HashMap(
    ('2', Set(2)),
    ('3', Set(3)),
    ('4', Set(4)),
    ('5', Set(5)),
    ('6', Set(6)),
    ('7', Set(7)),
    ('8', Set(8)),
    ('9', Set(9)),
    ('T', Set(10)),
    ('J', Set(11)),
    ('Q', Set(12)),
    ('K', Set(13)),
    ('A', Set(1, 14))
  )

  "Given a Player with name 'You'" when {
    "hole cards are Ah, As and getHoleCardsAsString is called" should {
      val player = Player("You", 200, Some(Card('A', 'h'), Card('A', 's')))
      "return correct string representation" in {
        player.getHoleCardsString() should be("[Ah][As]")
      }
    }
  }

  "Given a Player with name 'Ali'" when {
    val player = Player("Ali", 200, Some(Card('A', 'h'), Card('A', 's')))
    "hole cards are Ah, As and getHoleCardsAsString is called" should {
      "return correct string representation" in {
        player.getHoleCardsString() should be("[xx][xx]")
      }
    }
    "holding hole cards, isInRound" should {
      "return true" in {
        player.isInRound should be(true)
      }
    }
    "fold" should {
      "return he is not in round" in {
        player.fold().isInRound should be(false)
      }
    }
  }

  "Given is in round and has a stack of 200" when {
    val player = Player("Bob", 200, Some(Card('A', 'h'), Card('A', 's')))
    "calls 50" should {
      "return player with stack = 150 and currentBet = 50" in {
        val newPlayer = player.call(50)
        newPlayer.stack should be(150)
        newPlayer.currentBet should be(50)
      }
    }
    "calls where the highest overall bet is 300" should {
      "return player with stack = 0 and currentBet = 200" in {
        val newPlayer = player.call(300)
        newPlayer.stack should be(0)
        newPlayer.currentBet should be(200)
      }
    }
    "raises 50 where highest overall bet is 20" should {
      "return player with stack = 150 and currentBet = 50" in {
        val newPlayer = player.raise(50, 20).get
        newPlayer.stack should be(150)
        newPlayer.currentBet should be(50)
      }
    }
    "raises 200 where highest overall bet is 150" should {
      "return player with stack = 0 and currentBet = 200" in {
        val newPlayer = player.raise(200, 150).get
        newPlayer.stack should be(0)
        newPlayer.currentBet should be(200)
      }
    }
    "raises 100 where highest overall bet is 80" should {
      "fail, because the raise is not high enough" in {
        val newPlayer = player.raise(100, 80)
        newPlayer shouldBe a[Failure[_]]
      }
    }
    "safeRaises with 100 where highest overall bet is 50" should {
      "return player with stack = 100 and currentBet = 100" in {
        val newPlayer = player.safeRaise(100, 50)
        newPlayer.stack should be(100)
        newPlayer.currentBet should be(100)
      }
    }
    "safeRaises with 100 where highest overall bet is 60" should {
      "return a player with stack = 80 and currentBet is 120" in {
        val newPlayer = player.safeRaise(100, 60)
        newPlayer.stack should be(80)
        newPlayer.currentBet should be(120)
      }
    }
    "act with aces" should {
      "go all-in" in {
        val newPlayer = player.act(123, 2, values)
        newPlayer.stack should be(0)
        newPlayer.currentBet should be(200)
      }
    }
  }

  "Given a bot player with 9Qo with a hand value above 20" when {
    val player = Player("Bob", 200, Some(Card('Q', 'h'), Card('9', 's')))
    "act" should {
      "raise 3 BB" in {
        player.act(0, 2, values).currentBet should be(6)
      }
    }
  }

  "Given a bot player with 37o and a highest overall bet of 2 BB" when {
    val player = Player("Bob", 200, Some(Card('3', 'h'), Card('7', 's')))
    "act" should {
      "call" in {
        player.act(4, 2, values).currentBet should be(4)
      }
    }
  }

  "Given a bot player with less than 10 BB left" when {
    val player = Player("Bob", 16, Some(Card('3', 'h'), Card('7', 's')))
    "act" should {
      "all-in" in {
        player.act(4, 2, values).currentBet should be(16)
      }
    }
  }

  "Given a bot player with less than 10 BB left" when {
    val player = Player("Bob", 16, Some(Card('3', 'h'), Card('7', 's')))
    "act with 37o is called" should {
      "all-in" in {
        player.act(4, 2, values).currentBet should be(16)
      }
    }
  }

  "Given a bot player with 46o and a stack of 200 " when {
    val player = Player("Bob", 200, Some(Card('4', 'h'), Card('6', 's')))
    "act is called" should {
      "fold" in {
        val newPlayer = player.act(20, 2, values)
        newPlayer.currentBet should be(0)
        newPlayer.stack should be(200)
      }
    }
  }

  "Given a bot player" when {
    "hand value is calculated" should {
      "return 36" in {
        Player("Jim", 200, holeCards = Some(Card('A', 'h'), Card('A', 's')))
          .getHandValue(values) should be(36)
      }
      "return 39" in {
        Player("Jim", 200, holeCards = Some(Card('A', 'h'), Card('K', 'h')))
          .getHandValue(values) should be(39)
      }
      "return 30" in {
        Player("Jim", 200, holeCards = Some(Card('A', 'h'), Card('Q', 's')))
          .getHandValue(values) should be(30)
      }
      "return 0 when player has no cards" in {
        Player("Jim", 200, holeCards = None)
          .getHandValue(values) should be(0)
      }
    }
  }
}