#!/bin/bash

set -e

VERSION=$(cat ../version.resolved)
VERSION_REGEX="^(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)$"

if [[ $VERSION =~ $VERSION_REGEX ]]; then
    MAJOR="${BASH_REMATCH[1]}"
    MINOR="${BASH_REMATCH[2]}"
    PATCH="${BASH_REMATCH[3]}"
    if [[ "$@" =~ '--patch' ]]; then
        PATCH=$((PATCH + 1))
    fi
    if [[ "$@" =~ '--minor' ]]; then
        MINOR=$((MINOR + 1))
        PATCH=0
    fi
    VERSION="${MAJOR}.${MINOR}.${PATCH}"
else
    echo "Resolved version is invalid."
    exit -1
fi

# Writes resolved version to file
echo -n $VERSION > ../version.resolved
