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

object Manage extends Controller with myAuth {

  // Manage	///////////////////////////////////////////////////
  def manage(msg: String = "") = Administor { implicit request =>
    Ok(views.html.manage(msg))
  }

  def managed(kind: String) = Administor { implicit request =>
    kind match {
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