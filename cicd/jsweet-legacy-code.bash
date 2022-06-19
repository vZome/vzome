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

./gradlew --continue core:jsweet -x compileJava &> jsweet-errors.txt    # ignore the exit code, it always fails
cat jsweet-errors.txt

grep -q 'transpilation failed with 104 error(s) and 0 warning(s)' jsweet-errors.txt \
  && banner 'JSweet transpile found the expected errors' \
  || { banner 'UNEXPECTED CHANGE IN JSWEET ERRORS'; exit 1; }

banner 'Patching up the bundle as an ES6 module'

OUTJS='online/src/worker/legacy/transpiled-java.js'
echo 'import { java, javaemul } from "./j4ts-2.0.0/bundle.js"' > $OUTJS

cat 'online/jsweetOut/js/bundle.js' | \
  sed \
    -e 's/var com;/export var com;/' \
    -e 's/var java;//' \
    -e 's/(java || (java = {}));/(java);/' \
  >> $OUTJS
