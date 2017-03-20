package data;

import contracts.SnapshotContract;

import java.util.HashMap;

public class SimulationData {
    private HashMap<Double, SnapshotContract> snapshots = new HashMap<>();
    private double firstTimestep = Double.MAX_VALUE;
    private double lastTimestep = Double.MIN_VALUE;

    public HashMap<Double, SnapshotContract> getSnapshots() {
        return snapshots;
    }

    public double getFirstTimestep() {
        return firstTimestep;
    }

    public double getLastTimestep() {
        return lastTimestep;
    }

    public void addSnapshot(SnapshotContract snapshot) throws Exception {

        if (snapshots.containsKey(snapshot.getTime()))
            throw new Exception("Snapshot for time: " + snapshot.getTime() + " is already present");
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
