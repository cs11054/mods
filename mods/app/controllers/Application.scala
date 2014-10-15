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
import util.Utilities
import scala.io.Source
import java.io.File
import models.Comments
import models.Comment
import models.Iines

object Application extends Controller with myAuth with Utilities {

  // TOP	///////////////////////////////////////////////////
  def index = Authenticated { implicit request =>
    Redirect(routes.Application.subject(Subjects.newestNum))
  }
  // User	///////////////////////////////////////////////////
  def user = Authenticated { implicit request =>
    Ok(views.html.userhome(request.session.get("user").get))
  }

  // Subject	///////////////////////////////////////////////
  def subject(sid: Int, key: String="date") = Authenticated { implicit request =>
    Ok(views.html.subject(sid, sort = key))
  }

  // Task	///////////////////////////////////////////////////
  def task(sid: Int, uid: String, tid: Int) = Authenticated { implicit request =>
    Ok(views.html.task(sid, uid, tid))
  }

  // Comment	///////////////////////////////////////////////
  def cmtPost(sid: Int, uid: String, tid: Int) = Authenticated { implicit req =>
    Form(tuple("body" -> text, "anonymous" -> optional(text))).bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.task(sid, uid, tid, "コメントの投稿に失敗しました。")),
      cmt => {
        val user = cmt._2 match {
          case Some(x) => Users.ANONY + ("A120" + sid + req.session.get("user").get).hashCode()
          case None => req.session.get("user").get
        }
        println(s"${user}(${req.session.get("user").get}) Posted Comment to ${sid}/${uid}")
        Comments.add(sid, uid, user, cmt._1)
        Ok(views.html.task(sid, uid, tid))
      })
  }

  // IINE	////////////////////////////////////////////////////
  def iine(sid: Int, uid: String) = Authenticated { implicit req =>
    Ok(views.html.iine(sid, uid))
  }

  def pushIine(sid: Int, uid: String) = Authenticated { implicit req =>
    val user = req.session.get("user").get
    println(s"${user} push いいね at ${sid}/${uid}")
    Iines.add(sid, uid, user)
    Ok(views.html.iine(sid, uid))
  }

  // Upload	///////////////////////////////////////////////////
  def upload = Action { implicit req =>
    Ok(views.html.upload())
  }

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
      case Some(x) => Users.ANONY + ("A120" + req.session.get("user").get + sid).hashCode()
      case None => req.session.get("user").get
    }
    val caption = reqDate.get("caption").getOrElse("")

    req.body.file("source").map { src =>
      if (src.filename.endsWith(".java")) {
        val body = using(Source.fromFile(src.ref.file.getAbsoluteFile())) { _.getLines.mkString("\n") } getOrElse ("")
        src.ref.file.delete()
        Tasks.add(sid, user, caption, body)
        println(s"File [${src.filename}] Uploaded by ${req.session.get("user").get}")
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