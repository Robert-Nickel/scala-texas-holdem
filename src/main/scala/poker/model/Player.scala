package main.scala.poker.model

case class Player(name: String, stack: Int, holeCards: Option[(Card, Card)]) {

  def getHoleCardsString(): String = {
    if (name.equals("You")) {
      s"${
        if (holeCards.isDefined) {
          holeCards.get._1 + "" + holeCards.get._2
        } else {
          "None None"
        }
      }"
    } else {
      "[xx][xx]"
    }
  }

  def fold(): Player = {
    Thread.sleep(1_000)
    this.copy(holeCards = None)
  }

  def isInRound: Boolean = {
    holeCards.isDefined
  }
}
