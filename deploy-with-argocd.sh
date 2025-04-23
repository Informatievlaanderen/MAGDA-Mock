#!/bin/bash

function retrieveVersionFromMaven {
  export VERSION=$(mvn -q -s .m2/settings.xml help:evaluate -Dexpression=project.version -DforceStdout);
  echo "Retrieved version from maven";
  if [[ -z ${VERSION} ]]; then
    echo "Failed to retrieve artifact version from maven";
    exit 1;
  fi
}

function retrieveVersionFromGradle {
  export VERSION=$(./gradlew -q printVersion --init-script gradle/init.gradle);
  echo "Retrieved version from gradle";
  if [[ -z ${VERSION} ]]; then
    echo "Failed to retrieve artifact version from gradle";
    exit 1;
  fi
}

# check if the NAMESPACE var is set.
if [[ -z ${NAMESPACE} ]]; then
  echo "Failed to deploy application, missing variable 'NAMESPACE'";
  exit 1;
fi

# check if the AZURE_DEPLOY_DIR var is set, if not default to azure-<env>
if [[ -z ${AZURE_DEPLOY_DIR} ]]; then
  export AZURE_DEPLOY_DIR="azure-${BITBUCKET_DEPLOYMENT_ENVIRONMENT}"
fi

# check if the INFRA_REPO var is set
if [[ -z ${INFRA_REPO} ]]; then
  echo "Failed to deploy application, missing variable 'INFRA_REPO'";
  exit 1;
fi

# check if the APPS var is set
if [[ -z ${APPS} ]]; then
  echo "Failed to deploy application, missing or empty variable 'APPS'";
  exit 1;
fi

# include the common functions
source "commons.sh"

# prepare git
prepare_git

# set the version based on the Gradle project version.
if [[ -z ${VERSION} ]]; then
  if [[ -z ${GRADLE} ]]; then
    retrieveVersionFromMaven || exit 1
  else
    retrieveVersionFromGradle || exit 1
  fi
fi
echo "Deploying version ${VERSION}";

# checkout gitOps repo.
git config --global user.email "magda-pipeline@vlaanderen.be"
git config --global user.name "Magda Pipeline"

rm -rf "${INFRA_REPO}"
git clone "git@bitbucket.org:vlaamseoverheid/${INFRA_REPO}.git" || exit 1
cd "${INFRA_REPO}"

function updateAppVersion {
  local base_directory=$1
  local app=$2
  echo "Updating version of ${base_directory}/${app} to ${VERSION}";
  # update version.
  yq -i '.version = strenv(VERSION)' "./${base_directory}/${app}/Chart.yaml" || exit 1;
  #update lastDeploymentDate
  DATE=$(date '+%Y-%m-%dT%H:%M:%S') yq -i '.deployment.lastDeploymentDate = strenv(DATE)' "./${base_directory}/${app}/values.yaml" || exit 1;
}

# loop over the apps
for app in ${APPS}
do
  # Update CPC app
  updateAppVersion "$NAMESPACE" "$app"

  # Update Azure app, if there is an azure directory
  if [ -d "./$AZURE_DEPLOY_DIR" ] && [ -d "./$AZURE_DEPLOY_DIR/$app" ]; then
    echo "Update Azure version of app ${app}"
    updateAppVersion "$AZURE_DEPLOY_DIR" "$app"
  fi
done

## commit and push changes
git add .
git commit -m "Version bumped to ${VERSION} by pipeline ${BITBUCKET_BUILD_NUMBER}"
git push || exit 1