package data;

import contracts.SnapshotContract;

import java.util.ArrayList;
import java.util.List;

public class SimulationData {
    //private HashMap<Double, SnapshotContract> snapshots = new HashMap<>();
    private List<SnapshotContract> snapshots = new ArrayList<>();
    private double firstTimestep = Double.MAX_VALUE;
    private double lastTimestep = Double.MIN_VALUE;
    private double timestepSize = 1;

    public SimulationData(double timestepSize) {
        this.timestepSize = timestepSize;
    }

    public List<SnapshotContract> getSnapshots() {
        return snapshots;
    }

    public double getFirstTimestep() {
        return firstTimestep;
    }

    public double getLastTimestep() {
        return lastTimestep;
    }

    /*
    The snapshot is added as the last snapshot.

    RuntimeException is thrown if time of snapshot is smaller than its predecessors time
    RuntimeException is thrown if time of snapshot is bigger than predecessors time + timestepsize
     */
    public void addSnapshot(SnapshotContract snapshot) {

        //ensure timestep is bigger than before
        if (snapshots.size() > 0) {
            double previousTimestep = snapshots.get(snapshots.size() - 1).getTime();
            if (previousTimestep >= snapshot.getTime()) {
                throw new RuntimeException("Timestep of snapshot must be greater than timestep of snapshot added before");
            }
            if (previousTimestep + timestepSize + 0.0001 < snapshot.getTime()) {
                throw new RuntimeException("Timestep of snapshot must not be greater than its predecessors timestep + timestepsize");
            }
        }

        //add at the end
        snapshots.add(snapshot);
        setFirstOrLastTimestep(snapshot.getTime());
    }

    public SnapshotContract getSnapshot(double timestep) {

        //ensure timestep is within bounds round one milli
        if (firstTimestep - 0.0001 > timestep || lastTimestep + 0.0001 < timestep) {
            throw new RuntimeException("timestep was not within recorded timespan");
        }
        //calculate index: offset / timestepSize
        double index = (timestep - firstTimestep) / timestepSize;
        return snapshots.get((int) index);
    }

    public List<SnapshotContract> getSnapshots(double fromTimestep, double toTimestep) {

        //ensure timesteps are within bounds and in the right order
        if (fromTimestep >= toTimestep) {
            throw new RuntimeException("toTimestep must be greater than from Timestep");
        }
        if (firstTimestep - 0.0001 > fromTimestep || lastTimestep + 0.0001 < toTimestep) {
            throw new RuntimeException("timespan was not within recorded timespan");
        }
        //calculate starting index
        double index = (fromTimestep - firstTimestep) / timestepSize;
        //calculate how many frames to serve
        double size = ((toTimestep - fromTimestep) / timestepSize) + 1; //+1 so toTimestep is included
        List<SnapshotContract> result = new ArrayList<>((int) size + 1);
        for (int i = (int) index; i < index + size && i < snapshots.size(); i++) {
            result.add(snapshots.get(i));
        }
        return result;
    }

    private double roundFourDecimals(double value) {
        return (double) Math.round(value * 10000) / 10000;
    }

    private void setFirstOrLastTimestep(double time) {

        if (time < firstTimestep)
            firstTimestep = time;
        if (time > lastTimestep) {
            lastTimestep = time;
        }
    }
}
