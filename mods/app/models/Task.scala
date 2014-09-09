package models

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession

case class Task(subjectid: Int, userid: String, taskid: Int, path: String, date: String)

object Tasks extends Table[Task]("TASK") {

  def subjectid = column[Int]("SUBJECTID", O.PrimaryKey, O.NotNull)
  def userid = column[String]("USERID", O.PrimaryKey, O.NotNull)
  def taskid = column[Int]("TASKID", O.PrimaryKey, O.NotNull, O.AutoInc)
  def path = column[String]("PATH", O.NotNull)
  def date = column[String]("DATE", O.NotNull)
  def * = subjectid ~ userid ~ taskid ~ path ~ date <> (Task, Task.unapply _)

}