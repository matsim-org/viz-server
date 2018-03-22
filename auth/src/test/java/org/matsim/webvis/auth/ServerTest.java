package org.matsim.webvis.auth;

import com.beust.jcommander.JCommander;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.auth.config.CommandlineArgs;
import org.matsim.webvis.auth.config.Configuration;
import org.matsim.webvis.auth.relyingParty.RelyingPartyDAO;
import org.matsim.webvis.auth.user.UserDAO;

import java.io.FileNotFoundException;
import java.net.URLDecoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ServerTest {

    @Before
    public void setUp() {
        Configuration.clearConfig();
    }

    @After
    public void tearDown() {
        new RelyingPartyDAO().removeAllClients();
        new UserDAO().removeAllUsers();
    }

    @Test
    public void loadConfigFile_noConfigFile_defaultConfig() throws Exception {

        CommandlineArgs args = new CommandlineArgs();

        org.apache.logging.log4j.LogManager.getLogger().info("config file name" + args.getConfigFile());
        Server.loadConfigFile(args);

        assertEquals(3000, Configuration.getInstance().getPort());
        assertEquals(0, Configuration.getInstance().getUsers().size());
        assertEquals(0, Configuration.getInstance().getClients().size());
    }

    @Test(expected = FileNotFoundException.class)
    public void loadConfigFile_invalidFileName_exception() throws Exception {

        String[] args = new String[]{"-c", "invalid/config/path"};
        CommandlineArgs commandlineArgs = new CommandlineArgs();
        JCommander.newBuilder().addObject(commandlineArgs).build().parse(args);

        Server.loadConfigFile(commandlineArgs);

        fail("invalid config file path should throw exception");
    }

    @Test
    public void loadConfigFile_configFile_loadTestConfig() throws Exception {

        String testconfig = URLDecoder.decode(this.getClass().getResource("/test-config.json").getFile(), "UTF-8");
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
