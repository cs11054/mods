package models

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession
import scala.concurrent.SyncVar
import util.Utilities

case class Subject(subjectid: Int, name: String) {
  def toXML = <subjectid>{ subjectid }</subjectid>
              <name>{ name }</name>
}

object Subjects extends Table[Subject]("SUBJECT") with DBSupport with Utilities {

  def subjectid = column[Int]("SUBJECTID", O.AutoInc, O.PrimaryKey, O.NotNull)
  def name = column[String]("NAME", O.NotNull)
  def * = subjectid ~ name <> (Subject, Subject.unapply _)
  def ins = name returning subjectid

  val snameMap = scala.collection.concurrent.TrieMap.empty[Int, String]
  val SAVE_PATH = "/db/subjects.xml"

  def save() { writeXML(SAVE_PATH, all()) }

  def load() {
    val list = readXML(SAVE_PATH) { node =>
      val subjectid = (node \ "subjectid").text.toInt
      val name = (node \ "name").text
      Subject(subjectid, name)
    }
  }

  def add(name: String) = connectDB {
    Subjects.ins.insert(name)
  }

  def delete(id: Int) = connectDB {
    Subjects.filter(_.subjectid === id).delete
  }

  def all(): List[Subject] = connectDB {
    Query(Subjects).sortBy(_.subjectid.desc).list
  }

  def getTitle(sid: Int): String = snameMap.get(sid) match {
    case Some(x) => x
    case None =>
      getTitleFromDB(sid) match {
        case Some(x) =>
          snameMap.put(sid, x)
          x
        case None => sid.toString()
      }
  }

  private[this] def getTitleFromDB(sid: Int): Option[String] = connectDB {
    Query(Subjects).filter(_.subjectid === sid).map(_.name).firstOption
  }

  // 一番新しい課題の番号を取得、ないなら-1を返す
  def newestNum(): Int = all().map(_.subjectid).headOption.getOrElse(-1)

}