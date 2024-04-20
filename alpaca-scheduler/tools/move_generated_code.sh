#!/usr/bin/env bash

set -x
echo "Input: projectDir: $1"
echo "Input: openApiOutputDir: $2"
echo "Input: generatedSourceSet: $3"

projectDir=$1
openApiOutputDir=$2
generatedSourceSet=$3

mkdir -p "$generatedSourceSet/kotlin"
cp -r "$openApiOutputDir/src/main/kotlin/com" "$generatedSourceSet/kotlin"
