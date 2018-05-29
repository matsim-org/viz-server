package org.matsim.webvis.frameAnimation.data;


import org.matsim.api.core.v01.Id;
import org.matsim.webvis.frameAnimation.contracts.SnapshotContract;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SnapshotData {
    private List<SnapshotContract> snapshots = new ArrayList<>();
    private List<Id> agentIds = new ArrayList<>();
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

    public int addId(Id id) {
        int indexOfId = agentIds.indexOf(id);
        if (indexOfId < 0) {
            agentIds.add(id);
            indexOfId = agentIds.size() - 1;
        }
        return indexOfId;
    }

    public Id getId(int index) {
        return agentIds.get(index);
    }

    /**
     * Adds a snapshot after requesting its data as ByteArray, also adjusts the first and last
     * timestep if necessary
     *
     * @param snapshot - The snapshot to be added
     */
    public void addSnapshot(SnapshotContract snapshot) {
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
    public byte[] getSnapshots(double fromTimestep, int numberOfTimesteps, double speedFactor) throws IOException {

        int startingIndex = getStartingIndex(fromTimestep);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        if (speedFactor < 1.0) {
            speedFactor = 1.0;
        }

        for (int i = startingIndex; i < startingIndex + numberOfTimesteps && i < snapshots.size(); i += speedFactor) {
            stream.write(snapshots.get(i).getEncodedMessage());
        }
        return stream.toByteArray();
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
