package models

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession

case class User(id: String, password: String)

object Users extends Table[User]("USER") with DBSupport {

  def id = column[String]("ID", O.PrimaryKey)
  def password = column[String]("PASSWORD", O.NotNull)
  def ins = id ~ password

  def * = id ~ password <> (User, User.unapply _)
  val ANONY = "<?>"

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