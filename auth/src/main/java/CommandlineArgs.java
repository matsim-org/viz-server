import com.beust.jcommander.Parameter;

public class CommandlineArgs {

    @Parameter(names = {"-debug", "-d"}, description = "boolean value for debug/development mode" +
            "always set to false in production! default is false")
    public boolean debug = false;
}
