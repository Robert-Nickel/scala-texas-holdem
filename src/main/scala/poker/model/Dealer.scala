package poker.model

import poker.dsl.{TableDSL, PlayerDSL}

object Dealer :

    def showBoardIfRequired(table: Table): Table = 
        if table.currentBettingRound == 1 then flop(table)
        else if table.currentBettingRound == 2 then turn(table)
        else if table.currentBettingRound == 3 then river(table)
        else table

    def flop(table: Table): Table = 
        val newBoard = table.board :+ table.deck.head :+ table.deck.tail.head :+ table.deck.tail.tail.head
        val newDeck = table.deck.tail.tail.tail
        table.copy(board = newBoard, deck = newDeck)
    
    def turn(table: Table): Table = table.copy(board = table.board :+ table.deck.head, deck = table.deck.tail)
    
    def river(table: Table): Table = turn(table)

    def collectHoleCards(table: Table) = table.copy(players = table.players.map(player => player.copy(holeCards = None)))

    def collectCurrentBets(table: Table): Table = 
        table.copy(
            pot = table.pot + table.players.map(player => player.currentBet).sum,
            players = table.players.map(player => player.copy(currentBet = 0)))

    def payTheWinner(table: Table): Table = 
        val winner = table.getTheWinner
        val index = table.players.indexWhere(_.name == winner.name)
        // TODO: use roundInvestment to pay the winner AND reset it
        val newPlayers = table.players.updated(index, 
            table.players(index).copy(stack = winner.stack + table.pot))
        table.copy(pot = 0, players = newPlayers)

    def rotateButton(table: Table): Table = table.copy(players = table.players.drop(1) ++ table.players.take(1))

    def handOutCards(table: Table, deck: List[Card]): Table = handOutCardsToPlayers(table.players, deck)

    private def handOutCardsToPlayers(oldPlayers: List[Player], deck: List[Card], newPlayers: List[Player] = List()): Table =
        (oldPlayers.size, deck.size) match {
            case (oldPlayerSize, _) if oldPlayerSize == 0 => Table(newPlayers, deck)
            case _ =>
                if oldPlayers.head.isInGame then
                handOutCardsToPlayers(oldPlayers.tail, deck.tail.tail, newPlayers :+ oldPlayers.head.copy(holeCards = Some(deck.head, deck.tail.head)))
                else
                handOutCardsToPlayers(oldPlayers.tail, deck, newPlayers :+ oldPlayers.head)
        }

    def nextPlayer(table: Table): Table = table.copy(currentPlayer = (table.currentPlayer + 1) % table.players.length)
