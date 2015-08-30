package models

import play.api.libs.json.{JsPath, Reads, Json, Writes}
import play.api.libs.functional.syntax._
/**
 * Created by saheb on 8/30/15.
 */

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