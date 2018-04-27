package org.matsim.webvis.files.communication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.matsim.webvis.common.communication.EntityAdapterFactory;
import org.matsim.webvis.common.communication.IterableSerializer;
import org.matsim.webvis.files.file.FileEntryExclusionStrategy;

public class GsonFactory {
    public static Gson createParserWithExclusionStrategy() {
        return new GsonBuilder().
                registerTypeHierarchyAdapter(Iterable.class, new IterableSerializer())
                .registerTypeAdapterFactory(new EntityAdapterFactory())
                .setExclusionStrategies(new FileEntryExclusionStrategy())
                .create();
    }
}
