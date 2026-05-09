package eu.nahoj.xonsh.jetbrains.lsp

import com.intellij.openapi.project.Project
import com.intellij.util.EnvironmentUtil
import com.redhat.devtools.lsp4ij.server.ProcessStreamConnectionProvider
import java.io.File

class XonshLspServer(project: Project, serverCommand: List<String>? = null) : ProcessStreamConnectionProvider(
    serverCommand ?: listOf(resolveFromPath()),
    project.basePath,
) {
    companion object {
        // IntelliJ as a GUI app doesn't source shell rc files, so PATH is truncated.
        // Walk EnvironmentUtil's real user PATH to find the actual xonsh-lsp binary.
        fun resolveFromPath(): String {
            val userPath = EnvironmentUtil.getValue("PATH") ?: ""
            return userPath.split(File.pathSeparatorChar)
                .map { File(it, "xonsh-lsp") }
                .firstOrNull { it.canExecute() }
                ?.absolutePath ?: "xonsh-lsp"
        }
    }
}
