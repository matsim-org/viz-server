package contracts.geoJSON;

import java.util.ArrayList;
import java.util.List;

public class Feature extends GeoJsonElement {

    private List<Property> properties = new ArrayList<>();
    private Geometry geometry;

    private Feature() {
        super(Type.Feature);
    }

    public Feature(Geometry geometry) {
        this();
        this.geometry = geometry;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void addProperty(Property property) {
        this.properties.add(property);
    }
}
