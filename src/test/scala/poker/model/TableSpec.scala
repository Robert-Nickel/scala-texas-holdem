package poker.model

import main.scala.poker.model.{Card, Player, Table}
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class TableSpec extends AnyWordSpec with Matchers {
  val players = List(
    Player("Bob", 200, Some(Card('A', 'h'), Card('A', 's'))),
    Player("Jon", 200, Some(Card('K', 'h'), Card('K', 's'))))

  "A Table" when {
    "given players and a deck and no current player" should {
      val table = Table(players)
      "take the default of 0 and return a table with an updated current player" in {
        table.nextPlayer() should be(table.copy(currentPlayer = 1))
      }
    }
    "given players and a deck and currentPlayer is 1" should {
      val table = Table(players, currentPlayer = 1)
      "take the default of 0 and return a table with an updated current player" in {
        table.nextPlayer() should be(table.copy(currentPlayer = 0))
      }
    }
    "given players" should {
      val table = Table(players)
      "fold for the current player in" in {
        val newTable = table.currentPlayerFold()
        val currentPlayer = newTable.currentPlayer
        val playerWithoutHoleCards = Seq(newTable.players(currentPlayer).copy(holeCards = None))
        newTable should be(table.copy(players.patch(currentPlayer, playerWithoutHoleCards, 1)))
      }
    }
    "given currentPlayer is not in the round" should {
      val table = Table(List(
        Player("Bob", 200, None),
        Player("Ali", 200, Some(Card('A', 'h'), Card('A', 's')))))
      "skip the current player and fold for the next player" in {
        val newTable = table.currentPlayerFold()
        newTable.players.exists(p => p.isInRound) should be(false)
      }
    }
    "given no players have cards anymore" should {
      val table = Table(List(
        Player("Bob", 200, None),
        Player("Ali", 200, None)))
      "stop folding the next player and return the table" in {
        val newTable = table.currentPlayerFold()
        newTable should be(table)
      }
    }
  }
}
