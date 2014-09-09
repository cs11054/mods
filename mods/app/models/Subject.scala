package models

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession

case class Subject(subjectid: Int, name: String)

object Subjects extends Table[Subject]("SUBJECT") with DBSupport {

  def subjectid = column[Int]("SUBJECTID", O.AutoInc, O.PrimaryKey, O.NotNull)
  def name = column[String]("NAME", O.NotNull)

  def * = subjectid ~ name <> (Subject, Subject.unapply _)
  def ins = name returning subjectid

  def add(name: String) = connectDB {
    Subjects.ins.insert(name)
  }

  def delete(id: Int) = connectDB {
    Subjects.filter(_.subjectid === id).delete
  }

  def all(): List[Subject] = connectDB {
    Query(Subjects).sortBy(_.subjectid).list
  }

}