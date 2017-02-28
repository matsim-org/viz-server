package requestHandling;


import constants.Params;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public abstract class AbstractGetGetRequestHandler implements GetRequestHandler, Route {


    /*
    This is here in case we want to add some validation logic which is
    the same for all requests
     */
    public final Answer process(Map<String, String[]> parameters) {
        return processImpl(parameters);
    }

    protected abstract Answer processImpl(Map<String, String[]> parameters);

    public Object handle(Request request, Response response) throws Exception {

        //get the query parameters
        Map<String, String[]> parameters = request.queryMap().toMap();

        //handle the request
        Answer answer = process(parameters);

        //prepare the response
        response.status(answer.getCode());
        response.type(Params.RESPONSETYPE_JSON);
        response.body(answer.getBody());
        return answer.getBody();
    }
}
