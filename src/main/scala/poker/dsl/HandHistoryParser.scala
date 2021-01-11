package poker.dsl

import poker.model.{Action, Player, Verb}
import scala.util.parsing.combinator.RegexParsers

class HandHistoryParser extends RegexParsers {

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
