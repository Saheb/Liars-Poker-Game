package controllers

import com.google.inject.Inject
import models.Dao
import models.Dao.GameStatus
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by saheb on 8/15/15.
  */
class WatchGameController @Inject()(dao: Dao) extends Controller {

  def getLiveGames = Action {
    val liveGameList = Await.result(dao.getAllGames, Duration.Inf)
    Ok(Json.toJson(liveGameList))
  }

  def gotoWatchGamePage = Action {
    val liveGameList = Await.result(dao.getAllGames, Duration.Inf)
    Ok(views.html.watchGame(liveGameList))
  }
}
