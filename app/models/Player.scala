package models

import org.squeryl.{Optimistic, KeyedEntity, Query}
import org.squeryl.PrimitiveTypeMode._
import play.api.libs.functional.syntax._
import play.api.libs.json.{Writes, JsPath, Reads,Json}
import org.squeryl.annotations.Column
/**
 * Created by saheb on 8/4/15.
 */
object Player {
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
      "name" -> Json.toJson(p.name),
      "email" -> Json.toJson(p.email)
    )
  }

  implicit val playerReads : Reads[Player] = (
      (JsPath \ "id").read[Long] and
      (JsPath \ "name").read[String] and
      (JsPath \ "email").read[String]
  )(Player.apply _)
}

case class Player( @Column("player_id")
                   id : Long,
                   name : String,
                   email : String) extends KeyedEntity[Long]
{
  def player_id : Long = id
}