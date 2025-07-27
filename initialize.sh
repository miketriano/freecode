#!/bin/bash -e

NAME="python-sandbox"
MEMORY_LIMIT="128m"
CPU_LIMIT="0.1"
PIDS_LIMIT="10"
SCRIPT_PATH="/var/lib/freecode/scripts"
CONTAINER_SCRIPT_PATH="/app/scripts"

#docker build -t "$NAME" .

docker run \
  --rm \
  --name "$NAME" \
  --network none \
  --read-only \
  --memory="$MEMORY_LIMIT" \
  --memory-swap="$MEMORY_LIMIT" \
  --cpus="$CPU_LIMIT" \
  --pids-limit="$PIDS_LIMIT" \
  -v "$SCRIPT_PATH":"$CONTAINER_SCRIPT_PATH":ro \
  "$NAME" "/app/scripts/myscript.py"
