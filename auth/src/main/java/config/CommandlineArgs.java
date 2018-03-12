package config;

import com.beust.jcommander.Parameter;

public class CommandlineArgs {

    @Parameter(names = {"-config", "-c"}, description = "path to a configuration file")
    public String configFile;
}
