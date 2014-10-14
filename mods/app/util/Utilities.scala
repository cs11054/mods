package util

import scala.io.Source
import java.io.PrintWriter
import java.io.File
import scala.xml.XML

trait Utilities {
  def using[A <: { def close() }, B](resource: A)(func: A => B): Option[B] =
    try {
      Some(func(resource)) //成功したら、Someに包んで返す
    } catch {
      case e: Exception =>
        e.printStackTrace
        None //失敗したら、ログ吐いて、None返す
    } finally {
      if (resource != null) resource.close()
    }

  // データをXML形式でシリアライズ
  def readXML[E](path: String)(fromXML: scala.xml.Node => E): List[E] = if (new File(path) exists) {
    val items = XML.loadString(using(Source.fromFile(path, "UTF-8")) { _.getLines.mkString }.getOrElse("")) \\ "item"
    items.map(fromXML(_)).toList
  } else {
    List.empty
  }

  // データをXML形式でデシリアライズ
  def writeXML[E <: { def toXML: scala.xml.NodeBuffer }](path: String, list: List[E]) = using(new PrintWriter(path, "UTF-8")) {
    _.write {
      (<root>
         <items>
           { for (item <- list) yield <item>{ item.toXML }</item> }
         </items>
       </root>).toString()
    }
  }

}