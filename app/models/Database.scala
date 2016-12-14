package models

import models.SquerylEntryPoint._
import org.squeryl.Schema

/**
 * Created by saheb on 8/4/15.
 */
object Database extends Schema{

  val playerTable = table[Player]("player")
  val playerStats = table[PlayerStats]("player_stats")
  val gameBetTable = table[GameBet]("game_bet")
  val playerStatusTable = table[PlayerStatus]("player_status")
  val gameStatusTable = table[GameStatus]("game_status")
  val roundResultTable = table[RoundResult]("round_result")
  val gameHandTable = table[GameHand]("game_hand")

  on(playerTable) {
    p => declare{
      p.id is autoIncremented
      p.email is(unique)
    }
  }

  on(gameStatusTable){
    g => declare {
      g.id is autoIncremented
    }
  }


//  val gameStatus2GamePlay =
//      oneToManyRelation(gameStatusTable, gamePlayTable).via((a,b) => a.id === b.game_id)
//
//  val player2GamePlay =
//      oneToManyRelation(playerTable, gamePlayTable).via((a,b) => a.id === b.player_id)
//
//  val gameStatus2RoundResult =
//      oneToManyRelation(gameStatusTable, roundResultTable).via((a,b) => a.id  === b.game_id)
//
  val player2PlayerStatus =
      oneToManyRelation(playerTable, playerStatusTable).via((a,b) => a.id === b.player_id)

  val gameStatus2PlayerStatus =
      oneToManyRelation(gameStatusTable, playerStatusTable).via((a,b) => a.id === b.game_id)
}
