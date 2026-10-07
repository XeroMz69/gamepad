# Quick Start Guide - Virtual Gamepad

Get up and running in 5 minutes.

## Prerequisites (One-Time Setup)

### Windows PC
1. **Install .NET 6**: https://dotnet.microsoft.com/download/dotnet/6.0
2. **Install ViGEm**: https://github.com/nefarius/ViGEm/releases
3. Restart computer after ViGEm install

### Android Device
- Android 12 or newer
- Wi-Fi connection

## Build (5-10 minutes)

### Android APK
```bash
cd gamepad/android
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

### PC Server
```bash
cd gamepad/pc-server
dotnet publish --configuration Release
# Output: bin/Release/net6.0-windows/publish/VirtualGamepadServer.exe
```

## Installation (1 minute)

### Android
1. Transfer `app-release.apk` to Android device
2. Install the APK (enable Unknown Sources if needed)
3. Or: `adb install app-release.apk`

### PC
1. Copy `VirtualGamepadServer.exe` anywhere
2. Right-click → "Run as administrator"

## Connect (2 minutes)

### Step 1: Start PC Server
```bash
VirtualGamepadServer.exe
```
Look for: `[UdpServer] Listening on UDP port 26760`

### Step 2: Get PC IP Address
On Windows command prompt:
```cmd
ipconfig
```
Look for IPv4 address (e.g., `192.168.1.100`)

### Step 3: Connect Android
1. Launch Virtual Gamepad app
2. Go to **Connection** tab
3. Enter PC IP address
4. Tap **Connect**
5. Should show "Connected" (green)

## Test (1 minute)

1. Go to **Test** tab on Android
2. Press buttons → should see indicators light up
3. Open a game that supports Xbox controllers
4. Enjoy!

## Usage Tips

### Basic
- **Controller Tab**: Main control interface
- **Test Tab**: Verify buttons work (green indicators)
- **Settings Tab**: Adjust deadzone & sensitivity

### Customization
- **Layout Tab**: 
  - "Enter Edit Mode" to customize button positions
  - Create profiles for different games
  - Adjust opacity and size

### Troubleshooting
- **Can't connect**: Check IP address, ensure both on same Wi-Fi
- **High latency**: Move closer to router, reduce interference
- **Buttons not working**: Check Test tab, restart app

## Game Compatibility

Works with games that support Xbox controller:
- Honkai Star Rail ✓
- Elden Ring ✓
- Forza Motorsport ✓
- Most Steam games ✓
- RetroArch emulator ✓

## Advanced

### Custom Port
```bash
VirtualGamepadServer.exe 27000  # Use port 27000 instead of 26760
```

### Profile Switching
- **Layout Tab** → Select profile from dropdown
- Each profile saves independently

### Detailed Logs
- **Test Tab** → Debug Logs section
- Shows all connection and input events
- Clear with "Clear Logs" button

## Keyboard Shortcuts (Android)

None currently - use on-screen buttons

## Performance

- **Latency**: <20ms typical
- **Update Rate**: 60Hz
- **Bandwidth**: ~1.3 KB/s

## Support

If stuck:
1. Check relevant README (android/ or pc-server/)
2. Review COMPILATION_INSTRUCTIONS.md
3. Check PC server console for errors
4. Try Test tab to diagnose

---

**That's it!** You're ready to game. 🎮

For detailed documentation, see:
- `README.md` - Full overview
- `android/README.md` - Android details
- `pc-server/README.md` - PC details
- `COMPILATION_INSTRUCTIONS.md` - Build guide
