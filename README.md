# xonsh-jetbrains

Xonsh language support for JetBrains IDEs, backed by
[xonsh-language-server](https://github.com/FoamScience/xonsh-language-server)
through [LSP4IJ](https://github.com/redhat-developer/lsp4ij).

## Local IDE (optional, avoids re-downloading SDK)

Add to `~/.gradle/gradle.properties`:
```properties
ideaLocalPath=/path/to/your/intellij-idea-ultimate
```

## Prerequisites

- JDK 21 (e.g. `sdk install java 21.0.5-tem`)
- Gradle 8.10+ (only for bootstrap; build uses the wrapper afterwards)
- `xonsh-lsp` on `PATH`:
  ```sh
  uv tool install 'xonsh-lsp[jedi]'
  ```

Targets IntelliJ Platform 2026.1 (build 261).

## Build & run

```sh
gradle wrapper                # one-time, generates ./gradlew
./gradlew runIde              # launches a sandbox IDE with the plugin loaded
./gradlew buildPlugin         # produces build/distributions/*.zip for install
```

In the sandbox IDE, open a `.xsh` file — LSP4IJ should spawn `xonsh-lsp`
and provide completion/hover/diagnostics/goto/references.
