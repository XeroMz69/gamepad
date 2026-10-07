using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Threading.Tasks;
using VirtualGamepadServer.Data;

namespace VirtualGamepadServer.Network;

/// <summary>
/// UDP server that listens for gamepad input from Android
/// </summary>
public class UdpServer : IDisposable
{
    private UdpClient? _udpClient;
    private int _port;
    private ClientSession? _clientSession;
    private CancellationTokenSource? _cancellationTokenSource;
    private Task? _listenTask;
    
    public event Action<string>? OnLog;
    public event Action<UdpPacket>? OnPacketReceived;
    public event Action<string>? OnClientConnected;
    public event Action? OnClientDisconnected;
    
    public bool IsRunning { get; private set; }
    public ClientSession? Client => _clientSession;
    
    public UdpServer(int port = 26760)
    {
        _port = port;
    }
    
    /// <summary>
    /// Start listening for incoming packets
    /// </summary>
    public async Task Start()
    {
        try
        {
            _udpClient = new UdpClient(_port);
            _cancellationTokenSource = new CancellationTokenSource();
            IsRunning = true;
            
            OnLog?.Invoke($"[UdpServer] Listening on UDP port {_port}");
            
            _listenTask = ListenAsync(_cancellationTokenSource.Token);
            await _listenTask;
        }
        catch (Exception ex)
        {
            OnLog?.Invoke($"[UdpServer] Error starting server: {ex.Message}");
            IsRunning = false;
        }
    }
    
    /// <summary>
    /// Stop listening
    /// </summary>
    public void Stop()
    {
        try
        {
            IsRunning = false;
            _cancellationTokenSource?.Cancel();
            _udpClient?.Dispose();
            _clientSession = null;
            OnLog?.Invoke("[UdpServer] Server stopped");
            OnClientDisconnected?.Invoke();
        }
        catch (Exception ex)
        {
            OnLog?.Invoke($"[UdpServer] Error stopping server: {ex.Message}");
        }
    }
    
    /// <summary>
    /// Listen for incoming UDP packets
    /// </summary>
    private async Task ListenAsync(CancellationToken cancellationToken)
    {
        while (!cancellationToken.IsCancellationRequested && _udpClient != null)
        {
            try
            {
                var result = await _udpClient.ReceiveAsync(cancellationToken);
                ProcessPacket(result.Buffer, result.RemoteEndPoint);
            }
            catch (OperationCanceledException)
            {
                break;
            }
            catch (Exception ex)
            {
                OnLog?.Invoke($"[UdpServer] Receive error: {ex.Message}");
            }
        }
    }
    
    /// <summary>
    /// Process incoming packet
    /// </summary>
    private void ProcessPacket(byte[] data, IPEndPoint remoteEndPoint)
    {
        if (data.Length != 22)
        {
            OnLog?.Invoke($"[UdpServer] Invalid packet size: {data.Length}, expected 22");
            return;
        }
        
        try
        {
            var packet = UdpPacket.Parse(data);
            
            // Create or update client session
            string clientIp = remoteEndPoint.Address.ToString();
            if (_clientSession == null)
            {
                _clientSession = new ClientSession
                {
                    DeviceIp = clientIp,
                    Port = remoteEndPoint.Port,
                    ConnectedTime = DateTime.UtcNow
                };
                OnClientConnected?.Invoke($"{clientIp}:{remoteEndPoint.Port}");
                OnLog?.Invoke($"[UdpServer] Client connected: {clientIp}:{remoteEndPoint.Port}");
            }
            
            // Record packet
            _clientSession.RecordPacket(packet.Sequence);
            
            // Fire event
            OnPacketReceived?.Invoke(packet);
            
            // Send ACK
            SendAck(_udpClient, remoteEndPoint);
        }
        catch (Exception ex)
        {
            OnLog?.Invoke($"[UdpServer] Packet parsing error: {ex.Message}");
        }
    }
    
    /// <summary>
    /// Send ACK packet to client
    /// </summary>
    private void SendAck(UdpClient? client, IPEndPoint? endpoint)
    {
        try
        {
            if (client == null || endpoint == null) return;
            
            byte[] ackPacket = new byte[8];
            client.SendAsync(ackPacket, ackPacket.Length, endpoint);
        }
        catch
        {
            // Silently ignore ACK failures
        }
    }
    
    /// <summary>
    /// Check if client is still connected (heartbeat detection)
    /// </summary>
    public bool IsClientConnected()
    {
        if (_clientSession == null) return false;
        return _clientSession.IsConnected;
    }
    
    /// <summary>
    /// Get current client info
    /// </summary>
    public ClientSession? GetClientInfo()
    {
        return _clientSession;
    }
    
    /// <summary>
    /// Disconnect client
    /// </summary>
    public void DisconnectClient()
    {
        if (_clientSession != null)
        {
            OnLog?.Invoke($"[UdpServer] Client disconnected: {_clientSession.DeviceIp}");
            _clientSession = null;
            OnClientDisconnected?.Invoke();
        }
    }
    
    public void Dispose()
    {
        Stop();
        _udpClient?.Dispose();
        _cancellationTokenSource?.Dispose();
    }
}
