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


banner 'Transpiling core Java sources with JSweet' ######################################

rm -rf online/node_modules online/.jsweet

./gradlew --continue -p online coreClean core &> core-errors.txt    # ignore the exit code, it always fails
cat core-errors.txt

grep -q 'transpilation failed with 30 error(s) and 0 warning(s)' core-errors.txt \
  && banner 'JSweet core transpile found the expected errors' \
  || { banner 'UNEXPECTED CHANGE IN JSWEET CORE ERRORS'; exit 1; }

LEGACY=online/src/worker/legacy
CANDIES_IN=online/jsweetOut/candies
CANDIES_OUT="$LEGACY/candies"


banner 'Patching up the j4ts bundle as an ES6 module' ######################################

mkdir -p $CANDIES_OUT/j4ts-2.1.0-SNAPSHOT
cat $CANDIES_IN/j4ts-2.1.0-SNAPSHOT/bundle.js | \
  sed \
    -e 's/^var java/export var java/' \
  > $CANDIES_OUT/j4ts-2.1.0-SNAPSHOT/bundle.js || exit $?


# banner 'Patching up the j4ts-awt-swing bundle as an ES6 module' ######################################

# mkdir -p $CANDIES_OUT/j4ts-awt-swing-0.0.2-SNAPSHOT
# OUTJS=$CANDIES_OUT/j4ts-awt-swing-0.0.2-SNAPSHOT/bundle.js
# echo 'import { java, javaemul } from "../j4ts-2.1.0-SNAPSHOT/bundle.js"' > $OUTJS

# cat $CANDIES_IN/j4ts-awt-swing-0.0.2-SNAPSHOT/bundle.js | \
#   sed \
#     -e 's/^var javax;/export var javax;/' \
#     -e 's/var java;//' \
#     -e 's/(java || (java = {}));/(java);/' \
  # >> $OUTJS || exit $?


banner 'Patching up the vZome bundle as an ES6 module' ######################################

OUTJS=$LEGACY/core-java.js
echo 'import { java, javaemul } from "./candies/j4ts-2.1.0-SNAPSHOT/bundle.js"' > $OUTJS
# echo 'import { javax } from "./candies/j4ts-awt-swing-0.0.2-SNAPSHOT/bundle.js"' >> $OUTJS

cat 'online/jsweetOut/js/bundle.js' | \
  sed \
    -e 's/var com;/export var com;/' \
    -e 's/var java;//' \
    -e 's/(java || (java = {}));/(java);/' \
  >> $OUTJS
