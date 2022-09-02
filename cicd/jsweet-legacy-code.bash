#!/bin/bash

banner() {
  echo ''
  echo '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%'
  echo '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%'
  echo '%%%%    '$1
  echo '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%'
  echo '%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%'
  echo ''
}

# This is designed to run from the main repo as a working directory.
# From the command line there, run "cicd/jsweet-legacy-code.bash".

banner 'Transpiling Java sources with JSweet'

rm -rf online/node_modules

./gradlew --continue -p online jsweetClean jsweet -x compileJava &> jsweet-errors.txt    # ignore the exit code, it always fails
cat jsweet-errors.txt

grep -q 'transpilation failed with 32 error(s) and 0 warning(s)' jsweet-errors.txt \
  && banner 'JSweet transpile found the expected errors' \
  || { banner 'UNEXPECTED CHANGE IN JSWEET ERRORS'; exit 1; }

banner 'Patching up the j4ts bundle as an ES6 module'

OUTJS='online/src/worker/legacy/transpiled-java.js'
echo 'import { java, javaemul } from "./candies/j4ts-2.1.0-SNAPSHOT/bundle.js"' > $OUTJS

mkdir -p online/src/worker/legacy/candies/j4ts-2.1.0-SNAPSHOT

cat 'online/jsweetOut/candies/j4ts-2.1.0-SNAPSHOT/bundle.js' | \
  sed \
    -e 's/^var java;/export var java;/' \
    -e 's/^var javaemul;/export var javaemul;/' \
  > 'online/src/worker/legacy/candies/j4ts-2.1.0-SNAPSHOT/bundle.js'

banner 'Patching up the vZome bundle as an ES6 module'

OUTJS='online/src/worker/legacy/transpiled-java.js'
echo 'import { java, javaemul } from "./candies/j4ts-2.1.0-SNAPSHOT/bundle.js"' > $OUTJS

cat 'online/jsweetOut/js/bundle.js' | \
  sed \
    -e 's/var com;/export var com;/' \
    -e 's/var java;//' \
    -e 's/(java || (java = {}));/(java);/' \
  >> $OUTJS
