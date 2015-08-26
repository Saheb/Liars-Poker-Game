package models

import play.api.libs.json.{JsPath, Reads, Json, Writes}
import play.api.libs.functional.syntax._
/**
 * Created by saheb on 8/14/15.
 */
object GamePlay {
  implicit object GamePlayWrites extends Writes[GamePlay] {
    def writes(game : GamePlay) = Json.obj(
      "game_id" -> Json.toJson(game.game_id),
      "player_id" -> Json.toJson(game.player_id),
      "round_number" -> Json.toJson(game.round_number),
      "turn_number" -> Json.toJson(game.turn_number),
      "hand" -> Json.toJson(game.hand),
      "bet" -> Json.toJson(game.bet)
    )
  }

  implicit val GamePlayReads : Reads[GamePlay] = (
    (JsPath \ "game_id").read[Long] and
      (JsPath \ "round_number" ).read[Int] and
      (JsPath \ "player_id").read[Long] and
      (JsPath \ "turn_number").read[Int] and
      (JsPath \ "hand").read[String] and
      (JsPath \ "bet").read[String]
    )(GamePlay.apply _)
}

case class GamePlay(
                     game_id : Long,
                     round_number : Int,
                     player_id : Long,
                     turn_number : Int,
                     hand : String,
                     bet : String
                     )
