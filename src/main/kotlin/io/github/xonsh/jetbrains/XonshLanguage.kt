package io.github.xonsh.jetbrains

import com.intellij.lang.Language

object XonshLanguage : Language("Xonsh") {
    override fun isCaseSensitive(): Boolean = true
}
