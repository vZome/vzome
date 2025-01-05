#!/bin/bash

# JSweet has effectively died.  Keeping it working was already far too complex,
# and now their Artifactory server is gone.

# I'm now committing the generated Typescript and Javascript, and we'll move
# forward with manual porting for any changes we do to the Java source.

# See online/build.gradle for more details.


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

grep -q 'transpilation failed with 35 error(s) and 0 warning(s)' core-errors.txt \
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

cat 'online/jsweetOut/core/js/bundle.js' | \
  sed \
    -e 's/^var com;/export var com;/' \
    -e 's/var java;//' \
    -e 's/(java || (java = {}));/(java);/' \
  >> $OUTJS


# I really need to just script this.  Here is what needs to be automated:
#   1. remove online/src/worker/legacy/ts
#   2. set bundle=false in online/build.gradle, near line 94
#   3. run: cicd/online.bash jsweet
#   4. move online/jsweetOut/core/ts to online/src/worker/legacy
#   5. set bundle=true again in online/build.gradle
#   6. run: cicd/online.bash jsweet
banner 'You should regenerate the un-bundled Typescript!  See the comment in cicd/jsweet-legacy-code.bash.'

echo '/* THIS FILE IS CURRENTLY IGNORED! We are using the generated core-java.js instead, for now. */' > $LEGACY/ts/core-java.ts
cat online/jsweetOut/core/ts/bundle.ts >> $LEGACY/ts/core-java.ts

