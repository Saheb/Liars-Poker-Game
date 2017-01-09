package controllers

import com.google.inject.Inject
import models.Dao
import play.api.libs.json.Json
import play.api.mvc._
import models.Dao._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by saheb on 8/13/15.
  */
class CreateGameController @Inject()(dao: Dao) extends Controller {

  def createGame = Action(parse.json) { request =>
    val gameJson = request.body
    val gameStatus = gameJson.as[GameStatus]
    val gameStatusF = dao.insert(gameStatus)
    val playerF = dao.getPlayer(gameStatus.admin_player)
    for {
      player <- playerF
      game <- gameStatusF
    } yield {
      dao.insert(
        new PlayerStatus(gameStatus.admin_player,
                         player.get.name,
                         gameStatus.id,
                         1,
                         2,
                         "Admin"))
    }
    Ok(Json.toJson(gameStatus.id))
  }

  def gotoCreateGamePage(game_id: Long) = Action {
    val game = Await.result(dao.getGameStatus(game_id), Duration.Inf).get
    val playerList =
      Await.result(dao.getJoinedPlayerList(game_id), Duration.Inf).toList
    Ok(views.html.createGame(game.name, game_id, playerList))
  }

  def getJoinedPlayerList(game_id: Long) = Action {
    val joined_players =
      Await.result(dao.getJoinedPlayerList(game_id), Duration.Inf).toList
    Ok(Json.toJson(joined_players))
  }

  def modifyGame = Action {
    Ok("Game modified!")
  }
}
