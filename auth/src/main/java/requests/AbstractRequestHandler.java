package requests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import spark.Request;
import spark.Response;
import spark.Route;

public abstract class AbstractRequestHandler<T> implements Route {

    private static final String RESPONSETYPE_JSON = "application/json";

    private static Gson gson = new GsonBuilder().
            registerTypeHierarchyAdapter(Iterable.class, new IterableSerializer()).
            registerTypeAdapterFactory(new EntityAdapterFactory()).create();

    private Class<T> contractClass;

    protected AbstractRequestHandler(Class<T> contractClass) {
        this.contractClass = contractClass;
    }

    protected Gson getGson() {
        return gson;
    }

    public Object handle(Request request, Response response) {

        Answer answer = processRequest(request);
        return createResponse(answer, response);
    }

    protected abstract Answer process(T body);

    private Answer processRequest(Request request) {

        T body;
        try {
            body = processBody(request.body());
        } catch (Exception e) {
            return Answer.badRequest(e.getMessage());
        }

        return process(body);
    }

    private String createResponse(Answer answer, Response response) {

        response.status(answer.getStatusCode());
        response.type(RESPONSETYPE_JSON);

        if (answer.isOk()) {
            response.body(answer.getText());
        } else {
            String json = getGson().toJson(new ErrorResponse(answer.getStatusCode(), answer.getText()));
            response.body(json);
        }
        return response.body();
    }

    private T processBody(String body) throws Exception {

        T contract;
        try {
            contract = getGson().fromJson(body, contractClass);
        } catch (JsonSyntaxException e) {
            throw new Exception("the request body could not be parsed");
        }
        return contract;
    }
}
