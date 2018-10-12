package org.matsim.viz.auth.token;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;

public class InactiveIntrospectionResponseTest {

    @Test
    public void constructor_allParametersSet_instance() {

        InactiveIntrospectionResponse instance = new InactiveIntrospectionResponse();
        assertFalse(instance.isActive());
    }
}
