using System;
using System.Threading;
using System.Threading.Tasks;
using VirtualGamepadServer.Data;
using VirtualGamepadServer.Logging;
using VirtualGamepadServer.Network;
using VirtualGamepadServer.ViGEm;

namespace VirtualGamepadServer;

/// <summary>
/// Main application controller that orchestrates all components
/// </summary>
public class GamepadServerApplication : IDisposable
{
    private readonly Logger _logger;
    private readonly UdpServer _udpServer;
    private readonly ViGEmController _vigEmController;
    private CancellationTokenSource? _cancellationTokenSource;
    private int _packetCount;
    private DateTime _lastStatusTime;
    
    public GamepadServerApplication(int port = 26760)
    {
        _logger = new Logger();
        _udpServer = new UdpServer(port);
        _vigEmController = new ViGEmController(_logger);
        _packetCount = 0;
        _lastStatusTime = DateTime.Now;
        
        // Hook up events
        _udpServer.OnLog += msg => _logger.Info(msg);
        _udpServer.OnPacketReceived += HandlePacket;
        _udpServer.OnClientConnected += HandleClientConnected;
        _udpServer.OnClientDisconnected += HandleClientDisconnected;
    }
    
    /// <summary>
    /// Start the server
    /// </summary>
    public async Task Start()
    {
        try
        {
            _logger.Info("=".PadRight(50, '='));
            _logger.Info("Virtual Gamepad Server - Starting");
            _logger.Info("=".PadRight(50, '='));
            
            // Initialize ViGEm
            if (!_vigEmController.Initialize())
            {
                _logger.Error("Failed to initialize ViGEm controller");
                _logger.Error("Please ensure:");
                _logger.Error("1. ViGEm driver is installed");
                _logger.Error("2. Run as Administrator if on Windows");
                throw new Exception("ViGEm initialization failed");
            }
            
            _cancellationTokenSource = new CancellationTokenSource();
            
            // Start UDP server
            _logger.Info("Starting UDP server...");
            var serverTask = _udpServer.Start();
            
            // Start status monitoring
            var statusTask = MonitorStatus(_cancellationTokenSource.Token);
            
            await Task.WhenAll(serverTask, statusTask);
        }
        catch (Exception ex)
        {
            _logger.Error($"Startup error: {ex.Message}");
            throw;
        }
    }
    
    /// <summary>
    /// Stop the server gracefully
    /// </summary>
    public void Stop()
    {
        try
        {
            _logger.Info("Stopping server...");
            _cancellationTokenSource?.Cancel();
            _udpServer.Stop();
            _vigEmController.ResetState();
            _logger.Info("Server stopped");
        }
        catch (Exception ex)
        {
            _logger.Error($"Stop error: {ex.Message}");
        }
    }
    
    /// <summary>
    /// Handle incoming UDP packet
    /// </summary>
    private void HandlePacket(UdpPacket packet)
    {
        _packetCount++;
        
        // Map Android input to XInput
        var xInputState = InputMapper.Map(packet);
        
        // Update virtual controller
        _vigEmController.UpdateState(xInputState);
        
        // Record latency for client session
        if (_udpServer.Client != null)
        {
            var latency = (int)(DateTime.UtcNow.Ticks / 10000 - packet.Timestamp);
            _udpServer.Client.RecordLatency(Math.Max(latency, 1));
        }
    }
    
    /// <summary>
    /// Handle client connection
    /// </summary>
    private void HandleClientConnected(string clientInfo)
    {
        _logger.Info($"Client connected: {clientInfo}");
        _vigEmController.ResetState();
    }
    
    /// <summary>
    /// Handle client disconnection
    /// </summary>
    private void HandleClientDisconnected()
    {
        _logger.Info("Client disconnected");
        _vigEmController.ResetState();
        _packetCount = 0;
    }
    
    /// <summary>
    /// Monitor server status and display metrics
    /// </summary>
    private async Task MonitorStatus(CancellationToken cancellationToken)
    {
        while (!cancellationToken.IsCancellationRequested)
        {
            try
            {
                await Task.Delay(2000, cancellationToken);
                
                var now = DateTime.Now;
                if ((now - _lastStatusTime).TotalSeconds >= 5)
                {
                    DisplayStatus();
                    _lastStatusTime = now;
                }
            }
            catch (OperationCanceledException)
            {
                break;
            }
        }
    }
    
    /// <summary>
    /// Display current server status
    /// </summary>
    private void DisplayStatus()
    {
        var client = _udpServer.GetClientInfo();
        
        Console.WriteLine("\n" + "=".PadRight(50, '='));
        Console.WriteLine($"Time: {DateTime.Now:yyyy-MM-dd HH:mm:ss}");
        Console.WriteLine($"Server Status: {(_udpServer.IsRunning ? "Running" : "Stopped")}");
        
        if (client != null && _udpServer.IsClientConnected())
        {
            Console.WriteLine($"Client Connected: Yes");
            Console.WriteLine($"Client IP: {client.DeviceIp}:{client.Port}");
            Console.WriteLine($"Packets Received: {client.PacketCount}");
            Console.WriteLine($"Latency: {client.AverageLatency}ms");
            Console.WriteLine($"Packet Loss: {String.Format("{0:F2}%", client.PacketLossPercentage)}");
            Console.WriteLine($"ViGEm Controller: {(_vigEmController.IsConnected ? "Active" : "Inactive")}");
        }
        else
        {
            Console.WriteLine("Client Connected: No");
            Console.WriteLine("Waiting for Android connection...");
        }
        
        Console.WriteLine("=".PadRight(50, '=') + "\n");
    }
    
    /// <summary>
    /// Get current server metrics
    /// </summary>
    public (bool isConnected, string clientIp, int latency, float packetLoss, int packetCount) GetMetrics()
    {
        var client = _udpServer.GetClientInfo();
        
        if (client == null || !_udpServer.IsClientConnected())
        {
            return (false, "", 0, 0, 0);
        }
        
        return (true, $"{client.DeviceIp}:{client.Port}", client.AverageLatency, client.PacketLossPercentage, client.PacketCount);
    }
    
    public void Dispose()
    {
        Stop();
        _udpServer.Dispose();
        _vigEmController.Dispose();
        _cancellationTokenSource?.Dispose();
    }
}
