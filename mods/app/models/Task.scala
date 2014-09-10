import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession
import models.DBSupport

case class Task(subjectid: Int, userid: String, taskid: Int, date: Long)

object Tasks extends Table[Task]("TASK") with DBSupport {

  def subjectid = column[Int]("SUBJECTID", O.PrimaryKey, O.NotNull)
  def userid = column[String]("USERID", O.PrimaryKey, O.NotNull)
  def taskid = column[Int]("TASKID", O.PrimaryKey, O.NotNull, O.AutoInc)
  def date = column[Long]("DATE", O.NotNull)
  def * = subjectid ~ userid ~ taskid ~ date <> (Task, Task.unapply _)

  def ins = subjectid ~ userid ~ date returning taskid

  def all(): List[Task] = connectDB {
    Query(Tasks).sortBy(_.date).list
  }

  def add(subjectid: Int, userid: String, body: String) = connectDB {
    val date = System.currentTimeMillis()
    Tasks.ins.insert(subjectid, userid, date)
  }

  def delete(subjectid: Int, userid: String, taskid: Int) = connectDB {
    Tasks.filter(t => t.subjectid === subjectid
      && t.userid === userid && t.taskid === taskid).delete
  }

}
    
