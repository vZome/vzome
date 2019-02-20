vZome will now build and run from the gradle command line with either Java 8 or Java 11.

The project works in both Eclipse 2018-12 and NetBeans IDE 10.0 (Build incubator-netbeans-release-380-on-20181217).

Netbeans generates errors when loading the vzome project if netbeans_jdkhome is specifically set to Java 11 in netbeans.conf. 
The project loads OK with netbeans_jdkhome undefined in netbeans.conf.
To run vZome in Netbeans, the Gradle version had to be specifically set under 
  Tools | Options | Miscellaneous | Gradle | Gradle Installation | Gradle Installation Directory
Set it to Download from the given URL: http://services.gradle.org/distributions/gradle-5.2.1-bin.zip
It would not work when set to "Autodetect from the Gradle Wrapper" or "Download selected version [5.2.1] from Gradleware".
The "Prefer Wrapper" checkbox must also be unchecked.

The windows project works with launch4J as long as a zipped Java 11 is present in vzome\desktop\platform\windows\jre\x64\jre11.0.2.zip.

Either Java version requires a local installation of the appropriate jdk with the JAVA_HOME environment variable set appropriately.

Note that the JVM argument -Djava.ext.dirs is not supported by Java 11, so it has been removed from the build script. It was previously used for the Java 8 build. I have not seen any problem on Windows since removing it, but Scott will need to determine if there is any impact on integration with the Mac.

Java 8
======
* Set the JAVA_HOME environment variable to a valid path:
	For example, in Windows, from the command line: 
		set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_172
* Edit these two lines in build.gradle:
    sourceCompatibility = 1.8
    targetCompatibility = 1.8
* Run the project from the gradle command line
    gradlew desktop:run
	
Java 11
=======
* Set the JAVA_HOME environment variable to a valid path:
	For example, in Windows, from the command line: 
		set JAVA_HOME=C:\Program Files\Java\jdk-11.0.2
* Edit these two lines in build.gradle:
    sourceCompatibility = 11
    targetCompatibility = 11
* Run the project from the gradle command line
    gradlew desktop:run
