#!/bin/bash
set -e

# MarumaIME Asset Update Script
# This script generates UI screenshots using Paparazzi and updates the assets directory.

echo "==> Running Paparazzi to record screenshots..."
./gradlew recordPaparazziDebug

echo "==> Updating assets directory..."
mkdir -p assets

# Define source and target
SOURCE_IMG="app/src/test/snapshots/images/dev.marumasa.marumaime.ui_ScreenshotTest_captureKeyboard.png"
TARGET_IMG="assets/screenshot.png"

if [ -f "$SOURCE_IMG" ]; then
    cp "$SOURCE_IMG" "$TARGET_IMG"
    echo "Success: Screenshot updated at $TARGET_IMG"
else
    echo "Error: Generated screenshot not found at $SOURCE_IMG"
    exit 1
fi
