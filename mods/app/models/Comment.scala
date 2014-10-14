package models

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession
import java.util.Date

case class Comment(subjectid: Int, userid: String, commentid: Int, postUser: String, body: String, date: Long, isNew: Boolean) {

  def formatDate(form: String = "yyyy/mm/dd hh:mm"): String = form match {
    case "yyyy/mm/dd hh:mm" => "%tY/%<tm/%<td %<tR" format new Date(date)
    case _ => date.toString()
  }

}

object Comments extends Table[Comment]("COMMENT") with DBSupport {

  def subjectid = column[Int]("SUBJECTID", O.PrimaryKey, O.NotNull)
  def userid = column[String]("USERID", O.PrimaryKey, O.NotNull)
  def commentid = column[Int]("COMMENTID", O.PrimaryKey, O.NotNull)
  def postUser = column[String]("POSTUSER", O.NotNull)
  def body = column[String]("BODY")
  def date = column[Long]("DATE", O.NotNull)
  def isNew = column[Boolean]("NEW", O.NotNull)
  def * = subjectid ~ userid ~ commentid ~ postUser ~ body ~ date ~ isNew <> (Comment, Comment.unapply _)

  def ins = subjectid ~ userid ~ commentid ~ postUser ~ body ~ date ~ isNew

  def all(): List[Comment] = connectDB {
    Query(Comments).sortBy(_.date).list
  }

  def commentsOfTask(sid: Int, uid: String): List[Comment] = connectDB {
    Query(Comments).filter(c => c.subjectid === sid && c.userid === uid)
      .sortBy(_.date.desc).list
  }

  def countComment(sid: Int, uid: String): Int = commentsOfTask(sid, uid).size

  def add(subjectid: Int, userid: String, postUser: String, body: String) = connectDB {
    val nextid = Query(Comments).filter(t => t.subjectid === subjectid && t.userid === userid).list.size + 1
    val date = System.currentTimeMillis()
    Comments.ins.insert(subjectid, userid, nextid, postUser, body, date, userid != postUser)
    nextid
  }

  def delete(subjectid: Int, userid: String, commentID: Int) = connectDB {
    Comments.filter(c => c.subjectid === subjectid && c.userid === userid &&
      c.commentid === commentid).delete
  }

  def recvNewsAndRecentUntilN(id: String, n: Int): List[Comment] = connectDB {
    val news = Query(Comments).filter(c => c.userid === id && c.isNew && c.postUser =!= id).sortBy(_.date.desc).list
    val limit = if (n >= news.size) n - news.size else 0
    val ret = news ::: Query(Comments).filter(c => c.userid === id && c.postUser =!= id && !c.isNew).sortBy(_.date.desc).take(limit).list
    Query(Comments).filter(c => c.userid === id && c.isNew).map(_.isNew).update(false)
    ret
  }

  def newComments(id: String): List[Comment] = connectDB {
    val ret = Query(Comments).filter(c => c.userid === id && c.isNew && c.postUser =!= id).sortBy(_.date.desc).list
    Query(Comments).filter(c => c.userid === id && c.isNew).map(_.isNew).update(false)
    ret
  }

  def recvNComments(id: String, limit: Int): List[Comment] = connectDB {
    Query(Comments).filter(c => c.userid === id && c.postUser =!= id).sortBy(_.date.desc).take(limit).list
  }

  def postNComments(id: String, limit: Int): List[Comment] = connectDB {
    Query(Comments).filter(c => c.postUser === id).sortBy(_.date.desc).take(limit).list
  }

}