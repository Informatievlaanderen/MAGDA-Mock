#!/bin/bash

install_git_dependencies() {
  echo "Installing dependencies"
  apt-get update && apt-get install git curl jq -y
  echo "Dependencies installed"
}

install_helm() {
    curl https://baltocdn.com/helm/signing.asc | apt-key add -
    apt-get install apt-transport-https --yes
    echo "deb https://baltocdn.com/helm/stable/debian/ all main" | tee /etc/apt/sources.list.d/helm-stable-debian.list
    apt-get update && apt-get install helm
}

update_git_remote() {
  echo "Updating remote origin url"
  git remote set-url origin "git@bitbucket.org:${BITBUCKET_REPO_FULL_NAME}.git"
}

update_git_config() {
  echo "Updating git config"
  git config --replace-all remote.origin.fetch +refs/heads/*:refs/remotes/origin/*
  git fetch --all
}

update_helm_dependencies() {
    echo "Updating HELM dependencies in $(pwd)"
    helm repo add artifactory https://vlaamseoverheid.jfrog.io/artifactory/magda-helm-public \
        --username ${ARTIFACTORY_USER} --password ${ARTIFACTORY_PASSWORD}
    helm dependency update
    git add charts || true
    git add Chart.lock || true
}

prepare_git() {
  # install dependencies
  install_git_dependencies
  # update git remote
  update_git_remote
  # update git config
  update_git_config
}
