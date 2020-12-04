package poker.model

import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.must.Matchers.between
import org.scalatest.matchers.should.Matchers.{between, _}
import org.scalatest.wordspec.AnyWordSpec
import poker._

import scala.language.postfixOps
import scala.util.Failure

class PlayerSpec extends AnyWordSpec with Matchers with BeforeAndAfterAll {


  "Given a Player with name 'You', Stack is 200 and cards are Ah As" when {
    val player = (Player("You") are 200 deep) haveCards "Ah As"
    "hole cards are Ah, As and getHoleCardsAsString is called" should {
      "return [Ah][As]" in {
        player.getHoleCardsString() should be("[Ah][As]")
      }
    }
    "holding hole cards" should {
      "return areInRound = true" in {
        player.areInRound should be(true)
      }
      "return isInGame = true" in {
        player.isInGame should be(true)
      }
    }
    "is human player is called" in {
      player.isHumanPlayer should be(true)
    }
  }
  "Given a Player with name 'You', and no hole cards" when {
    val you = Player("You")
    "areInRound is called" should {
      "return areInRound = false" in {
        you.areInRound should be(false)
      }
    }
  }

  "Given a Player with name 'You', Stack is 0 and CurrentBet is 0" when {
    val you = (Player("You") are 0 deep)
    "areInGame is called" should {
      "return areInGame = false" in {
        you.areInGame should be(false)
      }
    }
    "raise is called" should {
      "return a failure" in {
        you.raise(50, 0) shouldBe a[Failure[_]]
      }
    }
  }

  "Given a Player with name 'Ali'" when {
    val ali = (Player("Ali") is 200 deep) hasCards "Ah As"
    "hole cards are Ah, As and getHoleCardsAsString is called" should {
      "return [xx][xx]" in {
        ali.getHoleCardsString() should be("[xx][xx]")
      }
    }
    "holding hole cards" should {
      "return isInRound = true" in {
        ali.isInRound should be(true)
      }
    }
    "fold" should {
      "return isInRound = false" in {
        ali.fold().isInRound should be(false)
      }
    }
    "is human player is called" in {
      ali.isHumanPlayer should be(false)
    }
  }

  "Given a Player with name 'Ali' and no hole cards" when {
    val ali = (Player("Ali") is 200 deep)
    "getHoleCardsAsString is called" should {
      "return whitespaces" in {
        ali.getHoleCardsString() should be("        ")
      }
    }
  }

  "Given a Player with name 'Ali' with aces" when {
    val ali = (Player("Ali") is 200 deep).hasCards("Ah As")
    "actAsBot is called" should {
      "call, if a 5BB raise is smaller than double the highestOverallBet" in {
        ali.actAsBot(8).currentBet should be(8)
      }
    }
  }

  "Given a Player with flopped quads" when {
    val player = (Player("Timon") is 200 deep).hasCards("A♥ A♠")
    val board = List(Card('A', '♣'), Card('A', '♦'), Card('2', '♦'))
    "getFlopValue is called" should {
      "return 32_920" in {
        player.getPostFlopValue(board) should be(32_920)
      }
    }
    "actAsBot is called" should {
      "shove all-in" in {
        player.actAsBot(20, board).currentBet should be(200)
      }
    }
  }

  "Given a Player with name 'Ali' with Q9" when {
    val ali = (Player("Ali") is 200 deep).hasCards("Qh 9s")
    "actAsBot is called" should {
      "call, if a 3BB raise is smaller than double the highestOverallBet" in {
        ali.actAsBot(4).currentBet should be(4)
      }
    }
  }

  "Given is in round and has a stack of 200" when {
    val bob = (Player("Bob") is 200 deep) hasCards "Ah As"
    "calls 50" should {
      "return bob with stack = 150 and currentBet = 50" in {
        val newPlayer = bob.call(50)
        newPlayer.stack should be(150)
        newPlayer.currentBet should be(50)
      }
    }
    "calls where the highest overall bet is 300" should {
      "return bob with stack = 0 and currentBet = 200" in {
        val newBob = bob.call(300)
        newBob.stack should be(0)
        newBob.currentBet should be(200)
      }
    }
    "raises 50 where highest overall bet is 20" should {
      "return bob with stack = 150 and currentBet = 50" in {
        val newBob = bob.raise(50, 20).get
        newBob.stack should be(150)
        newBob.currentBet should be(50)
      }
    }
    "raises 200 where highest overall bet is 150" should {
      "return bob with stack = 0 and currentBet = 200" in {
        val newBob = bob.raise(200, 150).get
        newBob.stack should be(0)
        newBob.currentBet should be(200)
      }
    }
    "raises 100 where highest overall bet is 80" should {
      "fail, because the raise is not high enough" in {
        val newBob = bob.raise(100, 80)
        newBob shouldBe a[Failure[_]]
      }
    }
    "shoves all-in with 200" should {
      "return bob with stack = 0 and currentBet = 200" in {
        val newBob = bob shoves()
        newBob.stack should be(0)
        newBob.currentBet should be(200)
      }
    }

    "act as bot with aces" should {
      "raise 5 bb" in {
        val newBob = bob.actAsBot(4)
        newBob.currentBet should be(10)
      }
    }

    "player posts sb" should {
      "have reduced stack" in {
        val newBob = bob.posts(sb).get
        newBob.stack should be(199)
      }
    }

    "you post bb" should {
      "have reduced stack" in {
        val you = (Player("You") are 200 deep) haveCards "Ah As"
        val newYou = you.post(bb).get
        newYou.stack should be(198)
      }
    }
  }

  "Given 9Qo resulting in a hand value above 20" when {
    val bob = (Player("Bob") is 200 deep) hasCards "Qh 9s"
    "act as bot" should {
      "raise 3 BB" in {
        bob.actAsBot(0).currentBet should be(6)
      }
    }
  }

  "Given 37o and a highest overall bet of 2 BB" when {
    val bob = (Player("Bob") is 200 deep) hasCards "3h 7s"
    "act as bot" should {
      "call" in {
        bob.actAsBot(4).currentBet should be(4)
      }
    }
  }

  "Given less than 10 BB left" when {
    val bob = (Player("Bob") is 16 deep) hasCards "3h 7s"
    "act as bot" should {
      "all-in" in {
        bob.actAsBot(4).currentBet should be(16)
      }
    }
  }

  "Given less than 10 BB left" when {
    val bob = (Player("Bob") is 16 deep) hasCards "3h 7s"
    "act as bot with 37o is called" should {
      "all-in" in {
        bob.actAsBot(4).currentBet should be(16)
      }
    }
  }

  "Given 46o and a stack of 200 " when {
    val bob = (Player("Bob") is 200 deep) hasCards "4h 6s"
    "act as bot is called" should {
      "fold" in {
        val newBob = bob.actAsBot(20)
        newBob.currentBet should be(0)
        newBob.stack should be(200)
      }
    }
  }

  "Given players with different hole cards" when {
    "hand value is calculated" should {
      "return 36" in {
        (Player("Jim") hasCards "Ah As").getHoleCardsValue() should be(36)
      }
      "return 39" in {
        (Player("Jim") hasCards "Ah Kh").getHoleCardsValue() should be(39)
      }
      "return 30" in {
        (Player("Jim") hasCards "Ah Qs").getHoleCardsValue() should be(30)
      }
      "return 0 when jim has no cards" in {
        Player("Jim").getHoleCardsValue() should be(0)
      }
    }
  }

  "Given a player (bot) with Ah Ks and flopped Top Pair" should {
    val bob = (Player("Bob") is 200 deep) hasCards "K♥ A♠"
    val board = List(Card('A', '♥'), Card('2', '♦'), Card('5', '♣'))
    "return post flop value" in {
      bob.getPostFlopValue(board) should be(12_815 +- 100)
    }
  }

  "Given a player (bot) with Ah Ks and turned Top Pair" should {
    val bob = (Player("Bob") is 200 deep) hasCards "K♥ A♠"
    val board = List(Card('8', '♥'), Card('2', '♦'), Card('5', '♣'), Card('A', '♣'))
    "return post flop value" in {
      bob.getPostFlopValue(board) should be(11_832)
    }
  }

  "Given a player (bot) with Ah Ks and rivered two pairs" should {
    val bob = (Player("Bob") is 200 deep) hasCards "K♥ A♠"
    val board = List(Card('8', '♥'), Card('2', '♦'), Card('5', '♣'), Card('A', '♣'), Card('2', '♣'))
    "return post flop value" in {
      bob.getPostFlopValue(board) should be(13_025)
    }
  }

  "Given a player (bot) with hand value 12001" should {
    val handValue = 12_001
    val highestOverallBet = 50
    val bob = (Player("Bob") is 200 deep)
    "raise 3 times the highestOverallBet" in {
      bob.actPostFlop(handValue, highestOverallBet).currentBet should be(3 * highestOverallBet)
    }
  }

  "Given a player (bot) with hand value 9001" should {
    val handValue = 9_001
    val highestOverallBet = 50
    val bob = (Player("Bob") is 200 deep)
    "call" in {
      bob.actPostFlop(handValue, highestOverallBet).currentBet should be(highestOverallBet)
    }
  }

  "Given a player (bot) with hand value 1000" should {
    val handValue = 1_000
    val highestOverallBet = 50
    val bob = (Player("Bob") is 200 deep)
    "fold" in {
      bob.actPostFlop(handValue, highestOverallBet).currentBet should be(0)
    }
  }

  "Given a player (bot) with hand value 12001" should {
    val handValue = 12_001
    val highestOverallBet = 50
    val bob = (Player("Bob") is 0 deep)
    "fold if invalid raise" in {
      bob.actPostFlop(handValue, highestOverallBet).isInRound should be(false)
    }
  }
}