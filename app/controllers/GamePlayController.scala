package controllers

import models.{GamePlay, Database}
import play.api.libs.iteratee.{Concurrent, Enumerator, Iteratee}
import play.api.libs.json._
import play.api.mvc.{WebSocket, Action, Controller}
import org.squeryl.PrimitiveTypeMode._
import play.api.libs.concurrent.Execution.Implicits._
import _root_.util.main.scala.Deck
import scala.collection.mutable.Map

/**
 * Created by saheb on 8/13/15.
 */

object GamePlayController extends Controller{

  import Database._

  def startGamePlay(game_id : Long) = Action {
    inTransaction {
      update(gameStatusTable)(g =>
        where(g.game_id === game_id) set (g.status := 0)) // This indicates current round number!
    }
    Ok(views.html.gameplay(game_id))
  }

  def hasGameStarted(game_id  :Long) = Action {
    inTransaction{
    val selectGameStatus = from(gameStatusTable)(g => where(g.game_id===game_id) select(g)).single
    if(selectGameStatus.status != -1)
      Ok(views.html.gameplay(game_id))
    else
      InternalServerError("Not started yet!!!")
   }
  }

  def takeAction(game_id : Long) = WebSocket.using[JsValue] { request =>

    //Concurrent.broadcast returns (Enumerator, Concurrent.Channel)
    val (out,channel) = Concurrent.broadcast[JsValue]

    //the Enumerator returned by Concurrent.broadcast subscribes to the channel and will
    //receive the pushed messages

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
               case JsString("Deal") =>
                 //val player_id = msg \ "player"_id"
                 println("Dealing Cards....")
                 val deck = new Deck
                 deck.initialize
                 inTransaction{
                   val playerStatusList = from(playerStatusTable)( ps => where(ps.game_id === game_id) select(ps))
                   val game = from(gameStatusTable)(g => where(g.game_id===game_id) select(g)).single
                   val playerCardList = Map.empty[Long, String]
                   for (p <- playerStatusList.toList)
                   {
                     var cards = Seq.empty[String]
                     for(x <- 1 to p.num_of_cards)
                     {
                       cards = cards :+ deck.pop.toString
                     }
                     playerCardList(p.player_id) = cards.mkString(",")
                     println("inserting hand for player" + p.player_id + " hand= " + playerCardList(p.player_id))
                     gamePlayTable.insert(new GamePlay(game_id, game.status,p.player_id,0,cards.mkString(","),"NA" ))
                   }
                     val gamePlay = from(gamePlayTable)(gp => where(gp.game_id===game_id and gp.round_number===game.status and gp.player_id===player_id) select(gp))
                     channel push(Json.toJson(gamePlay))
                 }
               case JsString("GetCards") =>
                 val player_id = (msg \ "player_id").asInstanceOf[Long]
                 val game = from(gameStatusTable)(g => where(g.game_id===game_id) select(g)).single
                 val gamePlay = from(gamePlayTable)(gp => where(gp.game_id===game_id and gp.round_number===game.status and gp.player_id===player_id) select(gp))
                 channel push(Json.toJson(gamePlay))

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
