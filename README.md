vZome will now build and run from the gradle command line with either Java 8 or Java 9.

I have not tried using Java 9 in Eclipse or NetBeans yet. Nor have I researched or evaluated the behavior of Windows launch4J with Java 9.

Either option requires a local installation of the appropriate jdk with the JAVA_HOME environment variable set appropriately.

Note that the JVM argument -Djava.ext.dirs is not supported by Java 9, so it has been removed from the build script. It was previously used for the Java 8 build. I have not seen any problem on Windows since removing it, but Scott will need to determine if there is any impact on integration with the Mac.

Java 8
======
* Set the JAVA_HOME environment variable to a valid Java 8 path:
	For example, in Windows, from the command line: 
		set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_162
* Edit these two lines in build.gradle:
    sourceCompatibility = 1.8
    targetCompatibility = 1.8
* gradle run
	
Java 9
======
* Set the JAVA_HOME environment variable to a valid Java 8 path:
	For example, in Windows, from the command line: 
		set JAVA_HOME=C:\Program Files\Java\jdk-9.0.4
* Edit these two lines in build.gradle:
    sourceCompatibility = 9
    targetCompatibility = 9
* gradle run
	Java 9 generates the following run-time warning which can safely be ignored, but should be addressed ASAP.

WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by org.vorthmann.zome.ui.ApplicationUI (file:/C:/Users/me/Documents/GitHub/vzome/desktop/build/classes/java/main/) to field java.util.logging.FileHandler.files
WARNING: Please consider reporting this to the maintainers of org.vorthmann.zome.ui.ApplicationUI
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
	







