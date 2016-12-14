package controllers

import _root_.util.main.scala.Deck
import models.SquerylEntryPoint._
import models._
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee.Concurrent.Channel
import play.api.libs.iteratee.{Concurrent, Enumerator, Iteratee}
import util.Card
import play.api.libs.json._
import play.api.mvc.{Action, Controller, WebSocket}

import scala.collection.mutable.Map

/**
 * Created by saheb on 8/13/15.
 */

object GamePlayController extends Controller{

  import Database._
  // (game_id, player_id) will act as key
  var socketMap = Map.empty[(Long,Long), (Enumerator[JsValue], Channel[JsValue])]

  def startGamePlay(game_id : Long) = Action {
    inTransaction {
      update(gameStatusTable)(g =>
        where(g.game_id === game_id and g.status===(-1)) set (g.status := 1)) // This indicates current round number!
    }
    Ok(views.html.gameplay(game_id))
  }

  def continueGamePlay(game_id : Long) = {
    inTransaction {
      update(gameStatusTable)(g =>
        where(g.game_id === game_id) set (g.status := (g.status.~ + 1))) // This indicates current round number!
    }
    //Ok("Updated the round number!")
  }

  def hasGameStarted(game_id  :Long) = Action {
    inTransaction{
    val selectGameStatus = from(gameStatusTable)(g => where(g.game_id===game_id) select(g)).single
    if(selectGameStatus.status != -1)
      Ok(views.html.gameplay(game_id))
    else
      InternalServerError("Game has not started yet!!!")
   }
  }

  def dealCards(game_id : Long, round_number : Int) = Action {

    inTransaction{
      val playerStatusList = from(playerStatusTable)( ps => where(ps.game_id === game_id and ps.status <> "Out") select(ps))
      val currentRound = from(gameStatusTable)(g => where(g.game_id===game_id) select(g)).single
      val gameHand = from(gameHandTable)(g => where(g.game_id===game_id and g.round_number === currentRound.status) select(g))
      val cardsNotDealt = (gameHand.toList.size == 0 )
      if(cardsNotDealt)
        {
          Logger.info("Dealing Cards....");
          val deck = new Deck
          deck.initialize
          Logger.info(s"Round_Number=${currentRound.status} begins.....")
          val playerCardList = Map.empty[Long, String]
          for (p <- playerStatusList.toList)
          {
            var cards = Seq.empty[String]
            for(x <- 1 to p.num_of_cards)
            {
              val card = new Card(deck.pop)
              cards = cards :+ (card.getValue.toString concat card.getSuit.toString)
            }
            playerCardList(p.player_id) = cards.mkString(",")
            Logger.info("inserting hand for player" + p.player_id + " hand= " + playerCardList(p.player_id))
            gameHandTable.insert(new GameHand(game_id, currentRound.status,p.player_id,cards.mkString(",")))
          }
          Ok("Cards are dealt")
        }
      else
        {
          Ok("Cards are already dealt")
        }

    }

  }

  def getCards(game_id : Long) = Action(parse.json) { req=>
    val playerJson = req.body
    val player = playerJson.as[Player]
    inTransaction{
      val game = from(gameStatusTable)(g => where(g.game_id===game_id) select(g)).single
      val gameHand = from(gameHandTable)(gp => where(gp.game_id===game_id and gp.round_number===game.status and gp.player_id===player.player_id) select(gp))
      Ok(Json.toJson(gameHand))
    }
  }

  def getAllHands(game_id : Long) = Action { req=>
    //val playerJson = req.body
    //val player = playerJson.as[Player]
    //TODO : verify if the player is acually out of the game or not!
    inTransaction{
      val game = from(gameStatusTable)(g => where(g.game_id===game_id) select(g)).single
      val allHands = from(gameHandTable)(gp => where(gp.game_id===game_id and gp.round_number===game.status) select(gp))
      Ok(Json.toJson(allHands))
    }
  }

  def getFinalStandings(game_id : Long) = Action { req =>
    inTransaction{
      Logger.info("Getting final standing for game " + game_id)
      val winnerPlayer = from(playerStatusTable)( ps => where(ps.game_id === game_id and ps.status <> "Out") select(ps)).single
      update(playerStatusTable)(p =>
        where(p.player_id === winnerPlayer.player_id and p.game_id === game_id) set(p.position := 1))
      val playerStatusList = from(playerStatusTable)( ps => where(ps.game_id === game_id) select(ps) orderBy(ps.position))
      Ok(Json.toJson(playerStatusList))
    }
  }

  def takeAction(game_id : Long, player_id : Long) = WebSocket.using[JsValue] { request =>

    Logger.info(s"Creating socket for Game_Id = ${game_id}, Player_Id = ${player_id}")

    val (out,channel) = socketMap.get((game_id,player_id)).getOrElse(Concurrent.broadcast[JsValue])

    if(!socketMap.contains((game_id,player_id)))
      socketMap.put((game_id,player_id), (out,channel))


    //the Enumerator returned by Concurrent.broadcast subscribes to the channel and will
    //receive the pushed messages

    val in = Iteratee.foreach[JsValue] {
         msg =>
           {
             val action = (msg \ "action")
             Logger.info(action.toString());
             action match {
               case JsString("GameStatus") =>
                 //log the message to stdout and send response back to client
                 val player = (msg \ "player").as[Player]
                 inTransaction{
                   val playerStatusList = from(playerStatusTable)( ps => where(ps.game_id === game_id and ps.status <> "Out") select(ps) orderBy(ps.position))
                   val channels = socketMap.filter(p => (p._1._1 == game_id && p._1._2 == player.player_id))
                   channels.foreach(f => f._2._2 push(Json.toJson(playerStatusList)))
                 }

               case JsString("Bet") =>
                   Logger.info(msg \ "bet" toString())
                   // persist bet and then push to all channels, as done above in GameStatus case!
                   val player = (msg \ "player").as[Player]
                   val bet = (msg \ "bet").as[GameBet]
                   val channels = socketMap.filter(p => (p._1._1 == game_id && p._1._2 != player.player_id))
                   channels.foreach(f => f._2._2 push(Json.toJson(bet)))
                   inTransaction{
                     gameBetTable.insert(new GameBet(bet.game_id, bet.round_number,bet.player_id,bet.turn_number,bet.bet))
                   }

               case JsString("Ready")=>
                 Logger.info(msg toString())
                 val player = (msg \ "player").as[Player]
                 val channels = socketMap.filter(p => (p._1._1 == game_id && p._1._2 != player.player_id))
                 channels.foreach(f => f._2._2 push(Json.toJson(msg)))

               case JsString("Chat") =>
                 Logger.info(msg \ "message" toString())
                 val player = (msg \ "player").as[Player]
                 val channels = socketMap.filter(p => (p._1._1 == game_id && p._1._2 != player.player_id))
                 channels.foreach(f => f._2._2 push(Json.toJson(msg)))

               case JsString("Challenge") =>
                 val roundResult = (msg \ "roundResult").as[RoundResult]
                 Logger.info(roundResult toString)
                 inTransaction{
                   val playerHandList = from(gameHandTable)(gh => where(gh.game_id === game_id and gh.round_number === roundResult.round_number) select(gh))
                   val channels = socketMap.filter(p => (p._1._1 == game_id))
                   channels.foreach(f => f._2._2 push(Json.toJson(roundResult)))
                   channels.foreach(f => f._2._2 push(Json.toJson(playerHandList)))
                   roundResultTable.insert(roundResult)
                 }

               case JsString("RoundResult") =>
                 val roundResult = (msg \ "roundResult").as[RoundResult]
                 Logger.info(roundResult toString)
                 Logger.info(s"Closing all sockets after round ${roundResult.round_number} for gameId ${game_id}");
                 inTransaction {
                   Logger.info("Socket Map Size Before=" + socketMap.size)
                   socketMap.retain((k,v) => (k._1 != game_id))
                   Logger.info("Socket Map Size After=" + socketMap.size)

                   update(roundResultTable)(r =>
                     where(r.game_id === roundResult.game_id and r.round_number === roundResult.round_number)
                       set (r.result := roundResult.result))
                   continueGamePlay(game_id) // this will change round_number

                   val playerStatusList = from(playerStatusTable)( ps => where(ps.game_id === game_id and ps.status <> "Out") select(ps))

                   if (roundResult.result.equals("WON")) {
                     update(playerStatusTable)(p =>
                       where(p.player_id === roundResult.player_bet_id and p.game_id === game_id) set(p.num_of_cards := p.num_of_cards.~ + 1))
                     val lostPlayer = from(playerStatusTable)(p=>
                       where(p.player_id === roundResult.player_bet_id and p.game_id === game_id) select(p)).single
                     if(lostPlayer.num_of_cards > 5)
                       {
                         //TODO : Find out why order of below two statements matter!
                         // Updating Final Standing
                         update(playerStatusTable)(p =>
                           where(p.player_id === roundResult.player_bet_id and p.game_id === game_id) set(p.position := playerStatusList.toList.size))

                         update(playerStatusTable)(p =>
                           where(p.player_id === roundResult.player_bet_id and p.game_id === game_id) set(p.status := "Out"))

                         update(gameStatusTable)(g=>
                           where(g.game_id === game_id) set(g.winner_player := roundResult.player_challenge_id))
                       }
                   }
                   else {
                     update(playerStatusTable)(p =>
                       where(p.player_id === roundResult.player_challenge_id and p.game_id === game_id) set(p.num_of_cards := p.num_of_cards.~ + 1))
                     val lostPlayer = from(playerStatusTable)(p=>
                       where(p.player_id === roundResult.player_challenge_id and p.game_id === game_id) select(p)).single
                     if(lostPlayer.num_of_cards > 5)
                     {
                       // Updating Final Standing
                       update(playerStatusTable)(p =>
                         where(p.player_id === roundResult.player_challenge_id and p.game_id === game_id) set(p.position := playerStatusList.toList.size))

                       update(playerStatusTable)(p =>
                         where(p.player_id === roundResult.player_challenge_id and p.game_id === game_id) set(p.status := "Out"))

                       update(gameStatusTable)(g=>
                       where(g.game_id === game_id) set(g.winner_player := roundResult.player_bet_id))
                     }
                   }
                 }

               case JsString("Close") =>
                 Logger.info(s"Removing socket for ${game_id} and ${player_id}");
                 //socketMap.remove((game_id,player_id));

               case _ => Logger.info("This should not be printed....!" + action)
             }
           }
    }
    (in,out)
  }
}
