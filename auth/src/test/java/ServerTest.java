import com.beust.jcommander.JCommander;
import config.CommandlineArgs;
import config.Configuration;
import org.junit.After;
import org.junit.Test;
import relyingParty.RelyingPartyDAO;
import user.UserDAO;

import java.net.URLDecoder;

import static org.junit.Assert.assertEquals;

public class ServerTest {

    @After
    public void tearDown() {
        new RelyingPartyDAO().removeAllClients();
        new UserDAO().removeAllUsers();
    }

    @Test
    public void loadConfigFile_noConfigFile_defaultConfig() throws Exception {

        CommandlineArgs args = new CommandlineArgs();

        Server.loadConfigFile(args);

        assertEquals(3000, Configuration.getInstance().getPort());
        assertEquals(0, Configuration.getInstance().getUsers().size());
        assertEquals(0, Configuration.getInstance().getClients().size());
    }

    @Test
    public void loadConfigFile_configFile_loadTestConfig() throws Exception {

        String testconfig = URLDecoder.decode(this.getClass().getResource("test-config.json").getFile(), "UTF-8");
        String[] args = new String[]{"-c", testconfig};
        CommandlineArgs commandlineArgs = new CommandlineArgs();
        JCommander.newBuilder().addObject(commandlineArgs).build().parse(args);

        Server.loadConfigFile(commandlineArgs);

        assertEquals(3000, Configuration.getInstance().getPort());
        assertEquals(1, Configuration.getInstance().getUsers().size());
        assertEquals(1, Configuration.getInstance().getClients().size());
        assertEquals(1, Configuration.getInstance().getProtectedResources().size());
    }
}
