package org.matsim.webvis.frameAnimation.config;

import com.beust.jcommander.Parameter;
import lombok.Getter;

@Getter
public class CommandlineArgs {

    @Parameter(names = {"-config", "-c"}, description = "path to a configuration file", required = true)
    private String configFile = "";

    @Parameter(names = {"-trust-selfsigned-tls"}, description = "if this is set TLS certificates not signed by a CA are trusted during communication with other services")
    private boolean trustSelfsignedTLSCertificates = false;
}
