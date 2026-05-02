import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import java.util.Properties

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.3.21"
    id("org.jetbrains.intellij.platform") version "2.16.0"
}

group = "eu.nahoj.xonsh"
version = "0.3.2"

// Load local.properties
val localProperties: Properties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { stream ->
        localProperties.load(stream)
    }
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2") // IntelliJ test framework uses JUnit 4

    intellijPlatform {
        // Set e.g. `localIdePath=/snap/pycharm-community/current` in `local.properties`.
        // Falls back to downloading IU 2025.3 from Maven if unset.
        val localPath = localProperties.getProperty("localIdePath")
        if (localPath != null) {
            local(localPath)
        } else {
            intellijIdeaUltimate("2025.3")
        }
        // LSP4IJ from Marketplace; must match a version compatible with build 261.
        plugin("com.redhat.devtools.lsp4ij", "0.13.0")
        testFramework(TestFrameworkType.Platform)
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "261"
        }
    }
}

kotlin {
    jvmToolchain(21)
}
