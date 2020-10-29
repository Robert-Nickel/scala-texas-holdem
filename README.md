 # Texas Holdem for the CLI with Scala for Modern Programming at HTWG WS20/21
 
 [![Build Status](https://travis-ci.com/Robert-Nickel/scala-texas-holdem.svg?branch=master)](https://travis-ci.com/Robert-Nickel/scala-texas-holdem)
 [![Coverage Status](https://coveralls.io/repos/github/Robert-Nickel/scala-texas-holdem/badge.svg?branch=master)](https://coveralls.io/github/Robert-Nickel/scala-texas-holdem?branch=master)  
 Notes to begin with:
 - Play No-limit Texas Holdem with 5 other players (6 handed)
 - The other players are bots that make random but valid moves, which take a couple of seconds each, so that the human player can follow along (optional: name generator? avoid stupid moves?)
 - The game follows the general cash game rules of No-limit Texas Holdem
 - The round consist of max. four betting rounds:
    - preflop
    - flop
    - turn
    - river
 - plus an optional showdown
 - The roles of the players are
    - small blind (SB)
    - big blind (BB)
    - under-the-gun (UTG)
    - Middle Player (MP)
    - Hijack (HJ)
    - Cutoff (CO)
    - Dealer aka Button (BTN)
- The commands the player can use in the game are
    - raise 20
    - fold
    - check
    - all-in
- The output is split into a static and a dynamic part
- The static part is the table that displays the community cards, the other players names and stacks and, in case of a showdown, the hole cards of the showing players
- The dynamic part shows the actions of each player e.g. "Player Bob raised to 50" and further info e.g. "Its your turn"
- Optional: exporting the history of each action