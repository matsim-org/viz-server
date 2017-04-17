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
     * Adds a Snapshot message to the simulation data the message is stored as
     * delimited byte[] see
     * https://developers.google.com/protocol-buffers/docs/reference/java/com/google/protobuf/AbstractMessageLite
     *
     * @param snapshot - The snapshot to be added
     * @throws IOException
     */
    public void addSnapshot(SnapshotContract snapshot) throws IOException {
        /*ByteArrayOutputStream stream = new ByteArrayOutputStream();
        snapshot.writeDelimitedTo(stream);
        byte[] bytes = stream.toByteArray();
        snapshotsAsBytes.add(bytes);
        setFirstOrLastTimestep(snapshot.getTime());
        */
        byte[] snapshotAsBytes = snapshot.toByteArray();
        snapshotsAsBytes.add(snapshotAsBytes);
        setFirstOrLastTimestep(snapshot.getTime());
    }

    /**
     * retreives a single encoded snapshot for the given timestep as byte[] the first byte delimits
     * the byte length of the message
     *
     * @param timestep
     * @return
     */
    public byte[] getSnapshot(int timestep) {

        int startingIndex = getStartingIndex(timestep, 1);
        return snapshotsAsBytes.get(startingIndex);
    }

    /**
     * Retreives encoded snapshots as byte[] before each message one byte delimits the
     * length of the following message
     *
     * @param fromTimestep      - first Timestep included into the result
     * @param numberOfTimesteps - retreived from simulation data
     * @return encoded snapshots seperated by one byte delimiting the length of each
     * individual message
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
