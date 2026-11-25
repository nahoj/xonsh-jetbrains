import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import java.util.*

// Build-time security pins: transitive deps of the Gradle plugins below (Dependabot alerts).
// Remove once the plugins ship fixed versions.
buildscript {
    dependencies {
        constraints {
            classpath("com.fasterxml.jackson.core:jackson-core:2.21.7")
            classpath("com.fasterxml.jackson.core:jackson-databind:2.21.7")
            classpath("org.jsoup:jsoup:1.23.2")
        }
    }
}

plugins {
    id("java")
    id("org.jetbrains.changelog") version "2.5.0"
    id("org.jetbrains.kotlin.jvm") version "2.4.21"
    id("org.jetbrains.intellij.platform") version "2.20.1"
}

group = "eu.nahoj.xonsh"
version = "1.3.0"

// Load local.properties
val localProperties: Properties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { stream ->
        localProperties.load(stream)
    }
}

// Local IDE for development (runIde, compile). -PlocalIdePath=... takes precedence over local.properties.
val localIdePath: String? = providers.gradleProperty("localIdePath").orNull
    ?: localProperties.getProperty("localIdePath")

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
        if (localIdePath != null) {
            local(localIdePath)
        } else {
            pycharm("2026.3.0.2")
            jetbrainsRuntime()
        }
        plugin("com.redhat.devtools.lsp4ij", "0.30.1")
        bundledPlugin("org.jetbrains.plugins.textmate")
        testFramework(TestFrameworkType.Platform)
        pluginVerifier("1.411")
    }
}

intellijPlatform {
    pluginVerification {
        ides {
            // Quick local check on a single IDE, instead of all recommended ones (as in CI):
            //   ./gradlew verifyPlugin -PverifyIde=local          # IDE at localIdePath (local.properties)
            //   ./gradlew verifyPlugin -PverifyIde=IU:2025.3.1    # downloaded; <product code>:<version>
            // Product codes: IC/IU (IntelliJ IDEA Community/Ultimate), PC/PY (PyCharm), ...
            // Can also be set as `verifyIde=...` in local.properties (-P takes precedence).
            val verifyIde = providers.gradleProperty("verifyIde").orNull
                ?: localProperties.getProperty("verifyIde")
            if (verifyIde == "local" && localIdePath != null) {
                local(localIdePath)
            } else if (verifyIde != null) {
                val (code, version) = verifyIde.split(":", limit = 2)
                create(IntelliJPlatformType.fromCode(code), version)
            } else {
                recommended()
            }
        }
    }
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "242" // Java 21
            untilBuild = null
        }
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
        vendor.set(JvmVendorSpec.JETBRAINS)
    }
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
