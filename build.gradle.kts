import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import java.util.Properties

plugins {
    id("java")
    id("org.jetbrains.changelog") version "2.5.0"
    id("org.jetbrains.kotlin.jvm") version "2.3.21"
    id("org.jetbrains.intellij.platform") version "2.16.0"
}

group = "eu.nahoj.xonsh"
version = "1.1.1"

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
        // Falls back to downloading the IDE from Maven if unset.
        val localPath = localProperties.getProperty("localIdePath")
        if (localPath != null) {
            local(localPath)
        } else {
            pycharm("2026.1.1")
        }
        plugin("com.redhat.devtools.lsp4ij", "0.19.3")
        bundledPlugin("org.jetbrains.plugins.textmate")
        testFramework(TestFrameworkType.Platform)
        pluginVerifier("1.402") // Temporary pin because of failure with 1.403
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "242" // Java 21
            untilBuild = null
        }
    }
}

kotlin {
    jvmToolchain(21)
}

val resolveGrammar by tasks.registering(Exec::class) {
    description = "Compile TextMate grammar JSON from vscode-xonsh's source YAML"
    group = "build"
    // TODO either make vscode-xonsh a proper submodule/dependency, or fork the grammar files
    onlyIf {
        file("../vscode-xonsh/src/tmlang").exists()
    }
    commandLine("python3", "scripts/resolve_grammar.py")
    inputs.dir("../vscode-xonsh/src/tmlang")
    inputs.file("scripts/resolve_grammar.py")
    outputs.file("src/main/resources/textmate/xonsh/syntaxes/xonsh.tmLanguage.json")
}

tasks.named("processResources") {
    dependsOn(resolveGrammar)
}
