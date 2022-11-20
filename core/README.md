vzome-core
==========

[vZome](http://vzome.com/) is a desktop application for editing virtual Zome models, or similar geometry.  This subproject contains the core behaviors and data model of vZome, without any of the user interface.

Build instructions
-----------

To build the vzome-core library JAR file, execute this command:

    ./gradlew clean core:build

Regression testing
----------

There is a "regression" task that is not automatically triggered, since it takes a little while to run.  It validates that the build can open a few dozen existing vZome files.

    ./gradlew regression

Eclipse integration
-----------

You can generate an Eclipse project by executing this command:

    ./gradlew core:eclipse
    
I have found that the generated project is not quite perfect, having some redundancies in the build path that Eclipse will complain about.  It is probably better to install an Eclipse Gradle plugin, which will allow Eclipse to open the project just based on the build.gradle file.  I have no experience with any such plugin, yet.




