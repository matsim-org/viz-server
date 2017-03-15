package contracts;

public class ConfigurationResponse {

    private String id;
    private RectContract bounds;
    private double firstTimestep;
    private double lasTimestep;
    private double timeStepSize;

    public ConfigurationResponse(String id, RectContract bounds, double firstTimestep, double lasTimestep,
                                 double timeStepSize) {
        this.id = id;
        this.bounds = bounds;
        this.firstTimestep = firstTimestep;
        this.lasTimestep = lasTimestep;
        this.timeStepSize = timeStepSize;
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

    public double getLasTimestep() {
        return lasTimestep;
    }

    public double getTimeStepSize() {
        return timeStepSize;
    }
}
