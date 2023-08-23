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

pushd online

echo "export const REVISION=\"${REVISION}\"" > src/revision.js

banner 'Preparing the distribution folder with NPM'
yarn install || exit $?
rm -rf dist || exit $?
yarn run build || exit $?

# Remove the detritus of earlier builds (& dev server)
rm -rf public/modules || exit $?
rm -rf public/classic/resources || exit $?

mkdir -p public/classic/resources || exit $?
cp -R ../desktop/src/main/resources/* public/classic/resources || exit $?

pushd dist

banner 'Creating the online.tgz archive'
  cp -R ../public/* app && \
  rm -rf app/test* && \
  echo 'Header always set Access-Control-Allow-Origin "*"' > modules/.htaccess && \
  echo ${REVISION} > modules/revision.txt && \
  tar czvf online.tgz app modules

banner 'finished building the vZome Online apps and web component'

