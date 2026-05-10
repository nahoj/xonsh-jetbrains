[![JetBrains Marketplace](https://img.shields.io/jetbrains/plugin/v/31595?label=Marketplace)](https://plugins.jetbrains.com/plugin/31595)
[![JetBrains Marketplace Downloads](https://img.shields.io/jetbrains/plugin/d/31595)](https://plugins.jetbrains.com/plugin/31595)
[![GitHub Downloads (all assets, all releases)](https://img.shields.io/github/downloads/nahoj/xonsh-jetbrains/total)](https://github.com/nahoj/xonsh-jetbrains/releases)
[![GitHub commits since latest release](https://img.shields.io/github/commits-since/nahoj/xonsh-jetbrains/latest)](https://github.com/nahoj/xonsh-jetbrains/commits)

This is a plugin that adds [Xonsh](https://xon.sh/) language support to JetBrains IDEs (IntelliJ, PyCharm, etc.).

You can install it from your IDE or the [Marketplace](https://plugins.jetbrains.com/plugin/31595), or build it from source (see below).

**Status:** It works. I intend to fix bugs and possibly make improvements over time. Issues and PRs are welcome!

## Features

Features are basically that of [xonsh-language-server](https://github.com/FoamScience/xonsh-language-server):

- Syntax highlighting
- Code completion
- Inspections / diagnostics
- Documentation on hover, and other useful info such as env var values.
- Go To Declaration, Show/Find Usages
- and more

## Install
### Prerequisites

IntelliJ-based IDE (PyCharm, etc.) v2024.2+ with [LSP4IJ plugin](https://plugins.jetbrains.com/plugin/23257-lsp4ij) (installed automatically).

[xonsh-lsp](https://github.com/FoamScience/xonsh-language-server) is run via `uvx` or `pipx` unless installed in `PATH` or specified in Language Server settings. ℹ️ As of 05/2026, `xonsh-lsp` doesn't work if installed/run with `mise` (starts but doesn't start its backend).

The plugin is known to work on Linux and macOS. I expect it to work on Windows if `xonsh-lsp` does as well.

### Python LSP backend

`xonsh-lsp` supports several Python backends:
- [jedi](https://github.com/davidhalter/jedi) (default, built-in), [pyright](https://github.com/microsoft/pyright), and [pylsp](https://github.com/python-lsp/python-lsp-server) work well out of the box.
- `basedpyright`, `ty`, and `lsp-proxy` don't work as well as of 05/2026.

### ⚠️ Known Issue

If `*.xsh` / `*.xonshrc` files are registered as Python in your IDE (which PyCharm may suggest), the plugin will not work on them. To fix it, go to Settings → Editor → File Types → Python and remove xsh extensions, then restart the IDE.

## Development
### Build from source

Requires JDK 21 (will download on build if you don't have it installed).

```sh
./gradlew buildPlugin
```

The package is created in `./build/distributions/`

### Sandbox IDE

`./gradlew runIde` launches a sandbox IDE with the plugin loaded.

By default, it downloads PyCharm from Maven. To skip the download and use your locally installed IDE, create `local.properties` at the project root:

```properties
#localIdePath=/Applications/PyCharm Professional.app/Contents
#localIdePath=/home/<user>/.local/share/JetBrains/Toolbox/apps/intellij-idea-ultimate
localIdePath=/snap/pycharm-community/current
```

## Credits

Most features are provided by [xonsh-language-server](https://github.com/FoamScience/xonsh-language-server).

Syntax highlighting outside LSP scope uses the TextMate grammar from [vscode-xonsh](https://github.com/jnoortheen/vscode-xonsh), itself based on [MagicPython](https://github.com/MagicStack/MagicPython).

Logo [from the Xonsh project](https://github.com/xonsh/logo) (MIT License).
