package org.matsim.webvis.files.file;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class FileEntryExclusionStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        return fieldAttributes.getName().equals("persistedFileName");
    }

    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
        return false;
    }
}
