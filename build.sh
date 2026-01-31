#!/bin/bash

# Reminders App Build Script for Linux/macOS
# Usage: ./build.sh [command]
# Commands: debug, release, clean, install, test

COMMAND=${1:-debug}

echo ""
echo "========================================"
echo "  Reminders App Build Script"
echo "========================================"
echo ""

case $COMMAND in
    debug)
        echo "Building DEBUG APK..."
        echo ""
        ./gradlew assembleDebug
        if [ $? -eq 0 ]; then
            echo ""
            echo "========================================"
            echo "  BUILD SUCCESSFUL!"
            echo "  APK: app/build/outputs/apk/debug/app-debug.apk"
            echo "========================================"
        else
            echo ""
            echo "BUILD FAILED!"
            exit 1
        fi
        ;;

    release)
        echo "Building RELEASE APK..."
        echo ""
        ./gradlew assembleRelease
        if [ $? -eq 0 ]; then
            echo ""
            echo "========================================"
            echo "  BUILD SUCCESSFUL!"
            echo "  APK: app/build/outputs/apk/release/app-release-unsigned.apk"
            echo "========================================"
        else
            echo ""
            echo "BUILD FAILED!"
            exit 1
        fi
        ;;

    clean)
        echo "Cleaning build files..."
        echo ""
        ./gradlew clean
        echo ""
        echo "Clean complete!"
        ;;

    install)
        echo "Building and installing DEBUG APK on connected device..."
        echo ""
        ./gradlew installDebug
        if [ $? -eq 0 ]; then
            echo ""
            echo "========================================"
            echo "  INSTALLED SUCCESSFULLY!"
            echo "========================================"
        else
            echo ""
            echo "INSTALL FAILED! Make sure a device is connected."
            exit 1
        fi
        ;;

    test)
        echo "Running tests..."
        echo ""
        ./gradlew test
        echo ""
        echo "Tests complete!"
        ;;

    help|--help|-h)
        echo "Usage: ./build.sh [command]"
        echo ""
        echo "Commands:"
        echo "  debug    - Build debug APK (default)"
        echo "  release  - Build release APK"
        echo "  clean    - Clean build files"
        echo "  install  - Build and install on connected device"
        echo "  test     - Run unit tests"
        echo "  help     - Show this help message"
        echo ""
        ;;

    *)
        echo "Unknown command: $COMMAND"
        echo "Run './build.sh help' for usage information."
        exit 1
        ;;
esac
