# Virtual Gamepad - Android Client

Android application that turns your smartphone into a wireless Xbox controller for PC games via Wi-Fi/LAN.

## Features

- **Full Xbox 360 Layout**: All 13 buttons, 2 analog sticks, and 2 triggers
- **Real-time Input**: 60Hz update rate (~16.67ms per frame)
- **Low Latency**: Typically <20ms on stable LAN
- **Fully Customizable**: Drag/resize/reposition any button or stick
- **Multiple Profiles**: Create and save different layouts for different games
- **Deadzone & Sensitivity**: Adjustable per-game settings
- **Response Curves**: Linear, Quadratic, Cubic options
- **Multi-touch Support**: Press multiple buttons simultaneously
- **Landscape Optimized**: Works on various screen sizes and aspect ratios
- **Real-time Diagnostics**: View connection status, latency, and packet loss

## Requirements

- Android 12 (API 31) or higher
- Wi-Fi connection (same network as PC)
- PC server application running on Windows

## Installation

### From APK

1. Download the latest APK release
2. Enable "Unknown Sources" in Android settings
3. Install the APK
4. Launch the app

### From Source

```bash
cd android
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk`

## Usage

### Step 1: Start PC Server

1. Download and install ViGEm driver (see PC Server README)
2. Run `VirtualGamepadServer.exe`
3. Note the PC's IP address

### Step 2: Connect Android

1. Launch Virtual Gamepad app
2. Go to **Connection** tab
3. Enter PC IP address (e.g., `192.168.1.100`)
4. Enter UDP port (default `26760`)
5. Tap **Connect**
6. Status should show **Connected** in green

### Step 3: Test the Connection

1. Go to **Test** tab
2. Press buttons on the controller
3. Observe button indicators light up
4. Check latency display
5. Open a game that supports Xbox controller
6. Enjoy!

## Configuration

### Controller Screen

Display with all buttons and sticks. Touch to control:
- **Face Buttons** (A/B/X/Y): Tap on the button
- **D-Pad**: Tap directional button
- **Analog Sticks**: Drag from center
- **Shoulder Buttons** (LB/RB): Tap
- **Triggers** (LT/RT): Tap or drag

### Layout Editor

Customize button positions and sizes:

1. Go to **Layout** tab
2. Tap **Enter Edit Mode**
3. Select a button from the list
4. Adjust position, size, and opacity with sliders
5. Tap **Save Changes**
6. Tap **Close** to return

#### Creating Profiles

1. In Layout Editor, select a profile from the dropdown
2. Or create a new profile with **+ New Profile**
3. Edit the layout
4. Select a different profile to switch
5. Each profile saves independently

### Settings

Configure controller behavior:

- **Deadzone** (0-50%): Prevents small stick drift
- **Sensitivity** (0.5x - 2.0x): Adjust stick responsiveness
- **Response Curve**: Choose input curve type
  - **Linear**: 1:1 stick movement
  - **Quadratic**: More responsive near center
  - **Cubic**: Smoothest curve
- **Stick Inversion**: Mirror X or Y axis

### Test Screen

Real-time diagnostics:

- **Connection Status**: Disconnected/Connecting/Connected/Error
- **Latency**: Round-trip time in milliseconds
- **Button Indicators**: Visual feedback of button presses
- **Stick Display**: Current X/Y values
- **Trigger Display**: Analog values
- **Debug Logs**: System messages and errors

## Troubleshooting

### Connection Fails

**Problem**: Can't connect to PC

**Solutions**:
1. Verify PC server is running (check console)
2. Check IP address is correct (use `ipconfig` on PC)
3. Verify firewall allows UDP port 26760
4. Ensure both devices on same Wi-Fi network
5. Try restarting both apps

### High Latency (>50ms)

**Problem**: Controller response is slow

**Solutions**:
1. Move closer to Wi-Fi router
2. Check for Wi-Fi interference
3. Reduce number of connected devices
4. Try 5GHz band if available
5. Check for background network usage

### Buttons/Sticks Not Responding

**Problem**: Some buttons don't work in games

**Solutions**:
1. Check Test tab to verify buttons register
2. Verify connection is established
3. Check game's controller settings
4. Some games require specific button mapping
5. Try a different game to test

### App Crashes

**Problem**: App closes unexpectedly

**Solutions**:
1. Clear app cache: Settings → Apps → Virtual Gamepad → Storage → Clear Cache
2. Uninstall and reinstall the app
3. Check Android version is 12+
4. Try on a different device if available

### Layout Resets

**Problem**: Custom layout reverts to default

**Solutions**:
1. Ensure device has sufficient storage
2. App may crash if layout data corrupted
3. Try creating a new profile instead
4. Export layout for backup before editing

## Advanced Configuration

### Recommended Settings by Game Type

#### Action Games (Honkai Star Rail, Elden Ring)
- Deadzone: 15-20%
- Sensitivity: 1.0x - 1.5x
- Response Curve: Quadratic
- Invert Y: Optional

#### FPS Games (Valorant, Counter-Strike)
- Deadzone: 10-15%
- Sensitivity: 0.8x - 1.2x
- Response Curve: Linear
- Invert Y: Usually enabled

#### Racing Games (Forza, Gran Turismo)
- Deadzone: 5-10%
- Sensitivity: 1.0x - 1.5x
- Response Curve: Linear or Cubic
- Invert Y: No

#### Emulators (RetroArch, Citra)
- Deadzone: 20-30%
- Sensitivity: 1.0x
- Response Curve: Linear
- Invert: As needed

## Protocol Details

### UDP Packet Format

22-byte packet sent @ 60Hz:

```
Offset  Size  Content
0-3     4B    Sequence Number
4-11    8B    Timestamp (ms since epoch)
12-13   2B    Button Bitmask
14-15   2B    Left Stick X (-32768 to 32767)
16-17   2B    Left Stick Y
18-19   2B    Right Stick X
20-21   2B    Right Stick Y
```

### Button Mapping

```
Bit  Button         Bit  Button
0    A              8    D-Pad Left
1    B              9    D-Pad Right
2    X              10   Menu
3    Y              11   View
4    LB             12   Xbox/Home
5    RB             13-15 Reserved
6    D-Pad Up
7    D-Pad Down
```

## Performance

- **CPU**: <5% usage (minimal)
- **Memory**: ~80MB RAM
- **Network**: ~1.3 KB/s upstream (60Hz @ 22 bytes)
- **Battery**: ~20mA drain (varies by device)

## Building from Source

### Prerequisites

- Android Studio 2021.3+
- Android SDK 31 (API 31)
- Kotlin 1.9+
- Gradle 7.0+

### Build Steps

```bash
cd android
./gradlew build
./gradlew assembleRelease  # For release APK
```

Outputs:
- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
- Release APK: `app/build/outputs/apk/release/app-release.apk`

### Installing on Device

```bash
./gradlew installDebug
```

## Architecture

```
MainActivity
    ↓
GamepadViewModel (State Management)
    ├── ControllerState (Button/Stick values)
    ├── ConnectionSettings (IP/Port)
    ├── ControllerSettings (Deadzone/Sensitivity)
    └── DebugLogs
    ↓
MainScreen (Navigation)
    ├── ControllerScreen (UI + Touch Input)
    ├── LayoutEditorScreen (Customization)
    ├── TestScreen (Diagnostics)
    ├── SettingsScreen (Configuration)
    └── ConnectionScreen (IP/Status)
    ↓
NetworkManager
    ↓
GamepadUdpClient
    ↓
UDP Socket → PC Server
```

## Dependencies

- **Jetpack Compose 1.5**: UI framework
- **Compose Material 3**: Material Design
- **Compose Navigation**: Screen navigation
- **DataStore**: Settings persistence
- **Coroutines**: Async operations
- **Android Core KTX**: Kotlin extensions

All via gradle build system.

## Known Limitations

1. **Single Stick Click**: Left/Right stick click buttons not fully implemented
2. **Vibration**: Not yet supported (Phase 2)
3. **Audio**: No audio transmission
4. **Wi-Fi Only**: Requires local network (for latency)
5. **One Connection**: Only one PC per app instance

## Future Enhancements (Phase 2+)

- [ ] Vibration feedback from PC
- [ ] Dual controller support
- [ ] Cloud profile sync
- [ ] QR code pairing
- [ ] Auto-discovery via mDNS
- [ ] Touch haptics customization
- [ ] Network optimization
- [ ] Performance profiling tools

## License

MIT License - See LICENSE file

## Support

For issues and feature requests:
1. Check this README
2. Open issue on GitHub
3. Contact developer

## Credits

- **ViGEm**: Virtual gamepad driver (Windows)
- **Jetpack Compose**: Modern Android UI
- **Kotlin**: Language

---

**Made with ❤️ for gamers**
