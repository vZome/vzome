This project requires launch4j to already be installed on the local platform. THe launch4j installation folder must be included in the PATH variable per the launch4j documentation.

The directory structure shown at the bottom of this document is used by this subproject to generate a 64 bit Windows self extracting zip file and bundle it with any of the available JRE versions. All JREs should be 64 bit Windows JREs. This project is designed to eventually support a 32 bit JRE distribution, but initially, some hard coded naming conventions are based on x64 versions. This has NOT been tested with a 32 but JRE.

In the example, the JRE versions available are 31, 40 and 45. Versions 31 and 45 are downloaded directly from Oracle and their default naming convention is the basis for the rest of this process. Version 40 is downloaded as zulu1.8.0_40-8.6.0.1-win64.zip from http://www.azulsystems.com/products/zulu/downloads#Windows.

The Zulu distribution is the entire JDK. We want just the JRE so the "jre" folder and the "release" file from the root of the zip file must be manually extracted and repackaged in a new zip file named jre1.8.0_40.zip which is placed in platform\windows\jre\x64 along with the other Oracle versions as shown. The resulting directory structure (within the zip archive) should be very similar to the directory structure within the Oracle JRE distributions.

Future versions of any JRE can be used simply by placing them in the same folder using the same naming convention and then specifying the last digit of the JRE build on the gradle command line or by modifying the default value in platform\windows\build.gradle.

The "windows" subproject will build the zip files and exe files found in build\distributions.

A useful task for reviewing the configuration of the subproject extraProperties. This task simpy displays information about the project configuration. It does not modify any files.

    gradle extraProperties

The cleanDistributions task removes just the build\distributions folder without disturbing the output of the main project or the build\launch4j folder.

    gradel cleanDistributions

The default JRE version for the distribution tasks is jre1.8.0_40 which is generated with:

    gradle distSfx

Specific JRE versions can be generated without modifying the subproject by specifying the desired version on the command line as shown. These commands can be executed from the root project folder (vzome-desktop).

    gradle distSfx -PjreBuildOverride=31
    gradle distSfx -PjreBuildOverride=45

The distribution exe files are simple self extracting versions of their zip counterpart. They simply extract themselves in the same folder where they are invoked. This is adequate to run vzome-desktop-5.0.exe, but usesr may be surprised that it extracts itself in place rather than in a normal installation path such as C:\Program Files.

When the file is extracted, the contents will be mostly identical to the directory structure under build\launch4j\windows.

The file platform\windows\tools\unzipsfx\unzipsfx.zip contains the sfx header used to generate the self extracting zip. The task that uses it will extract the necessary components from the zip file as needed. Thee is no need to manually unzip that file.

    +---build
    |   +---distributions
    |   |       setup-vzome-desktop-5.0-x64-jre1.8.0_31.exe
    |   |       setup-vzome-desktop-5.0-x64-jre1.8.0_31.zip
    |   |       setup-vzome-desktop-5.0-x64-jre1.8.0_40.exe
    |   |       setup-vzome-desktop-5.0-x64-jre1.8.0_40.zip
    |   |       setup-vzome-desktop-5.0-x64-jre1.8.0_45.exe
    |   |       setup-vzome-desktop-5.0-x64-jre1.8.0_45.zip
    |   |
    |   +---launch4j
    |   |   \---windows
    |   |       |   vzome-desktop.5.0.exe
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
    |   |               vzome-core-0.3.jar
    |   |               vzome-desktop-5.0.jar
    |   |               {more...}
    |   |
    |   +---libs
    |           vzome-desktop-5.0.jar
    |           windows-5.0.jar
    |
    +---platform
        \---windows
            |   build.gradle
            |
            +---jre
            |   \---x64
            |           jre1.8.0_31.zip
            |           jre1.8.0_40.zip
            |           jre1.8.0_45.zip
            |
            +---resources
            |   \---icons
            |           vZomeLogo.ico
            |
            \---tools
                \---unzipsfx
                        unzipsfx.zip
