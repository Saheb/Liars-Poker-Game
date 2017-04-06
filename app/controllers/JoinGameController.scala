package controllers

import javax.inject.Inject

import models.Dao
import models.Dao.{Player, PlayerStatus}
import play.Logger
import play.api.libs.EventSource
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee.{Concurrent, Enumeratee}
import play.api.libs.json._
import play.api.mvc.{Action, BodyParsers, Controller}

import scala.concurrent.Future

class JoinGameController @Inject()(dao: Dao) extends Controller {

  val (joined_players, update2client) = Concurrent.broadcast[JsValue]

  //TODO: Filter all games with active games only!
  def getActiveGames = Action.async {
    dao.getAllGames map { gameList =>
      Ok(Json.toJson(gameList))
    }
  }

  //TODO: Filter all games with active games only!
  def gotoJoinGamePage = Action.async {
    dao.getAllGames map { activeGames =>
      Ok(views.html.joinGame(activeGames.toList))
    }
  }

  def joinGame(game_id: Long) = Action(BodyParsers.parse.json[Player]) { request =>
    val player = request.body
    // did create a new harcoded one, instead of creating implicits writer for a custom object
    val playersInGame = dao.getPlayerStatusForGame(game_id)

    playersInGame map { playerList =>
      if (playerList.isEmpty) {
        dao.insert(PlayerStatus(player.id, player.name, game_id, 1, 2, "Joined"))
      } else {
        dao.insert(PlayerStatus(player.id, player.name, game_id, playerList.head.position + 1, 2, "Joined"))
      }
    }

    // Also update game status!
    dao.updateGameState(game_id)
    val selectPlayerStatus = dao.getPlayerStatus(player.id, game_id)

    val player2pushJson: Future[JsObject] = selectPlayerStatus map { ps =>
      {
        ps match {
          case Some(k) =>
            Json.obj(
              "player_id" -> JsNumber(player.id),
              "name"      -> JsString(player.name),
              "email"     -> JsString(player.email),
              "game_id"   -> JsNumber(game_id),
              "position"  -> JsNumber(k.position)
            )
        }
      }
    }

    player2pushJson.map(player => update2client.push(player))
    Ok("Joined")
  }
//  else {
//        // update query for player status! position or something!
//        Ok("Update Player Status is yet to be implemented!")
//      }

  def updatedJoinedPlayers(game_id: Long) = Action { req =>
    Ok.feed(
        joined_players
          &> filterJoinedPlayers(game_id)
          &> connDeathWatch(req.remoteAddress)
          &> EventSource()
      )
      .as("text/event-stream")
  }

  def filterJoinedPlayers(game_id: Long): Enumeratee[JsValue, JsValue] = {
    Enumeratee.filter[JsValue] { ps: JsValue =>
      (ps \ "game_id").as[Long] == game_id
    }
  }

  def connDeathWatch(addr: String): Enumeratee[JsValue, JsValue] =
    Enumeratee.onIterateeDone { () =>
      Logger.info(addr + " - SSE disconnected")
    }

}
