# Compile di Ubuntu - Ringkasan

Saya sudah siapkan 3 pilihan untuk compile di Ubuntu. Pilih salah satu:

## 🚀 Pilihan 1: Otomatis (RECOMMENDED - 5 menit)

```bash
cd ~/gamepad
bash UBUNTU_QUICK_BUILD.sh
```

Script ini akan:
1. Install semua prerequisites
2. Setup Android SDK
3. Build APK
4. Build PC Server

Selesai! ✅

---

## 📖 Pilihan 2: Manual (10 menit)

### Step 1: Install Prerequisites
```bash
sudo apt update
sudo apt install -y openjdk-17-jdk dotnet-sdk-6.0 gradle git wget curl unzip
```

### Step 2: Setup Android SDK
```bash
mkdir -p ~/Android/Sdk/cmdline-tools
cd ~/Android/Sdk/cmdline-tools
wget https://dl.google.com/android/repository/commandlinetools-linux-10135889_latest.zip
unzip commandlinetools-linux-10135889_latest.zip
rm commandlinetools-linux-10135889_latest.zip

# Add to ~/.bashrc
cat >> ~/.bashrc << 'EOF'
export ANDROID_SDK_ROOT=$HOME/Android/Sdk
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools
EOF

source ~/.bashrc
```

### Step 3: Build Android APK
```bash
cd ~/gamepad/android
chmod +x gradlew
./gradlew assembleRelease
```

**Output**: `app/build/outputs/apk/release/app-release.apk`

### Step 4: Build PC Server
```bash
cd ~/gamepad/pc-server
dotnet publish --configuration Release --runtime win-x64
```

**Output**: `bin/Release/net6.0-windows/publish/VirtualGamepadServer.exe`

---

## 📚 Pilihan 3: Detail (See UBUNTU_BUILD_GUIDE.md)

File `UBUNTU_BUILD_GUIDE.md` memiliki:
- Instruksi detail untuk setiap step
- Multiple options (Android Studio, CLI, Docker)
- Troubleshooting lengkap
- CI/CD configuration

---

## ✅ Hasil Build

Setelah selesai, Anda akan punya:

```
~/gamepad/android/app/build/outputs/apk/release/app-release.apk    (APK untuk Android)
~/gamepad/pc-server/bin/Release/net6.0-windows/publish/VirtualGamepadServer.exe   (EXE untuk Windows)
```

---

## 🎮 Deployment

### Android
```bash
adb install ~/gamepad/android/app/build/outputs/apk/release/app-release.apk
```

### PC (Windows)
```bash
# Copy ke Windows machine
cp ~/gamepad/pc-server/bin/Release/net6.0-windows/publish/VirtualGamepadServer.exe /path/to/windows/

# Atau gunakan SCP
scp ~/gamepad/pc-server/bin/Release/net6.0-windows/publish/VirtualGamepadServer.exe user@windows-pc:C:\\Users\\user\\Desktop\\
```

---

## ⚡ Quick Reference

| Tugas | Command |
|------|---------|
| Install all | `sudo apt install -y openjdk-17-jdk dotnet-sdk-6.0 gradle` |
| Setup Android SDK | `mkdir -p ~/Android/Sdk/cmdline-tools && cd ~/Android/Sdk/cmdline-tools && wget ... && unzip ...` |
| Build APK | `cd android && ./gradlew assembleRelease` |
| Build PC Server | `cd pc-server && dotnet publish --configuration Release --runtime win-x64` |
| Install APK | `adb install app-release.apk` |

---

## 🆘 Troubleshooting

**"Permission denied: ./gradlew"**
```bash
chmod +x gradlew
```

**"ANDROID_SDK_ROOT not set"**
```bash
export ANDROID_SDK_ROOT=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin
```

**"Java not found"**
```bash
sudo apt install -y openjdk-17-jdk
java -version
```

**".NET not found"**
```bash
sudo apt install -y dotnet-sdk-6.0
dotnet --version
```

---

## 💡 Tips

1. **Faster builds**: 
   ```bash
   export GRADLE_OPTS="-Dorg.gradle.parallel=true"
   ```

2. **First time takes longer** (downloading SDKs), subsequent builds faster

3. **Check build outputs**:
   ```bash
   ls -lh ~/gamepad/android/app/build/outputs/apk/release/
   ls -lh ~/gamepad/pc-server/bin/Release/net6.0-windows/publish/
   ```

4. **Clean build** (if having issues):
   ```bash
   cd android && ./gradlew clean && ./gradlew assembleRelease
   cd ../pc-server && dotnet clean && dotnet publish --configuration Release --runtime win-x64
   ```

---

## 🎯 Expected Time

- **First Build (with prerequisites)**: ~30 minutes
- **Subsequent Builds**: ~10 minutes
- **Just APK**: ~5 minutes
- **Just PC Server**: ~3 minutes

---

**Siap? Mulai dengan:**
```bash
cd ~/gamepad
bash UBUNTU_QUICK_BUILD.sh
```

atau lihat `UBUNTU_BUILD_GUIDE.md` untuk detail lebih lanjut.

Selamat! 🎉
