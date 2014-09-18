package models

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession
import java.util.Date

case class Task(subjectid: Int, userid: String, taskid: Int, body: String, date: Long) {

  def formatDate(form: String = "yyyy/mm/dd hh:mm"): String = form match {
    case "yyyy/mm/dd hh:mm" => "%tY/%<tm/%<td %<tR" format new Date(date)
    case _ => date.toString()
  }

}

object Tasks extends Table[Task]("TASK") with DBSupport {

  def subjectid = column[Int]("SUBJECTID", O.PrimaryKey, O.NotNull)
  def userid = column[String]("USERID", O.PrimaryKey, O.NotNull)
  def taskid = column[Int]("TASKID", O.PrimaryKey, O.NotNull, O.AutoInc)
  def body = column[String]("BODY")
  def date = column[Long]("DATE", O.NotNull)

  def * = subjectid ~ userid ~ taskid ~ body ~ date <> (Task, Task.unapply _)

  def ins = subjectid ~ userid ~ body ~ date returning taskid

  def all(): List[Task] = connectDB {
    Query(Tasks).sortBy(_.date).list
  }

  def sbjAll(sid: Int): List[Task] = connectDB {
    Query(Tasks).filter(_.subjectid === sid).list
  }

  def add(subjectid: Int, userid: String, body: String) = connectDB {
    val date = System.currentTimeMillis()
    Tasks.ins.insert(subjectid, userid, body, date)
  }

  def delete(subjectid: Int, userid: String, taskid: Int) = connectDB {
    Tasks.filter(t => t.subjectid === subjectid
      && t.userid === userid && t.taskid === taskid).delete
  }

}
    
