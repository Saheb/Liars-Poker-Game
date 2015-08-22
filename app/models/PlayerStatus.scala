package models

import org.squeryl.PrimitiveTypeMode._
import play.api.libs.json.{Json, Writes, JsPath, Reads}
import play.api.libs.functional.syntax._
/**
 * Created by saheb on 8/14/15.
 */
object PlayerStatus{

  import Database._

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
      "game_id" -> Json.toJson(p.game_id),
      "position" -> Json.toJson(p.position),
      "num_of_cards" -> Json.toJson(p.num_of_cards),
      "status" -> Json.toJson(p.status)
    )
  }

  implicit val playerStatusReads : Reads[PlayerStatus] = (
    (JsPath \ "player_id").read[Long] and
      (JsPath \ "game_id").read[Long] and
      (JsPath \ "position").read[Int] and
      (JsPath \ "num_of_cards").read[Int] and
      (JsPath \ "status").read[String]
    )(PlayerStatus.apply _)
}

case class PlayerStatus(
                         player_id : Long,
                         game_id : Long,
                         position : Int,
                         num_of_cards : Int,
                         status : String
                         )