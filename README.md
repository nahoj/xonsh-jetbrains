This is a plugin that adds [Xonsh](https://xon.sh/) language support to JetBrains IDEs (IntelliJ, PyCharm, etc.).

## Features

Features are basically that of [xonsh-language-server](https://github.com/FoamScience/xonsh-language-server):

- Syntax highlighting
- Code completion
- Inspections / diagnostics
- Documentation on hover, and other useful info such as env var values.
- Go To Declaration, Show/Find Usages
- and more

## Prerequisites

- IntelliJ, PyCharm, etc. v2024.2+
- [LSP4IJ plugin](https://plugins.jetbrains.com/plugin/23257-lsp4ij)
- `xonsh-lsp` (I only tested the built-in `jedi` backend, but others should work):
   ```sh
   uv tool install 'xonsh-lsp[jedi]'
   # or
   pipx install 'xonsh-lsp[jedi]'
   # mise use -g 'pipx:xonsh-lsp[jedi]' was not working: LSP4IJ couldn't start it.
   ```
- Tested on Linux only. I expect it to work on other systems provided `xonsh-lsp` does.

## Install

I haven't put the plugin on the Marketplace yet, I will if anyone expresses interest!

1. Get the latest zip from [the Releases page](https://github.com/nahoj/xonsh-jetbrains/releases).
2. In your IDE, Settings > Plugins > ⋮ > Install Plugin from Disk > `xonsh-jetbrains-x.y.z.zip`.

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
