[![JetBrains Marketplace](https://img.shields.io/jetbrains/plugin/v/31595?label=Marketplace)](https://plugins.jetbrains.com/plugin/31595)
[![JetBrains Marketplace Downloads](https://img.shields.io/jetbrains/plugin/d/31595)](https://plugins.jetbrains.com/plugin/31595)
![GitHub Downloads (all assets, all releases)](https://img.shields.io/github/downloads/nahoj/xonsh-jetbrains/total)
![GitHub commits since latest release](https://img.shields.io/github/commits-since/nahoj/xonsh-jetbrains/latest)

This is a plugin that adds [Xonsh](https://xon.sh/) language support to JetBrains IDEs (IntelliJ, PyCharm, etc.).

You can install it directly from your IDE, or from the [Marketplace](https://plugins.jetbrains.com/plugin/31595), the [Releases](https://github.com/nahoj/xonsh-jetbrains/releases) page, or build it from source (see below).

**Status:** It works. I intend to fix bugs and possibly make improvements over time. Issues and PRs are welcome!

## Features

Features are basically that of [xonsh-language-server](https://github.com/FoamScience/xonsh-language-server):

- Syntax highlighting
- Code completion
- Inspections / diagnostics
- Documentation on hover, and other useful info such as env var values.
- Go To Declaration, Show/Find Usages
- and more

## Prerequisites

- IntelliJ-based IDE (PyCharm, etc.) v2024.2+
- [LSP4IJ plugin](https://plugins.jetbrains.com/plugin/23257-lsp4ij) (installed automatically)
- [xonsh-lsp](https://github.com/FoamScience/xonsh-language-server) (package-run automatically with `uvx` or `pipx` by default)
  - If installed manually, either put it in your `PATH` or specify a custom command in Language Server settings after installing the plugin.
  - The Python backend server can also be selected in Language Server settings.
  - ℹ️ As of 05/2026, `xonsh-lsp` doesn't work if installed with `mise` (starts but doesn't start its backend).

The plugin is known to work on Linux and macOS. I expect it to work on Windows if `xonsh-lsp` does as well.

## ⚠️ Known Issue

If `*.xsh` / `*.xonshrc` files are registered as Python in your IDE (which PyCharm may suggest), the plugin will not work on them. To fix it, go to Settings → Editor → File Types → Python and remove xsh extensions, then restart the IDE.

## Install from source

Requires JDK 21 (will download on build if you don't have it installed).

```sh
./gradlew buildPlugin
```

The package is created in `./build/distributions/`

## Sandbox IDE

`./gradlew runIde` launches a sandbox IDE with the plugin loaded.

By default, it downloads PyCharm from Maven. To skip the download and use your locally installed IDE, create `local.properties` at the project root:

```properties
#localIdePath=/Applications/PyCharm Professional.app/Contents
#localIdePath=/home/<user>/.local/share/JetBrains/Toolbox/apps/intellij-idea-ultimate
localIdePath=/snap/pycharm-community/current
```

## Credits

Syntax highlighting uses the TextMate grammar from [vscode-xonsh](https://github.com/jnoortheen/vscode-xonsh), itself based on [MagicPython](https://github.com/MagicStack/MagicPython).

Other features are provided by [xonsh-language-server](https://github.com/FoamScience/xonsh-language-server).

Logo [from the Xonsh project](https://github.com/xonsh/logo) (MIT License).
