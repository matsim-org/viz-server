package data;


import contracts.SnapshotContract;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimulationDataAsBytes {
    private List<byte[]> snapshotsAsBytes = new ArrayList<>();
    private double firstTimestep = Double.MAX_VALUE;
    private double lastTimestep = Double.MIN_VALUE;
    private double timestepSize = 1;

    public SimulationDataAsBytes(double timestepSize) {
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
        byte[] snapshotAsBytes = snapshot.toByteArray();
        snapshotsAsBytes.add(snapshotAsBytes);
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

        int startingIndex = getStartingIndex(fromTimestep, numberOfTimesteps);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        for (int i = startingIndex; i < startingIndex + numberOfTimesteps && i < snapshotsAsBytes.size(); i++) {
            stream.write(snapshotsAsBytes.get(i));
        }
        return stream.toByteArray();
    }

    private int getStartingIndex(double fromTimestep, int numberOfTimesteps) {
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
