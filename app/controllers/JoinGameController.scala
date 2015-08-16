package controllers

import models.GameStatus
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

/**
 * Created by saheb on 8/15/15.
 */
object JoinGameController extends Controller{

  def getActiveGames = Action {
      val gameList = GameStatus.getGameList
      Ok(Json.toJson(gameList))
  }

  def gotoJoinGamePage = Action {
      val activeGames = GameStatus.getGameList
      Ok(views.html.joinGame(activeGames.toList))
  }

//  def joinGame = Action{
//
//  }
  
}
