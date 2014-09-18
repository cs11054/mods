package models

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession

case class Comment(subjectid: Int, userid: String, taskid: Int, commentid: Int, body: String, date: Long, isNew: Boolean)

object Comments extends Table[Comment]("COMMENT") with DBSupport {

  def subjectid = column[Int]("SUBJECTID", O.PrimaryKey, O.NotNull)
  def userid = column[String]("USERID", O.PrimaryKey, O.NotNull)
  def taskid = column[Int]("TASKID", O.PrimaryKey, O.NotNull)
  def commentid = column[Int]("COMMENTID", O.PrimaryKey, O.NotNull)
  def body = column[String]("BODY")
  def date = column[Long]("DATE", O.NotNull)
  def isNew = column[Boolean]("NEW", O.NotNull)
  def * = subjectid ~ userid ~ taskid ~ commentid ~ body ~ date ~ isNew <> (Comment, Comment.unapply _)

  def ins = subjectid ~ userid ~ taskid ~ body ~ date ~ isNew returning commentid

  def all(): List[Comment] = connectDB {
    Query(Comments).sortBy(_.date).list
  }

  def add(subjectid: Int, userid: String, taskid: Int, body: String) = connectDB {
    val date = System.currentTimeMillis()
    Comments.ins.insert(subjectid, userid, taskid, body, date, true)
  }

  def delete(subjectid: Int, userid: String, taskid: Int, commentID: Int) = connectDB {
    Comments.filter(c => c.subjectid === subjectid && c.userid === userid &&
      c.taskid === taskid && c.commentid === commentid).delete
  }

}