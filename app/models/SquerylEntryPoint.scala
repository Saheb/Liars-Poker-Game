package models

import org.squeryl.PrimitiveTypeMode.{from, inTransaction, join, select, where}
import org.squeryl.{KeyedEntity, PrimitiveTypeMode, Query}
import org.squeryl.annotations.Column
import play.api.libs.json.{JsPath, Json, Reads, Writes}
import play.api.libs.functional.syntax._
/**
  * Created by sahebmotiani on 14/12/2016.
  */
object SquerylEntryPoint extends PrimitiveTypeMode {

  object PlayerStatus{

    import Database._

    def getPlayerStatusById(player_id : Long, game_id : Long) = {
      from(playerStatusTable)(ps => where(ps.game_id===game_id and ps.player_id===player_id) select(ps)).single
    }

    def getJoinedPlayerList(game_id : Long) = {
      join(playerTable, playerStatusTable)( (p,ps) =>
        where(ps.game_id === game_id).
          select(p).
          on(ps.player_id === p.player_id)
      )
    }

    implicit object PlayerStatusWrites extends Writes[PlayerStatus]{
      def writes(p : PlayerStatus) = Json.obj(
        "player_id" -> Json.toJson(p.player_id),
        "name" -> Json.toJson(p.name),
        "game_id" -> Json.toJson(p.game_id),
        "position" -> Json.toJson(p.position),
        "num_of_cards" -> Json.toJson(p.num_of_cards),
        "status" -> Json.toJson(p.status)
      )
    }

    implicit val playerStatusReads : Reads[PlayerStatus] = (
      (JsPath \ "player_id").read[Long] and
        (JsPath \ "name").read[String] and
        (JsPath \ "game_id").read[Long] and
        (JsPath \ "position").read[Int] and
        (JsPath \ "num_of_cards").read[Int] and
        (JsPath \ "status").read[String]
      )(PlayerStatus.apply _)
  }

  case class PlayerStatus(
                           player_id : Long,
                           name : String,
                           game_id : Long,
                           position : Int,
                           num_of_cards : Int,
                           status : String
                         )

  object GameStatus {

    def findByGameId(game_id : Long) : GameStatus = from(Database.gameStatusTable) (
      game => where(game_id === game.game_id) select(game)
    ).single

    def getGame : Query[GameStatus] = from(Database.gameStatusTable) {
      game => where(game.status === -1) select(game)
    }

    def getGameList : Iterable[GameStatus] = inTransaction {
      getGame.toList
    }

    def getLiveGame : Query[GameStatus] = from(Database.gameStatusTable){
      game => where(game.status === 100 ) select(game)
    }

    def getLiveGamesList : Iterable[GameStatus] = inTransaction {
      getLiveGame.toList
    }

    implicit object GameStatusWrites extends Writes[GameStatus] {
      def writes(game : GameStatus) = Json.obj(
        "name" -> Json.toJson(game.name),
        "admin_player" -> Json.toJson(game.admin_player),
        "joined_players" -> Json.toJson(game.joined_players),
        "max_players" -> Json.toJson(game.max_players),
        "winner_player" -> Json.toJson(game.winner_player),
        "status" -> Json.toJson(game.status)
      )
    }
    implicit val GameStatusReads : Reads[GameStatus] = (
      (JsPath \ "id").read[Long] and
        (JsPath \ "name" ).read[String] and
        (JsPath \ "admin_player").read[Long] and
        (JsPath \ "joined_players").read[Int] and
        (JsPath \ "max_players").read[Int] and
        (JsPath \ "winner_player").read[Long] and
        (JsPath \ "status").read[Int]
      )(GameStatus.apply _)
  }

  case class GameStatus(
                         @Column("game_id")
                         id : Long,
                         name  : String,
                         admin_player : Long,
                         joined_players : Int,
                         max_players : Int,
                         winner_player : Long,
                         status : Int
                       ) extends KeyedEntity[Long]
  {
    def game_id : Long = id
    //lazy val getPlayerStatus : OneToMany[PlayerStatus] = Database.gameStatus2PlayerStatus.left(this)
  }

  object Player {
    import models.Database.{playerTable}

    def playerQ : Query[Player] = from(playerTable){
      player => select(player) orderBy(player.name desc)
    }

    def getPlayerList : Iterable[Player] = inTransaction{
      playerQ.toList
    }

    def getPlayerById(player_id : Long) : Player = from(playerTable)(
      player => where(player.player_id === player_id) select(player) ).single

    //  def insertPlayer(player : Player) : Player = inTransaction{
    //    val defensiveCopy = player.copy() // Why???
    //    playerTable.insert(defensiveCopy)
    //  }
    //
    //  def updatePlayer(player : Player) = inTransaction{
    //    playerTable.update(player)
    //  }

    implicit object PlayerWrites extends Writes[Player]{
      def writes(p : Player) = Json.obj(
        "id" -> Json.toJson(p.player_id),
        "name" -> Json.toJson(p.name),
        "email" -> Json.toJson(p.email)
      )
    }

    implicit val playerReads : Reads[Player] = (
      (JsPath \ "id").read[Long] and
        (JsPath \ "name").read[String] and
        (JsPath \ "email").read[String]
      )(Player.apply _)
  }

  case class Player( @Column("player_id")
                     id : Long,
                     name : String,
                     email : String) extends KeyedEntity[Long]
  {
    def player_id : Long = id
  }

  object PlayerStats
  case class PlayerStats(
                          player_id : Long,
                          games_played : Int,
                          games_won : Int,
                          rank : Int
                        )

  object RoundResult {
    implicit object RoundResultWrites extends Writes[RoundResult]{
      def writes(r : RoundResult) = Json.obj(
        "player_challenge_id" -> Json.toJson(r.player_challenge_id),
        "bet_challenged" -> Json.toJson(r.bet_challenged),
        "game_id" -> Json.toJson(r.game_id),
        "round_number" -> Json.toJson(r.round_number),
        "player_bet_id" -> Json.toJson(r.player_bet_id),
        "result" -> Json.toJson(r.result)
      )
    }

    implicit val roundResultReads : Reads[RoundResult] = (
      (JsPath \ "game_id").read[Long] and
        (JsPath \ "round_number").read[Int] and
        (JsPath \ "player_challenge_id").read[Long] and
        (JsPath \ "player_bet_id").read[Long] and
        (JsPath \ "bet_challenged").read[String] and
        (JsPath \ "result").read[String]
      )(RoundResult.apply _)
  }

  case class RoundResult(
                          game_id : Long,
                          round_number : Int,
                          player_challenge_id : Long, // player id of the player who challenged the bet
                          player_bet_id : Long, // player id of the player whose bet has been challenged
                          bet_challenged : String,
                          result : String
                        )

  object GameHand {
    implicit object GameHandWrites extends Writes[GameHand] {
      def writes(game : GameHand) = Json.obj(
        "game_id" -> Json.toJson(game.game_id),
        "player_id" -> Json.toJson(game.player_id),
        "round_number" -> Json.toJson(game.round_number),
        "hand" -> Json.toJson(game.hand)
      )
    }

    implicit val GameHandReads : Reads[GameHand] = (
      (JsPath \ "game_id").read[Long] and
        (JsPath \ "round_number" ).read[Int] and
        (JsPath \ "player_id").read[Long] and
        (JsPath \ "hand").read[String]
      )(GameHand.apply _)
  }

  case class GameHand(
                       game_id : Long,
                       round_number : Int,
                       player_id : Long,
                       hand : String
                     )


  object GameBet {
    implicit object GamePlayWrites extends Writes[GameBet] {
      def writes(game : GameBet) = Json.obj(
        "game_id" -> Json.toJson(game.game_id),
        "player_id" -> Json.toJson(game.player_id),
        "round_number" -> Json.toJson(game.round_number),
        "turn_number" -> Json.toJson(game.turn_number),
        "bet" -> Json.toJson(game.bet)
      )
    }

    implicit val GameBetReads : Reads[GameBet] = (
      (JsPath \ "game_id").read[Long] and
        (JsPath \ "round_number" ).read[Int] and
        (JsPath \ "player_id").read[Long] and
        (JsPath \ "turn_number").read[Int] and
        (JsPath \ "bet").read[String]
      )(GameBet.apply _)
  }

  case class GameBet(
                      game_id : Long,
                      round_number : Int,
                      player_id : Long,
                      turn_number : Int,
                      bet : String
                    )
}
