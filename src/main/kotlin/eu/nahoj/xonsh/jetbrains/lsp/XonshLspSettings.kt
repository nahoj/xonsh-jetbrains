package eu.nahoj.xonsh.jetbrains.lsp

import com.google.gson.JsonParser
import com.intellij.execution.util.ProgramParametersUtil
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.settings.GlobalLanguageServerSettings
import com.redhat.devtools.lsp4ij.settings.contributors.ServerInitializationOptionsContributor

object XonshLspSettings {
    const val SERVER_ID = "xonsh"

    private val emptyJsonObjectRegex = Regex("[{}\\s]*")

    fun readServerCommand(project: Project): List<String>? {
        val settings = GlobalLanguageServerSettings.getInstance()
            .getLanguageServerSettings(SERVER_ID)
            ?: return null

        // Initialize initialization options if empty
        if (settings.initializationOptionsContent?.matches(emptyJsonObjectRegex) ?: false) {
            settings.initializationOptionsContent = XonshInitializationOptionsContributor
                .getDefaultInitializationOptionsContent()
        }

        // Reimplement settings.getLanguageServerInitializationOptions() to
        // notify the user of a parsing error instead of silently ignoring it.
        val raw = settings.initializationOptionsContent!!
        val expanded = ProgramParametersUtil.expandPathAndMacros(raw, null, project)
        return try {
            JsonParser.parseString(expanded).asJsonObject
                .get("serverCommand")?.takeIf { !it.isJsonNull }
                ?.asJsonArray
                ?.map { it.asString }
                ?.takeIf { it.isNotEmpty() }
        } catch (e: Exception) {
            notifyParseError(project, e.message ?: e.javaClass.simpleName)
            null
        }
    }

    private fun notifyParseError(project: Project, detail: String) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Xonsh Language Server")
            .createNotification(
                "Xonsh Language Server: invalid initialization options",
                "Check Language Server settings → Xonsh Language Server → Configuration. " +
                    "Falling back to default xonsh-lsp command. Exception:<br><blockquote>$detail</blockquote>",
                NotificationType.ERROR,
            )
            .notify(project)
    }
}

object XonshInitializationOptionsContributor : ServerInitializationOptionsContributor {
    override fun getDefaultInitializationOptionsContent(): String = $$"""
{
  // Note: this JSON is not updated on Xonsh plugin upgrade. To reset it to the
  // latest default, set it to {} and restart the server.
  //
  // This JSON can use IDE macros such as $PROJECT_DIR$.

  // Custom command for xonsh-lsp
  //"serverCommand": ["/path/to/xonsh-lsp"],

  // Python analysis backend: "jedi" (built-in), "pyright", "basedpyright", "pylsp", "ty", or "lsp-proxy"
  // As of this writing, jedi, pyright, and pylsp work well out of the box. basedpyright and ty don't as well.
  //"pythonBackend": "jedi",

  // Custom command for the "lsp-proxy" backend (not needed for named backends)
  //"pythonBackendCommand": ["my-lsp-server", "--stdio"],

  // Settings sent to the backend during initialization.
  //"backendSettings": { },
  
  "_catchTrailingComma": ""
}
    """.trimIndent()
}
