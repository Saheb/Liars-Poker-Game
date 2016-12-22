//import play.api.{Application, GlobalSettings}
//
///**
//  * Created by saheb on 8/4/15.
//  */
//object Global extends GlobalSettings {
//
//  override def onStart(app: Application): Unit = {
//    SessionFactory.concreteFactory = Some(
//      () => Session.create(DB.getConnection()(app), new MySQLAdapter)
//    )
//    inTransaction {
//      Database.printDdl(println(_))
//    }
//  }
//}
