package org.matsim.viz.frameAnimation.persistenceModel;

import lombok.val;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.core.network.NetworkUtils;
import org.matsim.viz.frameAnimation.utils.TestUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class MatsimNetworkTest {

    private static Network network;

    @BeforeClass
    public static void setUpClass() {
        network = TestUtils.loadTestNetwork();
    }

    @Test
    public void initMatsimNetwork_metadataShouldBeSet() {

        val result = new MatsimNetwork(network);

        // magic numbers are coming from the test network
        assertEquals(192, result.getData().length); //12 links * 4 values * 4 byte, assuming we have 32 bit floats
        assertEquals(-2500.0, result.getMinEasting(), 0.1);
        assertEquals(-1000.0, result.getMinNorthing(), 0.1);
        assertEquals(1000.0, result.getMaxEasting(), 0.1);
        assertEquals(400.0, result.getMaxNorthing(), 0.1);
    }

    @Test
    public void initMatsimNetwork_encodedNetwork() {

        val network = NetworkUtils.createNetwork();
        val links = new ArrayList<Link>();
        for (int i = 0; i < 10000; i++) {
            val fromX = (Math.random() - 0.5) * 1000;
            val fromY = (Math.random() - 0.5) * 1000;
            val toX = (Math.random() - 0.5) * 1000;
            val toY = (Math.random() - 0.5) * 1000;
            links.add(addLinkToNetwork(new Coord(fromX, fromY), new Coord(toX, toY), network));
        }

        val result = new MatsimNetwork(network);

        assertEquals(160000, result.getData().length);//10000 links * 4 values * 4 byte [because we encode in 32bit floats]
        val buffer = ByteBuffer.wrap(result.getData());

        links.forEach(link -> {
            assertEquals(link.getFromNode().getCoord().getX(), buffer.getFloat(), 0.001);
            assertEquals(link.getFromNode().getCoord().getY(), buffer.getFloat(), 0.001);
            assertEquals(link.getToNode().getCoord().getX(), buffer.getFloat(), 0.001);
            assertEquals(link.getToNode().getCoord().getY(), buffer.getFloat(), 0.001);
        });

        assertFalse(buffer.hasRemaining());
    }

    private Link addLinkToNetwork(Coord from, Coord to, Network network) {

        val link = createLink(from, to, network.getFactory());
        network.addNode(link.getFromNode());
        network.addNode(link.getToNode());
        network.addLink(link);
        return link;
    }

    private Link createLink(Coord from, Coord to, NetworkFactory networkFactory) {

        val fromNode = networkFactory.createNode(Id.createNodeId(UUID.randomUUID().toString()), from);
        val toNode = networkFactory.createNode(Id.createNodeId(UUID.randomUUID().toString()), to);
        return networkFactory.createLink(Id.createLinkId(UUID.randomUUID().toString()), fromNode, toNode);
    }
}
