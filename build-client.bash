#!/bin/bash

./gradlew core:jsweet -x compileJava && exit $?

pushd client
./cheerpj.bash && exit $?

npm run build && exit $?


rm -rf app && \
  mv build app && \
  tar czvf app.tgz app

