# Ubuntu Setup - Step by Step

Panduan paling simple untuk setup dan build di Ubuntu.

## Prerequisites (One-Time, ~10 minutes)

### 1. Open Terminal
```bash
Ctrl+Alt+T
```

### 2. Install Everything
```bash
sudo apt update
sudo apt install -y openjdk-17-jdk dotnet-sdk-6.0 gradle git wget curl unzip
```

### 3. Setup Android SDK
```bash
# Create directory
mkdir -p ~/Android/Sdk/cmdline-tools
cd ~/Android/Sdk/cmdline-tools

# Download
wget https://dl.google.com/android/repository/commandlinetools-linux-10135889_latest.zip

# Extract
unzip commandlinetools-linux-10135889_latest.zip
rm commandlinetools-linux-10135889_latest.zip

# Add to path (add these lines to ~/.bashrc)
cat >> ~/.bashrc << 'EOF'
export ANDROID_SDK_ROOT=$HOME/Android/Sdk
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools
EOF

# Reload
source ~/.bashrc
```

### 4. Verify Installation
```bash
java -version          # Should show Java 17
dotnet --version       # Should show .NET 6.x
gradle --version       # Should work
```

## Build Process (~15 minutes)

### Navigate to Project
```bash
cd ~/gamepad
# or wherever you extracted the project
```

### Build Android APK
```bash
cd android
chmod +x gradlew
./gradlew assembleRelease
```

**Output**: `app/build/outputs/apk/release/app-release.apk`

### Build PC Server
```bash
cd ../pc-server
dotnet publish --configuration Release --runtime win-x64
```

**Output**: `bin/Release/net6.0-windows/publish/VirtualGamepadServer.exe`

## Deploy

### Android
```bash
# Connect phone with USB debugging enabled
adb install android/app/build/outputs/apk/release/app-release.apk
```

### PC (Windows)
```bash
# Copy to Windows machine
cp pc-server/bin/Release/net6.0-windows/publish/VirtualGamepadServer.exe ~/Desktop/
# Or use SCP for remote
scp pc-server/bin/Release/net6.0-windows/publish/VirtualGamepadServer.exe user@windows-machine:/path/
```

## Automated Script (Easiest Way)

We provide a script to automate everything:

```bash
# Make script executable
chmod +x UBUNTU_QUICK_BUILD.sh

# Run it
bash UBUNTU_QUICK_BUILD.sh
```

This will:
1. Install all prerequisites
2. Setup Android SDK
3. Build Android APK
4. Build PC Server

Takes ~20 minutes first time, ~5 minutes after.

## Troubleshooting

### "Permission denied: ./gradlew"
```bash
chmod +x gradlew
```

### "ANDROID_SDK_ROOT not set"
```bash
export ANDROID_SDK_ROOT=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin
```

### "Java not found"
```bash
# Install Java 17
sudo apt install -y openjdk-17-jdk

# Verify
which java
```

### ".NET not found"
```bash
# Install .NET 6
sudo apt install -y dotnet-sdk-6.0

# Verify
dotnet --version
```

### "gradle: command not found"
```bash
sudo apt install -y gradle
# Or use included gradlew
./gradlew build
```

## Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| Build takes forever | Run `export GRADLE_OPTS="-Dorg.gradle.parallel=true"` |
| Out of memory | `export GRADLE_OPTS="-Xmx2048m"` |
| Permission denied | `chmod +x gradlew` |
| SDK not found | Re-run Android SDK setup |
| .NET not found | `sudo apt install -y dotnet-sdk-6.0` |

## System Requirements

- **Ubuntu 20.04+ (LTS recommended)**
- **Disk Space**: ~5GB (SDK + builds)
- **RAM**: 4GB+ (8GB recommended)
- **Internet**: For downloading SDKs and dependencies

## File Locations

After building:
- **Android APK**: `~/gamepad/android/app/build/outputs/apk/release/app-release.apk`
- **PC Server**: `~/gamepad/pc-server/bin/Release/net6.0-windows/publish/VirtualGamepadServer.exe`
- **Android SDK**: `~/Android/Sdk/`
- **.NET Cache**: `~/.nuget/`

## Using Android Emulator (Optional)

Instead of physical device:

```bash
# Install emulator
sudo apt install -y android-tools-adb

# Create emulator (in Android Studio)
# Or use command line:
$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager "system-images;android-31;default;x86_64"
$ANDROID_SDK_ROOT/emulator/emulator -avd <AVD_NAME> &

# Install app on emulator
adb install app-release.apk
```

## Next Steps

1. ✅ Install prerequisites
2. ✅ Build APK
3. ✅ Build PC Server
4. 📱 Transfer APK to Android phone
5. 💻 Run PC Server on Windows machine
6. 🎮 Connect and play!

See `QUICK_START.md` for connection instructions.
