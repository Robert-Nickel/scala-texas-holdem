package poker

import main.scala.poker.Dealer
import main.scala.poker.model.{Card, Player, Table}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.Failure


class DealerSpec extends AnyWordSpec with Matchers {
  "Dealer" when {
    val deck = List(Card('T', 'h'), Card('T', 's'), Card('A', 'h'), Card('A', 's'), Card('Q', 'h'), Card('Q', 's'))
    val players = List(Player("Bob", 200, None), Player("Ali", 200, None))

    "given a list of players with no hole cards and a deck" should {
      "return a list of players with different hole cards and a reduced deck" in {
        val (newPlayers, newDeck) = Dealer.handOutCards(players, deck).get
        newPlayers should be(List(Player("Bob", 200, Some(Card('T', 'h'), Card('T', 's'))),
          Player("Ali", 200, Some(Card('A', 'h'), Card('A', 's'))))
        )
        newDeck should be(List(Card('Q', 'h'), Card('Q', 's')))
      }
    }
    "given an empty list of players and a deck" should {
      "return an empty list of players and a unchanged deck" in {
        val (newPlayers, newDeck) = Dealer.handOutCards(List(), deck).get
        newPlayers shouldBe empty
        newDeck should be(deck)
      }
    }
    "given an list of players and a single card as deck" should {
      val deck = List(Card('A', 's'))
      "return a list of players n empty list of players and a unchanged deck" in {
        val tryMonad = Dealer.handOutCards(players, deck)
        tryMonad shouldBe a[Failure[_]]
      }
    }
    "given an list of players and a deck" should {
      "play next round" in {
        Dealer.shouldPlayNextRound(Table(players, deck)) should be(true)
      }
    }
    "given an list of players where both players are all in" should {
      "play next round" in {
        val players = List(Player("Ali", currentBet = 200), Player("Bob", currentBet = 100))
        Dealer.shouldPlayNextRound(Table(players, deck)) should be(true)
      }
    }
    "given an list of players where only one player has chips left" should {
      "play next round" in {
        val players = List(Player("Ali", stack = 1200), Player("Bob"))
        Dealer.shouldPlayNextRound(Table(players, deck)) should be(false)
      }
    }

    "given a table at showdown" should {
      "not play next betting round" in {
        val table = Table(List(Player("Ali"), Player("Bob")), currentBettingRound = 4)
        Dealer.shouldPlayNextBettingRound(table) should be(false)
      }
    }

    "given a table where only one player remains in the round" should {
      "not play next betting round" in {
        val table = Table(List(Player("Ali").hasCards("Ah As"), Player("Bob")))
        Dealer.shouldPlayNextBettingRound(table) should be(false)
      }
    }

    "given a table where multiple players remain in the round after the flop" should {
      "play next betting round" in {
        val players = List(
          Player("Ali").hasCards("Ah As"),
          Player("Bob").hasCards("Ac Ad"))
        val table = Table(players, currentBettingRound = 1)
        Dealer.shouldPlayNextBettingRound(table) should be(true)
      }
    }

    "given a table where players have different current bets" should {
      "play next move" in {
        val players = List(
          Player("Ali", currentBet = 10).hasCards("Ah As"),
          Player("Bob", currentBet = 20).hasCards("Ac Ad"))
        Dealer.shouldPlayNextMove(Table(players, deck)) should be(true)
      }
    }

    "given a table where players have equal current bets" should {
      "not play next move" in {
        val players = List(
          Player("Ali", currentBet = 20).hasCards("Ah As"),
          Player("Bob", currentBet = 20).hasCards("Ac Ad"))
        Dealer.shouldPlayNextMove(Table(players, deck)) should be(false)
      }
    }

    "given a table where all players have either the equal current bets or have folded" should {
      "not play next move" in {
        val players = List(
          Player("Ali", currentBet = 20).hasCards("Ah As"),
          Player("Bob", currentBet = 20).hasCards("Ac Ad"),
          Player("Charles", currentBet = 10))
        Dealer.shouldPlayNextMove(Table(players, deck)) should be(false)
      }
    }
  }
}
