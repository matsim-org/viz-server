package contracts;

public class AgentRequest {

    private double fromTimestep;
    private double toTimestep;
    private RectContract bounds;

    public double getFromTimestep() {
        return fromTimestep;
    }

    public double getToTimestep() {
        return toTimestep;
    }

    public RectContract getBounds() {
        return bounds;
    }


}
