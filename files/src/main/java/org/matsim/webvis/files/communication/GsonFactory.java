package org.matsim.webvis.files.communication;

import com.google.gson.Gson;

public class GsonFactory {
    public static Gson createParserWithExclusionStrategy() {
        /*return new GsonBuilder().
                registerTypeHierarchyAdapter(Iterable.class, new IterableSerializer())
                .registerTypeAdapter(Map.class, new MapSerializer())
                .registerTypeAdapterFactory(new EntityAdapterFactory())
                .setExclusionStrategies(new FileEntryExclusionStrategy())
                .create();
                */
        return null;
    }
}
