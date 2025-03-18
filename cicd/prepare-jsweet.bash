#!/bin/bash

# JSweet has effectively died.  Keeping it working was already far too complex,
# and now their Artifactory server is gone.

# I'm now committing the generated Typescript and Javascript, and we'll move
# forward with manual porting for any changes we do to the Java source.

# See online/build.gradle for more details.


if [ -z ${REVISION+x} ]; then
  echo "This script is not meant to run as a top-level entry point."
  exit 1
fi

verifyJava

rm -rf jsweet-branches
mkdir jsweet-branches
cd jsweet-branches

clone() {
  banner "git clone vorth/$1"
  git clone -b $2 https://github.com/vorth/$1 || exit $?
}

clone jsweet develop
clone jsweet-maven-plugin master
clone j4ts master
clone jsweet-gradle-plugin update-jsweet

banner 'building JSweet'
cd jsweet/transpiler/ || exit $?
mvn clean install -Dmaven.test.skip=true -DskipJavadoc=true -DskipSigning=true || exit $?

banner 'building JSweet Maven plugin'
cd ../../jsweet-maven-plugin/ || exit $?
mvn clean install -Dmaven.test.skip=true -DskipJavadoc=true -DskipSigning=true || exit $?

banner 'building J4TS library'
cd ../j4ts || exit $?
chmod +x mvnw || exit $?
./mvnw clean compile install || exit $?

banner 'building JSweet Gradle plugin'
cd ../jsweet-gradle-plugin/ || exit $?
chmod +x gradlew || exit $?
./gradlew publishToMavenLocal -DskipSigning=true || exit $?

