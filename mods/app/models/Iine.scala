package models

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession
import java.util.Date

case class Iine(subjectid: Int, userid: String, pushUser: String, date: Long, isNew: Boolean) {

  def formatDate(form: String = "yyyy/mm/dd hh:mm"): String = form match {
    case "yyyy/mm/dd hh:mm" => "%tY/%<tm/%<td %<tR" format new Date(date)
    case _ => date.toString()
  }

}

object Iines extends Table[Iine]("IINE") with DBSupport {

  def subjectid = column[Int]("SUBJECTID", O.PrimaryKey, O.NotNull)
  def userid = column[String]("USERID", O.PrimaryKey, O.NotNull)
  def pushUser = column[String]("PUSHUSER", O.PrimaryKey, O.NotNull)
  def date = column[Long]("DATE", O.NotNull)
  def isNew = column[Boolean]("NEW", O.NotNull)
  def * = subjectid ~ userid ~ pushUser ~ date ~ isNew <> (Iine, Iine.unapply _)

  def ins = subjectid ~ userid ~ pushUser ~ date ~ isNew

  def all(): List[Iine] = ???

  def iineOfTask(sid: Int, uid: String): List[Iine] = connectDB {
    Query(Iines).filter(i => i.subjectid === sid && i.userid === uid).sortBy(_.date).list
  }

  def countIine(sid: Int, uid: String): Int = iineOfTask(sid, uid).size

  def add(sid: Int, uid: String, pushUser: String) = connectDB {
    val date = System.currentTimeMillis()
    val iines = iineOfTask(sid, uid)
    val IINEed = Query(Iines).filter(i => i.subjectid === sid && i.userid === uid && i.pushUser === pushUser).list.isEmpty
    if (IINEed) Iines.ins.insert(sid, uid, pushUser, date, true)
  }

  def recvNewIines(id: String, limit: Int): List[Iine] = connectDB {
    val ret = Query(Iines).filter(i => i.userid === id && i.pushUser =!= id && i.isNew).sortBy(_.date.desc).list
    Query(Iines).filter(i => i.userid === id && i.isNew).map(_.isNew).update(false)
    ret
  }

  def delete() = ???

}