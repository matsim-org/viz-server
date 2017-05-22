package contracts.geoJSON;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class FeatureSerializer implements JsonSerializer<Feature> {

    @Override
    public JsonElement serialize(Feature src, Type typeOfSrc, JsonSerializationContext context) {

        final JsonObject result = new JsonObject();
        result.addProperty("type", src.getType().toString());

        JsonObject propertiesObject = new JsonObject();
        for (Property prop : src.getProperties()) {
            propertiesObject.addProperty(prop.getKey(), prop.getValue());
        }
        result.add("properties", propertiesObject);

        JsonElement geom = context.serialize(src.getGeometry());
        result.add("geometry", geom);
        return result;
    }
}
