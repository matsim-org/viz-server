package org.matsim.webvis.frameAnimation.contracts;

public class ConfigurationResponse {

    private RectContract bounds;
    private double firstTimestep;
    private double lastTimestep;
    private double timestepSize;

    public ConfigurationResponse(RectContract bounds, double firstTimestep, double lasTimestep,
                                 double timeStepSize) {
        this.bounds = bounds;
        this.firstTimestep = firstTimestep;
        this.lastTimestep = lasTimestep;
        this.timestepSize = timeStepSize;
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
