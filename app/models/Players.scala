package models

import org.squeryl.Query
import org.squeryl.PrimitiveTypeMode._

/**
 * Created by saheb on 8/4/15.
 */
object Players {
  import models.Database.{playerTable}

  def playerQ : Query[Player] = from(playerTable){
    player => select(player) orderBy(player.name desc)
  }

  def getPlayerList : Iterable[Player] = inTransaction{
    playerQ.toList
  }

//  def insertPlayer(player : Player) : Player = inTransaction{
//    val defensiveCopy = player.copy() // Why???
//    playerTable.insert(defensiveCopy)
//  }
//
//  def updatePlayer(player : Player) = inTransaction{
//    playerTable.update(player)
//  }

}
