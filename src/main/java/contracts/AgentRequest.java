package contracts;

public class AgentRequest {

    private double fromTimestep;
    private int size;
    private RectContract bounds;

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
