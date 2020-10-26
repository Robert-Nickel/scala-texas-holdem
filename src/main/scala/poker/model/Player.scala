package main.scala.poker.model

case class Player(name: String, stack: Int, holeCards: (Option[Card], Option[Card])) {
  override def toString: String = s"Player: $name, Stack: $stack, Cards: ${holeCards._1.getOrElse("None")} ${holeCards._2.getOrElse("None")}"

  def getHoleCardsString(): String = s"[${holeCards._1.getOrElse("None")} ${holeCards._2.getOrElse("None")}]"

  def receiveCard(card: Card): Player = {
    if (holeCards._1.isEmpty) {
      Player(this.name, this.stack, (Option.apply(card), this.holeCards._2))
    }
    else if (holeCards._2.isEmpty) {
      Player(this.name, this.stack, (this.holeCards._1, Option.apply(card)))
    }
    this // TODO: define properly
  }
}
