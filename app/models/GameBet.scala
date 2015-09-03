package models

import play.api.libs.json.{JsPath, Reads, Json, Writes}
import play.api.libs.functional.syntax._

/**
 * Created by saheb on 8/14/15.
 */

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
