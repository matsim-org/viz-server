package data;

import contracts.SnapshotContract;

import java.util.HashMap;

public class SimulationData {
    private HashMap<Double, SnapshotContract> snapshots = new HashMap<>();
    private double firstTimestep = Double.MIN_VALUE;
    private double lastTimestep = Double.MAX_VALUE;

    public HashMap<Double, SnapshotContract> getSnapshots() {
        return snapshots;
    }

    public double getFirstTimestep() {
        return firstTimestep;
    }

    public double getLastTimestep() {
        return lastTimestep;
    }

    public void addSnapshot(SnapshotContract snapshot) {

        snapshots.put(snapshot.getTime(), snapshot);
        setFirstOrLastTimestep(snapshot.getTime());
    }

    public SnapshotContract getSnapshot(double timestep) {
        return snapshots.get(timestep);
    }

    private void setFirstOrLastTimestep(double time) {

        if (time < firstTimestep)
            firstTimestep = time;
        if (time > lastTimestep) {
            lastTimestep = time;
        }
    }
}
