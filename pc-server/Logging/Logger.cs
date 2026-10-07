using System;
using System.IO;
using System.Text;

namespace VirtualGamepadServer.Logging;

public enum LogLevel
{
    Debug,
    Info,
    Warning,
    Error
}

public class Logger
{
    private readonly string _logFilePath;
    private readonly object _lockObject = new object();
    
    public Logger(string logFileName = "gamepad_server.log")
    {
        _logFilePath = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, logFileName);
        try
        {
            // Ensure directory exists
            string? directory = Path.GetDirectoryName(_logFilePath);
            if (directory != null && !Directory.Exists(directory))
            {
                Directory.CreateDirectory(directory);
            }
        }
        catch
        {
            // If we can't create directory, just skip file logging
        }
    }
    
    public void Log(string message, LogLevel level = LogLevel.Info)
    {
        string timestamp = DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss.fff");
        string levelStr = level.ToString().ToUpper();
        string formatted = $"[{timestamp}] [{levelStr}] {message}";
        
        // Console output
        ConsoleColor originalColor = Console.ForegroundColor;
        try
        {
            Console.ForegroundColor = level switch
            {
                LogLevel.Debug => ConsoleColor.Gray,
                LogLevel.Info => ConsoleColor.Green,
                LogLevel.Warning => ConsoleColor.Yellow,
                LogLevel.Error => ConsoleColor.Red,
                _ => ConsoleColor.White
            };
            Console.WriteLine(formatted);
        }
        finally
        {
            Console.ForegroundColor = originalColor;
        }
        
        // File output
        lock (_lockObject)
        {
            try
            {
                File.AppendAllText(_logFilePath, formatted + Environment.NewLine, Encoding.UTF8);
            }
            catch
            {
                // Silently ignore file write errors
            }
        }
    }
    
    public void Debug(string message) => Log(message, LogLevel.Debug);
    public void Info(string message) => Log(message, LogLevel.Info);
    public void Warning(string message) => Log(message, LogLevel.Warning);
    public void Error(string message) => Log(message, LogLevel.Error);
}
