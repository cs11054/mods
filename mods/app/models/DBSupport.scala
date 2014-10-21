package models

import scala.slick.driver.H2Driver.simple._
import Database.ThreadLocal
import scala.slick.driver.PostgresDriver.simple._

import play.Play

trait DBSupport {

  val Heroku = false //true // or false

  val DB_PATH = if (!Heroku) "jdbc:h2:tcp://localhost:9093/db"
  else "postgres://zcgmsoybywazlx:HQt5HBNiYxgDE4LdQ337Ry_urb@ec2-54-83-204-78.compute-1.amazonaws.com:5432/dcn5isfl4oitng"

  val DRIVER = if (DB_PATH.contains("localhost")) "org.h2.Driver" else "org.postgresql.Driver"
  val USER = if (DB_PATH.contains("localhost")) "sa" else "zcgmsoybywazlx"
  val PASS = if (DB_PATH.contains("localhost")) null else "HQt5HBNiYxgDE4LdQ337Ry_urb"

  val DB = if (DB_PATH.contains("localhost")) scala.slick.driver.H2Driver.simple.Database
  else scala.slick.driver.PostgresDriver.simple.Database

  println(DRIVER, USER, PASS)

  // ソートの逆順用
  def Desc[T: Ordering] = implicitly[Ordering[T]].reverse

  // DBへの接続を補助
  def connectDB[T](f: => T): T = {
    DB.forURL(DB_PATH, driver = DRIVER, user = USER, password = PASS) withSession {
      f
    }
  }

}