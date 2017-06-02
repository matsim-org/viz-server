package contracts.geoJSON;

import java.util.ArrayList;
import java.util.List;

public class Polygon extends Geometry {

    private List<List<double[]>> coordinates = new ArrayList<>();

    public Polygon(List<List<double[]>> rings) {
        super(Type.Polygon);
        this.coordinates = rings;
    }

    public void addHole(List<double[]> coordinates) {
        this.coordinates.add(coordinates);
    }

    @Override
    public void addCoordinate(double x, double y) {
        if (coordinates.size() == 0) {
            ArrayList<double[]> exteriorRing = new ArrayList<>();
            coordinates.add(exteriorRing);
        }
        coordinates.get(0).add(new double[]{x, y});
    }
}
