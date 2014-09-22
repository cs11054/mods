package models

import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession
import scala.concurrent.SyncVar

case class Subject(subjectid: Int, name: String)

object Subjects extends Table[Subject]("SUBJECT") with DBSupport {

  def subjectid = column[Int]("SUBJECTID", O.AutoInc, O.PrimaryKey, O.NotNull)
  def name = column[String]("NAME", O.NotNull)

  def * = subjectid ~ name <> (Subject, Subject.unapply _)
  def ins = name returning subjectid

  private[this] val allCache = new SyncVar[List[Subject]] { put(allHelper) }

  private[this] def allHelper: List[Subject] = connectDB {
    Query(Subjects).sortBy(_.subjectid).list
  }

  def add(name: String) = connectDB {
    val n = Subjects.ins.insert(name)
    allCache.put(allHelper)
    n
  }

  def delete(id: Int) = connectDB {
    val n = Subjects.filter(_.subjectid === id).delete
    allCache.put(allHelper)
    n
  }

  def all(): List[Subject] = allCache.get

  // 一番新しい課題の番号を取得、ないなら-1を返す
  def newestNum(): Int = allCache.get.map(_.subjectid).sorted.headOption.getOrElse(-1)

}