#!/bin/bash

# This script is used on Dreamhost, called over ssh from GitHub during deployment.

cd vzome.com || exit $?

rm -f online.tar

gunzip online.tgz || exit $?

rm -rf app/* || exit $?
rm -rf modules/*.js || exit $?    # leave modules, it has old versions inside!

tar xvf online.tar || exit $?

revision=`cat modules/revision.txt`
mkdir modules/r$revision
cp modules/*.js modules/r$revision

# The vZome Online history inspector has been gone since I dropped React.
#  This restores it, using the last revision where it was supported.
cat app/index.html | \
  sed 's-modules/-modules/r114/-' > app/inspector.html

chmod a+x app/embed.py