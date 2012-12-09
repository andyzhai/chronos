# CHRONOS - Timeline made easy

CHRONOS is a simple to use and deploy web based application to easily manage your subject timeline.

## Requirements

* Vert.X IO 1.3.0 or above
* Maven 3 or above
* JDK 6 or 7 (Maven POM configured for 7 by default)

## Build from source

Download the distribution code from github in either a zip file or by means of a git clone. 
If zip file was used, unpack the contents into a directory of your choice.

From the root directory execute:

<mvn clean install>

A package named "chronos-X.X.X-SNAPSHOT-vertx.module.zip" should now be available. 
Note that a tar.gz and tar.bz2 package are also available for convenience.


## Install and execute as a Vert.X module

Unpack the generated "chronos-X.X.X-SNAPSHOT-vertx.module.zip" file into VERTX_HOME/mods directory.

The web application can then be initialized with vertx:

<vertx runmod chronos-X.X.X-SNAPSHOT>

## Run from Maven

You can also start the server from Maven by executing:

<mvn vertx:run>

For such case, no installation in Vert.X module directory is required.

## Run from Eclipse

If you decide to import the Maven project in Eclipse, then you can always use the provided Eclipse launcher file :

<chronos.launch>

