package controllers

import com.google.inject.Inject
import models.Dao
import play.api.Configuration
import play.api.mvc.{Action, Controller}
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

/**
  * Created by saheb on 9/12/15.
  */
class Application @Inject()(config: Configuration) extends Controller {

  val dbConfig =
    DatabaseConfig.forConfig[JdbcProfile](config.getString("h2").get)
  val db = dbConfig.db
  val dal = new Dao(dbConfig.driver)

  def home = Action {
    Ok(views.html.home())
  }

}
