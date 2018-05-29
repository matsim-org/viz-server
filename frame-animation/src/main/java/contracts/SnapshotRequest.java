package contracts;

public class SnapshotRequest {

    double speedFactor;
    private double fromTimestep;
    private int size;
    private RectContract bounds;

    public SnapshotRequest(RectContract bounds, double fromTimestep, int size, double speedFactor) {
        this.bounds = bounds;
        this.fromTimestep = fromTimestep;
        this.size = size;
        this.speedFactor = speedFactor;
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

    public double getSpeedFactor() {
        return this.speedFactor;
    }
}
