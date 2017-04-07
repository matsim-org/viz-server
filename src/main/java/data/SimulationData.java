package data;

import contracts.SnapshotContract;

import java.util.ArrayList;
import java.util.List;

public class SimulationData {

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

    public List<SnapshotContract> getSnapshots(double fromTimestep, int size) {

        if (firstTimestep - 0.0001 > fromTimestep) {
            throw new RuntimeException("fromTimestep was not within recorded timespan");
        }

        double startingIndex = (fromTimestep - firstTimestep) / timestepSize;
        List<SnapshotContract> result = new ArrayList<>(size);

        for (int i = (int) startingIndex; i < startingIndex + size && i < snapshots.size(); i++) {
            result.add(snapshots.get(i));
        }

        System.out.println("getSnapshots fromTime: " + fromTimestep + " with size " + size + " results in " + result.size() + " snapshots");
        if (result.size() > 0) {
            System.out.println("firstSnapshot: " + result.get(0).getTime() + " lastSnapshot: " + result.get(result.size() - 1).getTime());
        }
        return result;
    }

    private void setFirstOrLastTimestep(double time) {

        if (time < firstTimestep)
            firstTimestep = time;
        if (time > lastTimestep) {
            lastTimestep = time;
        }
    }
}
