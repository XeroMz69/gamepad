using System;
using Nefarius.ViGEm.Client;
using Nefarius.ViGEm.Client.Targets;
using Nefarius.ViGEm.Client.Targets.Xbox360;
using VirtualGamepadServer.Data;
using VirtualGamepadServer.Logging;

namespace VirtualGamepadServer.ViGEm;

/// <summary>
/// Wrapper for ViGEm virtual Xbox controller
/// </summary>
public class ViGEmController : IDisposable
{
    private ViGEmClient? _client;
    private IXbox360Controller? _controller;
    private readonly Logger _logger;
    private bool _isInitialized;
    
    public bool IsInitialized => _isInitialized;
    public bool IsConnected => _controller != null;
    
    public ViGEmController(Logger logger)
    {
        _logger = logger;
        _isInitialized = false;
    }
    
    /// <summary>
    /// Initialize ViGEm driver and create virtual controller
    /// </summary>
    public bool Initialize()
    {
        try
        {
            // Check if ViGEm driver is installed
            _client = new ViGEmClient();
            _logger.Info("[ViGEm] ViGEm client initialized");
            
            // Create virtual Xbox 360 controller
            _controller = _client.CreateXbox360Controller();
            _controller.Connect();
            
            _isInitialized = true;
            _logger.Info("[ViGEm] Virtual Xbox 360 controller created and connected");
            return true;
        }
        catch (Exception ex)
        {
            _logger.Error($"[ViGEm] Initialization failed: {ex.Message}");
            _logger.Error("[ViGEm] Please ensure ViGEm driver is installed from: https://github.com/nefarius/ViGEm/releases");
            _isInitialized = false;
            return false;
        }
    }
    
    /// <summary>
    /// Update controller state with input from Android
    /// </summary>
    public void UpdateState(InputMapper.XInputState state)
    {
        if (_controller == null || !_isInitialized)
            return;
        
        try
        {
            // Submit state to virtual controller
            _controller.SetButtonState(Xbox360Button.A, (state.ButtonFlags & InputMapper.XINPUT_GAMEPAD_A) != 0);
            _controller.SetButtonState(Xbox360Button.B, (state.ButtonFlags & InputMapper.XINPUT_GAMEPAD_B) != 0);
            _controller.SetButtonState(Xbox360Button.X, (state.ButtonFlags & InputMapper.XINPUT_GAMEPAD_X) != 0);
            _controller.SetButtonState(Xbox360Button.Y, (state.ButtonFlags & InputMapper.XINPUT_GAMEPAD_Y) != 0);
            _controller.SetButtonState(Xbox360Button.LB, (state.ButtonFlags & InputMapper.XINPUT_GAMEPAD_LB) != 0);
            _controller.SetButtonState(Xbox360Button.RB, (state.ButtonFlags & InputMapper.XINPUT_GAMEPAD_RB) != 0);
            _controller.SetButtonState(Xbox360Button.Start, (state.ButtonFlags & InputMapper.XINPUT_GAMEPAD_START) != 0);
            _controller.SetButtonState(Xbox360Button.Back, (state.ButtonFlags & InputMapper.XINPUT_GAMEPAD_BACK) != 0);
            
            // D-Pad
            _controller.SetButtonState(Xbox360Button.Up, (state.ButtonFlags & InputMapper.XINPUT_GAMEPAD_DPAD_UP) != 0);
            _controller.SetButtonState(Xbox360Button.Down, (state.ButtonFlags & InputMapper.XINPUT_GAMEPAD_DPAD_DOWN) != 0);
            _controller.SetButtonState(Xbox360Button.Left, (state.ButtonFlags & InputMapper.XINPUT_GAMEPAD_DPAD_LEFT) != 0);
            _controller.SetButtonState(Xbox360Button.Right, (state.ButtonFlags & InputMapper.XINPUT_GAMEPAD_DPAD_RIGHT) != 0);
            
            // Analog sticks
            _controller.SetAxisValue(Xbox360Axis.LeftThumbX, state.ThumbLX);
            _controller.SetAxisValue(Xbox360Axis.LeftThumbY, state.ThumbLY);
            _controller.SetAxisValue(Xbox360Axis.RightThumbX, state.ThumbRX);
            _controller.SetAxisValue(Xbox360Axis.RightThumbY, state.ThumbRY);
            
            // Triggers
            _controller.SetAxisValue(Xbox360Axis.LeftTrigger, (short)(state.TriggerL * 255));
            _controller.SetAxisValue(Xbox360Axis.RightTrigger, (short)(state.TriggerR * 255));
            
            _controller.SubmitReport();
        }
        catch (Exception ex)
        {
            _logger.Error($"[ViGEm] Update state error: {ex.Message}");
        }
    }
    
    /// <summary>
    /// Reset all controller inputs to neutral state
    /// </summary>
    public void ResetState()
    {
        if (_controller == null || !_isInitialized)
            return;
        
        try
        {
            var neutralState = new InputMapper.XInputState();
            UpdateState(neutralState);
            _logger.Debug("[ViGEm] Controller state reset to neutral");
        }
        catch (Exception ex)
        {
            _logger.Error($"[ViGEm] Reset state error: {ex.Message}");
        }
    }
    
    /// <summary>
    /// Disconnect and cleanup
    /// </summary>
    public void Disconnect()
    {
        try
        {
            ResetState();
            _controller?.Disconnect();
            _controller = null;
            _logger.Info("[ViGEm] Virtual controller disconnected");
        }
        catch (Exception ex)
        {
            _logger.Error($"[ViGEm] Disconnect error: {ex.Message}");
        }
    }
    
    public void Dispose()
    {
        Disconnect();
        _client?.Dispose();
    }
}
