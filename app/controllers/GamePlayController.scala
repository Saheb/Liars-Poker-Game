package controllers

import models.Database
import play.api.libs.iteratee.{Concurrent, Enumerator, Iteratee}
import play.api.libs.json.{JsString, JsValue, Json}
import play.api.mvc.{WebSocket, Action, Controller}
import org.squeryl.PrimitiveTypeMode._
import play.api.libs.concurrent.Execution.Implicits._
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
    Ok(views.html.gameplay(game_id))
  }

  def hasGameStarted(game_id  :Long) = Action {
    inTransaction{
    val selectGameStatus = from(gameStatusTable)(g => where(g.game_id===game_id) select(g)).single
    if(selectGameStatus.status.equals("Playing"))
      Ok(views.html.gameplay(game_id))
    else
      InternalServerError("Not started yet!!!")
   }
  }

  def takeAction(game_id : Long) = WebSocket.using[JsValue] { request =>

    //Concurrent.broadcast returns (Enumerator, Concurrent.Channel)
    val (out,channel) = Concurrent.broadcast[JsValue]

    val in = Iteratee.foreach[JsValue] {
         msg =>
           {
             println(msg \ "action")
             val action = (msg \ "action")
             action match {
               case JsString("GameStatus") =>
                 //log the message to stdout and send response back to client
                 inTransaction{
                   val playerStatusList = from(playerStatusTable)( ps => where(ps.game_id === game_id) select(ps))
                   channel push(Json.toJson(playerStatusList))
                 }
               //the Enumerator returned by Concurrent.broadcast subscribes to the channel and will
               //receive the pushed messages
               case _ => println("This should not be printed....!" + action)
             }
           }
    }
    (in,out)
  }

  def dealCards(game_id : Long) = Action {
    // fetch game and player statuses and deal cards accordingly!
    Ok(views.html.gameplay(game_id))
  }

  def persistBet(game_id : Long) = Action {
    Ok
  }
}
