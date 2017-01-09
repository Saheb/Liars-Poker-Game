package controllers

import java.util.concurrent.ExecutionException

import com.google.inject.Inject
import models.Dao
import models.Dao._
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by saheb on 8/12/15.
  */
class LoginController @Inject()(
    dao: Dao
) extends Controller {

  val logger = Logger(this.getClass)

  def persistLoginInfo = Action(parse.json) { request =>
    val playerJson = request.body
    val player = playerJson.as[Player]
    logger.info("Inserting record to database")
    val p = Await.result(dao.upsert(player), Duration.Inf)
    Ok(Json.toJson(p))
  }

}
