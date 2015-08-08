package models

import org.squeryl.KeyedEntity
import org.squeryl.dsl.OneToMany

/**
 * Created by saheb on 8/4/15.
 */

case class Player(
                 name : String,
                 email : String,
                 games_played : Int,
                 games_won : Int,
                 rank : Int) extends KeyedEntity[Long]
{
  val player_id : Long = 0
  val id : Long = player_id
}

case class GamePlay(
                   game_id : Long,
                   round_number : Int,
                   player_id : Long,
                   turn_number : Int,
                   hand : String,
                   bet : String
                     )

case class PlayerStatus(
                       player_id : Long,
                       game_id : Long,
                       position : Int,
                       num_of_cards : Int,
                       status : String
                         )

case class RoundResult(
                      game_id : Long,
                      round_number : Int,
                      player_challenge_id : Long, // player id of the player who challenged the bet
                      player_bet_id : Long, // player id of the player whose bet has been challenged
                      bet_challenged : String,
                      result : String
                        )

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

case class LoginDetails(
                       player_id : Long,
                       password : String
                         )