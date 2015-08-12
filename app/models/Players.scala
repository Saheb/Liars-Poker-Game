package models

import org.squeryl.Query
import org.squeryl.PrimitiveTypeMode._
import play.api.libs.functional.syntax._
import play.api.libs.json.{Writes, JsPath, Reads,Json}

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

  implicit object PlayerWrites extends Writes[Player]{
    def writes(p : Player) = Json.obj(
     "player_id" -> Json.toJson(p.player_id),
      "name" -> Json.toJson(p.name),
      "email" -> Json.toJson(p.email),
      "games_played" -> Json.toJson(p.games_played),
      "games_won" -> Json.toJson(p.games_won),
      "rank" -> Json.toJson(p.rank)
    )
  }
  implicit val playerReads : Reads[Player] = (
    (JsPath \ "player_id").read[Long] and
      (JsPath \ "name").read[String] and
      (JsPath \ "email").read[String] and
      (JsPath \ "games_won").read[Int] and
      (JsPath \ "games_played").read[Int] and
      (JsPath \ "rank").read[Int]
  )(Player.apply _)
}
