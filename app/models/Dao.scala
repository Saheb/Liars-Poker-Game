package models

import com.google.inject.Inject
import models.Dao.{GameStatus, Player, PlayerStatus}
import play.api.Configuration
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

/**
  * Created by sahebmotiani on 22/12/2016.
  */
object Dao {

  case class Player(id: Long, name: String, email: String)

  case class PlayerStats(
      player_id: Long,
      games_played: Int,
      games_won: Int,
      rank: Int
  )

  case class RoundResult(
      game_id: Long,
      round_number: Int,
      player_challenge_id: Long, // player id of the player who challenged the bet
      player_bet_id: Long, // player id of the player whose bet has been challenged
      bet_challenged: String,
      result: String
  )

  case class GameHand(
      game_id: Long,
      round_number: Int,
      player_id: Long,
      hand: String
  )

  case class GameBet(
      game_id: Long,
      round_number: Int,
      player_id: Long,
      turn_number: Int,
      bet: String
  )

  case class GameStatus(
      id: Long,
      name: String,
      admin_player: Long,
      joined_players: Int,
      max_players: Int,
      winner_player: Long,
      status: Int
  )

  case class PlayerStatus(
      id: Long,
      name: String,
      game_id: Long,
      position: Int,
      num_of_cards: Int,
      status: String
  )

}

class Dao @Inject()(val driver: JdbcProfile, config: Configuration)
    extends PlayerDao
    with PlayerStatDao
    with PlayerStatusDao
    with GameBetDao
    with GameStatusDao
    with GameHandDao
    with RoundResultDao
    with DriverComponent {

  import driver.api._

  val db =
    DatabaseConfig.forConfig[JdbcProfile](config.getString("dbConfig").get).db

  def upsert(player: Player) =
    db.run(playerTable.insertOrUpdate(player))

  def getPlayers =
    db.run(playerTable.sortBy(_.name).result)

  def getPlayer(playerId: Long) =
    db.run(playerTable.filter(_.id === playerId).result.headOption)

  def getJoinedPlayerList(gameId: Long) =
    db.run(playerStatusTable.join(playerTable).on(_.id === _.id).result)

  def getPlayerStatus(playerId: Long) =
    db.run(playerStatusTable.filter(_.id === playerId).result.headOption)

  def getPlayerStatusForGame(playerId: Long, gameId: String) =
    db.run(
      playerStatusTable
        .filter(_.id === playerId)
        .filter(record => record.gameId === gameId)
        .result
        .headOption)

  def insert(ps: PlayerStatus) =
    db.run(playerStatusTable += ps)

  def insert(gs: GameStatus) =
    db.run(gameStatusTable += gs)

  def getGameStatus(gameId: Long) =
    db.run(gameStatusTable.filter(_.id === gameId).result.headOption)

  def getAllGames =
    db.run(gameStatusTable.result)

}
