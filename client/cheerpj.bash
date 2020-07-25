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

banner 'building the distribution'
pushd ..
./gradlew distZip
popd

rm public/*.jar
rm public/*.jar.js

banner 'extracting JAR files'
unzip ../desktop/build/distributions/vZome-Linux-7.0.dev.zip -d .
mv vZome-Linux-7.0.dev/lib/jackson-*.jar public
mv vZome-Linux-7.0.dev/lib/javax.json*.jar public
mv vZome-Linux-7.0.dev/lib/vecmath*.jar public
mv vZome-Linux-7.0.dev/lib/core*.jar public
mv vZome-Linux-7.0.dev/lib/desktop*.jar public
rm -rf vZome-Linux-7.0.dev/

pushd public

banner 'compiling vecmath'
/Applications/cheerpj/cheerpjfy.py vecmath-1.6.0-final.jar 
banner 'compiling jackson-annotations'
/Applications/cheerpj/cheerpjfy.py jackson-annotations-2.9.3.jar 
banner 'compiling jackson-core'
/Applications/cheerpj/cheerpjfy.py jackson-core-2.9.5.jar 
banner 'compiling javax.json'
/Applications/cheerpj/cheerpjfy.py javax.json-1.0.4.jar 
banner 'compiling jackson-databind'
/Applications/cheerpj/cheerpjfy.py --deps jackson-core-2.9.5.jar:jackson-annotations-2.9.3.jar jackson-databind-2.9.5.jar 

banner 'compiling core'
/Applications/cheerpj/cheerpjfy.py --deps jackson-core-2.9.5.jar:jackson-annotations-2.9.3.jar:jackson-databind-2.9.5.jar:javax.json-1.0.4.jar:vecmath-1.6.0-final.jar core-7.0.jar 

banner 'compiling desktop'
/Applications/cheerpj/cheerpjfy.py --deps jackson-core-2.9.5.jar:jackson-annotations-2.9.3.jar:jackson-databind-2.9.5.jar:javax.json-1.0.4.jar:vecmath-1.6.0-final.jar:core-7.0.jar  desktop-7.0.jar 

banner 'FINISHED!'
banner 'Now run "python3 -m http.server 8080"'