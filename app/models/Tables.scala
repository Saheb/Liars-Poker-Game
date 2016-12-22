package models

import models.Dao._
import slick.driver.JdbcProfile

/**
  * Created by sahebmotiani on 22/12/2016.
  */
trait DriverComponent {
  val driver: JdbcProfile
}

trait PlayerDao { self: DriverComponent =>

  import driver.api._

  val playerTable = TableQuery[PlayerTable]

  def getPlayers =
    playerTable.sortBy(_.name).result

  def getPlayer(playerId: Long) =
    playerTable.filter(_.id === playerId).result.headOption

  class PlayerTable(tag: Tag) extends Table[Player](tag, "player") {

    def * = (id, name, email) <> ((Player.apply _).tupled, Player.unapply _)

    def id = column[Long]("player_id", O.PrimaryKey)

    def name = column[String]("name")

    def email = column[String]("email")
  }
}

trait PlayerStatDao { self: DriverComponent =>

  import driver.api._

  val playerStatsTable = TableQuery[PlayerStatsTable]

  class PlayerStatsTable(tag: Tag)
      extends Table[PlayerStats](tag, "player_stat") {
    def * =
      (playerId, gamesPlayed, gamesWon, rank) <> ((PlayerStats.apply _).tupled, PlayerStats.unapply _)

    def playerId = column[Long]("player_id", O.PrimaryKey)

    def gamesPlayed = column[Int]("games_played")

    def gamesWon = column[Int]("games_won")

    def rank = column[Int]("rank")
  }
}

trait RoundResultDao { self: DriverComponent =>

  import driver.api._

  val roundResultTable = TableQuery[RoundResultTable]

  class RoundResultTable(tag: Tag)
      extends Table[RoundResult](tag, "round_result") {
    def * =
      (gameId,
       roundNumber,
       playerChallengeId,
       playerBetId,
       betChallenged,
       result) <> ((RoundResult.apply _).tupled, RoundResult.unapply _)

    def gameId = column[Long]("game_id")

    def roundNumber = column[Int]("round_number")

    def playerChallengeId = column[Long]("player_challenge_id")

    def playerBetId = column[Long]("player_bet_id")

    def betChallenged = column[String]("bet_challenged")

    def result = column[String]("result")
  }
}

trait GameHandDao { self: DriverComponent =>

  import driver.api._

  val gameHandTable = TableQuery[GameHandTable]

  class GameHandTable(tag: Tag) extends Table[GameHand](tag, "game_hand") {
    def * =
      (gameId, roundNumber, playerId, hand) <> ((GameHand.apply _).tupled, GameHand.unapply _)

    def gameId = column[Long]("game_id")

    def roundNumber = column[Int]("round_number")

    def playerId = column[Long]("player_id")

    def hand = column[String]("hand")
  }
}

trait GameBetDao { self: DriverComponent =>

  import driver.api._

  val gameBetTable = TableQuery[GameBetTable]

  class GameBetTable(tag: Tag) extends Table[GameBet](tag, "game_bet") {
    def * =
      (gameId, roundNumber, playerId, turnNumber, bet) <> ((GameBet.apply _).tupled, GameBet.unapply _)

    def gameId = column[Long]("game_id")

    def roundNumber = column[Int]("round_number")

    def playerId = column[Long]("player_id")

    def turnNumber = column[Int]("turn_number")

    def bet = column[Int]("bet")
  }
}

trait GameStatusDao { self: DriverComponent =>

  import driver.api._

  val gameStatusTable = TableQuery[GameStatusTable]

  def getGameStatus(gameId: Long) =
    gameStatusTable.filter(_.id === gameId).result.headOption

  def getAllGames =
    gameStatusTable.result

  class GameStatusTable(tag: Tag)
      extends Table[GameStatus](tag, "game_status") {
    def * =
      (id, name, adminPlayer, joinedPlayers, maxPlayers, winnerPlayer, status) <> ((GameStatus.apply _).tupled, GameStatus.unapply _)

    def id = column[Long]("game_id", O.PrimaryKey)

    def name = column[String]("name")

    def adminPlayer = column[Long]("admin_player")

    def joinedPlayers = column[Int]("joined_players")

    def maxPlayers = column[Int]("max_players")

    def winnerPlayer = column[Long]("winner_player")

    def status = column[Int]("status")
  }

}

trait PlayerStatusDao { self: DriverComponent =>

  import driver.api._

  val playerStatusTable = TableQuery[PlayerStatusTable]

  def getPlayerStatus(playerId: Long) =
    playerStatusTable.filter(_.id === playerId).result.headOption

  def getPlayerStatusForGame(playerId: Long, gameId: String) =
    playerStatusTable
      .filter(_.id === playerId)
      .filter(record => record.gameId === gameId)
      .result
      .headOption

  class PlayerStatusTable(tag: Tag)
      extends Table[PlayerStatus](tag, "player_status") {
    def * =
      (id, name, gameId, position, numOfCards, status) <> ((PlayerStatus.apply _).tupled, PlayerStatus.unapply _)

    def id = column[Long]("player_id", O.PrimaryKey)

    def name = column[String]("name")

    def gameId = column[Long]("game_id")

    def position = column[Int]("position")

    def numOfCards = column[Int]("num_of_cards")

    def status = column[String]("status")
  }
}
