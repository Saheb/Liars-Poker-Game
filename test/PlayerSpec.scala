import models.{Player, Database}
import org.scalatest.FlatSpec
import org.specs2.matcher.ShouldMatchers
import play.api.test.FakeApplication
import play.api.test.Helpers._
import org.squeryl.PrimitiveTypeMode._
/**
 * Created by saheb on 8/7/15.
 */
class PlayerSpec extends FlatSpec with ShouldMatchers{

  "Player Details" should "be creatable" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
      inTransaction{
        Database.create
        val player = Database.playerTable insert(new Player(1,"Saheb", "sahebmotiani@gmail.com"))
        val selectPlayer = from(Database.playerTable)(p => where(p.player_id === player.player_id) select (p)).single
        assert(selectPlayer.player_id == player.player_id)
      }

      inTransaction{
        val players = from(Database.playerTable)(select(_))
        for(p <- players)
          println(p.name)
      }
    }
  }

}
