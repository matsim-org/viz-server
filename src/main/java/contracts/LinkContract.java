package contracts;

import org.matsim.api.core.v01.network.Link;

public class LinkContract {

    private CoordContract fromCoord;
    private CoordContract toCoord;

    private LinkContract(CoordContract fromCoord, CoordContract toCoord) {
        this.fromCoord = fromCoord;
        this.toCoord = toCoord;
    }

    public LinkContract(Link link) {
        this(new CoordContract(link.getFromNode().getCoord()), new CoordContract(link.getToNode().getCoord()));
    }
}
