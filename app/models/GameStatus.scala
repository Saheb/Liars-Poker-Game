package models

import play.api.libs.functional.syntax._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Optimistic, Query, KeyedEntity}
import play.api.libs.json.{JsPath, Reads, Json, Writes}
import org.squeryl.annotations.Column

/**
 * Created by saheb on 8/13/15.
 */
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
