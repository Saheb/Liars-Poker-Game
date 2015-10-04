# Liar's Poker Game - An online multiplayer card game.

###What is Liar's Poker Game?

Its a card game me and my friends used to play in college. Each player bets with knowelge of his cards and bets of other players. Bets are nothing but poker hands i.e. high card, pair,....flush etc. If next player thinks your bet will not be possible with cards of all players combined, then he will challenge you. If the bet doesn't materialize, he wins, and your card count increases, if you win, his card count increases. Player is out of the game once he exceeds maximum limit of cards.

###Aren't there games similar to this one?

Yes, this game is also known as Commune. Similar games are Liar's Dice where bets are permutations of dice outcomes.

###What are the technologies involved in creating this game?

Game is built in Scala using Play Framework 2. Lots of Javasript was required to make the client side work in real time with the backend. Websockets, Server Sent Events and Ajax are all used. WS for real time bet communcation. SSE while players join admin player's game and ajax to Get Player Cards or Game Status. Squeryl is used for the data access layer. Not to forget, bootstrap is used for designing simple pages of the game. 

###What are the future enhancements which are going to be added to this game?

1. Real time audio communication for players using WebRTC.
2. If above works, then video would be next option.
3. Watch Real time games which are being played.
4. Build computer players which can be used to practice.
5. Advanced computer players which can be added to the game with fewer players.

###How can I contribute?

1. If you have worked with WebRTC, then surely you can start with audio module.
2. If you want to build a mobile version of this game, then let me know so you can use the same backend as everthing is REST.
3. If you want to prepare a computer bot who can play this game, then send me a mail at saheb210692@gmail.com






