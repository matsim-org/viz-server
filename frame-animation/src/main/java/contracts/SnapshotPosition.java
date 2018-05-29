package contracts;

import org.matsim.vis.snapshotwriters.AgentSnapshotInfo;

public class SnapshotPosition {

    private String id;
    private int idIndex;
    private double x;
    private double y;

    public SnapshotPosition(AgentSnapshotInfo info, int idIndex) {
        this.id = info.getId().toString();
        this.idIndex = idIndex;
        this.x = info.getEasting();
        this.y = info.getNorthing();
    }

    public String getId() {
        return id;
    }

    public int getIdIndex() {
        return idIndex;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
