package contracts;

public class AgentRequest {

    private double fromTimestep;
    private int size;
    private RectContract bounds;

    public AgentRequest(RectContract bounds, double fromTimestep, int size) {
        this.bounds = bounds;
        this.fromTimestep = fromTimestep;
        this.size = size;
    }

    public double getFromTimestep() {
        return fromTimestep;
    }

    public int getSize() {
        return size;
    }

    public RectContract getBounds() {
        return bounds;
    }
}
