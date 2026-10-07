#!/bin/bash
# Quick Build Script for Ubuntu
# Usage: bash UBUNTU_QUICK_BUILD.sh

set -e  # Exit on error

echo "=========================================="
echo "Virtual Gamepad - Ubuntu Build Script"
echo "=========================================="

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Step 1: Install Prerequisites
echo -e "\n${BLUE}[1/4] Installing prerequisites...${NC}"

# Check if running on Ubuntu
if ! grep -qi ubuntu /etc/os-release; then
    echo -e "${YELLOW}Warning: This script is optimized for Ubuntu${NC}"
fi

sudo apt update -qq
sudo apt install -y -qq \
    openjdk-17-jdk \
    dotnet-sdk-6.0 \
    gradle \
    git \
    wget \
    curl \
    unzip > /dev/null 2>&1

echo -e "${GREEN}✓ Prerequisites installed${NC}"

# Step 2: Setup Android SDK
echo -e "\n${BLUE}[2/4] Setting up Android SDK...${NC}"

ANDROID_SDK_ROOT="$HOME/Android/Sdk"
mkdir -p "$ANDROID_SDK_ROOT/cmdline-tools"

if [ ! -f "$ANDROID_SDK_ROOT/cmdline-tools/commandlinetools/bin/sdkmanager" ]; then
    cd "$ANDROID_SDK_ROOT/cmdline-tools"
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-10135889_latest.zip
    unzip -q commandlinetools-linux-10135889_latest.zip
    rm commandlinetools-linux-10135889_latest.zip
    cd -
fi

# Add to PATH for this session
export ANDROID_SDK_ROOT="$HOME/Android/Sdk"
export ANDROID_HOME="$HOME/Android/Sdk"
export PATH="$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools"

echo -e "${GREEN}✓ Android SDK ready${NC}"

# Step 3: Build Android APK
echo -e "\n${BLUE}[3/4] Building Android APK...${NC}"

if [ -d "android" ]; then
    cd android
    chmod +x gradlew
    ./gradlew assembleRelease -q
    cd ..
    
    if [ -f "android/app/build/outputs/apk/release/app-release.apk" ]; then
        echo -e "${GREEN}✓ Android APK built: $(pwd)/android/app/build/outputs/apk/release/app-release.apk${NC}"
        ls -lh android/app/build/outputs/apk/release/app-release.apk
    else
        echo -e "${YELLOW}✗ APK build failed${NC}"
        exit 1
    fi
else
    echo -e "${YELLOW}✗ android directory not found${NC}"
    exit 1
fi

# Step 4: Build PC Server
echo -e "\n${BLUE}[4/4] Building PC Server (for Windows)...${NC}"

if [ -d "pc-server" ]; then
    cd pc-server
    dotnet publish --configuration Release --runtime win-x64 -q
    cd ..
    
    if [ -f "pc-server/bin/Release/net6.0-windows/publish/VirtualGamepadServer.exe" ]; then
        echo -e "${GREEN}✓ PC Server built: $(pwd)/pc-server/bin/Release/net6.0-windows/publish/VirtualGamepadServer.exe${NC}"
        ls -lh pc-server/bin/Release/net6.0-windows/publish/VirtualGamepadServer.exe
    else
        echo -e "${YELLOW}✗ PC Server build failed${NC}"
        exit 1
    fi
else
    echo -e "${YELLOW}✗ pc-server directory not found${NC}"
    exit 1
fi

# Success!
echo -e "\n${GREEN}=========================================="
echo "✓ BUILD COMPLETE!"
echo "==========================================${NC}"

echo -e "\n${BLUE}Next steps:${NC}"
echo "1. Transfer Android APK to your phone:"
echo "   adb install android/app/build/outputs/apk/release/app-release.apk"
echo ""
echo "2. Copy PC Server to Windows machine:"
echo "   cp pc-server/bin/Release/net6.0-windows/publish/VirtualGamepadServer.exe /path/to/windows/"
echo ""
echo "3. On Windows, run as administrator:"
echo "   VirtualGamepadServer.exe"
echo ""
echo "4. On Android, launch app and connect!"
echo ""
echo -e "${YELLOW}For detailed instructions, see UBUNTU_BUILD_GUIDE.md${NC}"
