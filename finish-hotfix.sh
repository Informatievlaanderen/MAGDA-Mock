#!/bin/bash

# include the common functions
source "commons.sh"

# prepare git
prepare_git

# check if current branch is a hotfix branch
if [[ ! ${BITBUCKET_BRANCH} =~ hotfix/* ]]; then
  echo "This branch is not a hotfix branch";
  exit 1;
fi

# get the current version
VERSION=$(mvn -q -s .m2/settings.xml help:evaluate -Dexpression=project.version -DforceStdout)
export VERSION=${VERSION/-SNAPSHOT/}

# enable case-insensitive comparison, so no issues with true vs True vs TRUE
shopt -s nocasematch

# no merge if skipMergeDev is true
if [ "$skipMergeDev" == "true" ]; then
  mvn -B com.amashchenko.maven.plugin:gitflow-maven-plugin:1.17.0:hotfix-finish \
    -Dverbose="true" \
    -DuseSnapshotInHotfix="true" \
    -DpreHotfixGoals="" \
    -DpostHotfixGoals="clean verify deploy -Pcucumber-tests -Pdocker,docker-release" \
    -DhotfixVersion="${VERSION}" \
    -DhotfixBranch="${BITBUCKET_BRANCH}" \
    -DversionsForceUpdate="true" \
    -DskipMergeDevBranch="true" \
    -DargLine="-B -s .m2/settings.xml"
else
  # finish gitflow hotfix with merge to develop
  mvn -B com.amashchenko.maven.plugin:gitflow-maven-plugin:1.17.0:hotfix-finish \
    -Dverbose="true" \
    -DuseSnapshotInHotfix="true" \
    -DpreHotfixGoals="" \
    -DpostHotfixGoals="clean verify deploy -Pcucumber-tests -Pdocker,docker-release" \
    -DhotfixVersion="${VERSION}" \
    -DhotfixBranch="${BITBUCKET_BRANCH}" \
    -DversionsForceUpdate="true" \
    -DargLine="-B -s .m2/settings.xml"
fi

