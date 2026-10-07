namespace VirtualGamepadServer.Data;

/// <summary>
/// Maps Android button/stick inputs to XInput equivalents
/// </summary>
public class InputMapper
{
    public class XInputState
    {
        public ushort ButtonFlags { get; set; }  // Button bitmask
        public short ThumbLX { get; set; }        // Left stick X (-32768 to 32767)
        public short ThumbLY { get; set; }        // Left stick Y
        public short ThumbRX { get; set; }        // Right stick X
        public short ThumbRY { get; set; }        // Right stick Y
        public byte TriggerL { get; set; }        // LT (0-255)
        public byte TriggerR { get; set; }        // RT
    }
    
    // XInput button constants
    public const ushort XINPUT_GAMEPAD_DPAD_UP = 0x0001;
    public const ushort XINPUT_GAMEPAD_DPAD_DOWN = 0x0002;
    public const ushort XINPUT_GAMEPAD_DPAD_LEFT = 0x0004;
    public const ushort XINPUT_GAMEPAD_DPAD_RIGHT = 0x0008;
    public const ushort XINPUT_GAMEPAD_START = 0x0010;      // Menu
    public const ushort XINPUT_GAMEPAD_BACK = 0x0020;       // View
    public const ushort XINPUT_GAMEPAD_LEFT_THUMB = 0x0040;  // Left stick click
    public const ushort XINPUT_GAMEPAD_RIGHT_THUMB = 0x0080; // Right stick click
    public const ushort XINPUT_GAMEPAD_LB = 0x0100;
    public const ushort XINPUT_GAMEPAD_RB = 0x0200;
    public const ushort XINPUT_GAMEPAD_A = 0x1000;
    public const ushort XINPUT_GAMEPAD_B = 0x2000;
    public const ushort XINPUT_GAMEPAD_X = 0x4000;
    public const ushort XINPUT_GAMEPAD_Y = 0x8000;
    
    public static XInputState Map(UdpPacket packet)
    {
        var state = new XInputState();
        
        // Map buttons
        if (packet.ButtonA) state.ButtonFlags |= XINPUT_GAMEPAD_A;
        if (packet.ButtonB) state.ButtonFlags |= XINPUT_GAMEPAD_B;
        if (packet.ButtonX) state.ButtonFlags |= XINPUT_GAMEPAD_X;
        if (packet.ButtonY) state.ButtonFlags |= XINPUT_GAMEPAD_Y;
        if (packet.ButtonLB) state.ButtonFlags |= XINPUT_GAMEPAD_LB;
        if (packet.ButtonRB) state.ButtonFlags |= XINPUT_GAMEPAD_RB;
        if (packet.DPadUp) state.ButtonFlags |= XINPUT_GAMEPAD_DPAD_UP;
        if (packet.DPadDown) state.ButtonFlags |= XINPUT_GAMEPAD_DPAD_DOWN;
        if (packet.DPadLeft) state.ButtonFlags |= XINPUT_GAMEPAD_DPAD_LEFT;
        if (packet.DPadRight) state.ButtonFlags |= XINPUT_GAMEPAD_DPAD_RIGHT;
        if (packet.ButtonMenu) state.ButtonFlags |= XINPUT_GAMEPAD_START;
        if (packet.ButtonView) state.ButtonFlags |= XINPUT_GAMEPAD_BACK;
        
        // Map analog sticks
        state.ThumbLX = packet.LeftStickX;
        state.ThumbLY = packet.LeftStickY;
        state.ThumbRX = packet.RightStickX;
        state.ThumbRY = packet.RightStickY;
        
        // Map triggers
        state.TriggerL = packet.TriggerLT;
        state.TriggerR = packet.TriggerRT;
        
        return state;
    }
}
