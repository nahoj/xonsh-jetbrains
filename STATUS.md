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

- **Executable resolution**: EnvironmentUtil.getValue("PATH") to locate `xonsh-lsp`
  since an IDE launched in GUI mode does not inherit the shell PATH. The executable
  is passed via the ProcessStreamConnectionProvider constructor (the `commands` field
  is read directly in start(), not via a getCommands() override).

## Backlog / discussed but not started

- Icon for the FileType (null for now)
- Plugin tests
