package poker.model

import main.scala.poker.model.{Card, Player, Table}
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class TableSpec extends AnyWordSpec with Matchers {

  val players = List(
    Player("Bob", 200, Some(Card('A', 'h'), Card('A', 's'))),
    Player("Jon", 200, Some(Card('K', 'h'), Card('K', 's'))))

  "Given a Table" when {
    "next player is called" should {
      val table = Table(players)
      "take the default of 0 and return a table with current player = 1" in {
        table.nextPlayer() should be(table.copy(currentPlayer = 1))
      }
    }
    "next player is called with current player = 1" should {
      val table = Table(players, currentPlayer = 1)
      "return a table with current player = 0" in {
        table.nextPlayer() should be(table.copy(currentPlayer = 0))
      }
    }
    "get current player is called" should {
      val player = Player("Zoe", 200, Some(Card('A', 'h'), Card('A', 's')))
      val table = Table(List(player))
      "return the current player" in {
        val currentPlayer = table.getCurrentPlayer()
        currentPlayer should be(player)
      }
    }
  }
}
