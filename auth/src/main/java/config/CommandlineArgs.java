package config;

import com.beust.jcommander.Parameter;

public class CommandlineArgs {

    @Parameter(names = {"-debug", "-d"}, description = "enables debug mode. NEVER use this flag in production! It enables" +
            "the use of plain http communication. OAuth must not be used without TLS in production!")
    public boolean debug = false;

    @Parameter(names = {"-config", "-c"}, description = "path to a configuration file")
    public String configFile;
}
