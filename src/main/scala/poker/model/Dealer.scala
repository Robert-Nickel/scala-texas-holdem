package poker.model

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