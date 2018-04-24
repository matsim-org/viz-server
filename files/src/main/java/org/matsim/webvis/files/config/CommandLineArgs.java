package org.matsim.webvis.files.config;

import com.beust.jcommander.Parameter;
import lombok.Getter;

@Getter
public class CommandLineArgs {

    @Parameter(names = {"-debug", "-d"}, description = "enables debug mode. NEVER use this flag in production! It enables" +
            "the use of plain http communication. OAuth must not be used without TLS in production!")
    private boolean debug = false;

    @Parameter(names = {"-config", "-c"}, description = "path to a configuration file")
    private String configFile = "";
}
