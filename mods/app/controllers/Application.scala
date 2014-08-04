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

  def task(id:Long) = TODO

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