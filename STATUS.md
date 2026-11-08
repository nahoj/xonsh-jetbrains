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

## Backlog / discussed but not started

- Icon for the FileType (null for now)
- Plugin tests

## Use Jetbrains built-in LSP support instead of LSP4IJ → Later

> - could the plugin use it instead of using LSP4IJ
> - should it
> - would it be easy to use the builtin support is available and LSP4IJ as fallback for users on earlier versions?

Short answer: yes, it could. I don't think it should right now. Supporting both is doable, but it's the most expensive of the three options.

1. Could it?

Yes. JetBrains' built-in LSP API (com.intellij.modules.lsp) has been free for third-party plugins since 2025.2.1 (build 252.25557), and it's in the unified IDEA and PyCharm. A port would be small:

- What maps directly:
  - XonshLspServerFactory + XonshLspServer become an LspIntegrationProvider + ProjectWideLspClientDescriptor. Before 2026.1.4 these were called LspServerSupportProvider / ProjectWideLspServerDescriptor; the old names are deprecated but still work.
  - createCommandLine() reuses resolveCommand().
  - isSupportedFile() replaces fileNamePatternMapping.
  - The descriptor has overrides for the language id and initialization options.
- What you'd have to rebuild:
  - The settings. XonshLspSettings stores and edits everything (serverCommand, pythonBackend, backendSettings, macro expansion) through LSP4IJ's GlobalLanguageServerSettings and its JSON editor. With the built-in API you need your own PersistentStateComponent + Configurable, plus a migration from the LSP4IJ settings.
  - lastDocumentDisconnectedTimeout="300". I found no documented equivalent in the built-in API. This needs checking.
- To verify: the built-in client should work on TextMate-typed files, since it's keyed on VirtualFile, not on a Language. Test it before committing.

2. Should it?

I'd say not now. What you'd lose:

- Users on older IDEs. Today sinceBuild = 242, and LSP4IJ also covers Community editions before 2025.3 and Android Studio. The built-in API covers neither.
- Rename on older versions. The built-in API only has rename since 2026.1.1, and on-type formatting since 2026.1.2. Rename is a feature you've worked on deliberately (NOTES/rename-plan.md).
- Debuggability. LSP4IJ is open source, you keep a fork of it to patch and debug, and it has an LSP console with message traces. The built-in client is closed source and its traces only go to idea.log.
- The settings UI described above.

What you'd gain: one less plugin for users to install, and a client maintained by JetBrains. That's real, but small for a plugin this thin.

3. Built-in when available, LSP4IJ as fallback?

It's mechanically simple: two optional dependencies, each with its own config file, and the code split into two packages so neither set of classes loads without its dependency:

<depends optional="true" config-file="xonsh-lsp4ij.xml">com.redhat.devtools.lsp4ij</depends>
<depends optional="true" config-file="xonsh-builtin-lsp.xml">com.intellij.modules.lsp</depends>

But there are real costs:

- Two servers at once. Many 2025.2+ users will have LSP4IJ installed anyway. Without arbitration, both clients would start xonsh-lsp and you'd get duplicate completions and diagnostics. You can handle it: XonshLspServerFactory implements LanguageServerEnablementSupport (I checked that isEnabled(project) exists in LSP4IJ), and the built-in provider skips ensureClientStarted when the other one is active.
- No auto-install for old IDEs. Once LSP4IJ is an optional dependency, the Marketplace no longer installs it automatically. Users on older IDEs would need a notification telling them to install it.
- Two clients to test. That means two settings paths and two sets of behaviour differences (rename, timeout, traces) for every release.

My recommendation: stay on LSP4IJ. If you later want to drop it, switch completely, without a fallback, once you're willing to raise sinceBuild to 2026.1.1 or later, where the built-in API has rename. Moving the settings into your own Configurable would help either way, and it's the one refactor that makes any later switch easy.
