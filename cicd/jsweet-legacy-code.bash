#!/bin/bash

if [ -z ${REVISION+x} ]; then
  echo "This script is not meant to run as a top-level entry point.  Use online.bash."
  exit 1
fi

verifyJava

# removing online/node_modules/@types because the JSweet TSC doesn't like Three.d.ts
rm -rf online/.jsweet online/jsweetOut online/node_modules/@types

banner 'Transpiling core Java sources with JSweet' ######################################

./gradlew --continue -p online coreClean core &> core-errors.txt    # ignore the exit code, it always fails
cat core-errors.txt

grep -q 'transpilation failed with 33 error(s) and 0 warning(s)' core-errors.txt \
  && banner 'JSweet core transpile found the expected errors' \
  || { banner 'UNEXPECTED CHANGE IN JSWEET CORE ERRORS'; exit 1; }


banner 'Patching up the j4ts bundle as an ES6 module' ######################################

# also, working around https://github.com/cincheo/jsweet/issues/740,
#  and avoiding the warnings from esbuild about top-level eval()

mkdir -p $CANDIES_OUT/j4ts-2.1.0-SNAPSHOT
cat $CANDIES_IN/j4ts-2.1.0-SNAPSHOT/bundle.js | \
  sed \
    -e 's/^var java/export var java/' \
    -e 's=eval[(]=(0,eval)(=g' \
    -e 's/return this.size();/return this.__parent.size();/' \
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

