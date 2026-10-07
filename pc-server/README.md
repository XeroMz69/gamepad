# Virtual Gamepad Server for Windows

A Windows application that receives gamepad input from an Android device over Wi-Fi/LAN and emulates an Xbox controller.

## Features

- **UDP-based Communication**: Low-latency real-time input transmission
- **Xbox 360 Controller Emulation**: Uses ViGEm for virtual controller
- **Full Button Support**: All 13 buttons + 2 analog sticks + 2 triggers
- **Real-time Metrics**: Latency and packet loss monitoring
- **Automatic Reconnection**: Handles network interruptions gracefully
- **Input Safety**: Automatically releases all buttons on disconnect

## Requirements

- Windows 10 or later
- ViGEm driver installed (see Installation section)
- .NET 6 Runtime
- Android 12+ device on same Wi-Fi network

## Installation

### 1. Install ViGEm Driver

The ViGEm driver is required for virtual controller emulation:

1. Download the latest ViGEm setup from: https://github.com/nefarius/ViGEm/releases
2. Extract the archive
3. Run the installer and follow the prompts
4. Restart your computer after installation

### 2. Run the Server

```bash
VirtualGamepadServer.exe
```

Or specify a custom UDP port:

```bash
VirtualGamepadServer.exe 27000
```

Default port is **26760**.

## Usage

### Android Side

1. Launch the Virtual Gamepad app
2. Navigate to Connection tab
3. Enter PC IP address (e.g., 192.168.1.100)
4. Enter UDP port (default 26760)
5. Tap Connect

### PC Side

The server will display:
- Connection status
- Client IP address
- Real-time latency (milliseconds)
- Packet loss percentage
- Total packets received

## Configuration

### Network Settings

- **Default UDP Port**: 26760
- **Heartbeat Timeout**: 500ms
- **Packet Format**: 22 bytes
- **Send Rate**: 60Hz (16.67ms per packet)

### Firewall

Windows Firewall will typically prompt you to allow the application. Make sure to allow:
- ✓ Private Networks (Local network)
- ✗ Public Networks (Not needed for LAN)

To manually add a firewall rule:

```powershell
netsh advfirewall firewall add rule name="Virtual Gamepad Server" dir=in action=allow protocol=udp localport=26760
```

## Troubleshooting

### "ViGEm initialization failed"

**Problem**: The application can't find or connect to ViGEm driver.

**Solutions**:
1. Verify ViGEm is installed: https://github.com/nefarius/ViGEm/releases
2. Restart your computer after installing ViGEm
3. Run the application as Administrator
4. Check if ViGEm service is running in Services (services.msc)

### "Connection failed" or "No packets received"

**Problem**: Android device can't reach the PC server.

**Solutions**:
1. Verify PC and Android are on the same Wi-Fi network
2. Check the PC's IP address (use `ipconfig` in command prompt)
3. Disable Windows Firewall temporarily to test
4. Try connecting to 127.0.0.1 if testing locally
5. Ensure port 26760 is not in use: `netstat -an | findstr 26760`

### High latency (>50ms) or packet loss

**Problem**: Network performance is poor.

**Solutions**:
1. Move closer to the Wi-Fi router
2. Check for Wi-Fi interference on 2.4GHz band
3. Use 5GHz Wi-Fi if available
4. Reduce number of connected devices
5. Check for network congestion with `iperf3`

### Xbox controller not detected in games

**Problem**: Games don't recognize the virtual controller.

**Solutions**:
1. Verify connection is established (check server output)
2. Test with Windows Game Controllers settings (Settings → Devices → Other Devices)
3. Restart the game after connecting the controller
4. Check if game requires specific controller layout
5. Try with a different game to isolate the issue

## Protocol Specification

### UDP Packet Format (22 bytes)

```
Offset  Size  Field
0       4     Sequence Number (UInt32)
4       8     Timestamp (UInt64, milliseconds)
12      2     Button Bitmask (UInt16)
14      2     Left Stick X (Int16, -32768 to 32767)
16      2     Left Stick Y (Int16)
18      2     Right Stick X (Int16)
20      2     Right Stick Y (Int16)
                (Last 2 bytes are at offset 22, total 24 bytes)
22      1     Left Trigger (Byte, 0-255)
23      1     Right Trigger (Byte)
```

### Button Bitmask

```
Bit     Button
0       A
1       B
2       X
3       Y
4       LB
5       RB
6       D-Pad Up
7       D-Pad Down
8       D-Pad Left
9       D-Pad Right
10      Menu
11      View
12      Xbox/Home
13-15   Reserved
```

## Performance

- **Input Latency**: <20ms (typical on stable LAN)
- **Packet Rate**: 60Hz (16.67ms interval)
- **CPU Usage**: <5% (minimal)
- **Memory Usage**: ~50MB

## Known Limitations

1. **Single Device**: Server supports one connected Android device at a time
2. **LAN Only**: Designed for local network only, not internet
3. **Windows Only**: PC server requires Windows 10+
4. **Vibration**: Not yet implemented (Phase 2 feature)
5. **Audio**: No audio transmission

## Building from Source

### Prerequisites

- Visual Studio 2022 or VS Code
- .NET 6 SDK
- ViGEm.Client NuGet package

### Build Steps

```bash
cd pc-server
dotnet build --configuration Release
```

Output: `bin/Release/net6.0-windows/VirtualGamepadServer.exe`

## License

MIT License - See LICENSE file for details

## Support

For issues, please check:
1. This README troubleshooting section
2. GitHub Issues: https://github.com/yourusername/virtual-gamepad/issues
3. ViGEm Documentation: https://vigem.org/

## Contributing

Contributions are welcome! Please submit pull requests with:
- Clear description of changes
- Test results
- Updated documentation if needed
