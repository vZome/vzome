This subproject generates a native Windows executable for vZome, packaged together with a dedicated Java runtime environment (JRE), all within a self-extracting archive.
To generate the archive, there are two external constraints that must be met.

First, launch4j must be already installed on the local platform. The launch4j installation folder must be included in the PATH variable per the launch4j documentation.

Second, a Windows-native JRE must be placed in a particular location within this subproject.
The directory structure shown at the bottom of this document shows the location for the required JRE archives.
By default, the task below assumes the existence of jre\x64\jre1.8.0_40.zip, but other versions can be used by adjusting a property on the gradle command-line..
All JREs should be 64-bit Windows JREs. This project is designed to eventually support a 32-bit JRE distribution, but currently some hard-coded naming conventions are based on x64 versions. This has NOT been tested with a 32-bit JRE.

If you have installed the necessary jre1.8.0_40.zip, and launch4j is on your PATH, this command will generate the self-extracting archive:

    gradle distSfx

(This task, and the others below, can all be run from the main project as well.)

A specific version of the JRE can be used (if present), by supplying the final digits of the version which follow the underscore (e.g. 74):

    gradle distSfx -PjreBuildOverride=31
    gradle distSfx -PjreBuildOverride=40

In the example below, the JRE versions available are 31, 40 and 74. Versions 31 and 74 are downloaded directly from Oracle, and their default naming convention is the basis for the rest of this process. Version 40 is downloaded as zulu1.8.0_40-8.6.0.1-win64.zip from http://www.azulsystems.com/products/zulu/downloads#Windows.
The Zulu distribution is the entire JDK. We want just the JRE, so the "jre" folder and the "release" file from the root of the zip file must be manually extracted and repackaged in a new zip file named jre1.8.0_40.zip, which is placed in platform\windows\jre\x64 along with the other (Oracle) versions as shown. The resulting directory structure (within the zip archive) should be very similar to the directory structure within the Oracle JRE distributions.

Future versions of any JRE can be used simply by placing them in the same folder using the same naming convention, and then specifying the version as described above, or by modifying the default value in platform\windows\build.gradle.

The generated distribution exe files are simple self-extracting versions of their zip counterparts. They simply extract themselves in the same folder where they are invoked. This is adequate to run the vzome executable, though users may be surprised that it extracts itself in place rather than in a normal installation path such as C:\Program Files.

When the file is extracted, the contents will be mostly identical to the directory structure under vzome\desktop\platform\windows\build\launch4j\vZome-Windows-x64-6.0b## where ## is the vzome build number.

The file platform\windows\tools\unzipsfx\unzipsfx.zip contains the sfx header used to generate the self extracting zip. The task that uses it will extract the necessary components from the zip file as needed. There is no need to manually unzip that file.

For example, a completed build 39 will have the following directory structure:

    +---build
    |   +---distributions
    |   |   \---vZome-Windows-x64-6.0b39
    |   |           vZome-Windows-x64-6.0b39.exe (this is the self extracting version of the zip file with the same name)
    |   |           vZome-Windows-x64-6.0b39.zip
    |   |
    |   +---launch4j
    |   |   \---vZome-Windows-x64-6.0b39.zip
    |   |       |   vzome-desktop.6.0.exe
    |   |       |
    |   |       +---jre
    |   |       |   |   release
    |   |       |   |   {more...}
    |   |       |   |
    |   |       |   +---bin
    |   |       |   |       {more...}
    |   |       |   |
    |   |       |   \---lib
    |   |       |           {more...}
    |   |       |
    |   |       \---lib
    |   |               core-6.0.jar
    |   |               desktop-6.0.jar
    |   |               {more...}
    |   |
    |   +---libs
    |           windows-6.0.jar
    |
    +---platform
        \---windows
            |   build.gradle
            |
            +---jre
            |   \---x64
            |           jre1.8.0_31.zip
            |           jre1.8.0_40.zip
            |           jre1.8.0_74.zip
            |
            +---resources
            |   \---icons
            |           vZomeLogo.ico
            |
            \---tools
                +---launch4j
                |       vZome-Windows-x64-6.0.l4j.ini
                \---unzipsfx
                        unzipsfx.zip

A useful task for reviewing the configuration of the subproject is "extraProperties".
This task simply displays information about the project configuration. It does not modify any files.

    gradle extraProperties

The cleanDistributions task removes just the build\distributions folder without disturbing the output of the main project or the build\launch4j folder.

    gradle cleanDistributions

