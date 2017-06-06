package data;


import contracts.SnapshotContract;
import org.matsim.api.core.v01.Id;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SnapshotData {
    private List<SnapshotContract> snapshots = new ArrayList<>();
    private double firstTimestep = Double.MAX_VALUE;
    private double lastTimestep = Double.MIN_VALUE;
    private double timestepSize = 1;

    public SnapshotData(double timestepSize) {
        this.timestepSize = timestepSize;
    }

    public double getFirstTimestep() {
        return firstTimestep;
    }

    public double getLastTimestep() {
        return lastTimestep;
    }

    /**
     * Adds a snapshot after requesting its data as ByteArray, also adjusts the first and last
     * timestep if necessary
     *
     * @param snapshot - The snapshot to be added
     * @throws IOException
     */
    public void addSnapshot(SnapshotContract snapshot) throws IOException {
        snapshot.encodeSnapshot();
        snapshots.add(snapshot);
        setFirstOrLastTimestep(snapshot.getTime());
    }

    /**
     * Retreives encoded snapshots as byte[] each Snapshot consists of
     *  timestep, sizeOfPositions, [x,y, x,y, x,y, ...] all encoded as Float32
     *
     * @param fromTimestep      - first Timestep included into the result
     * @param numberOfTimesteps - retreived from simulation data
     * @return encoded snapshots
     * @throws IOException
     */
    public byte[] getSnapshots(double fromTimestep, int numberOfTimesteps) throws IOException {

        int startingIndex = getStartingIndex(fromTimestep);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        for (int i = startingIndex; i < startingIndex + numberOfTimesteps && i < snapshots.size(); i++) {
            stream.write(snapshots.get(i).getEncodedMessage());
        }
        return stream.toByteArray();
    }

    public Id getId(double timestep, int index) {
        int snapshotIndex = getStartingIndex(timestep);
        return snapshots.get(snapshotIndex).getIdForIndex(index);
    }

    private int getStartingIndex(double fromTimestep) {
        if (firstTimestep - 0.001 > fromTimestep) {
            throw new RuntimeException("fromTimestep was smaller than first cached Timestep");
        }

        return (int) ((fromTimestep - firstTimestep) / timestepSize);
    }

    private void setFirstOrLastTimestep(double time) {

        if (time < firstTimestep)
            firstTimestep = time;
        if (time > lastTimestep) {
            lastTimestep = time;
        }
    }
}
