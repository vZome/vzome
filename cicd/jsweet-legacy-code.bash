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


banner 'Transpiling Java sources with JSweet' ######################################

rm -rf online/node_modules

./gradlew --continue -p online jsweetClean jsweet -x compileJava &> jsweet-errors.txt    # ignore the exit code, it always fails
cat jsweet-errors.txt

grep -q 'transpilation failed with 32 error(s) and 0 warning(s)' jsweet-errors.txt \
  && banner 'JSweet transpile found the expected errors' \
  || { banner 'UNEXPECTED CHANGE IN JSWEET ERRORS'; exit 1; }

LEGACY=online/src/worker/legacy
CANDIES_IN=online/jsweetOut/candies
CANDIES_OUT="$LEGACY/candies"


banner 'Patching up the j4ts bundle as an ES6 module' ######################################

mkdir -p $CANDIES_OUT/j4ts-2.1.0-SNAPSHOT
cat $CANDIES_IN/j4ts-2.1.0-SNAPSHOT/bundle.js | \
  sed \
    -e 's/^var java/export var java/' \
  > $CANDIES_OUT/j4ts-2.1.0-SNAPSHOT/bundle.js || exit $?


banner 'Patching up the j4ts-awt-swing bundle as an ES6 module' ######################################

mkdir -p $CANDIES_OUT/j4ts-awt-swing-0.0.2-SNAPSHOT
OUTJS=$CANDIES_OUT/j4ts-awt-swing-0.0.2-SNAPSHOT/bundle.js
echo 'import { java, javaemul } from "../j4ts-2.1.0-SNAPSHOT/bundle.js"' > $OUTJS

cat $CANDIES_IN/j4ts-awt-swing-0.0.2-SNAPSHOT/bundle.js | \
  sed \
    -e 's/^var javax;/export var javax;/' \
    -e 's/var java;//' \
    -e 's/(java || (java = {}));/(java);/' \
  >> $OUTJS || exit $?


banner 'Patching up the vZome bundle as an ES6 module' ######################################

OUTJS=$LEGACY/transpiled-java.js
echo 'import { java, javaemul } from "./candies/j4ts-2.1.0-SNAPSHOT/bundle.js"' > $OUTJS
echo 'import { javax } from "./candies/j4ts-awt-swing-0.0.2-SNAPSHOT/bundle.js"' > $OUTJS

cat 'online/jsweetOut/js/bundle.js' | \
  sed \
    -e 's/var com;/export var com;/' \
    -e 's/var java;//' \
    -e 's/(java || (java = {}));/(java);/' \
  >> $OUTJS
