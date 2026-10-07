namespace VirtualGamepadServer.Data;

public class ClientSession
{
    public string? DeviceIp { get; set; }
    public int Port { get; set; }
    public DateTime ConnectedTime { get; set; }
    public DateTime LastPacketTime { get; set; }
    public uint LastSequenceNumber { get; set; }
    public int PacketCount { get; set; }
    public int LostPackets { get; set; }
    public List<int> LatencyHistory { get; set; } = new();
    
    public int AverageLatency => LatencyHistory.Count > 0 
        ? (int)LatencyHistory.Average() 
        : 0;
    
    public float PacketLossPercentage => PacketCount > 0 
        ? (float)LostPackets / (PacketCount + LostPackets) * 100f 
        : 0f;
    
    public bool IsConnected => 
        DateTime.UtcNow - LastPacketTime < TimeSpan.FromMilliseconds(500);
    
    public void RecordPacket(uint sequence)
    {
        LastPacketTime = DateTime.UtcNow;
        PacketCount++;
        
        // Check for packet loss
        if (LastSequenceNumber > 0)
        {
            var expectedSequence = LastSequenceNumber + 1;
            if (sequence > expectedSequence)
            {
                LostPackets += (int)(sequence - expectedSequence);
            }
        }
        
        LastSequenceNumber = sequence;
    }
    
    public void RecordLatency(int ms)
    {
        LatencyHistory.Add(ms);
        if (LatencyHistory.Count > 100)
            LatencyHistory.RemoveAt(0);
    }
}
