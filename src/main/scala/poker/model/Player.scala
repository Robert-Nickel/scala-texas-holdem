package main.scala.poker.model

case class Player(name: String, stack: Int, holeCards: (Option[Card], Option[Card])) {
  override def toString: String = s"Player: $name, Stack: $stack, Cards: ${holeCards._1.getOrElse("None")} ${holeCards._2.getOrElse("None")}"

  def getHoleCardsString(): String = {
    if (name.equals("You")) {
      s"[${holeCards._1.getOrElse("None")}][${holeCards._2.getOrElse("None")}]"
    } else {
      "[xx][xx]"
    }
  }

  def act(): Player = {
    // TODO different behavior for human player
    fold()
  }

  def fold(): Player = {
    Thread.sleep(1_000)
    this.copy(holeCards = (None, None))
  }

  def isInRound: Boolean = {
    !holeCards.equals(None, None)
  }
}
