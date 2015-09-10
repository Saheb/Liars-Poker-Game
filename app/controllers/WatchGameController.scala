package controllers

import models.GameStatus
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import org.squeryl.PrimitiveTypeMode._
/**
 * Created by saheb on 8/15/15.
 */
object WatchGameController extends Controller{

  def getLiveGames = Action{
    inTransaction {
      val liveGameList = GameStatus.getLiveGamesList
      Ok(Json.toJson(liveGameList))
    }
  }

  def gotoWatchGamePage = Action{
    inTransaction{
      val liveGameList = GameStatus.getLiveGamesList
      Ok(views.html.watchGame(liveGameList.toList))
    }
  }
}
