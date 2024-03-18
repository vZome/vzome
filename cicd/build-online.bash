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
rm -rf public/modules || exit $?
rm -rf public/classic/resources || exit $?
# Marshall the resources from core and desktop
mkdir -p public/classic/resources/com/vzome/core/exporters || exit $?
cp -R ../desktop/src/main/resources/* public/classic/resources || exit $?
cp -R ../core/src/main/resources/com/vzome/core/exporters/* public/classic/resources/com/vzome/core/exporters || exit $?

if [ "$1" == 'quick' ]; then
  exit 0;
fi

banner 'Generating src/revision.js'
echo "export const REVISION=\"${REVISION}\";" > src/revision.js
echo "export const importLegacy = async () => import( './worker/legacy/dynamic.js' );" >> src/revision.js
# index exporter resources
( cd public/classic/resources
  echo 'export const resourceIndex = ['
  find com/vzome/core/exporters -type f -exec echo "  \"{}\"", \;
  echo '  "DUMMY-LAST-RESOURCE" ];' ) >> src/revision.js  # just so I can not worry about the last comma

banner 'Preparing the distribution folder with NPM'
yarn install || exit $?
rm -rf dist || exit $?
yarn run build || exit $?

cd dist

banner 'Creating the online.tgz archive'
  cp -R ../public/* app && \
  rm -rf app/test* && \
  echo 'Header always set Access-Control-Allow-Origin "*"' > modules/.htaccess && \
  echo ${REVISION} > modules/revision.txt && \
  tar czvf online.tgz app modules

banner 'finished building the vZome Online apps and web component'

