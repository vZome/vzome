vzome-desktop
=============

[vZome](http://vzome.com/) is a desktop application for creating virtual Zome models.  This project contains the source code for just the controllers and views of the user interface; it requires the [vzome-core](https://github.com/vorth/vzome-core/) library to provide all the models and basic behaviors, including loading and saving files.

Build Instructions
-------------

(These instructions are primarily for Linux or Mac, or any other Unix, but some basic functionality has also been adapted for Windows.)

The build was originally implemented using Maven but now uses Gradle with a local Maven repository. Most of the dependencies will be pulled from the Maven repositories automatically, but there are two special requirements.

First is the [vzome-core](https://github.com/vorth/vzome-core/) project, which must be built locally, so that the JAR appears in your local Maven repository to satisfy the dependency.

The second special requirement is Java3d, and the latest version of Java3d is not available in any Maven repository.  Therefore, you must seed your local Maven repository with the Java3d JAR files before attempting the build, using the following steps:

    curl http://jogamp.org/deployment/java3d/1.6.0-pre11/vecmath.jar > vecmath.jar
    mvn install:install-file -DgroupId=org.jogamp.java3d -Dpackaging=jar -Dversion=1.6.0 -DartifactId=vecmath -Dfile=vecmath.jar
    rm vecmath.jar

    curl http://jogamp.org/deployment/java3d/1.6.0-pre11/j3dutils.jar > j3dutils.jar
    mvn install:install-file -DgroupId=org.jogamp.java3d -Dpackaging=jar -Dversion=1.6.0 -DartifactId=j3dutils -Dfile=j3dutils.jar
    rm j3dutils.jar

    curl http://jogamp.org/deployment/java3d/1.6.0-pre11/j3dcore.jar > j3dcore.jar
    mvn install:install-file -DgroupId=org.jogamp.java3d -Dpackaging=jar -Dversion=1.6.0 -DartifactId=j3dcore -Dfile=j3dcore.jar
    rm j3dcore.jar

You don't really need to use "curl" to download the JARs, of course. The jar files can also be downloaded interactively from a browser. The key step is the "mvn install:install-file" command, which seeds your local Maven repository.

Having done those steps, the standard Gradle build command should work fine. For Mac or Unix:

    gradle clean build
	
... or in Windows PowerShell:

	./gradlew clean build
	
Note that vzome-core is not yet converted to use Gradle, so it must be built using Maven:

	mvn clean install

Building with Gradle
-------------

Gradle is a command line build tool. After the Maven repositories have been seeded as described above, Gradle can be installed in a Windows environment by running 

	.\gradlew.bat. 
	
Thereafter, vzome-desktop can be built by running the following command from within the vzome-desktop directory:

	.\gradlew build

Developing with Eclipse
-------------

Maven has great support for IDE integration.  I use Eclipse, so the Maven command for generating an Eclipse project is extremely helpful to me:

    mvn eclipse:eclipse

This command must be executed twice. Once from within the vzome-core folder and again in the vzome-desktop folder before the two projects can be opened by Eclipse. An Eclipse launch configuration file (vzome-desktop.launch) is included. It will be used in the generated Eclipse project for vzome-desktop.

Existing projects are opened in Eclipse by using "File | Import... | Existing Projects into Workspace" and selecting the folder where the mvn command was executed (vzome-core and/or vzome-desktop).

TODO: When the two projects are fully moved to Gradle, update these build instructions for Eclipse.

Developing with NetBeans IDE
-------------

The NetBeans IDE supports Gradle projects with the Gradle plug-in available at http://plugins.netbeans.org/plugin/44510/gradle-support. Version 1.3.2 of the plug-in has been used with NetBeans 8.0. The Gradle plug-in should be installed in NetBeans before attempting to load the vzome-desktop project. For the vzome-core project, NetBeans reads the build.gradle or pom.xml and that is the NetBeans project. Simply use File | Open Project and select vzome-desktop/build.gradle and/or vzome-core/pom.xml. Note that if NetBeans finds both a build.gradle and a pom.xml file, it will revert to using pom.xml. This is NOT correct, so be sure that vzome-desktop includes build.gradle and vzome-core uses pom.xml

A NetBeans configuration file (nbactions.xml) is included.


Running vZome from the Command Line
--------------

Once you have successfully built vZome, you can run it using "run-vzome.sh".  You'll have to make the script executable, first, using "chmod u+x run-vzome.sh". To run vZome in Windows, use run-vzome.bat. In Windows PowerShell, the syntax is .\run-vzome.bat.

It can also be run as a Gradle task using the command:

	gradle run
	
... or in Windows PowerShell:

	./gradlew run

Packaging vZome
---------------

I have not yet ported the the packaging steps for the self-contained Mac application, nor the deployment script for the JavaWebStart version.  There is no self-contained packaging for Windows, yet.

