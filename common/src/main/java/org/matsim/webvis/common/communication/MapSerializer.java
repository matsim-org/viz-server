package org.matsim.webvis.common.communication;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Map;

public class MapSerializer implements JsonSerializer<Map<String, Object>> {
    @Override
    public JsonElement serialize(Map<String, Object> map, Type type, JsonSerializationContext jsonSerializationContext) {

        JsonObject result = new JsonObject();
        try {
            map.forEach((key, value) -> result.add(key, jsonSerializationContext.serialize(value)));
        } catch (Exception e) {
            //continue and return emtpy map/json object
        }

        return result;
    }
}
