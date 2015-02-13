vzome-desktop
=============

[vZome](http://vzome.com/) is a desktop application for creating virtual Zome models.  This project contains the source code for just the controllers and views of the user interface; it requires the [vzome-core](https://github.com/vorth/vzome-core/) library to provide all the models and basic behaviors, including loading and saving files.

Build Instructions
-------------

(These instructions are primarily for Linux or Mac, or any other Unix but some basic functionality has also been adapted for Windows.)

The build is implemented using Maven, and most dependencies are captured in the POM file.  Most of the dependencies will be pulled by Maven automatically, but there are two special requirements.  

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

Having done those steps, the standard Maven build command should work fine:

    mvn clean install

Building with Gradle
-------------

Limited support for Graven is also provided. Graven a command line build tool. After the Maven repositories have been seeded as described above, Gradle can be installed in a Windows environment by running 

	.\gradlew.bat. 
	
Thereafter, vZome-desktop can be built by running the following command from within the vZome-desktop directory:

	./gradlew build

Developing with Eclipse
-------------

Maven has great support for IDE integration.  I use Eclipse, so the Maven command for generating an Eclipse project is extremely helpful to me:

    mvn eclipse:eclipse

This command must be executed twice. Once from within the vzome-core folder and again in the vzome-desktop folder before the two projects can be opened by Eclipse. An Eclipse launch configuration file (vzome-desktop.launch) is included. It will be used in the generated Eclipse project for vzome-desktop.

Existing projects are opened in Eclipse by using "File | Import... | Existing Projects into Workspace" and selecting the folder where the mvn command was executed (vzome-core and/or vzome-desktop).

Developing with NetBeans IDE
-------------

The NetBeans IDE supports Maven projects natively. NetBeans reads the pom.xml and that is the NetBeans project. Simply use File | Open Project and select the folder with the pom.xml file. 

A NetBeans configuration file (nbactions.xml) is included.


Running vZome from the Command Line
--------------

Once you have successfully built vZome, you can run it using "run-vzome.sh".  You'll have to make the script executable, first, using "chmod u+x run-vzome.sh". To run vZome in Windows, use run-vzome.bat. In Windows PowerShell, the syntax is .\run-vzome.bat.

Packaging vZome
---------------

I have not yet ported the the packaging steps for the self-contained Mac application, nor the deployment script for the JavaWebStart version.  There is no self-contained packaging for Windows, yet.

