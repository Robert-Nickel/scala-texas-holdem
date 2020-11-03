package poker

import main.scala.poker.model.{Card, Player}

import scala.collection.immutable.HashMap

object InitHelper {
  def createDeck(values: HashMap[Char, Set[Int]], symbols: List[Char]): List[Card] = {
    // values.flatMap(v => symbols.map(s => Card(v._1, s))).toList
    (for {
      v <- values
      s <- symbols
    } yield Card(v._1, s)).toList
  }

  def createPlayers(names: List[String], startingStack: Int): List[Player] = {
    names.map(name => Player(name, startingStack, (Option.empty, Option.empty)))
  }
}
