package contracts.geoJSON;


import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class FeatureCollection extends GeoJsonElement {

    private List<Feature> features = new ArrayList<>();

    public FeatureCollection() {
        super(Type.FeatureCollection);
    }

    public void addFeature(Feature feature) {
        this.features.add(feature);
    }

    public void addFeature(Geometry geometry) {
        Feature feature = new Feature(geometry);
        this.features.add(feature);
    }

    public void addFeature(Geometry geometry, String type) {
        Feature feature = new Feature(geometry);
        feature.addProperty(new Property("type", type));
        this.features.add(feature);
    }

    public String toGeoJson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Feature.class, new FeatureSerializer());
        return builder.create().toJson(this);
    }
}
