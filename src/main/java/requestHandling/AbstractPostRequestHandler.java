package requestHandling;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import constants.Params;
import spark.Request;
import spark.Response;
import spark.Route;

public abstract class AbstractPostRequestHandler<T> implements Route {

    private Class<T> classInfo;

    protected AbstractPostRequestHandler(Class<T> classInfo) {
        this.classInfo = classInfo;
    }

    public abstract Answer process(T body);

    @Override
    public Object handle(Request request, Response response) throws Exception {

        Answer answer = processBody(request.body());

        //prepare the response
        response.status(answer.getCode());
        response.type(Params.RESPONSETYPE_JSON);
        response.body(answer.getBody());
        return answer.getBody();
    }

    protected Answer processBody(String body) {
        T contractClass;
        try {
            contractClass = new Gson().fromJson(body, classInfo);
        } catch (JsonSyntaxException e) {
            return new Answer(Params.STATUS_BADREQUEST, "the request body did NOT have the correct format");
        }
        return process(contractClass);
    }
}
