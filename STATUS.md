# xonsh-jetbrains — project status

## Architecture decisions made

- **LSP**: xonsh-language-server (FoamScience) via pygls + tree-sitter-xonsh + Jedi.
  Pipeline: tree-sitter parse → transpile xonsh→Python (bidirectional column mapping)
  → Jedi for completion/hover/goto/references/diagnostics. 302 upstream tests pass.
  Server installed via `uv tool install 'xonsh-lsp[jedi]'`.

- **IDE client**: native IntelliJ plugin using LSP4IJ (com.redhat.devtools.lsp4ij)
  as the LSP client. The plugin declares a Xonsh FileType (.xsh, .xonshrc) and starts
  `xonsh-lsp` over stdio.

- **Syntax highlighting**: native IntelliJ lexer (XonshLexer, LexerBase) — simpler than
  juggling TextMate bundle paths inside a jar. Covers Python + xonsh extensions
  ($, @, !, env vars). A TM grammar (recycled from vscode-xonsh) could eventually
  replace it for finer coverage.

- **Executable resolution**: EnvironmentUtil.getValue("PATH") to locate `xonsh-lsp`
  since an IDE launched in GUI mode does not inherit the shell PATH. The executable
  is passed via the ProcessStreamConnectionProvider constructor (the `commands` field
  is read directly in start(), not via a getCommands() override).

- **Local SDK**: `ideaLocalPath` in `~/.gradle/gradle.properties` (not committed) to
  point at the IDE installed via Toolbox. Maven fallback to IU 2025.3 for CI/others.

## Working (verified in runIde sandbox)

- [x] Xonsh FileType recognized (.xsh, .xonshrc, xonsh shebang)
- [x] LSP server starts (visible in View → Tool Windows → Language Servers)
- [x] Tooltips/hover on symbols
- [x] Basic highlighting (keywords, strings, comments, numbers, builtins, env vars)
- [x] buildPlugin produces an installable .zip (Settings → Plugins → Install from Disk)
- [x] goto definition (Ctrl+B)
- [x] find usages (Alt+F7)
- [x] completion (Ctrl+Space)

## Known broken / to fix

- [ ] Highlighting of shell ops $()  !()  @()  $[]  ![] — the 2-char sequences are
      not emitted as a single SHELL_OP token; the `(` falls into OPERATOR
- [ ] No XONSH_* entry in Settings → Editor → Color Scheme because ColorSettingsPage
      is not registered
- [ ] Rename (textDocument/rename) not implemented on the xonsh-lsp side — decision: defer

## Backlog / discussed but not started

- Full TextMate grammar (recycle vscode-xonsh dist/tmlang-xonsh.json once
  the `$apply` is resolved via node rebuild)
- ColorSettingsPage to expose the XONSH_* keys in Settings
- Upstream rename contribution to xonsh-language-server (FoamScience)
- Open untilBuild (currently "261.*") — to be relaxed before Marketplace publish
- Icon for the FileType (null for now)
- Plugin tests

## Tech stack

| Component                       | Version                                  |
|---------------------------------|------------------------------------------|
| IntelliJ Platform               | 2026.1 (local) / 2025.3 (Maven fallback) |
| IntelliJ Platform Gradle Plugin | 2.15.0                                   |
| Kotlin                          | 2.3.0                                    |
| Gradle wrapper                  | 9.6.0-milestone-1                        |
| Build JDK                       | 21                                       |
| LSP4IJ                          | 0.13.0                                   |
| xonsh-lsp                       | 0.2.0                                    |
| tree-sitter-xonsh               | 0.2.0                                    |
| Jedi                            | 0.19.2                                   |

## Useful commands

```sh
# Build
JAVA_HOME=$HOME/.local/share/mise/installs/java/temurin-21.0.10+7.0.LTS \
  ./gradlew buildPlugin
# → build/distributions/xonsh-jetbrains-x.y.z.zip

# IDE sandbox
JAVA_HOME=$HOME/.local/share/mise/installs/java/temurin-21.0.10+7.0.LTS \
  ./gradlew runIde

# Runtime prerequisites
uv tool install 'xonsh-lsp[jedi]'   # once
# + LSP4IJ installed from the Marketplace in the target IDE
```
