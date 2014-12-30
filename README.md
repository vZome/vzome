vzome-desktop
=============

vZome is a desktop application for creating virtual Zome models.  This project contains the source code for just the controllers and views of the user interface; it requires the *vzome-core* library to provide all the models and basic behaviors, including loading and saving files.

Build Instructions
-------------

(These instructions are for Linux or Mac, or any other Unix.  They can be adapted for Windows fairly easily.)

The build is implemented using Maven, and most dependencies are captured in the POM file.  However, vZome still uses Java3d, a somewhat outmoded Java scenegraph technology, and the latest version of Java3d is not available in any Maven repository.  Therefore, you must seed your local Maven repository with the Java3d JAR files before attempting the build, using the following steps:

    curl http://jogamp.org/deployment/java3d/1.6.0-pre11/vecmath.jar > vecmath.jar
    mvn install:install-file -DgroupId=org.jogamp.java3d -Dpackaging=jar -Dversion=1.6.0 -DartifactId=vecmath -Dfile=vecmath.jar
    rm vecmath.jar

    curl http://jogamp.org/deployment/java3d/1.6.0-pre11/j3dutils.jar > j3dutils.jar
    mvn install:install-file -DgroupId=org.jogamp.java3d -Dpackaging=jar -Dversion=1.6.0 -DartifactId=j3dutils -Dfile=j3dutils.jar
    rm j3dutils.jar

    curl http://jogamp.org/deployment/java3d/1.6.0-pre11/j3dcore.jar > j3dcore.jar
    mvn install:install-file -DgroupId=org.jogamp.java3d -Dpackaging=jar -Dversion=1.6.0 -DartifactId=j3dcore -Dfile=j3dcore.jar
    rm j3dcore.jar

You don't really need to use "curl" to download the JARs, of course.  The key step is the "mvn install:install-file" command, which seeds your local Maven repository.

Having done that step, the standard Maven build command should work fine:

    mvn clean install

Running vZome from the Command Line
--------------

Once you have successfully built vZome, you can run it using "run-vzome.sh".  You'll have to make the script executable, first, using "chmod u+x run-vzome.sh".

Packaging vZome
---------------

I have not yet ported the the packaging steps for the self-contained Mac application, nor the deployment script for the JavaWebStart version.  There is no self-contained packaging for Windows, yet.

