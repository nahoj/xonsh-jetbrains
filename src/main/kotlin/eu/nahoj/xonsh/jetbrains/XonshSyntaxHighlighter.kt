package eu.nahoj.xonsh.jetbrains

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType

private val COMMENT = key("XONSH_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
private val STRING = key("XONSH_STRING", DefaultLanguageHighlighterColors.STRING)
private val NUMBER = key("XONSH_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
private val KEYWORD = key("XONSH_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
private val BUILTIN = key("XONSH_BUILTIN", DefaultLanguageHighlighterColors.PREDEFINED_SYMBOL)
private val IDENTIFIER = key("XONSH_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
private val OPERATOR = key("XONSH_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
private val SHELL_OP = key("XONSH_SHELL_OP", DefaultLanguageHighlighterColors.MARKUP_TAG)
private val ENV_VAR = key("XONSH_ENV_VAR", DefaultLanguageHighlighterColors.STATIC_FIELD)
private val DECORATOR = key("XONSH_DECORATOR", DefaultLanguageHighlighterColors.METADATA)
private val BAD = key("XONSH_BAD_CHAR", DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE)

private fun key(name: String, default: TextAttributesKey) =
    TextAttributesKey.createTextAttributesKey(name, default)

class XonshSyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer = XonshLexer()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> = when (tokenType) {
        XonshTokenTypes.COMMENT -> arrayOf(COMMENT)
        XonshTokenTypes.STRING -> arrayOf(STRING)
        XonshTokenTypes.NUMBER -> arrayOf(NUMBER)
        XonshTokenTypes.KEYWORD -> arrayOf(KEYWORD)
        XonshTokenTypes.BUILTIN -> arrayOf(BUILTIN)
        XonshTokenTypes.IDENTIFIER -> arrayOf(IDENTIFIER)
        XonshTokenTypes.OPERATOR -> arrayOf(OPERATOR)
        XonshTokenTypes.SHELL_OP -> arrayOf(SHELL_OP)
        XonshTokenTypes.ENV_VAR -> arrayOf(ENV_VAR)
        XonshTokenTypes.DECORATOR -> arrayOf(DECORATOR)
        XonshTokenTypes.BAD_CHARACTER -> arrayOf(BAD)
        else -> emptyArray()
    }
}

class XonshSyntaxHighlighterFactory : SyntaxHighlighterFactory() {
    override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter =
        XonshSyntaxHighlighter()
}
