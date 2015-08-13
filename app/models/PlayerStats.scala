package models

/**
 * Created by saheb on 8/14/15.
 */
object PlayerStats
case class PlayerStats(
                        player_id : Long,
                        games_played : Int,
                        games_won : Int,
                        rank : Int
                        )
