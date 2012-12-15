# CHRONOS - Timeline made easy

CHRONOS is a simple to use and deploy web based application used to easily manage any given subject timeline.

Check the demo at [Openshift](http://chronos-cfp.rhcloud.com)

## Requirements

* Vert.X IO 1.3.0 or above
* Maven 3 or above
* JDK 6 or 7 (Maven POM configured for 7 by default)
* MongoDB 2.0 or above
* git


## 1: Openshift cloud deployment in 15 minutes! (Good for non-developers)

* Login into Openshift (Create account if required)

### Create Application from github

* Add Application of type Do-It-Yourself
* Set you application name for the public URL 
* On Source Code click on change and paste the git url for CHRONOS: git://github.com/crazyfrozenpenguin/chronos.git
* Then click on Create Application

### Add MongoDB cartridge

* Click on My Applications
* Click on your application name
* Click on Add Cartridge
* Click on Select for MongoDB NoSQL Database
* Click on Add Cartridge

### Clone Openshift Repo

* Open a terminal window and create a working directory of your choice

* Clone git repo using your openshift git repository address for your app (see web page for app).
  Substitute the ssh address below with the one from your app:
	git clone ssh://xxxxxxxxxxxxxxxxxxxxxxxxxx@yyyy-cfp.rhcloud.com/~/git/yyyy.git/
	
* cd into the new directory that should have your app name on it and execute:
	git init
	touch go.txt
	git add go.txt
	git commit -a -m "GO"
	git push ssh://xxxxxxxxxxxxxxxxxxxxxxxxxx@yyyy-cfp.rhcloud.com/~/git/yyyy.git/
	
* Open a browser and browse to you app URL
* Login into the app using the default admin account:
	username: admin
	password: password

* Change the password on the options web page
* Edit the timeline, save it and it should be visible on the main page

At this stage the app is only supporting a single login account. You can always SSH into openshift and manage the mongo database. 
The database has your app name and the user is stored under the users table. Change the username and password as wanted.


## 2: Build from source and run locally (Good for developers)

### Download and build

Download the distribution code from github in either a zip file or by means of a git clone. 
If zip file was used, unpack the contents into a directory of your choice.

From the root directory execute:

	mvn clean install

A package named **chronos-X.X.X-SNAPSHOT-vertx.module.zip** should now be available. 
Note that a tar.gz and tar.bz2 package are also available for convenience.


### Install and execute as a Vert.X module

Unpack the generated **chronos-X.X.X-SNAPSHOT-vertx.module.zip** file into VERTX_HOME/mods directory.

The web application can then be initialized with vertx:

	vertx runmod chronos-X.X.X-SNAPSHOT


### Run from Maven

You can also start the server from Maven by executing:

	mvn vertx:run -Dchronos.webroot.dir=./target/classes/webroot

For such case, no installation in Vert.X module directory is required.


### Run from Eclipse

If you decide to import the Maven project in Eclipse, then you can always use the provided Eclipse launcher file :

	chronos.launch


# Used Technologies

[vert.x](http://vertx.io/)
[Knockout JS](http://knockoutjs.com/)
[Twitter Bootstrap](http://twitter.github.com/bootstrap)
[Timeline JS](http://timeline.verite.co/)
[flipCounter](http://bloggingsquared.com/jquery/flipcounter/)
[Openshift](https://openshift.redhat.com) 

And the usual [jQuery](http://jquery.com/), [Maven](http://maven.apache.org/index.html) and several of their related plugins.
