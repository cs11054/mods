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
import models.Task
import models.Tasks

object Application extends Controller with myAuth {

  // TOP	///////////////////////////////////////////////////
  def index = Authenticated { implicit request =>
    Redirect(routes.Application.subject(Subjects.newestNum))
  }

  // User	///////////////////////////////////////////////////
  def user = Authenticated { implicit request =>
    Ok(views.html.userhome(request.session.get("user").get))
  }

  // Subject	///////////////////////////////////////////////
  def subject(sid: Int) = Authenticated { implicit request =>
    Ok(views.html.subject(sid))
  }

  // Task	///////////////////////////////////////////////////
  def task(sid: Int, uid: String) = Authenticated { implicit request =>
    Ok(views.html.review(Tasks.getCaptionAndCodeLines(sid, uid)))
  }

  // Upload	///////////////////////////////////////////////////
  def upload = Action { implicit req =>
    Ok(views.html.upload())
  }

  val SAVE_PATH = "./source"
  def uploaded = Action(parse.multipartFormData) { req =>
    // いきなりPOSTしてくるハッカー対策、必要か不明
    if (req.session.get("user").isEmpty) BadRequest(views.html.subject(msg = "投稿に失敗しました。"))
    // 形式がなぜか Map[String,Seq[String] なので、 Map[String,String] に変換
    val reqDate = req.body.asFormUrlEncoded.map(m => m._1 -> m._2.head)

    val sid = reqDate.get("sid").getOrElse(0).toString().forall(_.isDigit) match {
      case true => reqDate.get("sid").getOrElse(0).toString().toInt
      case false => 0
    }

    val user = reqDate.get("anonymous") match {
      case Some(x) => ("A120" + req.session.get("user").get + sid).hashCode().toString
      case None => req.session.get("user").get
    }
    val comment = reqDate.get("comment").getOrElse("")

    req.body.file("source").map { src =>
      if (src.filename.endsWith(".scala")) {
        val n = Tasks.add(sid, user, comment)
        println(s"File [${src.filename}] Uploaded to ${sid}/${user}_${n}")
        src.ref.moveTo(new java.io.File(s"${SAVE_PATH}/${sid}/${user}_${n}"), true)
        Ok(views.html.subject(sid, "投稿しました。"))
      } else {
        BadRequest(views.html.subject(sid, "投稿に失敗しました。"))
      }
    }.getOrElse {
      BadRequest(views.html.subject(sid, "投稿に失敗しました。"))
    }
  }

  // Login	///////////////////////////////////////////////////
  def login = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }

  def loginCheck = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      user => {
        Console.println("login:" + user._1)
        Redirect(routes.Application.index).withSession("user" -> user._1)
      })
  }

  def logout = Action { implicit request =>
    Console.println("logout:" + session.get("user").getOrElse(""))
    Redirect(routes.Application.login).withNewSession
  }

}