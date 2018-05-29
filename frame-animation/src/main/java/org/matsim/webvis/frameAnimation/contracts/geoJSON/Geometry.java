package org.matsim.webvis.frameAnimation.contracts.geoJSON;

import org.matsim.api.core.v01.Coord;

public abstract class Geometry extends GeoJsonElement {

    Geometry(Type type) {

        super(type);
    }

    public void addCoordinate(Coord coord) {
        addCoordinate(coord.getX(), coord.getY());
    }

    public abstract void addCoordinate(double x, double y);
}
