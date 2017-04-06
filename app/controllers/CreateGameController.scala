package controllers

import com.google.inject.Inject
import models.Dao
import play.api.libs.json.Json
import play.api.mvc._
import models.Dao._
import scala.async.Async.{await}

class CreateGameController @Inject()(dao: Dao) extends Controller {

  def createGame = Action(BodyParsers.parse.json[GameStatus]) { request =>
    val gameStatus = request.body
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
    val gameF = dao.getGameStatus(game_id)
    val playerListF = dao.getJoinedPlayerList(game_id)
    await(gameF) match {
      case Some(game) =>
        Ok(
          views.html.createGame(game.name, game_id, await(playerListF).toList))
      case None => NotFound
    }
  }

  def getJoinedPlayerList(game_id: Long) = Action.async {
    dao.getJoinedPlayerList(game_id) map { playerList =>
      Ok(Json.toJson(playerList.toList))
    }
  }

  def modifyGame = Action {
    Ok("Game modified!")
  }
}
