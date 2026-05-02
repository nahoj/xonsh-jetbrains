# xonsh-jetbrains

Xonsh language support for JetBrains IDEs, backed by
[xonsh-language-server](https://github.com/FoamScience/xonsh-language-server)
through [LSP4IJ](https://github.com/redhat-developer/lsp4ij).

## Local IDE (optional, avoids re-downloading SDK)

Create `local.properties` at the project root:
```properties
# localIdePath=/Applications/PyCharm Professional.app/Contents
# localIdePath=/opt/pycharm-professional
localIdePath=/path/to/your/current/choice
```

`runIde` uses `localIdePath` from `local.properties` when set, and downloads IntelliJ from Maven when it is not set.

## Prerequisites

- JDK 21 (e.g. `mise use java@21` or `sdk install java 21.0.5-tem`)
- Gradle 8.10+ (only for bootstrap; build uses the wrapper afterwards)
- `xonsh-lsp` on `PATH`:
  ```sh
  uv tool install 'xonsh-lsp[jedi]'
  # mise use -g 'pipx:xonsh-lsp[jedi]' was not working: LSP4IJ couldn't start it.
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

## Credits

Syntax highlighting uses the TextMate grammar from
[vscode-xonsh](https://github.com/jnoortheen/xonsh-vscode-ext)
(`dist/tmlang-xonsh.json`), © 2020 Noortheen Raja NJ, MIT-licensed.
