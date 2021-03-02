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

banner 'Transpiling Java sources with JSweet'
./gradlew core:jsweet -x compileJava && exit $?

banner 'Patching up the bundle as an ES6 module'

OUTJS='react-library/src/jsweet/bundle.js'
echo 'import { java, javaemul } from "./j4ts-2.0.0/bundle"' > $OUTJS

cat 'react-library/jsweetOut/js/bundle.js' | \
  sed \
    -e 's/var com;/export var com;/' \
    -e 's/var java;//' \
    -e 's/(java || (java = {}));/(java);/' \
    -e 's/com.vzome.core.editor.CommandEdit.loadAndPerformLgger_/try { com.vzome.core.editor.CommandEdit.loadAndPerformLgger_/' \
  >> $OUTJS

echo '} catch (error) {console.log( error )}' >> $OUTJS


pushd react-library

# source ./cheerpj.bash || exit $?

banner 'Preparing the distribution folder with NPM'
npm install || exit $?
npm run build || exit $?

npm pack || exit $?


banner 'finished building the react-vzome component library'

