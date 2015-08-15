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

  def getGame : Query[GameStatus] = from(Database.gameStatusTable) {
    game => select(game)
      //where(game.status.equals("Waiting"))
  }

  def getGameList : Iterable[GameStatus] = inTransaction {
    getGame.toList
  }

  implicit object GameStatusWrites extends Writes[GameStatus] {
    def writes(game : GameStatus) = Json.obj(
      "name" -> Json.toJson(game.name),
      "joined_players" -> Json.toJson(game.joined_players),
      "max_players" -> Json.toJson(game.max_players),
      "winner_player" -> Json.toJson(game.winner_player),
      "status" -> Json.toJson(game.status)
    )
  }
    implicit val GameStatusReads : Reads[GameStatus] = (
      (JsPath \ "id").read[Long] and
      (JsPath \ "name" ).read[String] and
      (JsPath \ "joined_players").read[Int] and
      (JsPath \ "max_players").read[Int] and
        (JsPath \ "winner_player").read[Long] and
        (JsPath \ "status").read[String]
  )(GameStatus.apply _)
}

case class GameStatus(
                       @Column("game_id")
                       id : Long,
                       name  : String,
                       joined_players : Int,
                       max_players : Int,
                       winner_player : Long,
                       status : String
                       ) extends KeyedEntity[Long]
{
  def game_id : Long = id
  //lazy val getPlayerStatus : OneToMany[PlayerStatus] = Database.gameStatus2PlayerStatus.left(this)
}
