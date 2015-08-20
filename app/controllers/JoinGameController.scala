package controllers

import models._
import play.api.libs.json.Json
import org.squeryl.PrimitiveTypeMode._
import play.api.libs.functional.syntax._
import play.api.mvc.{Action, Controller}

/**
 * Created by saheb on 8/15/15.
 */
object JoinGameController extends Controller{

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
    inTransaction {
      val selectQuery = from(playerStatusTable)(ps => where(ps.player_id === player.player_id and ps.game_id === game_id) select(ps))
      if(selectQuery.isEmpty)
        {
          val lastPlayer = from(playerStatusTable)(ps => where(ps.game_id === game_id) select(ps) orderBy(ps.position desc))
          if(lastPlayer.isEmpty)
            playerStatusTable.insert(new PlayerStatus(player.player_id, game_id, 1, 2, "Joined"))
          else
            playerStatusTable.insert(new PlayerStatus(player.player_id, game_id, lastPlayer.head.position + 1, 2, "Joined"))
          // Also update game status!
          update(gameStatusTable)(s =>
            where(s.id === game_id) set(s.joined_players := s.joined_players.~ + 1))
          Ok("Joined")
        }
      else
        {
          // update query for player status! position or something!
          Ok("Update Player Status is yet to be implemented!")
        }
    }

  }

  
}
