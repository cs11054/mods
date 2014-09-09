package models

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession

case class Comment(id: Int, userid: String, taskid: Int, commentid: Int, body: String, date: String)

object Comments extends Table[Comment]("COMMENT") with DBSupport {

  def subjectid = column[Int]("SUBJECTID", O.PrimaryKey, O.NotNull)
  def userid = column[String]("USERID", O.PrimaryKey, O.NotNull)
  def taskid = column[Int]("TASKID", O.PrimaryKey, O.NotNull)
  def commentid = column[Int]("COMMENTID", O.PrimaryKey, O.NotNull, O.AutoInc)
  def body = column[String]("BODY")
  def date = column[String]("DATE", O.NotNull)
  def * = subjectid ~ userid ~ taskid ~ commentid ~ body ~ date <> (Comment, Comment.unapply _)

}
   