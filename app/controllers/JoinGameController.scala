package controllers

import models.SquerylEntryPoint._
import models._
import play.Logger
import play.api.libs.EventSource
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee.{Concurrent, Enumeratee}
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

/**
 * Created by saheb on 8/15/15.
 */
object JoinGameController extends Controller{

  val (joined_players, update2client) = Concurrent.broadcast[JsValue]

  import Database._

  def getActiveGames = Action {
    val gameList = GameStatus.getGameList
    Ok(Json.toJson(gameList))
  }

  def gotoJoinGamePage = Action {
    val activeGames = GameStatus.getGameList
    Ok(views.html.joinGame(activeGames.toList))
  }

  def joinGame(game_id : Long) = Action(parse.json){ request =>
    val playerJson = request.body
    val player = playerJson.as[Player]

    // did create a new harcoded one, instead of creating implcits writer for a custom object

    inTransaction {
      val selectQuery = from(playerStatusTable)(ps => where(ps.player_id === player.player_id and ps.game_id === game_id) select(ps))
      if(selectQuery.isEmpty)
        {
          val lastPlayer = from(playerStatusTable)(ps => where(ps.game_id === game_id) select(ps) orderBy(ps.position desc))
          if(lastPlayer.isEmpty)
            playerStatusTable.insert(new PlayerStatus(player.player_id,player.name, game_id, 1, 2, "Joined"))
          else
            playerStatusTable.insert(new PlayerStatus(player.player_id,player.name, game_id, lastPlayer.head.position + 1, 2, "Joined"))
          // Also update game status!
          update(gameStatusTable)(g =>
            where(g.id === game_id) set(g.joined_players := g.joined_players.~ + 1))

          val selectPlayerStatus = PlayerStatus.getPlayerStatusById(player.player_id, game_id)
          val player2pushJson : JsValue = Json.obj(
            "player_id" -> JsNumber(player.player_id),
            "name" -> JsString(player.name),
            "email" -> JsString(player.email),
            "game_id" -> JsNumber(game_id),
            "position" -> JsNumber(selectPlayerStatus.position)
          )
          update2client.push(player2pushJson)
          Ok("Joined")
        }
      else
        {
          // update query for player status! position or something!
          Ok("Update Player Status is yet to be implemented!")
        }
    }
  }

  def filterJoinedPlayers(game_id : Long) : Enumeratee[JsValue, JsValue] = {
    Enumeratee.filter[JsValue]{
      ps : JsValue => (ps \ "game_id").as[Long] == game_id
    }
  }

  def connDeathWatch(addr: String): Enumeratee[JsValue, JsValue] =
    Enumeratee.onIterateeDone{ () => Logger.info(addr + " - SSE disconnected") }

  def updatedJoinedPlayers(game_id : Long) = Action { req =>
    Ok.feed(joined_players
      &> filterJoinedPlayers(game_id)
      &> connDeathWatch(req.remoteAddress)
      &> EventSource()).as("text/event-stream")
  }
  
}
