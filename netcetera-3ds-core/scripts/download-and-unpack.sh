#!/bin/bash

if [ -z "$1" ]; then
  echo "Usage: $0 <URL>"
  exit 1
fi

URL="$1"
FILENAME=$(basename "$URL")
SDK_DIR="${FILENAME%.*}"

if curl -L "$URL" -o "$FILENAME"; then
  echo "Downloaded $FILENAME successfully."
else
  echo "Failed to download: $URL"
  exit 1
fi

unzip -o "$FILENAME" -d "$SDK_DIR"

# jniLibs
rm -rf netcetera-3ds-core/src/main/jniLibs/*
cp -r "$SDK_DIR"/jni/* netcetera-3ds-core/src/main/jniLibs/

# assets
rm -rf netcetera-3ds-core/src/main/assets/*
cp -r "$SDK_DIR"/assets/* netcetera-3ds-core/src/main/assets/

# res
rm -rf netcetera-3ds-core/src/main/res/*
cp -r "$SDK_DIR"/res/* netcetera-3ds-core/src/main/res/

# classes.jar
cp -f "$SDK_DIR"/classes.jar netcetera-3ds-core/libs/

# proguard & dexguard
cp -f "$SDK_DIR"/proguard.txt netcetera-3ds-core/netcetera-consumer-rules.pro
cp -f "$SDK_DIR"/dexguard.txt netcetera-3ds-core/
