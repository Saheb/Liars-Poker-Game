import models.Database
import org.squeryl.adapters.H2Adapter
import org.squeryl.{Session, SessionFactory}
import play.api.db.DB
import play.api.{Application, GlobalSettings}
import org.squeryl.PrimitiveTypeMode._
/**
 * Created by saheb on 8/4/15.
 */

object Global extends GlobalSettings{

  override def onStart(app : Application): Unit = {
      SessionFactory.concreteFactory = Some(
        () => Session.create(DB.getConnection()(app), new H2Adapter)
      )
      inTransaction {
        //Database.create
      }
  }
}
