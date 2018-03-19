package communication;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import database.AbstractEntity;

public class EntityAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {

        if (!AbstractEntity.class.isAssignableFrom(typeToken.getRawType())) {
            return null;
        }

        TypeAdapter defaultAdapter = gson.getDelegateAdapter(this, typeToken);
        return (TypeAdapter<T>) new EntityAdapter(defaultAdapter);
    }
}
