package org.matsim.viz.files.serialization;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@JsonIdentityInfo(
        generator = JSOGGenerator.class
)
public abstract class AbstractEntityMixin {
}
