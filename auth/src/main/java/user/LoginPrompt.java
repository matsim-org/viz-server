package user;

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;
import java.util.Map;

public class LoginPrompt {
    public static String renderLogin() {
        return renderLogin(new HashMap<>());
    }

    public static String renderLoginWithError() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("error", true);
        parameters.put("errorMessage", "username or password was wrong");
        return renderLogin(parameters);
    }

    private static String renderLogin(Map<String, Object> model) {
        return new MustacheTemplateEngine().render(new ModelAndView(model, "login.mustache"));
    }
}
