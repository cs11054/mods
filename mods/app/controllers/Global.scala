import play.api._
import controllers.MyLogger
import controllers.Manage

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    println("システムスタート")
  }

  override def onStop(app: Application) {
    Manage.backup()
    println("システム終了")
  }

}