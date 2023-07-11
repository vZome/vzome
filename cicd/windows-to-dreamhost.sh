#!/bin/bash

workflowId=$( curl -s https://api.github.com/repos/vZome/vzome/actions/workflows/desktop.yml | jq '.id' )

runId=$( curl -s https://api.github.com/repos/vZome/vzome/actions/runs \
  | jq "[.workflow_runs[] | select(.workflow_id==$workflowId)] [0] | .id" ) || \
  { echo API rate exceeded? ; exit 1; }

if [ "$runId" == 'null' ]; then
  echo no runs
  exit 1
else
  echo workflow $workflowId, run $runId
fi

artifact=$( curl -s https://api.github.com/repos/vZome/vzome/actions/artifacts \
  | jq "[.artifacts[] | select( .workflow_run.id==$runId)][0]" ) || exit $?

name=$( echo $artifact | jq --raw-output '.name' )
url=$( echo $artifact | jq --raw-output '.archive_download_url' )

if echo $name | grep 'vZome-Windows-'; then

  curl --fail-with-body $url > $name.zip || exit $?

  suffix=$( echo "$name" | awk -F- '{print $3}' )
  version=$( echo $suffix | awk -F. 'OFS="." {print $1,$2}' )
  build=$( echo $suffix | awk -F. '{print $3}' )

  sftp -b - scottvorthmann@sandy.dreamhost.com << END
cd vzome.com/download/$version/$build
mkdir win
cd win
put $name.zip
END

  rm -f $name.zip

else
  echo workflow $workflowId, run $runId artifact is not a vZome Windows build:
  echo
  echo $artifact | jq '.'
  exit 1
fi

