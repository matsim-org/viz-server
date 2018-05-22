package org.matsim.webvis.auth;

import org.matsim.webvis.auth.authorization.AuthorizationRequestHandler;
import org.matsim.webvis.auth.token.IntrospectionRequestHandler;
import org.matsim.webvis.auth.token.TokenRequestHandler;
import org.matsim.webvis.auth.user.CreateUserRequestHandler;
import org.matsim.webvis.auth.user.LoginPrompt;
import org.matsim.webvis.auth.user.LoginUserRequestHandler;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Routes {

    private final static String USER = "user/";
    private final static String TOKEN = "token/";
    private final static String INTROSPECT = "introspect/";
    private final static String AUTHORIZE = "authorize/";
    public final static String LOGIN = "/login/";
    private final static String LOGIN_FORM = "login/form/";

    static void initialize() throws Exception {

        put(USER, new CreateUserRequestHandler());
        post(TOKEN, new TokenRequestHandler());
        post(INTROSPECT, new IntrospectionRequestHandler());
        post(AUTHORIZE, new AuthorizationRequestHandler());
        get(AUTHORIZE, new AuthorizationRequestHandler());
        get(LOGIN_FORM, (request, response) -> LoginPrompt.renderLogin());
        post(LOGIN, new LoginUserRequestHandler());
        get(LOGIN, ((request, response) -> LoginPrompt.renderLogin()));
    }
}
