package org.matsim.viz.postprocessing.eventAnimation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.viz.database.AbstractEntity;

import javax.persistence.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class MatsimNetwork extends AbstractEntity {

	@Transient
	private int currentLinkId = -1;

	@Getter
	@Transient
	private Map<Id<Link>, Float> linkIdMapping = new HashMap<>();

	@Transient
	private double minEasting = Double.POSITIVE_INFINITY;
	@Transient
	private double maxEasting = Double.NEGATIVE_INFINITY;
	@Transient
	private double minNorthing = Double.POSITIVE_INFINITY;
	@Transient
	private double maxNorthing = Double.NEGATIVE_INFINITY;

	@Lob
	private byte[] data;

	@OneToOne(fetch = FetchType.LAZY)
	private Visualization visualization;

	public MatsimNetwork(Network network) {

		val size = network.getLinks().size();
		val valueSize = Float.BYTES;
		val numberOfValuesPerLink = 5; //id, x1, y1, x2, y2
		val buffer = ByteBuffer.allocate(valueSize * numberOfValuesPerLink * size);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		network.getLinks().values().forEach(link -> {
			this.putLink(buffer, link);
			this.adjustBoundingRectangle(link);
		});
		data = buffer.array();
	}

	private void putLink(ByteBuffer buffer, Link link) {

		linkIdMapping.put(link.getId(), (float) currentLinkId++);
		buffer.putFloat(currentLinkId);
		buffer.putFloat((float) link.getFromNode().getCoord().getX());
		buffer.putFloat((float) link.getFromNode().getCoord().getY());
		buffer.putFloat((float) link.getToNode().getCoord().getX());
		buffer.putFloat((float) link.getToNode().getCoord().getY());
	}

	private void adjustBoundingRectangle(Link link) {
		adjustBoundingRectangle(link.getFromNode());
		adjustBoundingRectangle(link.getToNode());
	}

	private void adjustBoundingRectangle(Node node) {
		minEasting = Math.min(minEasting, node.getCoord().getX());
		maxEasting = Math.max(maxEasting, node.getCoord().getX());
		minNorthing = Math.min(minNorthing, node.getCoord().getY());
		maxNorthing = Math.max(maxNorthing, node.getCoord().getY());
	}

}
