@echo off
if not exist .\build\dependencies\*.jar call .\gradlew copyDependencies
pushd build\dependencies
java -Xmx3048M -cp antlr-2.7.7.jar;gluegen-rt-2.2.4.jar;jogl-all-2.2.4.jar;vecmath-1.6.0.jar;j3dcore-1.6.0.jar;j3dutils-1.6.0.jar;vzome-core-5.0-SNAPSHOT.jar;..\libs\vzome-desktop-5.0-SNAPSHOT.jar org.vorthmann.zome.ui.ApplicationUI -entitlement.model.edit true -entitlement.lesson.edit true -entitlement.all.tools true -licensed.user "(run-vzome.bat)"
popd
