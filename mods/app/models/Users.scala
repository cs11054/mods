package models

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession

case class User(id: String, password: String)

object Users extends Table[User]("USER") with DBSupport {

  def id = column[String]("ID", O.PrimaryKey)
  def password = column[String]("PASSWORD", O.NotNull)
  def ins = password ~ id

  def * = id ~ password <> (User, User.unapply _)

  def isRegistered(id: String, password: String): Boolean = connectDB {
    Query(Users).list().exists(u => u.id == id && u.password == password)
  }

  def addUser(id: String, password: String) = connectDB {
    Users.ins.insert(id, password)
  }

}