using System;
using System.Threading.Tasks;
using VirtualGamepadServer;

class Program
{
    static async Task Main(string[] args)
    {
        Console.OutputEncoding = System.Text.Encoding.UTF8;
        Console.WriteLine("╔══════════════════════════════════════════════╗");
        Console.WriteLine("║    Virtual Gamepad Server for Windows        ║");
        Console.WriteLine("║    Version 1.0.0                             ║");
        Console.WriteLine("╚══════════════════════════════════════════════╝\n");
        
        int port = 26760;
        
        // Parse command line arguments
        if (args.Length > 0 && int.TryParse(args[0], out int customPort))
        {
            port = customPort;
        }
        
        using (var app = new GamepadServerApplication(port))
        {
            try
            {
                // Setup console cancel handler for graceful shutdown
                Console.CancelKeyPress += (sender, e) =>
                {
                    e.Cancel = true;
                    Console.WriteLine("\nShutdown requested...");
                    app.Stop();
                };
                
                // Start server
                await app.Start();
            }
            catch (Exception ex)
            {
                Console.ForegroundColor = ConsoleColor.Red;
                Console.WriteLine($"\nFatal error: {ex.Message}");
                Console.ResetColor();
                
                Console.WriteLine("\nTroubleshooting:");
                Console.WriteLine("1. Ensure ViGEm driver is installed: https://github.com/nefarius/ViGEm/releases");
                Console.WriteLine("2. Run this application as Administrator");
                Console.WriteLine("3. Check Windows Firewall settings for UDP port {port}");
                Console.WriteLine("4. Ensure Android device is on the same network");
                
                Environment.Exit(1);
            }
        }
    }
}
