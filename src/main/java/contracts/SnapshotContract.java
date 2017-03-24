package contracts;

import org.matsim.vis.snapshotwriters.AgentSnapshotInfo;

import java.util.ArrayList;
import java.util.List;

public class SnapshotContract {

    private double time;
    private List<AgentSnapshotContract> positions = new ArrayList<>();

    public SnapshotContract(double time) {

        this.time = roundFourDecimals(time);
    }

    public List<AgentSnapshotContract> getAgentInformations() {
        return positions;
    }

    public double getTime() {
        return time;
    }

    public void add(AgentSnapshotInfo info) {
        positions.add(new AgentSnapshotContract(info));
    }

    private double roundFourDecimals(double value) {
        return (double) Math.round(value * 10000) / 10000;
    }
}
