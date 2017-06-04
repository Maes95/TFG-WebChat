package controllers;

import actors.User;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;


public class Application extends Controller {

    public Result index() {
        return ok(views.html.chat.render());
    }

    public WebSocket<String> socket() {
        return WebSocket.withActor(User::props);
    }

}
