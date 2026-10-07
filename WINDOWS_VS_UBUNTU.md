# Windows vs Ubuntu - Build Differences

Penjelasan perbedaan antara build di Windows dan Ubuntu.

## 📊 Perbandingan

| Aspek | Windows | Ubuntu |
|-------|---------|--------|
| **Android APK** | ✅ Full support | ✅ Full support |
| **PC Server EXE** | ✅ Full support (run on Windows) | ❌ Can't run on Ubuntu (ViGEm Windows-only) |
| **Cross-compile** | N/A | ✅ Can build .exe for Windows |
| **Installation Time** | ~20-30 min | ~20-30 min |
| **Build Time (first)** | ~15-20 min | ~15-20 min |
| **Build Time (next)** | ~5-10 min | ~5-10 min |
| **Ease of Use** | Easy (GUI available) | Medium (CLI-focused) |

## 🎯 Key Difference

**PC Server dapat HANYA dijalankan di Windows** karena ViGEm (virtual controller driver) Windows-only.

```
Ubuntu: Build APK (✅) + Build EXE (✅) → Transfer EXE ke Windows → Run di Windows (✅)
Windows: Build APK (✅) + Build EXE (✅) → Run keduanya di Windows (✅)
```

## 📱 Android APK

**Sama di kedua platform:**

```bash
# Windows
cd android
gradlew.bat assembleRelease

# Ubuntu
cd android
./gradlew assembleRelease

# Hasil: app-release.apk (identical)
```

## 💻 PC Server

### Build di Windows
```cmd
cd pc-server
dotnet publish --configuration Release

# Output: bin/Release/net6.0-windows/publish/VirtualGamepadServer.exe
# Siap dijalankan langsung di Windows
```

### Build di Ubuntu (untuk Windows)
```bash
cd pc-server
dotnet publish --configuration Release --runtime win-x64

# Output: bin/Release/net6.0-windows/publish/VirtualGamepadServer.exe
# Harus dipindahkan ke Windows untuk dijalankan
```

### Build di Ubuntu (untuk Linux - Testing only)
```bash
cd pc-server
dotnet publish --configuration Release --runtime linux-x64

# Output: VirtualGamepadServer (executable Linux)
# Tidak akan work dengan ViGEm (driver Windows-only)
# Hanya untuk testing networking layer
```

## 🚀 Recommended Workflow

### Option 1: Pure Windows Build
```
Windows PC:
  1. Build Android APK
  2. Build PC Server
  3. Install APK on Android via ADB
  4. Run PC Server on Windows
  5. Connect and test
```
**Advantage**: Everything runs locally, no file transfer needed

### Option 2: Ubuntu Build + Windows Run (Recommended for developers)
```
Ubuntu PC:
  1. Build Android APK
  2. Build PC Server (--runtime win-x64)

Windows PC:
  3. Transfer both files
  4. Install APK on Android via ADB
  5. Run PC Server on Windows
  6. Connect and test
```
**Advantage**: Can develop on Ubuntu, test/deploy on Windows

### Option 3: Ubuntu + WSL2 (Windows Subsystem for Linux)
```
WSL2 (Ubuntu on Windows 11):
  1. Build everything in WSL2
  2. Access Windows files via /mnt/c/
  3. Transfer files to Windows
  4. Run on Windows
```
**Advantage**: Single machine, both OS environments

## 📦 Prerequisites Differences

### Windows
```
✅ Android Studio (includes SDK, JDK, Gradle)
✅ .NET 6 SDK
✅ ViGEm driver
✅ Git
```

### Ubuntu
```
✅ OpenJDK 17
✅ Android Command-line Tools (or Android Studio)
✅ .NET 6 SDK
✅ Gradle
✅ Git

❌ ViGEm (Windows-only, tidak diperlukan di Ubuntu)
```

## 🔄 File Transfer

After building on Ubuntu, transfer to Windows:

### Option 1: USB Drive
```bash
# Ubuntu
cp app-release.apk /media/usb/
cp VirtualGamepadServer.exe /media/usb/

# Then use USB drive on Windows
```

### Option 2: Network SCP (if same network)
```bash
# Ubuntu → Windows
scp app-release.apk user@windows-pc:C:\\Users\\user\\Desktop\\
scp VirtualGamepadServer.exe user@windows-pc:C:\\Users\\user\\Desktop\\
```

### Option 3: Email/Cloud
```bash
# Zip and upload
zip release-files.zip app-release.apk VirtualGamepadServer.exe
# Upload to cloud (Google Drive, Dropbox, etc)
```

### Option 4: Git
```bash
git add app-release.apk
git add VirtualGamepadServer.exe
git commit -m "Release builds"
git push

# Di Windows: git pull
```

## ⚡ Performance

Build performance is similar on both platforms:

```
Benchmark (measured in practice):
- Android APK build: ~5-8 minutes (Ubuntu & Windows similar)
- PC Server build: ~2-3 minutes (Ubuntu & Windows similar)
- Full build: ~15-20 minutes first time
```

**Why Ubuntu might be faster:**
- No GUI overhead
- Native Linux Gradle performance
- Better resource management on servers

**Why Windows might be faster:**
- ViGEm integration faster (no cross-compilation)
- Direct file system (no WSL conversion)

## 🐳 Docker Alternative (Ubuntu)

Build in isolated container:

```bash
# Build Docker image
docker build -t gamepad-builder .

# Build APK in container
docker run -v $(pwd):/workspace gamepad-builder bash -c \
  "cd /workspace/android && ./gradlew assembleRelease"

# Result: APK in ~/gamepad/android/app/build/outputs/apk/release/
```

## 🔧 Troubleshooting Differences

### "Cannot find gradle" (Ubuntu only)
```bash
sudo apt install -y gradle
# Or use included ./gradlew
```

### ".NET not found" (Both)
```bash
# Windows: Run installer from dotnet.microsoft.com
# Ubuntu:
sudo apt install -y dotnet-sdk-6.0
```

### "Android SDK not found" (Both)
```bash
# Windows: Set in Android Studio or environment variable
# Ubuntu: Set ANDROID_SDK_ROOT in ~/.bashrc
```

## 📋 Checklist: Ubuntu to Windows Deployment

- [ ] Build APK on Ubuntu
- [ ] Build PC Server on Ubuntu (--runtime win-x64)
- [ ] Transfer both files to Windows
- [ ] Verify APK on Windows (right-click → properties)
- [ ] Verify EXE on Windows (right-click → properties)
- [ ] Install ViGEm driver on Windows
- [ ] Connect Android device via USB
- [ ] Install APK: `adb install app-release.apk`
- [ ] Run PC Server: `VirtualGamepadServer.exe`
- [ ] Connect from Android
- [ ] Test in game

## 🎯 Recommendations

### For Personal Development
**Ubuntu** (if you prefer Linux)
- Save PC for testing only
- Build both artifacts on Ubuntu
- Transfer to Windows when ready to test

### For Production Deployment
**Windows** (primary)
- Build everything on Windows
- Test directly
- Deploy to Windows

### For Team Development
**Ubuntu** (development server)
- CI/CD build on Linux server
- Generate both APK and EXE
- Distribute to team

### For Learning/Testing
**Any** (doesn't matter)
- Pick your preferred OS
- Process is identical
- Results are the same

## 📚 Related Documentation

- `COMPILATION_INSTRUCTIONS.md` - Detailed Windows build
- `UBUNTU_BUILD_GUIDE.md` - Detailed Ubuntu build
- `BUILD_UBUNTU.md` - Quick Ubuntu reference
- `UBUNTU_QUICK_BUILD.sh` - Automated Ubuntu build

## Summary

| Task | Where | How |
|------|-------|-----|
| **Build APK** | Windows or Ubuntu | `./gradlew assembleRelease` |
| **Build PC Exe** | Windows or Ubuntu | `dotnet publish --runtime win-x64` |
| **Run PC Exe** | Windows only | `VirtualGamepadServer.exe` (requires ViGEm) |
| **Test APK** | Windows or Ubuntu | `adb install app-release.apk` |
| **Full Test** | Windows required | Connect APK to PC server |

**Bottom line**: Build on Ubuntu, run on Windows. Or build and run both on Windows. Your choice! 🎉
