package poker

case class Player(name: String, stack: Int, holeCards: (Option[Card], Option[Card]))