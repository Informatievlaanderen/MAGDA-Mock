#!/bin/bash

# include the common functions
source "commons.sh"

# prepare git
prepare_git

if [[ ${BITBUCKET_EXIT_CODE} -eq 0 ]]; then
  echo "Job execution is ok for ${BITBUCKET_REPO_FULL_NAME}."
  exit 0
fi

if [ "$#" -ne 1 ]; then
  echo "Illegal number of parameters"
  exit 1
fi

if [ "$1" == "owasp-dependency-check" ]; then

## building slack message
  SLACK_MESSAGE="{\
\"blocks\": [\
{\
\"type\": \"section\",\
\"block_id\": \"section\",\
\"text\": {\
\"type\": \"mrkdwn\",\
\"text\": \":x: ${BITBUCKET_REPO_SLUG} :x: \n OWASP Dependency check failed for ${BITBUCKET_REPO_SLUG} - build ${BITBUCKET_BUILD_NUMBER}\"\
}\
},\
{\
\"type\": \"actions\",\
\"elements\": [\
{\
\"type\": \"button\",\
\"text\": {\
\"type\": \"plain_text\",\
\"text\": \"Go to Build\",\
},\
\"url\": \"${BITBUCKET_GIT_HTTP_ORIGIN}/addon/pipelines/home#!/results/${BITBUCKET_BUILD_NUMBER}\"\
},\
{\
\"type\": \"button\",\
\"text\": {\
\"type\": \"plain_text\",\
\"text\": \"Download Report\",\
},\
\"url\": \"https://vlaamseoverheid.jfrog.io/artifactory/magda-owasp/${BITBUCKET_REPO_SLUG}/${BITBUCKET_REPO_SLUG}-${BITBUCKET_BUILD_NUMBER}-$(date +%Y%m%d).html\"\
}\
]\
}\
]\
}"

  ## send slack message
  curl -d "$SLACK_MESSAGE" -H "Content-Type: application/json" -X POST ${SLACK_WEBHOOK_URL_OWASP}

elif [ "$1" == "update-dependencies" ]; then

## building slack message
  SLACK_MESSAGE="{\
\"blocks\": [\
{\
\"type\": \"section\",\
\"block_id\": \"section\",\
\"text\": {\
\"type\": \"mrkdwn\",\
\"text\": \":x: ${BITBUCKET_REPO_SLUG} :x: \n Update dependencies failed for ${BITBUCKET_REPO_SLUG} - build ${BITBUCKET_BUILD_NUMBER}\"\
}\
},\
{\
\"type\": \"actions\",\
\"elements\": [\
{\
\"type\": \"button\",\
\"text\": {\
\"type\": \"plain_text\",\
\"text\": \"Go to Build\",\
},\
\"url\": \"${BITBUCKET_GIT_HTTP_ORIGIN}/addon/pipelines/home#!/results/${BITBUCKET_BUILD_NUMBER}\"\
}\
]\
}\
]\
}"

  ## send slack message
  curl -d "$SLACK_MESSAGE" -H "Content-Type: application/json" -X POST ${SLACK_WEBHOOK_URL_UPDATEDEPENDENCIES}
elif [ "$1" == "stable-develop-check" ]; then

  ## building slack message
  SLACK_MESSAGE="{\
\"blocks\": [\
{\
\"type\": \"section\",\
\"block_id\": \"section\",\
\"text\": {\
\"type\": \"mrkdwn\",\
\"text\": \":x: ${BITBUCKET_REPO_SLUG} :x: \n Develop branch is unstable: ${BITBUCKET_REPO_SLUG} - build ${BITBUCKET_BUILD_NUMBER}\"\
}\
},\
{\
\"type\": \"actions\",\
\"elements\": [\
{\
\"type\": \"button\",\
\"text\": {\
\"type\": \"plain_text\",\
\"text\": \"Go to Build\",\
},\
\"url\": \"${BITBUCKET_GIT_HTTP_ORIGIN}/addon/pipelines/home#!/results/${BITBUCKET_BUILD_NUMBER}\"\
}\
]\
}\
]\
}"

  ## send slack message
  curl -d "$SLACK_MESSAGE" -H "Content-Type: application/json" -X POST ${SLACK_WEBHOOK_URL_DEVELOPSTABLE}

else
  echo "Invalid argument passed to identify job type."
  exit 1
fi
