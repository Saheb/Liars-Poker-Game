import models._
import org.scalatest.FunSuite
import play.api.test.FakeApplication
import play.api.test.Helpers._
import org.squeryl.PrimitiveTypeMode._
/**
 * Created by saheb on 8/8/15.
 */
class GamePlaySpec extends FunSuite{

  test("Game Play starts adding records once Game Starts!") {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        Database.create
        val player = Database.playerTable.insert(new Player(1, "Neel", "neelshah@gmail.com"))
        val game = Database.gameStatusTable.insert(new GameStatus(1, "Neel's Game",player.player_id, 1, 6, -1, -1))
        val playerStatus = Database.playerStatusTable insert (new PlayerStatus(player.player_id, player.name, game.id, 1, 2, "Admin"))
        val new_player = Database.playerTable.insert(new Player(2, "Saheb", "sm@gmail.com"))
        val newPlayerStatus = Database.playerStatusTable insert (new PlayerStatus(new_player.player_id, new_player.name,game.id, 2, 2, "Joined"))

        update(Database.gameStatusTable)(s =>
          where(s.id === game.id) set (s.joined_players := s.joined_players.~ + 1)) // Remember the ~~~~

        // Here we need to update GameStatus to running from Waiting!
        update(Database.gameStatusTable)(g => where(g.id === game.id) set (g.status := 0))

        // Check whether status of the game has been updated or not!
        val selectGame = from(Database.gameStatusTable)(g => where(g.id === game.id) select (g)).single
        assert(selectGame.joined_players == 2)
        //assert(selectGame.status.equals(0))

        //Dealing of cards happens.
        val gamePlayRecord = Database.gamePlayTable.insert(new GamePlay(game.id, 0, player.player_id, 0, "23,42", "NA"))
        val gamePlayRecord_2 = Database.gamePlayTable.insert(new GamePlay(game.id, 0, new_player.player_id, 0, "31,12", "NA"))

        //Round #1 Turn #1
        val player_round_1 = Database.gamePlayTable.insert(new GamePlay(game.id, 1, player.player_id, 1, "23,42", "High_8_NA_NA"))
        val new_player_round1 = Database.gamePlayTable.insert(new GamePlay(game.id, 1, new_player.player_id, 1, "31,12", "High_K_NA_NA"))

        //Round #1 Turn #2
        val player_round2 = Database.gamePlayTable.insert(new GamePlay(game.id, 2, player.player_id, 2, "23,42", "High_A_NA_NA"))
        val new_player_round2 = Database.gamePlayTable.insert(new GamePlay(game.id, 2, new_player.player_id, 2, "23,42", "Pair_A_NA_NA"))

        // player challenge new_player
        // We check all cards to form Ace Pair, it doesn't then player wins. If it does new_player wins.
        // Adding record in RoundResult Table for this Round
        val roundResult = Database.roundResultTable.insert(new RoundResult(game.id, 1, player.player_id, new_player.player_id, "Pair_A_NA_NA", "Won"))

        // lets increase num_of_cards for new_player!
        update(Database.playerStatusTable)(p => where(p.player_id === new_player.player_id) set (p.num_of_cards := p.num_of_cards.~ + 1))

        val players = from(Database.playerTable)(select(_))
        for(p <- players)
          println(p.name + " " + p.player_id)

        val selectPlayer = from(Database.playerStatusTable)(p => where(p.player_id === new_player.player_id) select (p)).single
        assert(selectPlayer.num_of_cards == 3)
      }
    }
  }
}
