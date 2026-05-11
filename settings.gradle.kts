rootProject.name = "xonsh-jetbrains"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    // Auto-detect installed JDKs (mise/sdkman/etc.) and download a matching one
    // if none is found — so `jvmToolchain(21)` in build.gradle.kts keeps working
    // regardless of the user's default `java -version`.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
