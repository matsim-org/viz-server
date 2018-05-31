package org.matsim.webvis.frameAnimation.contracts.geoJSON;

import org.matsim.api.core.v01.Coord;

public class Point extends Geometry {

    private double[] coordinates;

    Point() {
        super(Type.Point);
    }

    public Point(Coord coord) {
        this();
        addCoordinate(coord);
    }

    @Override
    public void addCoordinate(double x, double y) {
        coordinates = new double[]{x, y};
    }
}
