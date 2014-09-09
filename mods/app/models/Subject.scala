package models

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession

case class Subject(subjectid: Int, name: String)

object Subjects extends Table[Subject]("SUBJECT") with DBSupport {

  def subjectid = column[Int]("SUBJECTID", O.PrimaryKey, O.NotNull)
  def name = column[String]("NAME", O.NotNull)

  def * = subjectid ~ name <> (Subject, Subject.unapply _)
  def ins = name returning subjectid

  def addSubject(name: String) = connectDB {
    Subjects.ins.insert(name)
  }

}