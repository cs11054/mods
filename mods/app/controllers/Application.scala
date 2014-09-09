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

object Application extends Controller with myAuth {

  // TOP	///////////////////////////////////////////////////
  def index = Authenticated { implicit request =>
    Ok(views.html.top())
  }

  // User	///////////////////////////////////////////////////
  def user = Authenticated { implicit request =>
    Ok(views.html.userhome(request.session.get("user").get))
  }

  // Task	///////////////////////////////////////////////////
  def task(id: Long) = Authenticated { implicit request =>
    Ok(views.html.review())
  }

  // Upload	///////////////////////////////////////////////////
  def upload = Action { implicit req =>
    Ok(views.html.upload())
  }

  def uploaded = Action(parse.multipartFormData) { req =>
    req.body.file("source").map { src =>
      println("Uploaded", src.filename, src.contentType)
      if (src.filename.endsWith(".scala")) {
        src.ref.moveTo(new java.io.File(s"./source/${src.filename}"), true)
        Ok(views.html.upload("File uploaded"))
      } else {
        Ok(views.html.upload("拡張子がおかしいよ"))
      }
    }.getOrElse {
      Ok(views.html.upload("ファイルが選ばれてないよ"))
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