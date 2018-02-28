package requests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import spark.Request;
import spark.Response;
import spark.Route;

public abstract class AbstractRequestHandler<T> implements Route {

    private static final String TYPE_JSON = "application/json";


    private static Gson gson = new GsonBuilder().
            registerTypeHierarchyAdapter(Iterable.class, new IterableSerializer()).
            registerTypeAdapterFactory(new EntityAdapterFactory()).create();

    private Class<T> contractClass;

    protected AbstractRequestHandler(Class<T> contractClass) {
        this.contractClass = contractClass;
    }

    private Gson getGson() {
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
            if (request.contentType() != null && request.contentType().equals(TYPE_JSON))
                body = parseJsonBody(request.body());
            else
                body = parseBody(request);
        } catch (RequestException e) {
            return Answer.badRequest(e.getErrorCode(), e.getMessage());
        }

        return process(body);
    }

    private String createResponse(Answer answer, Response response) {

        response.status(answer.getStatusCode());
        response.type(TYPE_JSON);
        String json = getGson().toJson(answer.getResponse());
        response.body(json);
        return response.body();
    }

    /**
     * AbstractRequestHandler only handles JSON requests. All other request-types must be parsed by the extending
     * class.
     *
     * @param request The request
     * @return parsed request
     * @throws RequestException if the request could not be parsed with a meaningful message
     */
    protected T parseBody(Request request) throws RequestException {
        throw new RequestException(ErrorCode.UNSUPPORTED_CONTENT_TYPE, "content type is not supported for this request");
    }

    private T parseJsonBody(String body) throws RequestException {

        T contract;
        try {
            contract = getGson().fromJson(body, contractClass);
        } catch (JsonSyntaxException e) {
            throw new RequestException(ErrorCode.INVALID_REQUEST, "the request body could not be parsed");
        }
        return contract;
    }
}
