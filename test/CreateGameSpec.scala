import models.{Player, PlayerStatus, GameStatus, Database}
import org.scalatest.{FunSuite}
import play.api.test.FakeApplication
import play.api.test.Helpers._
import org.squeryl.PrimitiveTypeMode._
/**
 * Created by saheb on 8/7/15.
 */
class CreateGameSpec extends FunSuite{

  test("Game create should create a record in GameStatus Table"){
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        Database.create
        val player = Database.playerTable.insert(new Player("Neel", "neelshah@gmail.com"))
        val game = Database.gameStatusTable.insert(new GameStatus("Neel's Game", 1, 6, -1, "Waiting"))
        val playerStatus = Database.playerStatusTable insert (new PlayerStatus(player.player_id,game.id,1,2,"Admin"))
        assert(game.id == 1)
        assert(playerStatus.game_id==game.id)
        assert(playerStatus.num_of_cards == 2)
      }
    }
  }
}
