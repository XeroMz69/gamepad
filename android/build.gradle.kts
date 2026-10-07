// Root level build.gradle.kts - only for plugin management
// Actual configuration is in app/build.gradle.kts

plugins {
    // These are declared but not applied at root level
    id("com.android.application") version "8.1.0" apply false
    kotlin("android") version "1.9.0" apply false
}
