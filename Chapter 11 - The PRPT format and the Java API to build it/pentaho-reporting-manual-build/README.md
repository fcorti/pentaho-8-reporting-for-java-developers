Pentaho Reporting Manual Build
===

This web application renders a Pentaho Report programmatically defined in the Java source code using the Pentaho Report Engine.
To compile the project open a terminal, move into this folder and execute the command below.

    mvn clean install

To package the project open a terminal, move into this folder and execute the command below.

    mvn package

To run the project open a terminal, move into this folder and execute the command below.

    java -jar target/dependency/jetty-runner.jar target/*.war

Then open a browser and access to the page below for the described examples.

    http://localhost:8080

# Disclaimer

All the examples contained into this repository are developed using an Ubuntu Operating System v16.04 LTS with 4Gb of RAM and Processor Intel i7. 

The environment is composed by: Java JVM 1.8.0_131, Apache Maven 3.3.9, git version 2.7.4.

We can't be responsible for any damage done to your system, which hopefully will not happen.
