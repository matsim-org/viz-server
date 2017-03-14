package contracts;

import org.matsim.vis.snapshotwriters.AgentSnapshotInfo;

public class AgentSnapshotContract {
    private String id;
    private double x;
    private double y;

    public AgentSnapshotContract(AgentSnapshotInfo info) {
        this.id = info.getId().toString();
        this.x = info.getNorthing();
        this.y = info.getEasting();
    }
}
