## How to set up the project for development

Before one can work on the project some preperations need to be done.

### 1. Set up your IDE

The project is using [Lombok](https://projectlombok.org/) to generate getters 
and setters.You need to install a plugin, so that the IDE gives IntelliSense 
support for the generated methods. In IntelliJ you can do so by selecting
File -> Settings. In the settings menu select Plugins -> Browse repositories. In
the repository browser Search for 'lombok' and click 'install'.

### 2. Compile the project

The project is also using [QueryDSL](http://www.querydsl.com/) for typesafe 
queries against a database. The library relies on generated source code which,
well, needs to be generated before the first run. To do so, simply execute

```
mvn clean compile
```

### 3. Build the project for deployment

To build deployable artifacts simply run the maven install command

```
mvn clean install
```

This will build fat jars for all server-modules. (Currently, there are three server-components, auth, files, frame-animation) To run one of the built components find the fat jar in the component's target folder and execute 

```
java -jar <jar-name>.jar server <path/to/config/file>
```
NOTE: Jre8 or newer is required.

This will start the component as a server as configured in the config file.

### 4. Start the servers with a config file

All server components use the [dropwizard](https://dropwizard.io) server framework which also defines a configuration file format. All possible options are documented [here](https://www.dropwizard.io/1.3.5/docs/manual/configuration.html#man-configuration) 

Additionally, each component defines its own configuration properties. They can be found within the following classes. 

```
org.matsim.viz.auth.config.AppConfiguration
org.matsim.viz.files.config.AppConfiguration
org.matsim.viz.frameAnimation.config.AppConfiguration
```

Refer to example configurations in the [wiki](https://github.com/matsim-org/viz-server/wiki)

Since OAuth and OpenID-Connect are used for authentication all server components must be configured to use TLS! If run as standalone Jetty-Servers (dropwizard comes with a jetty server) all application connectors must be of type 'https'. Since, https-connectors require TLS-certificates whithin a JKS-Keystore it might be easier to run a reverse proxy server which terminates TLS-connections and forwards requests to the corresponding components. This way the management and renewal of TLS-certificates becomes easier.

### 5. Deployment

The project has a continous deployment pipeline. On each pullrequest into develop or master a travis build is started which deploys the generated artifacts to AWS-Elastic-Beanstalk. The deployment script can be found within the 'aws-deploy' folder. As soon as this project becomes more stable a second deployment will be started, to have separate staging and production environments. 

The current test deployment can be found at https://viz.matsim.org
