import scala.collection.immutable.HashMap
import poker.model.{Player, Card, Table}
import scala.util.Try

package poker {
    val names = List("Amy", "Bob", "Dev", "Fox", "Udo", "You")
    val players = names.map(name => Player(name, 200))
    val sb = 1
    val bb = 2
    val cardSymbols = List('♥', '♠', '♦', '♣')
    val cardValues: HashMap[Char, Set[Int]] = HashMap(
        ('2', Set(2)),
        ('3', Set(3)),
        ('4', Set(4)),
        ('5', Set(5)),
        ('6', Set(6)),
        ('7', Set(7)),
        ('8', Set(8)),
        ('9', Set(9)),
        ('T', Set(10)),
        ('J', Set(11)),
        ('Q', Set(12)),
        ('K', Set(13)),
        ('A', Set(1, 14)))
    def getDeck: List[Card] = {
        (for {
        v <- cardValues
        s <- cardSymbols
        } yield Card(v._1, s)).toList
    }
    implicit class TableDSL(table: Table) {

        def getCurrentPlayer: Player = table.players(table.currentPlayer)

        def getHighestOverallBet: Int = table.players.map(player => player.currentBet).max

        def isOnlyOnePlayerInRound: Boolean = table.players.count(p => p.isInRound) == 1

        def isSB(player: Player): Boolean = table.players(1) == player

        def isBB(player: Player): Boolean = table.players(2) == player

        def isPreFlop: Boolean = table.currentBettingRound == 0

        def shouldPlayNextRound: Boolean = table.players.count(p => p.isInGame) > 1
        
        def shouldPlayNextBettingRound: Boolean = table.currentBettingRound < 3 && !table.isOnlyOnePlayerInRound
        
        def shouldPlayNextMove: Boolean =
            val maxCurrentBet = table.players.map(p => p.currentBet).max
            table.players.exists(player =>
                player.isInRound && (player.currentBet != maxCurrentBet || !player.hasActedThisBettingRound))

        def getPrintableWinning: String = 
            val winner = table.getTheWinner
            if (table.currentBettingRound == 0) {
                table.getPrintableTable(showCards = true) + "\n" +
                s"${winner.name} wins ${table.pot}\n\n"
            } else {
                val evaluation = table.evaluate(winner)
                table.getPrintableTable(showCards = true) + "\n" +
                s"${winner.name} wins ${table.pot} with ${evaluation.handName}\n\n"
            }

        def getPrintableTable(showCards: Boolean = false): String =
            def getPot = " " * (42 - (table.pot.toString.length / 2)) + s"Pot ${table.pot}"

            def getBoard = " " * (44 - (table.board.size * 4) / 2)
                + table.board.map(card => s"[${card.value}${card.symbol}]").mkString
            

            def getCurrentBets = table.players.map(player => {
                val spacesAfterCurrentBet = 16 - player.currentBet.toString.length
                s"${player.currentBet}" + " " * spacesAfterCurrentBet
                }).mkString

            def getBettingLine = "_" * 88

            def getHoleCards(showCards: Boolean = false) =
                table.players.map(player => {
                s"${player.getHoleCardsString(showCards)}" + " " * 8
                }).mkString

            def getNames = table.players.head.name + " (D) " + " " * 8 +
                table.players.tail.map(player => {
                    s"${player.name} " + " " * 12
                }).mkString

            def getStacks = table.players.map(player => {
                val spacesAfterStack = 16 - player.stack.toString.length
                s"${player.stack}" + " " * spacesAfterStack
                }).mkString

            def getCurrentPlayerUnderline = s"${" " * 16 * table.currentPlayer}" + "_" * 8
            
            "\n" +
                getPot + "\n" +
                getBoard + "\n\n" +
                getCurrentBets + "\n" +
                getBettingLine + "\n" +
                getHoleCards(showCards) + "\n" +
                getNames + "\n" +
                getStacks + "\n" +
                getCurrentPlayerUnderline + "\n"
        }

    implicit class PlayerDSL(player: Player) {
        def is(stack: Int): PlayerDSL = PlayerDSL(player = player.copy(stack = stack))

        def are(stack: Int): PlayerDSL = is(stack)

        def deep: Player = player

        def hasCards(cards: String): Player = player.copy(holeCards = Some((Card(cards(0), cards(1)), Card(cards(3), cards(4)))))

        def haveCards(cards: String): Player = hasCards(cards)

        def posts(blind: Int): Try[Player] = player.raise(blind, 0)

        def post(blind: Int): Try[Player] = posts(blind)

        def isInRound: Boolean = player.holeCards.isDefined

        def areInRound = isInRound

        def isInGame: Boolean = player.stack > 0 || player.currentBet > 0

        def isAllIn: Boolean = player.stack == 0 && isInRound

        def areInGame: Boolean = isInGame

        // highest overall bet is not necessary when going all-in
        // TODO: handle failure case if shove is called with stack == 0
        def shoves(unit: Unit): Player = player.raise(player.stack, 0).get

        def isHumanPlayer: Boolean = player.name == "You"
    }
}