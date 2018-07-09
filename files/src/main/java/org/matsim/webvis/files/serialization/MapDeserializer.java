package org.matsim.webvis.files.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapDeserializer extends JsonDeserializer<Map<Object, Object>> implements ContextualDeserializer {

    private Class<?> keyType;
    private Class<?> valueType;

    @Override
    public Map<Object, Object> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        JsonNode node = jsonParser.readValueAsTree(); // the overall map object should be [[...],[...]]
        Map<Object, Object> result = new HashMap<>();
        if (node.isArray()) {

            for (JsonNode entry : node) {
                if (entry.isArray()) {
                    try {
                        JsonNode keyNode = entry.get(0);
                        JsonNode valueNode = entry.get(1);
                        result.put(keyNode.traverse(jsonParser.getCodec()).readValueAs(keyType),
                                valueNode.traverse(jsonParser.getCodec()).readValueAs(valueType));
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new IOException("Could not parse map", e);
                    }
                } else {
                    throw new IOException("Incorrect map format.");
                }
            }
        }

        return result;
    }


    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) {

        keyType = beanProperty.getType().getKeyType().getRawClass();
        valueType = beanProperty.getType().getContentType().getRawClass();
        return this;
    }
}
