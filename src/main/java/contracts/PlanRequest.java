package contracts;

public class PlanRequest {
    private double timestep;
    private int index;

    public PlanRequest(double timestep, int index) {
        this.timestep = timestep;
        this.index = index;
    }

    public double getTimestep() {
        return timestep;
    }

    public int getIndex() {
        return index;
    }
}
