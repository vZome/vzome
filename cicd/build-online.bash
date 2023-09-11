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

cicd/jsweet-legacy-code.bash || exit $?

cd online

echo "export const REVISION=\"${REVISION}\";" > src/revision.js

# banner 'Indexing resources'
( cd public/classic/resources
  echo 'export const resourceIndex = ['
  find com/vzome/core/exporters -type f -exec echo "  \"{}\"", \;
  echo '  "" ];' ) >> src/revision.js

banner 'Preparing the distribution folder with NPM'
yarn install || exit $?
rm -rf dist || exit $?
yarn run build || exit $?

# Remove the detritus of earlier builds (& dev server)
rm -rf public/modules || exit $?
rm -rf public/classic/resources || exit $?

mkdir -p public/classic/resources/com/vzome/core/exporters || exit $?
cp -R ../desktop/src/main/resources/* public/classic/resources || exit $?
cp -R ../core/src/main/resources/com/vzome/core/exporters/* public/classic/resources/com/vzome/core/exporters || exit $?

cd dist

banner 'Creating the online.tgz archive'
  cp -R ../public/* app && \
  rm -rf app/test* && \
  echo 'Header always set Access-Control-Allow-Origin "*"' > modules/.htaccess && \
  echo ${REVISION} > modules/revision.txt && \
  tar czvf online.tgz app modules

banner 'finished building the vZome Online apps and web component'

