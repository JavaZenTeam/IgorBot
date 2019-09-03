#!/bin/sh

IMAGE_NAME="${DOCKER_IMAGE_NAME}:${TRAVIS_JOB_ID}"

docker build -t $IMAGE_NAME .

if [ "$TRAVIS_BRANCH" = "master" ]
then
    docker login -u "$DOCKERHUB_USERNAME" -p "$DOCKERHUB_PASSWORD"
    FULL_IMAGE_NAME="${DOCKER_IMAGE_NAME}:latest"
    docker tag $IMAGE_NAME $FULL_IMAGE_NAME
    docker push $FULL_IMAGE_NAME
fi
