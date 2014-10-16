package controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import play.api.data.validation.Constraints._
import models.User
import models.Subject
import models.Users
import models.Subjects
import models.Tasks
import models.Iines
import models.Comments
import models.FamillyNames

object Manage extends Controller with myAuth {

  // Manage	///////////////////////////////////////////////////
  def manage(msg: String = "") = Administor { implicit request =>
    Ok(views.html.manage(msg))
  }

  // DBのデータ保存
  private def save(path: String) {
    println("DBのデータを保存しました")
    Users.save(path)
    Tasks.save(path)
    Subjects.save(path)
    Iines.save(path)
    Comments.save(path)
    FamillyNames.save(path)
  }

  // DBのデータ読み込み
  private def load(path: String) {
    println("DBのデータを読み込みました")
    Users.load(path)
    Tasks.load(path)
    Subjects.load(path)
    Iines.load(path)
    Comments.load(path)
    FamillyNames.load(path)
  }
  // DBのデータを消す
  private def del() {
    println("DBのデータを削除しました")
    Users.allDel
    Tasks.allDel
    Subjects.allDel
    Iines.allDel
    Comments.allDel
    FamillyNames.allDel
  }

  val SAVE_PATH = "/db/"
  def managed(kind: String) = Administor { implicit request =>
    kind match {
      case "saveDB" =>
        save(SAVE_PATH)
        Ok(views.html.manage("DBのデータを保存しました。"))

      case "loadDB" =>
        load(SAVE_PATH)
        Ok(views.html.manage("DBのデータを読み込みました。"))

      case "delDB" =>
        save(SAVE_PATH + "/bk/")
        del()
        Ok(views.html.manage("DBのデータを削除しました。"))

      case "loadDemo" =>
        load(SAVE_PATH + "/demo/")
        Ok(views.html.manage("デモデータを読み込みました。"))

      case "addUser" =>
        Form(tuple("userId" -> nonEmptyText, "userPassword" -> nonEmptyText)).bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.manage("ユーザーの登録に失敗しました")),
          user => {
            if (Users.add(user._1, user._2) != 0) Ok(views.html.manage(s"ユーザー[ ${user._1} ]を追加しました"))
            else BadRequest(views.html.manage("ユーザーの登録に失敗しました"))
          })

      case "addSubject" =>
        Form("subjectName" -> nonEmptyText).bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.manage("課題の登録に失敗しました")),
          sbj => {
            Subjects.add(sbj)
            Ok(views.html.manage(s"課題[ $sbj.name ]を追加しました"))
          })

      case "deleteUser" =>
        Form("dUserId" -> nonEmptyText).bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.manage("ユーザーの削除に失敗しました")),
          user => {
            if (Users.delete(user) != 0) Ok(views.html.manage(s"ユーザー[ $user ] を削除しましたしました"))
            else BadRequest(views.html.manage("ユーザーの削除に失敗しました"))
          })

      case "deleteSubject" =>
        Form(tuple("dSubjectId" -> number, "dSubjectName" -> nonEmptyText)).bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.manage("課題の削除に失敗しました")),
          sbj => {
            if (Subjects.delete(sbj._1) != 0) Ok(views.html.manage(s"課題[ ${sbj._2} ]を削除しました"))
            else BadRequest(views.html.manage("課題の削除に失敗しました"))
          })

      case _ => throw new IllegalArgumentException()
    }
  }

}