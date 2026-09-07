package eu.nahoj.xonsh.jetbrains.lsp

import com.intellij.execution.configurations.PathEnvironmentVariableUtil
import com.intellij.openapi.project.Project
import com.intellij.util.system.OS
import com.redhat.devtools.lsp4ij.server.ProcessStreamConnectionProvider

class XonshLspServer(project: Project, customCommand: List<String>? = null) : ProcessStreamConnectionProvider(
    resolveCommand(customCommand),
    project.basePath,
) {
    companion object {
        fun resolveCommand(customCommand: List<String>?): List<String> {
            return customCommand
                ?: findExecutable("xonsh-lsp")
                    ?.let { listOf(it) }
                ?: findExecutable("uvx")
                    ?.let { listOf(it, "--from", "xonsh-lsp[jedi]", "xonsh-lsp") }
                ?: findExecutable("pipx")
                    ?.let { listOf(it, "run", "--spec", "xonsh-lsp[jedi]", "xonsh-lsp") }
                ?: listOf("xonsh-lsp")
        }

        /// Looks up [baseName] in the real user PATH, as opposed to the environment from
        /// which the IDE was run. Handles the Windows executable extensions (.exe, .bat...).
        private fun findExecutable(baseName: String): String? =
            if (OS.CURRENT == OS.Windows) {
                PathEnvironmentVariableUtil.findExecutableInWindowsPath(baseName, null)
            } else {
                PathEnvironmentVariableUtil.findInPath(baseName)?.absolutePath
            }
    }
}
