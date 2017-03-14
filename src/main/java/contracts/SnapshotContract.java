package contracts;

import org.matsim.vis.snapshotwriters.AgentSnapshotInfo;

import java.util.ArrayList;
import java.util.List;

public class SnapshotContract {

    double time;
    private List<AgentSnapshotContract> infos = new ArrayList<>();

    public SnapshotContract(double time) {
        this.time = time;
    }

    public double getTime() {
        return time;
    }

    public void add(AgentSnapshotInfo info) {
        infos.add(new AgentSnapshotContract(info));
    }
}
