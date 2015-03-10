vzome-core
==========

[vZome](http://vzome.com/) is a desktop application for editing virtual Zome models, or similar geometry.  This project contains the core behaviors of vZome, without any of the user interface.  The API includes a mechanism to load a document created in the desktop application, and introspect or export the resulting model.  The API should be considered provisional at this point.

Build instructions
-----------

To build the vzome-core library, execute this command:

    ./gradlew clean build install
    
This uses the Gradle Wrapper to install Gradle, then builds the project and installs the JAR artifact into your local Maven repository, for subsequent use in downstream projects like vzome-desktop.

Eclipse integration
-----------

You can generate an Eclipse project by executing this command:

    ./gradlew eclipse
    
I have found that the generated project is not quite perfect, having some redundancies in the build path that Eclipse will complain about.  It is probably better to install an Eclipse Gradle plugin, which will allow Eclipse to open the project just based on the build.gradle file.  I have no experience with any such plugin, yet.



