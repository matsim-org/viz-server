package org.matsim.webvis.auth;

import com.beust.jcommander.JCommander;
import org.matsim.webvis.auth.config.*;
import org.matsim.webvis.auth.entities.RelyingParty;
import org.matsim.webvis.auth.entities.User;
import org.matsim.webvis.auth.relyingParty.RelyingPartyService;
import org.matsim.webvis.auth.user.UserService;
import org.matsim.webvis.common.communication.StartSpark;

public class Server {

    public static void main(String[] args) {

        CommandlineArgs ca = new CommandlineArgs();
        JCommander.newBuilder().addObject(ca).build().parse(args);

        try {
            loadConfigFile(ca);
        } catch (Exception e) {
            //TODO logger.error(e);
            System.exit(100);
        }

        startSparkServer(ca);
    }

    static void loadConfigFile(CommandlineArgs ca) throws Exception {

        if (!ca.getConfigFile().isEmpty())
            Configuration.loadConfigFile(ca.getConfigFile(), ca.isDebug());

        UserService userService = UserService.Instance;
        for (ConfigUser user : Configuration.getInstance().getUsers()) {
            User created = userService.createUser(user);
            //TODO logger.info("Created User: " + created.getEMail());
        }

        RelyingPartyService relyingPartyService = RelyingPartyService.Instance;
        for (ConfigClient client : Configuration.getInstance().getClients()) {
            RelyingParty created = relyingPartyService.createClient(client);
            //TODO logger.info("Created client: " + created.getName());
        }

        for (ConfigRelyingParty party : Configuration.getInstance().getProtectedResources()) {
            RelyingParty created = relyingPartyService.createRelyingParty(party);
            //TODO logger.info("Created relying party: " + created.getName());
        }
    }

    private static void startSparkServer(CommandlineArgs ca) {

        StartSpark.withPort(Configuration.getInstance().getPort());
        StartSpark.withInitializationExceptionHandler(Server::handleInitializationFailure);

        if (!ca.isDebug() || !Configuration.getInstance().getTlsKeyStore().isEmpty()) {
            StartSpark.withTLS(Configuration.getInstance().getTlsKeyStore(), Configuration.getInstance().getTlsKeyStorePassword(), null, null);
        }

        StartSpark.withPermissiveAccessControl();
        StartSpark.withExceptionMapping();

        try {
            Routes.initialize();
        } catch (Exception e) {
            handleInitializationFailure(e);
        }

        //TODO logger.info("\n\nStarted auth Server on Port: " + Configuration.getInstance().getPort() + "\n");
    }

    private static void handleInitializationFailure(Exception e) {

        String tlsKeyStore = Configuration.getInstance().getTlsKeyStore();
        String tlsKeyStorePassword = Configuration.getInstance().getTlsKeyStorePassword();

        String signingKeyStore = Configuration.getInstance().getTokenSigningKeyStore();
        String signingKeyStorePassword = Configuration.getInstance().getTokenSigningKeyStorePassword();

        if (tlsKeyStore == null || tlsKeyStore.isEmpty()) {
            //TODO logger.error("\n\nInitialization failed. TlsKeystore location was not present.\n\n");
        } else if (tlsKeyStorePassword == null || tlsKeyStorePassword.isEmpty()) {
            //TODO logger.error("\n\nInitialization failed. TlsKeystore password was not present\n\n");
        } else if (signingKeyStore == null || signingKeyStore.isEmpty()) {
            //TODO logger.error("\n\nInitialization failed. Signing keystore was not present\n\n");
        } else if (signingKeyStorePassword == null || signingKeyStorePassword.isEmpty()) {
            //TODO logger.error("\n\nInitialization failed. Signing keystore password was not present\n\n");
        } else {
            //TODO logger.error("\n\nInitialization failed.\n\n");
        }
        //TODO logger.error("Exception which caused failure was: ", e);
        System.exit(100);
    }
}
