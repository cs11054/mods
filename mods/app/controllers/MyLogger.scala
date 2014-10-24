package controllers

import scala.concurrent._
import ExecutionContext.Implicits.global

object MyLogger {

  val PATH = s"./logs/${System.currentTimeMillis()}log.txt"

  private val logger = new java.io.PrintWriter(PATH)

  // 文字列を受け取ったらロギングを別のスレッドにまかせてすぐ帰る
  def log(str: String) {
    // 実際にログを取る関数、排他制御
    def prvLog(str: String) {
      synchronized {
        println(str)
        logger.println(str)
      }
    }
    future { prvLog(str) }
  }

  def fin() {
    logger.close()
  }

}