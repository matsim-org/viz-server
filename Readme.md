## How to set up the project for development

Before one can work on the project some preperations need to be done.

### 1. Set up your IDE

The project is using [Lombok] (https://projectlombok.org/) to generate getters 
and setters.You need to install a plugin, so that the IDE gives IntelliSense 
support for the generated methods. In IntelliJ you can do so by selecting
File -> Settings. In the settings menu select Plugins -> Browse repositories. In
the repository browser Search for 'lombok' and click 'install'.

### 2. Compile the project

The project is also using [QueryDSL] (http://www.querydsl.com/) for typesafe 
queries against a database. The library relies on generated source code which,
well, needs to be generated before the first run. To do so, simply execute

```
mvn clean compile
```

### 3. Generate Keys for TLS-Communication and Token-Signing

Since we are using OAuth for Authorization the use of TLS (https) is required. 
For development the use of self signed keys is sufficient. The Java SDK comes 
with the [keytool] (https://docs.oracle.com/javase/8/docs/technotes/tools/unix/keytool.html) 
for that purpose. To generate RSA-Keys for TLS-communication execute the following
command:

```
keytool -genkeypair -keyalg RSA -keysize 2048 -keystore keystore.jks -storetype JKS -alias selfsigned -validity 180 -dname "cn=Janek Laudan ou=VSP o=TU Berlin c=DE" -ext SAN=URI:https://localhost
```

This command generates a keystore file named 'keystore.jks' which can be used by 
the auth-server component to encrypt its communication with clients. To consume
this key as a client, the client needs to trust the public key of the generated
RSA-Key. A client could be the file-server component when doing token introspection.
To make the file-server trust the auth-server certificate it must be exported into
a truststore. Execute the following commmands to create a truststore:

```
keytool -keystore keystore.jks -alias selfsigned -export -file selfsigned.cert
keytool -keystore truststore.jks -alias selfsigned -import -file selfsigned.cert
```

The public part of the RSA key is first exported as a certificate and then imported
into a truststore file named 'truststore.jks'.

### 4. Start the servers with a config file

All server components can be configured with a configuration file in JSON-Format.
To load a configuration file you can add the following parameter when starting the
program:

```
-config /path/to/your/config/file.json
```

### 5. Build for deployment

The server components use [sparkjava] (http://sparkjava.com/) for network communication.
This framework comes with a build in Jetty server. To deploy it a 'fat jar' with all
dependencies bundled within that jar-file needs to be generated. To achieve this the maven
package command must be executed like this

```
mvn clean package assembly:single
```

The working directory must be set to the component that's supposed to be bundled. If you
want to create a 'fat jar' for the auth server the working directory should be `<project-foler>/auth`.
For the file server the working directory is `<project-folder>/files`.

At the moment both components must be packaged individually.