# example-project
`master`![status?](https://github.com/matsim-melbourne/example-project/workflows/build/badge.svg?branch=master) `dev`![status?](https://github.com/matsim-melbourne/example-project/workflows/build/badge.svg?branch=dev)

An example MATSim model for Melbourne.

By default, this project uses the latest MATSim development head. In order to use a different version, edit [`pom.xml`](./pom.xml).


### Import into IntelliJ

`File -> New -> Project from Version Control` paste the repository url and hit 'clone'. IntelliJ usually figures out that the project is a maven project. If not: `Right click on pom.xml -> import as maven project`.

### Java version

The project uses Java 11. Usually a suitable SDK is packaged within IntelliJ or Eclipse. Otherwise, one must install a suitable sdk manually, which is available [here](https://openjdk.java.net/).

### Building the model

You can build an executable jar-file by executing the following command:

```sh
mvn clean package
```
This will download all necessary dependencies (it might take a while the first time it is run) and create `java -jar target/example-project-1.0-SNAPSHOT-jar-with-dependencies.jar`.

### Running the example scenario

**Getting the scenario data**: Large scenario related input files are not stored in this git repository and must first be downloaded (see [`./scenario/data/README.md`](./scenario/data/README.md)).


This example model jar-file can then be executed with Java on the command line using:

```sh
java -jar -Xmx8g -Xms8g target/example-project-1.0-SNAPSHOT-jar-with-dependencies.jar
```
