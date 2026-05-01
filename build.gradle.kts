plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.3.0"
    id("org.jetbrains.intellij.platform") version "2.15.0"
}

group = "eu.nahoj.xonsh"
version = "0.2.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        // Path to a locally installed IDE — set `ideaLocalPath` in
        // ~/.gradle/gradle.properties (never committed).
        // Falls back to downloading IU 2025.3 from Maven if unset.
        val localPath = providers.gradleProperty("ideaLocalPath").orNull
        if (localPath != null) local(localPath) else intellijIdeaUltimate("2025.3")
        // LSP4IJ from Marketplace; must match a version compatible with build 261.
        plugin("com.redhat.devtools.lsp4ij", "0.13.0")
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
