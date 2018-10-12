package org.matsim.viz.frameAnimation.data;

public class NetworkDataTest {
/*
    private static Network network;
    private NetworkData testObject;

    @BeforeClass
    public static void setUpClass() {
        network = TestUtils.loadTestNetwork();
    }

    @Before
    public void setUp() {
        testObject = new NetworkData(new QuadTree.Rect(-10000, -10000, 10000, 10000));
    }

    @Test
    public void addLink() throws IOException {

        //act
        Collection<? extends Link> values = network.getLinks().values();
        for (Link link : values) {
            testObject.addLink(link);
        }

        //assert
        byte[] links = testObject.getLinks(new QuadTree.Rect(-5000, -5000, 5000, 5000));
        ByteBuffer buffer = ByteBuffer.wrap(links);
        buffer.order(ByteOrder.BIG_ENDIAN);

        while (buffer.position() < buffer.capacity()) {

            float fromX = buffer.getFloat();
            float fromY = buffer.getFloat();
            float toX = buffer.getFloat();
            float toY = buffer.getFloat();

            //the order of the returned values is not necessarily the same as the input order
            boolean found = values.stream().anyMatch(link ->
                                                             (float) link.getFromNode().getCoord().getX() == fromX &&
                                                                     (float) link.getFromNode().getCoord().getY() == fromY &&
                                                                     (float) link.getToNode().getCoord().getX() == toX &&
                                                                     (float) link.getToNode().getCoord().getY() == toY);
            assertEquals(true, found);
        }
        assertEquals(buffer.position(), buffer.capacity());
    }

    @Test
    public void getLinks() throws IOException {

        //act
        Collection<? extends Link> values = network.getLinks().values();
        for (Link link : values) {
            testObject.addLink(link);
        }

        //assert
        byte[] links = testObject.getLinks(new QuadTree.Rect(-5000, -5000, 5000, 5000));
        ByteBuffer buffer = ByteBuffer.wrap(links);
        buffer.order(ByteOrder.BIG_ENDIAN);

        while (buffer.position() < buffer.capacity()) {

            float fromX = buffer.getFloat();
            float fromY = buffer.getFloat();
            float toX = buffer.getFloat();
            float toY = buffer.getFloat();

            //the order of the returned values is not necessarily the same as the input order
            boolean found = values.stream().anyMatch(link ->
                                                             (float) link.getFromNode().getCoord().getX() == fromX &&
                                                                     (float) link.getFromNode().getCoord().getY() == fromY &&
                                                                     (float) link.getToNode().getCoord().getX() == toX &&
                                                                     (float) link.getToNode().getCoord().getY() == toY);
            assertEquals(true, found);
        }
        assertEquals(buffer.position(), buffer.capacity());
    }

    @Test
    public void getBounds() {
        //act
        RectContract bounds = testObject.getBounds();

        //assert
        assertEquals(bounds.getLeft(), -10000, 0.001);
        assertEquals(bounds.getTop(), 10000, 0.001);
        assertEquals(bounds.getRight(), 10000, 0.001);
        assertEquals(bounds.getBottom(), -10000, 0.001);
    }
    */
}
