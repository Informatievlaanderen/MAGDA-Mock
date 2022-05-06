#!/bin/bash

# include the common functions
source "commons.sh"

# prepare git
prepare_git

# define the hotfix options
OPTS='
'

# start gitflow hotfix
# check if the starting branch is defined from which to create a hotfix
if [ ! -z "${starting_branch_of_hotfix}" ]; then
  echo "Using ${starting_branch_of_hotfix} branch/tag for hotfix"
  export OPTS="-DfromBranch=${starting_branch_of_hotfix}"
else
  echo "Using master branch for hotfix"
fi

mvn -B com.amashchenko.maven.plugin:gitflow-maven-plugin:1.17.0:hotfix-start \
  -Dverbose="true" \
  -DuseSnapshotInHotfix="true" \
  -DpreHotfixGoals="clean deploy -Pcucumber-tests -Pdocker" \
  -DpostHotfixGoals="deploy -Pcucumber-tests -Pdocker-release" \
  -DpushRemote="true" \
  -DversionsForceUpdate="true" \
  -DargLine="-B -s .m2/settings.xml" \
  ${OPTS}
