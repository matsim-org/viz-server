package contracts;

public class ConfigurationResponse {

    private String id;
    private RectContract bounds;
    private double firstTimestep;
    private double lastTimestep;
    private double timestepSize;

    public ConfigurationResponse(String id, RectContract bounds, double firstTimestep, double lasTimestep,
                                 double timeStepSize) {
        this.id = id;
        this.bounds = bounds;
        this.firstTimestep = firstTimestep;
        this.lastTimestep = lasTimestep;
        this.timestepSize = timeStepSize;
    }

    public String getId() {
        return id;
    }

    public RectContract getBounds() {
        return bounds;
    }

    public double getFirstTimestep() {
        return firstTimestep;
    }

    public double getLastTimestep() {
        return lastTimestep;
    }

    public double getTimestepSize() {
        return timestepSize;
    }
}
