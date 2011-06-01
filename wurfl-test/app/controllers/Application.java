package controllers;

import play.mvc.Controller;
import play.mvc.With;
import controllers.wurfl.WurflAware;

@With(WurflAware.class)
public class Application extends Controller {

    public static void index() {
        render();
    }

}