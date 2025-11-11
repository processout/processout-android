#!/bin/bash

if [ -z "$1" ]; then
  echo "Usage: $0 <URL>"
  exit 1
fi

URL="$1"
FILENAME=$(basename "$URL")
FOLDER="${FILENAME%.*}"

if curl -L "$URL" -o "$FILENAME"; then
  echo "Downloaded $FILENAME successfully."
else
  echo "Failed to download: $URL"
  exit 1
fi

unzip "$FILENAME" -d "$FOLDER"
