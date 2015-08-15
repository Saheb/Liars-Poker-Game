import models.{PlayerStatus, GameStatus, Player, Database}
import org.scalatest.FunSuite
import org.squeryl.PrimitiveTypeMode._
import play.api.test.FakeApplication
import play.api.test.Helpers._

/**
 * Created by saheb on 8/7/15.
 */
class JoinGameSpec extends FunSuite{

  test("Player joins a game and his record is added in PlayerStatus Table!") {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        Database.create
        val player = Database.playerTable.insert(new Player(1,"Neel", "neelshah@gmail.com"))
        val game = Database.gameStatusTable.insert(new GameStatus(1,"Neel's Game", 1, 6, -1, "Waiting"))
        val playerStatus = Database.playerStatusTable insert (new PlayerStatus(player.player_id,game.id,1,2,"Admin"))
        val new_player = Database.playerTable.insert(new Player(2, "Saheb", "sm@gmail.com"))
        //new_player joins the game -> Neel's Game
        val newPlayerStatus = Database.playerStatusTable insert (new PlayerStatus(new_player.player_id,game.id,2,2,"Joined"))
        // we also need to update game status as 2 player have joined the game!
        //val newGameStatus = game.copy(joined_players = game.joined_players + 1)

        update(Database.gameStatusTable)(s =>
        where(s.id === game.id) set(s.joined_players := s.joined_players.~ + 1)) // Remember the ~~~~

        val players = from(Database.playerTable)(select(_))
        for(p <- players)
          println(p.name + " " + p.player_id)

        val updatedGameStatus = from(Database.gameStatusTable)(g => where(g.id === game.id) select(g)).single
        assert(updatedGameStatus.joined_players == 2)
      }
    }
  }
}
