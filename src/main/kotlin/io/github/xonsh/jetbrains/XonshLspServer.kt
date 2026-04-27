package io.github.xonsh.jetbrains

import com.intellij.openapi.project.Project
import com.intellij.util.EnvironmentUtil
import com.redhat.devtools.lsp4ij.server.ProcessStreamConnectionProvider
import java.io.File

class XonshLspServer(project: Project) : ProcessStreamConnectionProvider(
    listOf(resolveExecutable()),
    project.basePath,
) {
    companion object {
        // IntelliJ as a GUI app doesn't source shell rc files, so PATH is truncated.
        // Walk EnvironmentUtil's real user PATH to find the actual xonsh-lsp binary.
        fun resolveExecutable(): String {
            val userPath = EnvironmentUtil.getValue("PATH") ?: ""
            return userPath.split(File.pathSeparatorChar)
                .map { File(it, "xonsh-lsp") }
                .firstOrNull { it.canExecute() }
                ?.absolutePath ?: "xonsh-lsp"
        }
    }
}
