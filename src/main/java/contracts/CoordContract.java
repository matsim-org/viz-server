package contracts;

import org.matsim.api.core.v01.Coord;

public class CoordContract {

    private double x;
    private double y;

    public CoordContract(double x, double y) {
        this.x = x;
        this.y = y;
    }

    CoordContract(Coord matsimCoord) {
        this.x = matsimCoord.getX();
        this.y = matsimCoord.getY();
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
