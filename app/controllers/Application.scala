package controllers

import play.api.mvc.{Action, Controller}

/**
 * Created by saheb on 9/12/15.
 */
class Application extends Controller {

  def home = Action {
    Ok(views.html.home())
  }

}