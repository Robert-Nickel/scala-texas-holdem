import poker.model.{Action, Player, Verb}

import scala.util.parsing.combinator.RegexParsers

/**
 * Aim is to get multiple tables in correct order so its possible to replay the
 * game by calling printText(Table)
 */
class HandHistoryParser extends RegexParsers {
  // first parse the Player Names and only show the first 3 characters
  // read the table blind amounts
  // get the player stack and convert it to bb
  // get the player positions
  // 1. Table: All players sitting no blinds are posted
  // 2. Table: SB did post
  // 3. Table BB did post
  // 4. Table Hero gets Hand Card
  // 5. Table Action

  def integer: Parser[Int] =  """\d+""".r ^^ (_.trim.toInt)
  def word: Parser[String] = "([^\\s]+)".r

  def name1 = "Seat [1-6]: ".r ~> word
  def name2 = "([^:]+)".r  <~ ":"
  def name = name1 | name2

  def action = name2 ~> word ~ opt(integer) ^^ {
    case word ~ integer =>
      word match {
      case "calls" => Action(Verb.CALL, integer)
      case "checks" => Action(Verb.CHECK, None)
      case "folds" => Action(Verb.FOLD, None)
      case "raises" => Action(Verb.RAISE, integer)
    }
  }

  def chips: Parser[Int] = "[(]".r ~> integer <~ "in chips[)]".r

  def player: Parser[Player] = name ~ chips ^^ { case name ~ chips => Player(name, chips)}
}
