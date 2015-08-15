package controllers

import models.{Database, Player}
import play.api.db.DB
import play.api.mvc._
import play.api.Play.current
import models.Player._
import org.squeryl.PrimitiveTypeMode._
import play.api.Logger
/**
 * Created by saheb on 8/12/15.
 */
object LoginController extends Controller{

  val logger = Logger(this.getClass)

  def persistLoginInfo = Action(parse.json) { request =>
    val playerJson = request.body
    val player = playerJson.as[Player]
    val conn = DB.getConnection()
    logger.info("Inserting record to database")
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
