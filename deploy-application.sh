#!/bin/bash

# check if the APP_NAME & APP_IMAGE variables are present
if [[ -z ${APP_NAME} ]]; then
  echo "Failed to deploy application, missing variable 'APP_NAME'";
  exit 1;
fi
if [[ -z ${APP_IMAGE} ]]; then
  echo "Failed to deploy application, missing variable 'APP_IMAGE'";
  exit 1;
fi

# configure the OpenShift client
echo "Configuring OpenShift client"
oc config set-credentials magda-deployer --token="${API_TOKEN}"
oc config set-cluster magda --server="https://${SERVER}:${SERVER_PORT}" --insecure-skip-tls-verify=true
oc config set-context cluster-magda-deployer --cluster=magda --namespace="${NAMESPACE}" --user=magda-deployer
oc config use-context cluster-magda-deployer

# set the version based on the pom version if no version has been given to the pipeline
if [[ -z ${VERSION} ]]; then
  export VERSION=$(mvn -q -s .m2/settings.xml help:evaluate -Dexpression=project.version -DforceStdout)

  if [[ -z ${VERSION} ]]; then
    echo "Failed to retrieve artifact version from maven";
    exit 1;
  fi
fi
echo "Deploying version ${VERSION}";

# set the deployment date & time
export DEPLOYMENT_DATE=$(date '+%Y-%m-%dT%H:%M:%S')

# create deployment patch definition
echo "Creating deployment patch definition"
# keep new lines before executing the command substitution
IFS=
deployment_definition=$(envsubst < ./deployment_template.yml)
printf "Applying effective patch template in OpenShift:\n-----\n${deployment_definition}\n-----\n"
oc patch deployment ${APP_NAME} --patch "${deployment_definition}"

# check the exit status of the last command
if [[ ! $? -eq 0 ]]; then
  echo "Application deployment failed";
  exit 1;
fi
