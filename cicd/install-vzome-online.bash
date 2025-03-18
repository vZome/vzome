#!/bin/bash

# This script is used on the web server, installed and then called over ssh from GitHub during deployment.
#  See .github/workflows/online.yml

cd vzome.com || exit $?

rm -f online.tar

gunzip online.tgz || exit $?

rm -rf app/* || exit $?
rm -rf modules/*.js || exit $?    # leave modules, it has old versions inside!

tar xvf online.tar || exit $?

# duplicate modules with the revision number
revision=`cat modules/revision.txt`
mkdir modules/r$revision
cp modules/*.js modules/r$revision

chmod a+x app/embed.py