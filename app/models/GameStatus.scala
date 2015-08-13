package models

import play.api.libs.functional.syntax._
import org.squeryl.KeyedEntity
import play.api.libs.json.{JsPath, Reads, Json, Writes}

/**
 * Created by saheb on 8/13/15.
 */
object GameStatus {

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
      (JsPath \ "name" ).read[String] and
      (JsPath \ "joined_players").read[Int] and
      (JsPath \ "max_players").read[Int] and
        (JsPath \ "winner_player").read[Long] and
        (JsPath \ "status").read[String]
  )(GameStatus.apply _)
}

case class GameStatus(
                       name  : String,
                       joined_players : Int,
                       max_players : Int,
                       winner_player : Long,
                       status : String
                       ) extends KeyedEntity[Long]
{
  val game_id : Long = 0
  val id : Long = game_id
  //lazy val getPlayerStatus : OneToMany[PlayerStatus] = Database.gameStatus2PlayerStatus.left(this)
}
