#!/bin/sh

IMAGE_NAME="${DOCKER_IMAGE_NAME}:${TRAVIS_JOB_ID}"

docker build -t $IMAGE_NAME .

if [ "$TRAVIS_BRANCH" = "master" ]
then
    docker login -u "$DOCKERHUB_USERNAME" -p "$DOCKERHUB_PASSWORD"
    FULL_IMAGE_NAME_LATEST="${DOCKER_IMAGE_NAME}:latest"
    DATE_TAG=$(date '+%d%m%Y_%H%M%S')
    FULL_IMAGE_NAME_TAG="${DOCKER_IMAGE_NAME}:${DATE_TAG}"

    docker tag $IMAGE_NAME $FULL_IMAGE_NAME_LATEST
    docker tag $IMAGE_NAME $FULL_IMAGE_NAME_TAG

    docker push $FULL_IMAGE_NAME_LATEST
    docker push $FULL_IMAGE_NAME_TAG
fi
