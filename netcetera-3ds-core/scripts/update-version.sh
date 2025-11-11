#!/bin/bash

if [ -z "$1" ]; then
  echo "Usage: $0 <version>"
  exit 1
fi

echo -n "$1" > netcetera-3ds-core/version.resolved
