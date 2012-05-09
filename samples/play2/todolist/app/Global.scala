import db.TodoSchema
import play.api.GlobalSettings
import org.squeryl.SessionFactory
import org.squeryl.Session
import org.squeryl.adapters.H2Adapter
import org.squeryl.PrimitiveTypeMode._
import play.api.db.DB
import play.api.Application
import models.Todo
import org.joda.time.DateTime

object Global extends GlobalSettings {
  override def onStart(app: Application) {
    SessionFactory.concreteFactory = Some(() =>
      Session.create(DB.getConnection()(app), new H2Adapter))
    
    inTransaction {
      TodoSchema.create
    }
    
    // Load sample Todo's
    inTransaction {
      Todo.insert(Todo("Create sample app for swaggerkit-play2", new DateTime(2012, 5, 5, 14, 9), true))
      Todo.insert(Todo("Write more documentation", (new DateTime()).plusHours(4), false))
      Todo.insert(Todo("Fix bugs in swaggerkit-play", (new DateTime()).plusDays(1), false))
    }
  }
}