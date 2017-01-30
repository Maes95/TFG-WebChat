JAVA_HOME=/usr/lib/jvm/java-8-oracle
mvn "-Dexec.args=-classpath %classpath com.globex.app.ChatManager" -Dexec.executable=/usr/lib/jvm/java-8-oracle/bin/java org.codehaus.mojo:exec-maven-plugin:1.2.1:exec
