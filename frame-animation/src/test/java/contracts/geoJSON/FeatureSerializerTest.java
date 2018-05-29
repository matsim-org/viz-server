package contracts.geoJSON;

import com.google.gson.GsonBuilder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FeatureSerializerTest {

    @Test
    public void serialize() {

        //arrange
        String expectedJson = "{\"type\":\"Feature\",\"properties\":{\"some\":\"property\"},\"geometry\":{\"coordinates\":[1.0,2.0],\"type\":\"Point\"}}";
        Property prop = new Property("some", "property");
        Geometry point = new Point();
        point.addCoordinate(1, 2);
        Feature feature = new Feature(point);
        feature.addProperty(prop);

        //act
        String json = new GsonBuilder().registerTypeAdapter(Feature.class, new FeatureSerializer()).
                create().toJson(feature);

        //assert
        assertEquals(expectedJson, json);
    }
}
