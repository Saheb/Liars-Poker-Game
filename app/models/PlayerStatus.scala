package models
/**
 * Created by saheb on 8/14/15.
 */
object PlayerStatus
case class PlayerStatus(
                         player_id : Long,
                         game_id : Long,
                         position : Int,
                         num_of_cards : Int,
                         status : String
                         )