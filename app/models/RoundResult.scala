package models

/**
 * Created by saheb on 8/14/15.
 */
object RoundResult
case class RoundResult(
                        game_id : Long,
                        round_number : Int,
                        player_challenge_id : Long, // player id of the player who challenged the bet
                        player_bet_id : Long, // player id of the player whose bet has been challenged
                        bet_challenged : String,
                        result : String
                        )
