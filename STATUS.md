# xonsh-jetbrains — état du projet

## Décisions d'architecture prises

- **LSP** : xonsh-language-server (FoamScience) via pygls + tree-sitter-xonsh + Jedi.
  Pipeline : tree-sitter parse → transpile xonsh→Python (mapping colonne bidirectionnel)
  → Jedi pour completion/hover/goto/references/diagnostics. 302 tests upstream passent.
  Serveur installé via `uv tool install 'xonsh-lsp[jedi]'`.

- **Client IDE** : plugin IntelliJ natif utilisant LSP4IJ (com.redhat.devtools.lsp4ij)
  comme client LSP. Le plugin déclare un FileType Xonsh (.xsh, .xonshrc) et démarre
  `xonsh-lsp` en stdio.

- **Coloration** : lexer IntelliJ natif (XonshLexer, LexerBase) — plus simple que de
  jongler avec les chemins de bundles TextMate dans un jar. Couvre Python + extensions
  xonsh ($, @, !, env vars). Une grammaire TM (recyclée de vscode-xonsh) pourrait remplacer
  à terme pour une couverture plus fine.

- **Résolution de l'exécutable** : EnvironmentUtil.getValue("PATH") pour trouver `xonsh-lsp`
  car l'IDE lancé en GUI n'hérite pas du PATH shell. L'exécutable est passé via le
  constructeur de ProcessStreamConnectionProvider (le champ `commands` est lu directement
  dans start(), pas via override de getCommands()).

- **SDK local** : `ideaLocalPath` dans `~/.gradle/gradle.properties` (non committé) pour
  pointer sur l'IDE installé via Toolbox. Fallback Maven vers IU 2025.3 pour CI/autres.

## Fonctionnel (vérifié en sandbox runIde)

- [x] FileType xonsh reconnu (.xsh, .xonshrc, shebang xonsh)
- [x] LSP server démarre (visible dans View → Tool Windows → Language Servers)
- [x] Tooltips/hover sur les symboles
- [x] Coloration de base (mots-clés, strings, commentaires, nombres, builtins, env vars)
- [x] buildPlugin produit un .zip installable (Settings → Plugins → Install from Disk)
- [x] goto definition (Ctrl+B)
- [x] find usages (Alt+F7)
- [x] completion (Ctrl+Espace)

## Connu cassé / À corriger

- [ ] Coloration des shell ops $()  !()  @()  $[]  ![] — les 2-char sequences ne sont
      pas émises comme un token SHELL_OP unique ; le `(` tombe dans OPERATOR
- [ ] Pas d'entrée XONSH_* dans Settings → Editor → Color Scheme car ColorSettingsPage
      non enregistrée
- [ ] Rename (textDocument/rename) non implémenté côté xonsh-lsp — décision : reporter

## Backlog / Discuté mais non commencé

- Grammaire TextMate complète (recycler vscode-xonsh dist/tmlang-xonsh.json une fois
  le `$apply` résolu via rebuild node)
- ColorSettingsPage pour exposer les XONSH_* keys dans Settings
- Contribution rename upstream à xonsh-language-server (FoamScience)
- untilBuild ouvert (actuellement "261.*") — à assouplir avant publish Marketplace
- Icon pour le FileType (null pour l'instant)
- Tests du plugin

## Stack technique

| Composant                       | Version                                  |
|---------------------------------|------------------------------------------|
| IntelliJ Platform               | 2026.1 (local) / 2025.3 (fallback Maven) |
| IntelliJ Platform Gradle Plugin | 2.15.0                                   |
| Kotlin                          | 2.3.0                                    |
| Gradle wrapper                  | 9.6.0-milestone-1                        |
| JDK build                       | 21                                       |
| LSP4IJ                          | 0.13.0                                   |
| xonsh-lsp                       | 0.2.0                                    |
| tree-sitter-xonsh               | 0.2.0                                    |
| Jedi                            | 0.19.2                                   |

## Commandes utiles

```sh
# Build
JAVA_HOME=$HOME/.local/share/mise/installs/java/temurin-21.0.10+7.0.LTS \
  ./gradlew buildPlugin
# → build/distributions/xonsh-jetbrains-x.y.z.zip

# Sandbox IDE
JAVA_HOME=$HOME/.local/share/mise/installs/java/temurin-21.0.10+7.0.LTS \
  ./gradlew runIde

# Prérequis runtime
uv tool install 'xonsh-lsp[jedi]'   # une fois
# + LSP4IJ installé depuis la Marketplace dans l'IDE cible
```
