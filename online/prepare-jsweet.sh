#!/bin/bash

cd ..
rm -rf jsweet-branches
mkdir jsweet-branches
cd jsweet-branches

banner() {
  echo '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%'
  echo '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%'
  echo '%%%%    '$1
  echo '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%'
  echo '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%'
}

clone() {
  banner "git clone vorth/$1"
  git clone -b $2 https://github.com/vorth/$1 || exit $?
}

clone jsweet develop
clone jsweet-maven-plugin master
clone j4ts update-jsweet
clone jsweet-gradle-plugin update-jsweet

banner 'setting java 11 home'
export JAVA_HOME=`/usr/libexec/java_home -v 11` || exit $?

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

