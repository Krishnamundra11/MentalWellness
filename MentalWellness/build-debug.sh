#!/bin/bash
cd "$(dirname "$0")"
./gradlew clean
./gradlew assembleDebug
echo "Build complete. APK location: ./app/build/outputs/apk/debug/app-debug.apk" 