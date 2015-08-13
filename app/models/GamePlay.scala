package models

/**
 * Created by saheb on 8/14/15.
 */
object GamePlay {

}

case class GamePlay(
                     game_id : Long,
                     round_number : Int,
                     player_id : Long,
                     turn_number : Int,
                     hand : String,
                     bet : String
                     )
