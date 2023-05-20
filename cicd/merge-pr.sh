#!/bin/bash

branch=$( git branch --show-current )

git checkout '0-main' || exit $?
git fetch --prune upstream || exit $?
git fetch --prune origin || exit $?
git merge --log upstream/main || exit $?
git push -v --set-upstream origin refs/heads/0-main:refs/heads/main || exit $?
git branch -d $branch

