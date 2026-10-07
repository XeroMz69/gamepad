# Compilation Instructions - Virtual Gamepad

Complete step-by-step guide to build and run both Android client and PC server.

## Android Application

### Prerequisites

- **Android Studio**: Latest version (2021.3 or newer)
- **Android SDK**: API 31+ (Android 12+)
- **JDK**: Java 17 (included with Android Studio)
- **Gradle**: 7.0+ (included with project)

### Build Steps

#### Option 1: Using Android Studio GUI

1. **Open Project**
   - Launch Android Studio
   - Select "Open"
   - Navigate to `gamepad/android/` folder
   - Click "Open"
   - Wait for Gradle sync

2. **Configure SDK**
   - If prompted, install missing SDK components
   - SDK Manager: Tools → SDK Manager
   - Ensure Android SDK Platform 31+ is installed
   - Ensure Build Tools 34+ is installed

3. **Build Debug APK**
   - Menu: Build → Build Bundle(s) / APK(s) → Build APK(s)
   - Wait for build to complete
   - Notification shows build success

4. **Run on Device**
   - Connect Android device via USB (enable USB debugging)
   - Or use emulator (AVD)
   - Click Run (▶ button)
   - Select device
   - App launches on device

5. **Build Release APK**
   - Menu: Build → Build Bundle(s) / APK(s) → Build APK(s)
   - Select "Release" from dropdown
   - Click "Build"
   - Output: `app/build/outputs/apk/release/app-release.apk`

#### Option 2: Using Command Line (Gradle)

```bash
# Navigate to Android project
cd gamepad/android

# Build debug APK
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk

# Build release APK
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk

# Install debug APK on connected device
./gradlew installDebug

# Run on device/emulator
./gradlew run
```

#### Option 3: Using Windows Command Prompt (Windows)

```cmd
cd gamepad\android

REM Build debug APK
gradlew.bat assembleDebug

REM Build release APK
gradlew.bat assembleRelease

REM Install and run
gradlew.bat installDebug
```

### Testing Build

After building:

1. **Verify APK**
   ```bash
   # Check APK file size (should be ~5-10MB)
   ls -lh app/build/outputs/apk/debug/app-debug.apk
   ```

2. **Install on Device**
   ```bash
   # Using adb (Android Debug Bridge)
   adb install app/build/outputs/apk/debug/app-debug.apk
   
   # Or sideload via Android Studio
   ```

3. **Run App**
   - Find "Virtual Gamepad" icon on home screen
   - Tap to launch
   - Allow permissions if prompted
   - Should display controller UI in landscape

## PC Server Application

### Prerequisites

- **Visual Studio 2022** OR **Visual Studio Code**
- **.NET 6 SDK**: https://dotnet.microsoft.com/download/dotnet/6.0
- **ViGEm Driver**: https://github.com/nefarius/ViGEm/releases
- **Administrator Access** (required for ViGEm)

### Prerequisites Installation

#### 1. Install .NET 6 SDK

**Windows**:
```bash
# Using Windows Package Manager (if available)
winget install Microsoft.DotNet.SDK.6

# Or download from https://dotnet.microsoft.com/download/dotnet/6.0
# Run installer, follow prompts
```

Verify installation:
```bash
dotnet --version
# Should output: 6.0.x or higher
```

#### 2. Install ViGEm Driver

**Critical**: ViGEm is required for virtual controller emulation.

1. Visit: https://github.com/nefarius/ViGEm/releases
2. Download latest `ViGEm Setup` (e.g., `ViGEm_1.22.0_Setup.exe`)
3. Run installer as Administrator
4. Follow installation wizard
5. Restart computer
6. Verify: Open Device Manager, look for "Xbox 360 Controller Emulation"

### Build Steps

#### Option 1: Using Visual Studio 2022

1. **Open Solution**
   - Launch Visual Studio 2022
   - File → Open → Project/Solution
   - Navigate to `gamepad/pc-server/VirtualGamepadServer.csproj`
   - Click Open

2. **Build Project**
   - Menu: Build → Build Solution
   - Or press Ctrl+Shift+B
   - Wait for build to complete
   - Check Output window for success/errors

3. **Run Server**
   - Press F5 or Ctrl+F5 (without debugging)
   - Console window opens
   - Should display "Listening on UDP port 26760"

4. **Build Release Executable**
   - Menu: Build → Publish Solution
   - Or: Release → Publish
   - Output folder: `bin/Release/net6.0-windows/`

#### Option 2: Using Visual Studio Code + .NET CLI

```bash
# Navigate to PC server project
cd gamepad/pc-server

# Restore dependencies
dotnet restore

# Build project
dotnet build

# Run server (debug)
dotnet run

# Build release
dotnet publish --configuration Release
# Output: bin/Release/net6.0-windows/publish/VirtualGamepadServer.exe
```

#### Option 3: Using Command Line (Windows)

```cmd
cd gamepad\pc-server

REM Restore dependencies
dotnet restore

REM Build debug
dotnet build

REM Build release
dotnet publish --configuration Release

REM Run server
dotnet run
```

#### Option 4: Using Bash (Linux Subsystem for Windows)

```bash
cd gamepad/pc-server

# Restore and build
dotnet restore
dotnet build --configuration Release

# Output is in: bin/Release/net6.0-windows/

# Note: To run, you need Windows (not WSL alone)
# Copy exe to Windows, run as Administrator
```

### Running the Server

#### Direct Execution

```bash
# Debug mode (with console output)
cd pc-server
dotnet run

# Release executable
bin/Release/net6.0-windows/VirtualGamepadServer.exe
```

#### Command Line Arguments

```bash
# Use custom UDP port
VirtualGamepadServer.exe 27000
```

#### Expected Output

```
╔══════════════════════════════════════════════╗
║    Virtual Gamepad Server for Windows        ║
║    Version 1.0.0                             ║
╚══════════════════════════════════════════════╝

[INFO] ==================================================
[INFO] Virtual Gamepad Server - Starting
[INFO] ==================================================
[INFO] [ViGEm] ViGEm client initialized
[INFO] [ViGEm] Virtual Xbox 360 controller created and connected
[INFO] Starting UDP server...
[INFO] [UdpServer] Listening on UDP port 26760

==================================================
Time: 2024-01-15 10:30:45
Server Status: Running
Client Connected: No
Waiting for Android connection...
==================================================
```

### Testing Build

After building:

1. **Check Executable**
   ```bash
   # Verify file exists and runs
   dir bin\Release\net6.0-windows\VirtualGamepadServer.exe
   
   # Test execution
   VirtualGamepadServer.exe --help  # May not exist, but shouldn't crash
   ```

2. **Test ViGEm Detection**
   ```bash
   # Run server - should NOT show "ViGEm initialization failed"
   VirtualGamepadServer.exe
   
   # If ViGEm fails:
   # 1. Verify driver installed
   # 2. Run as Administrator
   # 3. Restart computer
   # 4. Check Device Manager
   ```

3. **Test Network Listening**
   ```cmd
   # Check if port 26760 is listening
   netstat -an | findstr 26760
   # Should show: UDP    0.0.0.0:26760    0.0.0.0:0    LISTENING
   ```

## Full Build & Test Workflow

### Complete Setup (from scratch)

#### Step 1: Install Prerequisites

```bash
# Windows
winget install Microsoft.DotNet.SDK.6
winget install JetBrains.AndroidStudio

# Download & install ViGEm from:
# https://github.com/nefarius/ViGEm/releases

# Restart computer after ViGEm install
```

#### Step 2: Build Android App

```bash
cd gamepad/android

# Build release APK
./gradlew assembleRelease

# Output: app/build/outputs/apk/release/app-release.apk
```

#### Step 3: Build PC Server

```bash
cd gamepad/pc-server

# Build release
dotnet publish --configuration Release

# Output: bin/Release/net6.0-windows/publish/VirtualGamepadServer.exe
```

#### Step 4: Prepare Deployment

Create folder structure:
```
Virtual-Gamepad-Release/
├── VirtualGamepadServer.exe
└── app-release.apk
```

#### Step 5: Test

**On Windows PC:**
1. Run `VirtualGamepadServer.exe` (as Administrator)
2. Should display listening on port 26760

**On Android:**
1. Install `app-release.apk`
2. Launch app
3. Go to Connection tab
4. Enter PC's IP address (from Windows: `ipconfig`)
5. Tap Connect
6. Should show "Connected" status

**Full Test:**
1. Press buttons in app
2. Should see in PC server output
3. Open a game
4. Test controller functionality

## Troubleshooting Build Issues

### Android Build Fails

**Error**: "SDK location not found"
```
Solution:
1. Android Studio → File → Project Structure
2. SDK Location: Set to your Android SDK path
3. Usually: C:\Users\[username]\AppData\Local\Android\Sdk
4. Retry build
```

**Error**: "Gradle sync failed"
```
Solution:
1. File → Invalidate Caches
2. Restart Android Studio
3. Try again
```

**Error**: "Compilation failed: Module not found"
```
Solution:
1. File → Invalidate Caches / Restart
2. Or from terminal:
   ./gradlew clean
   ./gradlew build
```

### PC Build Fails

**Error**: ".NET SDK not found"
```
Solution:
1. Install .NET 6 SDK from dotnet.microsoft.com
2. Verify: dotnet --version
3. Restart terminal/IDE
4. Retry build
```

**Error**: "ViGEm.Client package not found"
```
Solution:
1. dotnet restore  # Restore NuGet packages
2. Check internet connection
3. Try: dotnet nuget list source  # Verify NuGet source
```

**Error**: "Build succeeds but won't run"
```
Solution:
1. Check ViGEm driver installed
2. Run as Administrator
3. Check firewall allows UDP 26760:
   netsh advfirewall firewall add rule name="Virtual Gamepad" dir=in action=allow protocol=udp localport=26760
```

## CI/CD Integration

### GitHub Actions Example

Create `.github/workflows/build.yml`:

```yaml
name: Build

on: [push, pull_request]

jobs:
  build-android:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK
        uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Build Android APK
        run: |
          cd android
          ./gradlew assembleRelease
      - name: Upload APK
        uses: actions/upload-artifact@v3
        with:
          name: app-release.apk
          path: android/app/build/outputs/apk/release/

  build-pc:
    runs-on: windows-latest
    steps:
      - uses: actions/checkout@v3
      - name: Setup .NET
        uses: actions/setup-dotnet@v3
        with:
          dotnet-version: '6.0'
      - name: Build PC Server
        run: |
          cd pc-server
          dotnet publish --configuration Release
      - name: Upload Executable
        uses: actions/upload-artifact@v3
        with:
          name: VirtualGamepadServer.exe
          path: pc-server/bin/Release/net6.0-windows/publish/
```

## Distribution

### Android Release

1. Build release APK: `./gradlew assembleRelease`
2. Sign APK (if for Play Store):
   ```bash
   jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 \
     -keystore my.keystore app-release.apk alias_name
   ```
3. Upload to Play Store or distribute as APK

### Windows Release

1. Build release: `dotnet publish --configuration Release`
2. Create installer (optional):
   - Use NSIS or Wix Toolset
   - Include ViGEm installer requirement in README
3. Distribute `.exe` + `README.md`

## Final Verification

After building both:

```bash
# Android
ls -lh gamepad/android/app/build/outputs/apk/release/app-release.apk

# PC Server
ls -lh gamepad/pc-server/bin/Release/net6.0-windows/publish/VirtualGamepadServer.exe

# Both should exist and be >1MB
```

---

**Build complete!** Both applications are ready to deploy.
