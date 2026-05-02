package eu.nahoj.xonsh.jetbrains.textmate

import com.intellij.openapi.application.PathManager
import org.jetbrains.plugins.textmate.api.TextMateBundleProvider
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

private const val BUNDLE_NAME = "xonsh"
private val BUNDLE_FILES = listOf(
    "package.json",
    "language-configuration.json",
    "syntaxes/xonsh.tmLanguage.json",
)

class XonshTmBundleProvider : TextMateBundleProvider {
    override fun getBundles(): List<TextMateBundleProvider.PluginBundle> {
        val target = PathManager.getSystemDir().resolve("xonsh-textmate").resolve(BUNDLE_NAME)
        extract(target)
        return listOf(TextMateBundleProvider.PluginBundle(BUNDLE_NAME, target))
    }

    private fun extract(target: Path) {
        Files.createDirectories(target)
        for (rel in BUNDLE_FILES) {
            val out = target.resolve(rel)
            Files.createDirectories(out.parent)
            javaClass.classLoader.getResourceAsStream("textmate/$BUNDLE_NAME/$rel")?.use { input ->
                Files.copy(input, out, StandardCopyOption.REPLACE_EXISTING)
            } ?: error("Missing bundled TextMate resource: textmate/$BUNDLE_NAME/$rel")
        }
    }
}
