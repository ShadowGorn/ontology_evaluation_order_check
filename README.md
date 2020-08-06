## SWRLAPI based parsing c language, build evaluation order DAG and fault reasons determining

https://taiga.seschool.ru/project/oasychev-modelirovanie-ponimaniia-iadro/issue/17

### Building and Running

To build and run this project you must have the following items installed:

+ [Java 9](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
+ A tool for checking out a [Git](http://git-scm.com/) repository
+ Apache's [Maven](http://maven.apache.org/index.html)

Build it with Maven:
    mvn clean install

On build completion, your local Maven repository will contain generated ```swrlapi-example-${version}.jar```
and ```swrlapi-example-${version}-jar-with-dependencies.jar``` files.
The ```./target``` directory will also contain these JARs.

You can then run the application as follows:

    mvn exec:java

![Java CI with Maven](https://github.com/ShadowGorn/ontology_cpp_parsing/workflows/Java%20CI%20with%20Maven/badge.svg)
