package models

import org.squeryl.Schema
import org.squeryl.PrimitiveTypeMode._

/**
 * Created by saheb on 8/4/15.
 */
object Database extends Schema{

  val playerTable = table[Player]("player")
  val gamePlayTable = table[GamePLay]("game_play")
  val playerStatusTable = table[PlayerStatus]("player_status")
  val gameStatusTable = table[GameStatus]("game_status")
  val roundResultTable = table[RoundResult]("round_result")
  val loginDetailsTable = table[LoginDetails]("login_details")

  on(playerTable) {
    p => declare{
      p.player_id is(autoIncremented)
    }
  }

  on(gameStatusTable){
    g => declare {
      g.game_id is(autoIncremented)
    }
  }
}
