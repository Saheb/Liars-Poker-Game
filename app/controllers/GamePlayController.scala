package controllers

import models.Database
import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import org.squeryl.PrimitiveTypeMode._
import play.mvc.Results._

/**
 * Created by saheb on 8/13/15.
 */

object GamePlayController extends Controller{

  import Database._

  def startGamePlay(game_id : Long) = Action {
    inTransaction {
      update(gameStatusTable)(g =>
        where(g.game_id === game_id) set (g.status := "Playing"))
    }
    Ok(views.html.gameplay())
  }

  def hasGameStarted(game_id  :Long) = Action {
    inTransaction{
    val selectGameStatus = from(gameStatusTable)(g => where(g.game_id===game_id) select(g)).single
    if(selectGameStatus.status.equals("Playing"))
      Ok(views.html.gameplay())
    else
      InternalServerError("Not started yet!!!")
   }
  }
}
