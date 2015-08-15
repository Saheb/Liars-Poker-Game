package controllers

import models.{Database, GameStatus}
import play.api.db.DB
import play.api.mvc._
import play.api.Play.current
import org.squeryl.PrimitiveTypeMode._
import models.GameStatus._
import play.api.libs.json.Json

/**
 * Created by saheb on 8/13/15.
 */
object CreateGameController extends Controller{

  def createGame = Action(parse.json) { request =>
    val gameJson = request.body
    val gameStatus = gameJson.as[GameStatus]
    val conn = DB.getConnection()
    try {
      inTransaction {
        val selectGameStatus = Database.gameStatusTable.insert(gameStatus)
        Ok(Json.toJson(selectGameStatus.game_id))
      }
    }

    catch {
      case e : IllegalArgumentException => BadRequest("Player Not Found")
    }
    finally {
      conn.close()
    }
  }

  def gotoCreateGamePage(game_id : Long) = Action{
    inTransaction{
      val game = GameStatus.findByGameId(game_id)
      Ok(views.html.createGame(game.name))
    }
  }

  def modifyGame = Action {
    Ok("Game modified!")
  }

  def startGame = Action {
    Ok("Game starts!")
  }

}
