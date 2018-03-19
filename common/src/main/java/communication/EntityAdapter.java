package communication;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import database.AbstractEntity;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Some entities have bi-directional relations which can not be represented in JSON. Hence, if an object has been
 * serialized in the object graph already the ID is serialized as a placeholder.
 */
public class EntityAdapter extends TypeAdapter<AbstractEntity> {

    private static ThreadLocal<Set<AbstractEntity>> visitedEntities = ThreadLocal.withInitial(HashSet::new);
    private TypeAdapter<Object> defaultAdapter;

    EntityAdapter(TypeAdapter<Object> defaultAdapter) {
        this.defaultAdapter = defaultAdapter;
    }

    @Override
    public void write(JsonWriter jsonWriter, AbstractEntity entity) throws IOException {

        boolean shouldCleanUp = (visitedEntities.get().size() == 0);

        if (entity == null) {
            jsonWriter.nullValue();
        } else if (visitedEntities.get().add(entity)) {
            defaultAdapter.write(jsonWriter, entity);
        } else {
            jsonWriter.beginObject();
            jsonWriter.name("id").value(entity.getId());
            jsonWriter.endObject();
        }

        if (shouldCleanUp) {
            visitedEntities.get().clear();
        }
    }

    @Override
    public AbstractEntity read(JsonReader jsonReader) throws IOException {
        return (AbstractEntity) defaultAdapter.read(jsonReader);
    }
}
