package models

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession

trait DBSupport {
  def connectDB[T](f: => T): T = {
    Database.forURL("jdbc:h2:tcp://localhost:9093/db", driver = "org.h2.Driver", user = "sa") withSession {
      f
    }
  }
}