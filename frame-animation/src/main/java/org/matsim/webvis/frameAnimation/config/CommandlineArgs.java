package org.matsim.webvis.frameAnimation.config;

import com.beust.jcommander.Parameter;
import lombok.Getter;

@Getter
public class CommandlineArgs {

    @Parameter(names = {"-config", "-c"}, description = "path to a configuration file", required = true)
    private String configFile = "";
}
