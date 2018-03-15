package communication;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Entity objects have lazy loading Iterables. If they haven't been initialized during database transaction iterating
 * such a collection would throw a LazyInitializationException. This serializer catches such exceptions during JSON
 * serialization and returns an empty JSONArray if the collection wasn't initialized during the db transaction
 */
public class IterableSerializer implements JsonSerializer<Iterable> {
    @Override
    public JsonElement serialize(Iterable iterable, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonArray result = new JsonArray();

        try {
            iterable.forEach(o -> result.add(jsonSerializationContext.serialize(o)));
        } catch (Exception e) {
            //continue return empty array;
        }
        return result;
    }
}
