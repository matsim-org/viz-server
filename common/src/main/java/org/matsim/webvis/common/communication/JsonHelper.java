package org.matsim.webvis.common.communication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.Response;

public class JsonHelper {

    private static Gson gson = new GsonBuilder().
            registerTypeHierarchyAdapter(Iterable.class, new IterableSerializer()).
            registerTypeAdapterFactory(new EntityAdapterFactory()).create();

    public static <T> T parseJson(String json, Class<T> parseTo) {
        return parseJson(json, parseTo, null);
    }

    static <T> T parseJson(String json, Class<T> parseTo, Gson parser) {

        if (parser == null) parser = gson;
        T result = parser.fromJson(json, parseTo);
        if (result == null) throw new IllegalArgumentException("no json-body present");
        return result;
    }

    public static Object createJsonResponse(Answer answer, Response response) {
        return createJsonResponse(answer, response, null);
    }

    public static Object createJsonResponse(Answer answer, Response response, Gson parser) {
        if (parser == null) parser = gson;
        response.status(answer.getStatusCode());
        response.type(ContentType.APPLICATION_JSON);
        response.body(parser.toJson(answer.getResponse()));
        return response.body();
    }
}
