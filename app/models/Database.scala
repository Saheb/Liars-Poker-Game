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

  val gameStatus2GamePlay =
      oneToManyRelation(gameStatusTable, gamePlayTable).via((a,b) => a.id === b.game_id)

  val player2GamePlay =
      oneToManyRelation(playerTable, gamePlayTable).via((a,b) => a.id === b.player_id)

  val gameStatus2RoundResult =
      oneToManyRelation(gameStatusTable, roundResultTable).via((a,b) => a.id  === b.game_id)

  val player2PlayerStatus =
      oneToManyRelation(playerTable, playerStatusTable).via((a,b) => a.id === b.player_id)

  val gameStatus2PlayerStatus =
      oneToManyRelation(gameStatusTable, playerStatusTable).via((a,b) => a.id === b.game_id)
}
