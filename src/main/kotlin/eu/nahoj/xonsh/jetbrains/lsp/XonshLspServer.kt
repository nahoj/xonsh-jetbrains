package eu.nahoj.xonsh.jetbrains.lsp

import com.intellij.execution.configurations.PathEnvironmentVariableUtil
import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.server.ProcessStreamConnectionProvider

class XonshLspServer(project: Project, customCommand: List<String>? = null) : ProcessStreamConnectionProvider(
    resolveCommand(customCommand),
    project.basePath,
) {
    companion object {
        fun resolveCommand(customCommand: List<String>?): List<String> {
            return customCommand
                // findExecutableInPathOnAnyOS uses the real user PATH, as opposed to the
                // environment from which the IDE was run.
                ?: PathEnvironmentVariableUtil.findExecutableInPathOnAnyOS("xonsh-lsp")
                    ?.let { listOf(it.absolutePath) }
                ?: PathEnvironmentVariableUtil.findExecutableInPathOnAnyOS("uvx")
                    ?.let { listOf(it.absolutePath, "--from", "xonsh-lsp[jedi]", "xonsh-lsp") }
                ?: PathEnvironmentVariableUtil.findExecutableInPathOnAnyOS("pipx")
                    ?.let { listOf(it.absolutePath, "run", "--spec", "xonsh-lsp[jedi]", "xonsh-lsp") }
                ?: listOf("xonsh-lsp")
        }
    }
}
