package controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._

object Application extends Controller with myAuth {

  // TOP	///////////////////////////////////////////////////
  def index = Authenticated { implicit request =>
    Ok(views.html.index(request.user))
  }

  // User	///////////////////////////////////////////////////
  def user(id: String) = TODO

  // Task	///////////////////////////////////////////////////
  def taskTop = TODO

  def task(id: Long) = TODO

  // Upload	///////////////////////////////////////////////////
  def upload = Action { implicit req =>
    Ok(views.html.upload())
  }

  def uploaded = Action(parse.multipartFormData) { req =>
    req.body.file("source").map { src =>
      println(src.filename, src.contentType)
      val file = new java.io.File(s"./source/${src.filename}")
      src.ref.moveTo(file,true)
      Ok(views.html.upload("File uploaded"))
    }.getOrElse {
      Ok(views.html.upload("MISSING"))
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