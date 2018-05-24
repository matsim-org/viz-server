package org.matsim.webvis.common.communication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.matsim.webvis.common.service.InvalidInputException;
import spark.Response;

import java.util.Map;

public class JsonHelper {

    private static Gson gson = new GsonBuilder().
            registerTypeHierarchyAdapter(Iterable.class, new IterableSerializer()).
            registerTypeHierarchyAdapter(Map.class, new MapSerializer()).
            registerTypeAdapterFactory(new EntityAdapterFactory()).create();

    public static <T> T parseJson(String json, Class<T> parseTo) {
        return parseJson(json, parseTo, null);
    }

    static <T> T parseJson(String json, Class<T> parseTo, Gson parser) {

        if (parser == null) parser = gson;
        try {
            T result = parser.fromJson(json, parseTo);
            if (result == null) throw new InvalidInputException("no json-body present");
            return result;
        } catch (JsonSyntaxException e) {
            throw new InvalidInputException("invalid JSON syntax.");
        }
    }

    static Object createJsonResponse(Answer answer, Response response) {
        return createJsonResponse(answer, response, null);
    }

    static Object createJsonResponse(Answer answer, Response response, Gson parser) {
        if (parser == null) parser = gson;
        response.status(answer.getStatusCode());
        response.type(ContentType.APPLICATION_JSON);
        response.body(parser.toJson(answer.getResponse()));
        return response.body();
    }
}
