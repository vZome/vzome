#!/bin/bash

REVISION=${BUILD_NUMBER:-'DEV'}

LEGACY=online/src/worker/legacy
CANDIES_IN=online/jsweetOut/core/candies
CANDIES_OUT="$LEGACY/candies"

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

verifyJava(){
  JAVA_VER=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | sed '/^1\./s///' | cut -d'.' -f1)
  echo "detected Java $JAVA_VER"
  if [ "$JAVA_VER" -ne 11 ]; then
    echo "This script requires JAVA_HOME to point at a Java 11 JDK."
    exit 1
  fi
}

clean() {
  rm -rf jsweet-branches || exit $?
  cd online

  rm -rf src/worker/legacy/candies || exit $?
  rm -rf src/worker/legacy/core-java.js || exit $?

  rm -rf .jsweet || exit $?
  rm -rf jsweetOut || exit $?
  rm -rf dist || exit $?
  rm -rf node_modules || exit $?
  rm -rf serve/modules || exit $?
  rm -rf serve/app/classic/resources || exit $?
}

marshallResources() {
  banner 'Marshalling core and desktop resources'
  # Remove the detritus of earlier builds (& dev server)
  rm -rf serve/modules || exit $?
  rm -rf serve/app/classic/resources || exit $?
  # Marshall the resources from core and desktop
  mkdir -p serve/app/classic/resources/com/vzome/core/exporters || exit $?
  cp -R ../desktop/src/main/resources/* serve/app/classic/resources || exit $?
  cp -R ../core/src/main/resources/com/vzome/core/exporters/* serve/app/classic/resources/com/vzome/core/exporters || exit $?
}

generateRevisionJs() {
  banner 'Generating src/revision.js'
  if [ -z ${1+x} ]; then
    echo "export const REVISION=\"${REVISION}\";" > src/revision.js
    echo "export const importLegacy = async () => import( './worker/legacy/dynamic.js' );" >> src/revision.js
    echo "export const importZomic = async () => import( './worker/legacy/zomic/index.js' );" >> src/revision.js
  else
    echo "export const REVISION=\"${1}\";" > src/revision.js
    echo "export const importLegacy = async () => import( 'https://www.vzome.com/modules/vzome-legacy.js' );" >> src/revision.js
    echo "export const importZomic = async () => import( 'https://www.vzome.com/modules/vzome-zomic.js' );" >> src/revision.js
  fi

  # index exporter resources
  ( cd serve/app/classic/resources
    echo 'export const resourceIndex = ['
    find com/vzome/core/exporters -type f -exec echo "  \"{}\"", \;
    echo ' ];' ) >> src/revision.js
}

installJsDependencies(){
  banner 'Preparing the distribution folder with Yarn'
  yarn install || exit $?
}

buildForProduction() {
  installJsDependencies || exit $?

  rm -rf dist || exit $?
  yarn run build || exit $?

  cd dist

  banner 'Creating the online.tgz archive'
  cp -R ../serve/app/* app && \
    rm -rf app/test* && \
    echo 'Header always set Access-Control-Allow-Origin "*"' > modules/.htaccess && \
    echo ${REVISION} > modules/revision.txt && \
    tar czvf online.tgz app modules
}

prepareJSweet(){
  banner 'JSweet no longer builds successfully. See online/build.gradle for more details.'
  # source cicd/prepare-jsweet.bash || exit $?
}

jsweet(){
  source cicd/jsweet-legacy-code.bash || exit $?
}

devJava(){
  banner 'We are no longer using JSweet automatically. You can still use the "jsweet" command here manually.'
}

devJs(){
  cd online

  marshallResources || exit $?

  generateRevisionJs || exit $?

  installJsDependencies || exit $?

  yarn run dev
}

devQuick(){
  cd online

  marshallResources || exit $?

  generateRevisionJs 'QUICKSTART' || exit $?

  installJsDependencies || exit $?

  yarn run nolegacy
}

productionBuild(){
  cd online

  marshallResources || exit $?

  generateRevisionJs || exit $?

  buildForProduction || exit $?

  banner 'finished building the vZome Online apps and web component'
}


case $1 in

  quickstart)
    devQuick
    ;;

  dev)
    devJs
    ;;

  prepareJSweet)
    prepareJSweet
    ;;

  jsweet)
    jsweet
    ;;

  java)
    devJava
    ;;

  prod)
    productionBuild
    ;;

  clean)
    clean
    ;;

  *)
    banner 'no command (quickstart|dev|java|prod) provided'
    ;;
esac


