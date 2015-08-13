package controllers

import models.{Database, Player}
import play.api.db.DB
import play.api.mvc._
import play.api.Play.current
import models.Player._
import org.squeryl.PrimitiveTypeMode._
/**
 * Created by saheb on 8/12/15.
 */
object Login extends Controller{

  def persistLoginInfo = Action(parse.json) { request =>
    val playerJson = request.body
    val player = playerJson.as[Player]
    val conn = DB.getConnection()
    try {
      inTransaction {
        Database.playerTable.insert(player)
      }
      Ok("Player record inserted")
    }
      catch {
        case e : IllegalArgumentException => BadRequest("Player Not Found")
      }
    finally {
      conn.close()
    }

  }

}
