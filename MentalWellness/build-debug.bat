@echo off
echo Building Mental Wellness app...
call gradlew clean
call gradlew assembleDebug
echo Build complete. APK location: app\build\outputs\apk\debug\app-debug.apk 