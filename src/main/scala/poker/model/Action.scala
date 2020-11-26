package poker.model

object Verb extends Enumeration {
  type Verb = Value
  val FOLD, CHECK, RAISE, CALL, ALLIN = Value
  // TODO: BB POSTEN
}

import Verb._
case class Action(verb: Verb, amount: Option[Int])
