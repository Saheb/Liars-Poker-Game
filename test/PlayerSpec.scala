import models.{Player, Database}
import org.scalatest.FlatSpec
import org.specs2.matcher.ShouldMatchers
import play.api.test.FakeApplication
import play.api.test.Helpers._
import org.squeryl.PrimitiveTypeMode.inTransaction
import org.squeryl.PrimitiveTypeMode._
/**
 * Created by saheb on 8/7/15.
 */
class PlayerSpec extends FlatSpec with ShouldMatchers{

  "Player Details" should "be creatable" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
      inTransaction{
        Database.create
        val player = Database.playerTable insert(new Player("Saheb", "sahebmotiani@gmail.com", 0, 0 ,0))
        println("Saheb inserted in player table")
        player.id should not equals(0)
      }

      inTransaction{
        val players = from(Database.playerTable)(select(_))
        for(p <- players)
          println(p.name)
      }
    }
  }

}
