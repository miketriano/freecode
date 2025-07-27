#!/bin/bash -e

IMAGE_NAME="python-sandbox"

if [[ "$(docker images -q "$IMAGE_NAME" 2> /dev/null)" == "" ]]; then
  docker build -t "$IMAGE_NAME" .
fi

mvn clean install -DskipTests -B

mvn exec:java -Dexec.mainClass=com.miketriano.freecode.App -Dexec.cleanupDaemonThreads=false