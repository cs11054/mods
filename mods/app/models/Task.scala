package models

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession
import java.util.Date
import util.Utilities
import java.io.File
import scala.io.Source

case class Task(subjectid: Int, userid: String, taskid: Int, caption: String, body: String, date: Long) {

  def formatDate(form: String = "yyyy/mm/dd hh:mm"): String = form match {
    case "yyyy/mm/dd hh:mm" => "%tY/%<tm/%<td %<tR" format new Date(date)
    case _ => date.toString()
  }

}

object Tasks extends Table[Task]("TASK") with DBSupport with Utilities {

  def subjectid = column[Int]("SUBJECTID", O.PrimaryKey, O.NotNull)
  def userid = column[String]("USERID", O.PrimaryKey, O.NotNull)
  def taskid = column[Int]("TASKID", O.PrimaryKey, O.NotNull)
  def caption = column[String]("CAPTION")
  def body = column[String]("BODY")
  def date = column[Long]("DATE", O.NotNull)

  def * = subjectid ~ userid ~ taskid ~ caption ~ body ~ date <> (Task, Task.unapply _)

  def ins = subjectid ~ userid ~ taskid ~ caption ~ body ~ date

  def all(): List[Task] = connectDB {
    Query(Tasks).sortBy(_.date).list
  }

  def tasksOfSbj(sid: Int): List[Task] = connectDB {
    Query(Tasks).filter(_.subjectid === sid).sortBy(_.date.desc).list
  }

  def getTasks(sid: Int, uid: String): List[Task] = connectDB {
    Query(Tasks).filter(t => t.subjectid === sid && t.userid === uid).sortBy(_.taskid).list
  }

  def getCaptionAndCodeLines(sid: Int, uid: String): List[(String, String)] = {
    getTasks(sid, uid).map(t => (t.caption, t.body))
  }

  def add(subjectid: Int, userid: String, caption: String, body: String): Int = connectDB {
    val nextid = Query(Tasks).filter(t => t.subjectid === subjectid && t.userid === userid).list.size + 1
    val date = System.currentTimeMillis()
    Tasks.ins.insert(subjectid, userid, nextid, caption, body, date)
    nextid
  }

  def postNTasks(id: String, limit: Int): List[Task] = connectDB {
    Query(Tasks).filter(t => t.userid === id).sortBy(_.date.desc).take(limit).list
  }

  def delete(subjectid: Int, userid: String, taskid: Int): Int = connectDB {
    Tasks.filter(t => t.subjectid === subjectid
      && t.userid === userid && t.taskid === taskid).delete
  }

}
    
