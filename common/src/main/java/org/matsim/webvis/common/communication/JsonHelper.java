package org.matsim.webvis.common.communication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.Response;

public class JsonHelper {

    private static Gson gson = new GsonBuilder().
            registerTypeHierarchyAdapter(Iterable.class, new IterableSerializer()).
            registerTypeAdapterFactory(new EntityAdapterFactory()).create();

    public static <T> T parseJson(String json, Class<T> parseTo) {
        return parseJson(json, parseTo, gson);
    }

    private static <T> T parseJson(String json, Class<T> parseTo, Gson gson) {
        T result = gson.fromJson(json, parseTo);
        if (result == null) throw new IllegalArgumentException("no json-body present");
        return result;
    }

    public static Object createJsonResponse(Answer answer, Response response) {
        return createJsonResponse(answer, response, gson);
    }

    public static Object createJsonResponse(Answer answer, Response response, Gson gson) {
        response.status(answer.getStatusCode());
        response.type(ContentType.APPLICATION_JSON);
        response.body(gson.toJson(answer.getResponse()));
        return response.body();
    }
}
