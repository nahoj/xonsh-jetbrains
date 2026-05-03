# xonsh-jetbrains — project status

## Architecture decisions made

- **LSP**: xonsh-language-server (FoamScience) via pygls + tree-sitter-xonsh + Jedi.
  Pipeline: tree-sitter parse → transpile xonsh→Python (bidirectional column mapping)
  → Jedi for completion/hover/goto/references/diagnostics. 302 upstream tests pass.
  Server installed via `uv tool install 'xonsh-lsp[jedi]'`.

- **IDE client**: IntelliJ plugin using LSP4IJ (com.redhat.devtools.lsp4ij)
  as the LSP client. No custom Xonsh `Language` / `FileType` — the bundled TextMate
  plugin claims `.xsh` / `.xonshrc` via the grammar bundle, and LSP4IJ binds the
  server through `fileNamePatternMapping` (`*.xsh;.xonshrc;xonshrc`,
  languageId=`xonsh`). `xonsh-lsp` runs over stdio.

- **Syntax highlighting**: TextMate grammar recycled from vscode-xonsh (MIT).
  Shipped as a VS Code-style bundle under `src/main/resources/textmate/xonsh/`
  (`package.json` + `language-configuration.json` + `syntaxes/xonsh.tmLanguage.json`)
  and registered via `com.intellij.textmate.bundleProvider`. Provider extracts to
  `PathManager.getSystemDir()/xonsh-textmate/xonsh/` because the TM plugin reads
  bundles from a real filesystem `Path` (NIO), not from the jar.

  Note: vscode-xonsh's `dist/tmlang-xonsh.json` leaves the custom `$apply` macro
  unresolved (their `postbuild` only runs `js-yaml`). VS Code's TM engine tolerates
  it; IntelliJ's does not — line comments and most strings/regex broke. We resolve
  `$apply` ourselves with `scripts/resolve_grammar.py` (PyYAML) before shipping.
  Re-run after pulling upstream grammar changes.

- **Executable resolution**: EnvironmentUtil.getValue("PATH") to locate `xonsh-lsp`
  since an IDE launched in GUI mode does not inherit the shell PATH. The executable
  is passed via the ProcessStreamConnectionProvider constructor (the `commands` field
  is read directly in start(), not via a getCommands() override).

- **Local SDK**: `ideaLocalPath` in `~/.gradle/gradle.properties` (not committed) to
  point at the IDE installed via Toolbox. Maven fallback to IU 2025.3 for CI/others.

## Working (verified in runIde sandbox)

- [x] `.xsh` / `.xonshrc` / xonsh shebang recognized (via TM bundle)
- [x] LSP server starts (visible in View → Tool Windows → Language Servers)
- [x] Tooltips/hover on symbols
- [x] buildPlugin produces an installable .zip (Settings → Plugins → Install from Disk)
- [x] goto definition (Ctrl+B)
- [x] find usages (Alt+F7)
- [x] completion (Ctrl+Space)
- [x] Full Python-grade highlighting via vscode-xonsh TextMate grammar
      (keywords, strings, f-strings, regex, numbers, decorators, builtins…)

## Backlog / discussed but not started

- Open untilBuild (currently "261.*") — to be relaxed before Marketplace publish
- Icon for the FileType (null for now)
- Plugin tests

## External components

| Component                       | Version                                  |
|---------------------------------|------------------------------------------|
| LSP4IJ                          | 0.13.0                                   |
| xonsh-lsp                       | 0.2.0                                    |
| tree-sitter-xonsh               | 0.2.0                                    |
| Jedi                            | 0.19.2                                   |

## Useful commands

```sh
# Build
./gradlew buildPlugin
# → build/distributions/xonsh-jetbrains-x.y.z.zip

# IDE sandbox
./gradlew runIde

# Runtime prerequisites
uv tool install 'xonsh-lsp[jedi]'   # once
# + LSP4IJ installed from the Marketplace in the target IDE
```
