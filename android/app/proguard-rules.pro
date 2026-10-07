# Keep Compose classes
-keep class androidx.compose.** { *; }
-keep class androidx.activity.** { *; }

# Keep data classes
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep generic type information
-keepattributes Signature
