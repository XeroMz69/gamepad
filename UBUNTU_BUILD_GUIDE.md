# Ubuntu Build Guide - Virtual Gamepad

Panduan lengkap untuk compile dan run aplikasi di Ubuntu Linux.

## Prerequisites Installation

### 1. Update System
```bash
sudo apt update
sudo apt upgrade -y
```

### 2. Install Android SDK & Tools

#### Option A: Using Android Studio (Recommended)
```bash
# Download Android Studio
wget https://redirector.gstatic.com/android/studio/ide-zips/2024.1.1.8/android-studio-2024.1.1.8-linux.tar.gz

# Extract
tar -xzf android-studio-2024.1.1.8-linux.tar.gz
sudo mv android-studio /opt/

# Create launcher
echo '#!/bin/bash
/opt/android-studio/bin/studio.sh' | sudo tee /usr/local/bin/android-studio
sudo chmod +x /usr/local/bin/android-studio

# Launch
android-studio &
```

#### Option B: Using Command Line Only (CLI-only, faster)
```bash
# Install Java
sudo apt install -y openjdk-17-jdk openjdk-17-jdk-headless

# Download Android SDK command-line tools
mkdir -p ~/Android/Sdk/cmdline-tools
cd ~/Android/Sdk/cmdline-tools

wget https://dl.google.com/android/repository/commandlinetools-linux-10135889_latest.zip
unzip commandlinetools-linux-10135889_latest.zip

# Set environment variables
cat >> ~/.bashrc << 'EOF'
export ANDROID_SDK_ROOT=$HOME/Android/Sdk
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/build-tools/34.0.0
EOF

source ~/.bashrc

# Install SDK packages
sdkmanager --sdk_root=$ANDROID_SDK_ROOT "platform-tools" "build-tools;34.0.0" "platforms;android-34" "platforms;android-31"
```

### 3. Install .NET 6 SDK

```bash
# Add Microsoft package repository
wget https://packages.microsoft.com/config/ubuntu/22.04/packages-microsoft-prod.deb -O packages-microsoft-prod.deb
sudo dpkg -i packages-microsoft-prod.deb
rm packages-microsoft-prod.deb

# Install .NET 6 SDK
sudo apt update
sudo apt install -y dotnet-sdk-6.0

# Verify installation
dotnet --version
# Should output: 6.0.x
```

### 4. Install Build Tools

```bash
sudo apt install -y \
    build-essential \
    git \
    wget \
    curl \
    gradle \
    unzip
```

### 5. Setup Gradle (if not already installed)

```bash
# Check if gradle is available
gradle --version

# If not, install via apt
sudo apt install -y gradle

# Or install latest via SDKMAN
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install gradle 8.0
```

## Android Build on Ubuntu

### Step 1: Navigate to Android Project

```bash
cd ~/gamepad/android
# or wherever you have the project
```

### Step 2: Set Permissions

```bash
chmod +x gradlew
```

### Step 3: Build Debug APK

```bash
./gradlew assembleDebug
```

Output akan berada di: `app/build/outputs/apk/debug/app-debug.apk`

### Step 4: Build Release APK

```bash
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk`

### Step 5: Install on Android Device/Emulator

#### Option A: Using USB-connected Android device

```bash
# Enable USB debugging on Android device
# Then:
adb devices  # List connected devices

./gradlew installDebug
# Or manually:
adb install app/build/outputs/apk/debug/app-debug.apk
```

#### Option B: Using Android Emulator

```bash
# Start emulator first
emulator -avd AVD_NAME &

# Then install
./gradlew installDebug
```

### Troubleshooting Android Build

**Error: "ANDROID_SDK_ROOT not set"**
```bash
# Add to ~/.bashrc or ~/.zshrc
export ANDROID_SDK_ROOT=$HOME/Android/Sdk
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools

source ~/.bashrc
```

**Error: "Could not find gradle"**
```bash
sudo apt install -y gradle
# Or use included gradlew
./gradlew build
```

**Error: "Compilation failed - unsupported Java version"**
```bash
# Update Java
sudo apt install -y openjdk-17-jdk
java -version  # Should show Java 17
```

## PC Server Build on Ubuntu

### Important Note
**The PC server requires Windows for ViGEm virtual controller support.** 

However, you can:
1. Build the server executable on Ubuntu (targeting Windows)
2. Run it on Windows using the .exe file

Or, for Linux testing without ViGEm:
- Build and run without ViGEm (mock driver)
- Won't work with actual games, but tests networking

### Option 1: Build for Windows (Cross-compile)

```bash
cd ~/gamepad/pc-server

# Build for Windows runtime (from Ubuntu)
dotnet publish --configuration Release --runtime win-x64 --self-contained false

# Output: bin/Release/net6.0-windows/publish/
# Copy VirtualGamepadServer.exe to Windows machine to run
```

### Option 2: Build for Linux (Testing Only)

```bash
cd ~/gamepad/pc-server

# Build for Linux (won't work with ViGEm, but tests networking)
dotnet publish --configuration Release --runtime linux-x64

# Output: bin/Release/net6.0-linux-x64/publish/VirtualGamepadServer
```

### Option 3: Run in WSL2 (Windows Subsystem for Linux)

If you have Windows 11 with WSL2:

```bash
# From Ubuntu in WSL2:
dotnet publish --configuration Release

# Copy to Windows:
cp bin/Release/net6.0-windows/publish/VirtualGamepadServer.exe /mnt/c/Users/YourUsername/Desktop/

# Then run on Windows PowerShell:
# .\VirtualGamepadServer.exe
```

## Complete Ubuntu Build Workflow

### 1. Install Everything

```bash
#!/bin/bash
# Save as install-prereqs.sh

# Update system
sudo apt update && sudo apt upgrade -y

# Install Java
sudo apt install -y openjdk-17-jdk openjdk-17-jdk-headless

# Install build tools
sudo apt install -y \
    build-essential \
    git \
    wget \
    curl \
    gradle \
    unzip

# Install .NET 6
wget https://packages.microsoft.com/config/ubuntu/22.04/packages-microsoft-prod.deb -O packages-microsoft-prod.deb
sudo dpkg -i packages-microsoft-prod.deb
rm packages-microsoft-prod.deb
sudo apt update
sudo apt install -y dotnet-sdk-6.0

# Setup Android SDK (command-line only)
mkdir -p ~/Android/Sdk/cmdline-tools
cd ~/Android/Sdk/cmdline-tools
wget https://dl.google.com/android/repository/commandlinetools-linux-10135889_latest.zip
unzip commandlinetools-linux-10135889_latest.zip

# Add to PATH
cat >> ~/.bashrc << 'EOF'
export ANDROID_SDK_ROOT=$HOME/Android/Sdk
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/build-tools/34.0.0
EOF

source ~/.bashrc

# Verify installations
echo "=== Java Version ==="
java -version
echo "=== .NET Version ==="
dotnet --version
echo "=== Gradle Version ==="
gradle --version

echo "All prerequisites installed!"
```

Run it:
```bash
bash install-prereqs.sh
```

### 2. Clone/Setup Project

```bash
# If not already done
git clone <repo-url> ~/gamepad
cd ~/gamepad
```

### 3. Build APK

```bash
cd android
chmod +x gradlew

# Build
./gradlew assembleRelease

# Output
echo "APK built: app/build/outputs/apk/release/app-release.apk"
ls -lh app/build/outputs/apk/release/app-release.apk
```

### 4. Build PC Server (for Windows)

```bash
cd ../pc-server

# Build for Windows
dotnet publish --configuration Release --runtime win-x64

# Output
echo "PC Server built for Windows"
ls -lh bin/Release/net6.0-windows/publish/VirtualGamepadServer.exe
```

### 5. Deploy

```bash
# Copy APK to Android
adb install app/build/outputs/apk/release/app-release.apk

# Copy PC exe to Windows (via USB/network)
# Or use scp if remote machine:
scp pc-server/bin/Release/net6.0-windows/publish/VirtualGamepadServer.exe user@windows-pc:/path/
```

## Docker (Optional - Advanced)

Build Android APK in Docker container:

```dockerfile
# Dockerfile
FROM ubuntu:22.04

RUN apt update && apt install -y \
    openjdk-17-jdk \
    gradle \
    wget \
    unzip

# Setup Android SDK
RUN mkdir -p /root/Android/Sdk/cmdline-tools && \
    cd /root/Android/Sdk/cmdline-tools && \
    wget https://dl.google.com/android/repository/commandlinetools-linux-10135889_latest.zip && \
    unzip commandlinetools-linux-10135889_latest.zip && \
    rm commandlinetools-linux-10135889_latest.zip

ENV ANDROID_SDK_ROOT=/root/Android/Sdk
ENV ANDROID_HOME=/root/Android/Sdk
ENV PATH=$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools

WORKDIR /workspace
```

Build:
```bash
docker build -t gamepad-builder .
docker run -v $(pwd)/gamepad:/workspace gamepad-builder bash -c "cd /workspace/android && ./gradlew assembleRelease"
```

## Troubleshooting on Ubuntu

### Issue: "Permission denied: ./gradlew"
```bash
chmod +x gradlew
./gradlew assembleRelease
```

### Issue: "JAVA_HOME not set"
```bash
# Find Java
which java
# Should be: /usr/lib/jvm/java-17-openjdk-amd64/bin/java

# Add to ~/.bashrc
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$PATH:$JAVA_HOME/bin

source ~/.bashrc
```

### Issue: "Could not find android.jar"
```bash
# Install missing SDK packages
cd ~/Android/Sdk
./cmdline-tools/latest/bin/sdkmanager "platform-tools" "build-tools;34.0.0" "platforms;android-34" "platforms;android-31"
```

### Issue: ".NET SDK not found after installation"
```bash
# Verify installation
which dotnet
# Should be: /usr/bin/dotnet

# If not found, add to PATH
export PATH=$PATH:/usr/bin
```

### Issue: "Cannot build for Windows from Ubuntu"
```bash
# This is expected limitation. Solutions:
# 1. Build .exe on Ubuntu, transfer to Windows to run
# 2. Use cross-compilation:
dotnet publish --configuration Release --runtime win-x64

# 3. Or build on Windows directly
```

## Building on Different Ubuntu Versions

### Ubuntu 20.04 LTS
```bash
# Java 17 not available by default
sudo apt install -y openjdk-11-jdk
# Or use SDKMAN:
curl -s "https://get.sdkman.io" | bash
source ~/.sdkman/bin/sdkman-init.sh
sdk install java 17.0.7-tem
```

### Ubuntu 22.04 LTS (Recommended)
```bash
# All packages available
sudo apt install -y openjdk-17-jdk dotnet-sdk-6.0
```

### Ubuntu 24.04
```bash
# Latest packages
sudo apt install -y openjdk-17-jdk dotnet-sdk-6.0
```

## CI/CD on Ubuntu (GitHub Actions)

Create `.github/workflows/build-ubuntu.yml`:

```yaml
name: Build on Ubuntu

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Setup Java
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Setup .NET
        uses: actions/setup-dotnet@v3
        with:
          dotnet-version: '6.0'
      
      - name: Build Android APK
        run: |
          cd android
          chmod +x gradlew
          ./gradlew assembleRelease
      
      - name: Build PC Server
        run: |
          cd pc-server
          dotnet publish --configuration Release --runtime win-x64
      
      - name: Upload APK
        uses: actions/upload-artifact@v3
        with:
          name: app-release.apk
          path: android/app/build/outputs/apk/release/
      
      - name: Upload PC Server
        uses: actions/upload-artifact@v3
        with:
          name: VirtualGamepadServer.exe
          path: pc-server/bin/Release/net6.0-windows/publish/
```

## Performance Tips

### Speed Up Build

```bash
# Use parallel compilation
export GRADLE_OPTS="-Dorg.gradle.parallel=true -Dorg.gradle.workers.max=4"

# Or in gradle.properties
echo "org.gradle.parallel=true
org.gradle.workers.max=4" >> gradle.properties
```

### Cache Dependencies

```bash
# Gradle uses ~/.gradle by default
# For faster subsequent builds, keep this directory

# .NET caches in ~/.nuget
# Similarly persists across builds
```

## Summary

**Ubuntu Build Process:**

1. **Install prerequisites** (10 min)
   ```bash
   sudo apt update
   sudo apt install -y openjdk-17-jdk dotnet-sdk-6.0 gradle
   ```

2. **Setup Android SDK** (5 min)
   ```bash
   # Option: Use Android Studio OR CLI tools
   ```

3. **Build Android APK** (5-10 min)
   ```bash
   cd android && ./gradlew assembleRelease
   ```

4. **Build PC Server** (2-3 min)
   ```bash
   cd pc-server && dotnet publish --configuration Release --runtime win-x64
   ```

5. **Deploy**
   ```bash
   adb install app-release.apk
   # Copy .exe to Windows machine
   ```

**Total Time: ~30 minutes (first time), ~10 minutes (subsequent)**

---

## Next Steps

- After building, test APK on Android device: `adb install app-release.apk`
- Transfer PC server .exe to Windows machine to run
- Follow `QUICK_START.md` for connection & testing

Good luck building on Ubuntu! 🐧
