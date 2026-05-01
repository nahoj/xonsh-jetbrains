package eu.nahoj.xonsh.jetbrains

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object XonshFileType : LanguageFileType(XonshLanguage) {
    override fun getName(): String = "Xonsh"
    override fun getDescription(): String = "Xonsh shell script"
    override fun getDefaultExtension(): String = "xsh"
    override fun getIcon(): Icon? = null
}
