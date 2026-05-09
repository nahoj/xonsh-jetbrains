package eu.nahoj.xonsh.jetbrains.lsp

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerFactory
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider
import com.redhat.devtools.lsp4ij.settings.contributors.LanguageServerSettingsContributor
import com.redhat.devtools.lsp4ij.settings.contributors.LanguageServerSettingsContributorBase

class XonshLspServerFactory : LanguageServerFactory {
    override fun createConnectionProvider(project: Project): StreamConnectionProvider =
        XonshLspServer(project, XonshLspSettings.readServerCommand(project))

    override fun createLanguageClient(project: Project): LanguageClientImpl =
        LanguageClientImpl(project)

    override fun createLanguageServerSettingsContributor(): LanguageServerSettingsContributor =
        LanguageServerSettingsContributorBase().apply {
            serverInitializationOptionsContributor = XonshInitializationOptionsContributor
        }
}
