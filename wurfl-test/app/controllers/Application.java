package controllers;

import play.mvc.Controller;
import play.mvc.With;
import controllers.mobi.MobiAware;

@With(MobiAware.class)
public class Application extends Controller {

    public static void index() {
        render();
    }

}