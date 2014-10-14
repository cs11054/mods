package models

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession
import util.Utilities

case class User(id: String, password: String) {
  def toXML = <id>{ id }</id>
              <password>{ password }</password>
}

object Users extends Table[User]("USER") with DBSupport with Utilities {

  def id = column[String]("ID", O.PrimaryKey)
  def password = column[String]("PASSWORD", O.NotNull)
  def ins = id ~ password

  def * = id ~ password <> (User, User.unapply _)
  val ANONY = "<?>"
  val SAVE_PATH = "/db/users.xml"

  def save() { writeXML(SAVE_PATH, all()) }

  def load() {
    val list = readXML(SAVE_PATH) { node =>
      val id = (node \ "id").text
      val password = (node \ "password").text
      User(id, password)
    }
  }

  def isRegistered(id: String, password: String): Boolean = connectDB {
    Query(Users).list().exists(u => u.id == id)
  }

  def add(id: String, password: String) = connectDB {
    if (!isRegistered(id, password)) Users.ins.insert(id, password)
    else 0
  }

  def getName(id: String): String = if (!id.startsWith(ANONY)) id else FamillyNames.anony2famname(id)

  def delete(id: String) = connectDB {
    if (id != "cs11054") Users.filter(_.id === id).delete
    else 0
  }

  def all(): List[User] = connectDB {
    Query(Users).sortBy(_.id).list
  }

}