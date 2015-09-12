package controllers

import java.util.concurrent.ExecutionException

import models.{Database, Player}
import org.h2.jdbc.JdbcSQLException
import play.api.db.DB
import play.api.libs.json.Json
import play.api.mvc._
import play.api.Play.current
import models.Player._
import org.squeryl.PrimitiveTypeMode._
import play.api.Logger
/**
 * Created by saheb on 8/12/15.
 */
object LoginController extends Controller{

  import Database._

  val logger = Logger(this.getClass)

  def persistLoginInfo = Action(parse.json) { request =>
    val playerJson = request.body
    val player = playerJson.as[Player]
    val conn = DB.getConnection()
    logger.info("Inserting record to database")
    try {
      inTransaction {
        val selectQuery = from(Database.playerTable) (p => where(p.email === player.email) select(p))
        if(selectQuery.isEmpty) {
          val insertedPlayer = Database.playerTable.insert(player)
          Ok(Json.toJson(insertedPlayer.player_id))
        }
        else
          Ok(Json.toJson(selectQuery.single.player_id))
      }
    }
      catch {
        case e : IllegalArgumentException => BadRequest(e.getMessage)
        case j : JdbcSQLException => BadRequest(j.getMessage)
        case ex : ExecutionException => BadRequest(ex.getMessage)
      }
    finally {
      conn.close()
    }
  }

}
