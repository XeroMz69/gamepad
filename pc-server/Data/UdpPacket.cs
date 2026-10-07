namespace VirtualGamepadServer.Data;

/// <summary>
/// Represents a UDP packet received from Android controller.
/// Total size: 22 bytes
/// </summary>
public struct UdpPacket
{
    // 4 bytes: Sequence number
    public uint Sequence { get; set; }
    
    // 8 bytes: Timestamp (milliseconds since epoch)
    public ulong Timestamp { get; set; }
    
    // 2 bytes: Button bitmask
    // Bit 0: A
    // Bit 1: B
    // Bit 2: X
    // Bit 3: Y
    // Bit 4: LB
    // Bit 5: RB
    // Bit 6: D-Pad Up
    // Bit 7: D-Pad Down
    // Bit 8: D-Pad Left
    // Bit 9: D-Pad Right
    // Bit 10: Menu
    // Bit 11: View
    // Bit 12: Xbox/Home
    // Bit 13-15: Reserved
    public ushort Buttons { get; set; }
    
    // 8 bytes: Analog sticks
    // 2 bytes: Left Stick X (-32768 to 32767 = -1.0 to 1.0)
    // 2 bytes: Left Stick Y
    // 2 bytes: Right Stick X
    // 2 bytes: Right Stick Y
    public short LeftStickX { get; set; }
    public short LeftStickY { get; set; }
    public short RightStickX { get; set; }
    public short RightStickY { get; set; }
    
    // 2 bytes: Triggers
    // 1 byte: LT (0-255 = 0% to 100%)
    // 1 byte: RT
    public byte TriggerLT { get; set; }
    public byte TriggerRT { get; set; }
    
    // Helper methods to get button states
    public bool GetButton(int bitPosition) => (Buttons & (1 << bitPosition)) != 0;
    
    public bool ButtonA => GetButton(0);
    public bool ButtonB => GetButton(1);
    public bool ButtonX => GetButton(2);
    public bool ButtonY => GetButton(3);
    public bool ButtonLB => GetButton(4);
    public bool ButtonRB => GetButton(5);
    public bool DPadUp => GetButton(6);
    public bool DPadDown => GetButton(7);
    public bool DPadLeft => GetButton(8);
    public bool DPadRight => GetButton(9);
    public bool ButtonMenu => GetButton(10);
    public bool ButtonView => GetButton(11);
    public bool ButtonXbox => GetButton(12);
    
    // Normalize analog values to 0-1 range
    public float LeftStickXNormalized => LeftStickX / 32767f;
    public float LeftStickYNormalized => LeftStickY / 32767f;
    public float RightStickXNormalized => RightStickX / 32767f;
    public float RightStickYNormalized => RightStickY / 32767f;
    
    public float TriggerLTNormalized => TriggerLT / 255f;
    public float TriggerRTNormalized => TriggerRT / 255f;
    
    public static UdpPacket Parse(byte[] data)
    {
        if (data.Length != 22)
            throw new ArgumentException($"Invalid packet size: {data.Length}, expected 22");
        
        var packet = new UdpPacket();
        int offset = 0;
        
        // Sequence (4 bytes)
        packet.Sequence = BitConverter.ToUInt32(data, offset);
        offset += 4;
        
        // Timestamp (8 bytes)
        packet.Timestamp = BitConverter.ToUInt64(data, offset);
        offset += 8;
        
        // Buttons (2 bytes)
        packet.Buttons = BitConverter.ToUInt16(data, offset);
        offset += 2;
        
        // Left Stick X (2 bytes)
        packet.LeftStickX = BitConverter.ToInt16(data, offset);
        offset += 2;
        
        // Left Stick Y (2 bytes)
        packet.LeftStickY = BitConverter.ToInt16(data, offset);
        offset += 2;
        
        // Right Stick X (2 bytes)
        packet.RightStickX = BitConverter.ToInt16(data, offset);
        offset += 2;
        
        // Right Stick Y (2 bytes)
        packet.RightStickY = BitConverter.ToInt16(data, offset);
        offset += 2;
        
        // Trigger LT (1 byte)
        packet.TriggerLT = data[offset];
        offset += 1;
        
        // Trigger RT (1 byte)
        packet.TriggerRT = data[offset];
        offset += 1;
        
        return packet;
    }
}
