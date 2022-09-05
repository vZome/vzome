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


rm -rf online/node_modules online/.jsweet

banner 'Transpiling desktop Java sources with JSweet' ######################################

./gradlew --continue -p online desktopClean desktop &> desktop-errors.txt    # ignore the exit code, it always fails
cat desktop-errors.txt

grep -q 'transpilation failed with 12 error(s) and 1 warning(s)' desktop-errors.txt \
  && banner 'JSweet desktop transpile found the expected errors' \
  || { banner 'UNEXPECTED CHANGE IN JSWEET DESKTOP ERRORS'; exit 1; }


banner 'Transpiling core Java sources with JSweet' ######################################

./gradlew --continue -p online coreClean core &> core-errors.txt    # ignore the exit code, it always fails
cat core-errors.txt

grep -q 'transpilation failed with 31 error(s) and 1 warning(s)' core-errors.txt \
  && banner 'JSweet core transpile found the expected errors' \
  || { banner 'UNEXPECTED CHANGE IN JSWEET CORE ERRORS'; exit 1; }



LEGACY=online/src/worker/legacy
CANDIES_IN=online/jsweetOut/core/candies
CANDIES_OUT="$LEGACY/candies"

banner 'Patching up the j4ts bundle as an ES6 module' ######################################

mkdir -p $CANDIES_OUT/j4ts-2.1.0-SNAPSHOT
cat $CANDIES_IN/j4ts-2.1.0-SNAPSHOT/bundle.js | \
  sed \
    -e 's/^var java/export var java/' \
  > $CANDIES_OUT/j4ts-2.1.0-SNAPSHOT/bundle.js || exit $?

banner 'Patching up the core bundle as an ES6 module' ######################################

OUTJS=$LEGACY/core-java.js
echo 'import { java, javaemul } from "./candies/j4ts-2.1.0-SNAPSHOT/bundle.js"' > $OUTJS
# echo 'import { javax } from "./candies/j4ts-awt-swing-0.0.2-SNAPSHOT/bundle.js"' >> $OUTJS

cat 'online/jsweetOut/core/js/bundle.js' | \
  sed \
    -e 's/^var com;/export var com;/' \
    -e 's/var java;//' \
    -e 's/(java || (java = {}));/(java);/' \
  >> $OUTJS


LEGACY=online/src/ui/legacy
CANDIES_IN=online/jsweetOut/desktop/candies
CANDIES_OUT="$LEGACY/candies"

banner 'Patching up the j4ts-awt-swing bundle as an ES6 module' ######################################

mkdir -p $CANDIES_OUT/j4ts-awt-swing-0.0.2-SNAPSHOT
OUTJS=$CANDIES_OUT/j4ts-awt-swing-0.0.2-SNAPSHOT/bundle.js
echo 'import { java, javaemul } from "../../../../worker/legacy/candies/j4ts-2.1.0-SNAPSHOT/bundle.js"' > $OUTJS

cat $CANDIES_IN/j4ts-awt-swing-0.0.2-SNAPSHOT/bundle.js | \
  sed \
    -e 's/^var javax;/export var javax;/' \
    -e 's/var java;//' \
    -e 's/(java || (java = {}));/(java);/' \
  >> $OUTJS || exit $?

banner 'Patching up the desktop bundle as an ES6 module' ######################################

OUTJS=$LEGACY/desktop-java.js
echo 'import { java, javaemul } from "../../worker/legacy/candies/j4ts-2.1.0-SNAPSHOT/bundle.js"' > $OUTJS
echo 'import { javax } from "./candies/j4ts-awt-swing-0.0.2-SNAPSHOT/bundle.js"' >> $OUTJS

cat 'online/jsweetOut/desktop/js/bundle.js' | \
  sed \
    -e 's/^var com;/export var com;/' \
    -e 's/^var org;/export var org;/' \
    -e 's/var java;//' \
    -e 's/(java || (java = {}));/(java);/' \
  >> $OUTJS
