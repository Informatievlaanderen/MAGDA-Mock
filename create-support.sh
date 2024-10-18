#!/bin/bash

# include the common functions
source "commons.sh"

# prepare git
prepare_git

# start gitflow support branch flow
# check if the starting branch is defined from which to create a support
if [ ! -z "${starting_tag}" ]; then
  echo "Using ${starting_tag} tag for support branch"
else
  echo "No release tag has been provided for the support branch";
  exit 1;
fi

mvn -B com.amashchenko.maven.plugin:gitflow-maven-plugin:1.19.0:support-start \
  -Dverbose="true" \
  -DuseSnapshotInSupport="true" \
  -DpushRemote="true" \
  -DversionsForceUpdate="true" \
  -DtagName="${starting_tag}" \
  -DargLine="-B -s .m2/settings.xml"