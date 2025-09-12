#!/bin/bash

# include the common functions
source "commons.sh"

# prepare git
prepare_git

# start gitflow release
echo "Starting release process"
mvn -B com.amashchenko.maven.plugin:gitflow-maven-plugin:1.19.0:release \
  -s .m2/settings.xml \
  -Dverbose="true" \
  -DproductionBranch="master" \
  -DdevelopmentBranch="develop" \
  -DversionTagPrefix="" \
  -DpreReleaseGoals="" \
  -DpostReleaseGoals="clean deploy -Pcucumber-tests -Pdocker,docker-release" \
  -DversionsForceUpdate="true" \
  -DversionDigitToIncrement="1" \
  -DargLine="-B -U -s .m2/settings.xml"
