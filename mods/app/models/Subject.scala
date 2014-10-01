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

  def add(name: String) = connectDB {
    Subjects.ins.insert(name)
  }

  def delete(id: Int) = connectDB {
    Subjects.filter(_.subjectid === id).delete
  }

  def all(): List[Subject] = connectDB{
    Query(Subjects).sortBy(_.subjectid.desc).list
  }

  // 一番新しい課題の番号を取得、ないなら-1を返す
  def newestNum(): Int = all().map(_.subjectid).headOption.getOrElse(-1)

}