#!/bin/bash

banner() {
  echo '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%'
  echo '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%'
  echo '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%'
  echo '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%'
  echo '%%%%    '$1
  echo '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%'
  echo '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%'
  echo '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%'
  echo '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%'
}

BN=${BUILD_NUMBER:-'dev'}

banner 'building the distribution'
pushd ..
./gradlew distZip || exit $?
popd

if [ "$1" == "" ]; then
  rm -rf unpacked public/*.jar public/*.jar.js
fi

banner 'extracting JAR files'
unzip -o ../desktop/build/distributions/vZome-Linux-*.${BN}.zip -d . || exit $?

JARS=$( ls vZome-Linux-*.${BN}/lib | grep -e jackson -e vecmath -e '^core' -e desktop -e javax.json -e cheerp ) || exit $?
mkdir -p unpacked || exit $?
for JAR in $JARS; do
  mv vZome-Linux-*.${BN}/lib/$JAR unpacked || exit $?
done
rm -rf vZome-Linux-*.${BN}/ || exit $?

DEPS=$( echo $JARS | sed 's/ /:/g' )
echo DEPS: $DEPS

pushd unpacked || exit $?

  if [ "$1" == "" ]; then
    for JAR in $JARS; do
      [ $JAR == core*.jar ] && continue
      [ $JAR == desktop*.jar ] && continue
      banner "compiling $JAR"
      /Applications/cheerpj/cheerpjfy.py --deps $DEPS --pack-jar=../public/$JAR $JAR || exit $?
    done
  fi

  JAR=core-7.0.jar
  banner "compiling $JAR"
  /Applications/cheerpj/cheerpjfy.py --deps $DEPS --pack-jar=../public/$JAR $JAR || exit $?

  JAR=desktop-7.0.jar
  banner "compiling $JAR"
  /Applications/cheerpj/cheerpjfy.py --deps $DEPS --pack-jar=../public/$JAR $JAR || exit $?

  mv *.jar.js ../public || exit $?

popd
rm -rf unpacked

banner 'FINISHED!'
banner 'Now run "python3 -m http.server 8080"'