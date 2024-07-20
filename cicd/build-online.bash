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

REVISION=${BUILD_NUMBER:-'DEV'}

if [ "$1" != 'quick' ]; then
  cicd/jsweet-legacy-code.bash || exit $?
fi

cd online

banner 'Marshalling core and desktop resources'
# Remove the detritus of earlier builds (& dev server)
rm -rf serve/modules || exit $?
rm -rf serve/app/classic/resources || exit $?
# Marshall the resources from core and desktop
mkdir -p serve/app/classic/resources/com/vzome/core/exporters || exit $?
cp -R ../desktop/src/main/resources/* serve/app/classic/resources || exit $?
cp -R ../core/src/main/resources/com/vzome/core/exporters/* serve/app/classic/resources/com/vzome/core/exporters || exit $?

if [ "$1" == 'quick' ]; then
  exit 0;
fi

banner 'Generating src/revision.js'
echo "export const REVISION=\"${REVISION}\";" > src/revision.js
echo "export const importLegacy = async () => import( './worker/legacy/dynamic.js' );" >> src/revision.js
echo "export const importZomic = async () => import( './worker/legacy/zomic/index.js' );" >> src/revision.js
# index exporter resources
( cd serve/app/classic/resources
  echo 'export const resourceIndex = ['
  find com/vzome/core/exporters -type f -exec echo "  \"{}\"", \;
  echo ' ];' ) >> src/revision.js

banner 'Preparing the distribution folder with NPM'
yarn install || exit $?
rm -rf dist || exit $?
yarn run build || exit $?

cd dist

banner 'Creating the online.tgz archive'
  cp -R ../serve/app/* app && \
  rm -rf app/test* && \
  echo 'Header always set Access-Control-Allow-Origin "*"' > modules/.htaccess && \
  echo ${REVISION} > modules/revision.txt && \
  tar czvf online.tgz app modules

banner 'finished building the vZome Online apps and web component'

