package models

import play.api.libs.json.{JsPath, Reads, Json, Writes}
import play.api.libs.functional.syntax._
/**
 * Created by saheb on 8/14/15.
 */
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
