package org.matsim.webvis.frameAnimation.contracts.geoJSON;

public abstract class GeoJsonElement {

    private Type type;

    GeoJsonElement(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    //there are more geoJSON types which can be implemented if necessary
    public enum Type {
        Feature, FeatureCollection, Point, LineString, Polygon
    }
}
