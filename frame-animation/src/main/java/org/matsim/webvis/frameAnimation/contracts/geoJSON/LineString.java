package org.matsim.webvis.frameAnimation.contracts.geoJSON;

import org.matsim.api.core.v01.Coord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LineString extends Geometry {

    private List<double[]> coordinates = new ArrayList<>();

    public LineString() {
        super(Type.LineString);
    }

    public LineString(Collection<Coord> coordinates) {
        this();
        addCoordinates(coordinates);
    }

    @Override
    public void addCoordinate(double x, double y) {
        coordinates.add(new double[]{x, y});
    }

    public void addCoordinates(Collection<Coord> coordinates) {
        for (Coord coord : coordinates) {
            addCoordinate(coord);
        }
    }
}
