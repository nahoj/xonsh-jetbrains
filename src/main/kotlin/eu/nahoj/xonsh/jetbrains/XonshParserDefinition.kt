package eu.nahoj.xonsh.jetbrains

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

private val XONSH_FILE = IFileElementType(XonshLanguage)
private val XONSH_COMMENTS = TokenSet.create(XonshTokenTypes.COMMENT)
private val XONSH_STRINGS = TokenSet.create(XonshTokenTypes.STRING)

class XonshParserDefinition : ParserDefinition {
    override fun createLexer(project: Project?): Lexer = XonshLexer()

    override fun createParser(project: Project?): PsiParser = PsiParser { root, builder ->
        val marker = builder.mark()
        while (!builder.eof()) {
            builder.advanceLexer()
        }
        marker.done(root)
        builder.treeBuilt
    }

    override fun getFileNodeType(): IFileElementType = XONSH_FILE

    override fun getWhitespaceTokens(): TokenSet = TokenSet.create(TokenType.WHITE_SPACE)

    override fun getCommentTokens(): TokenSet = XONSH_COMMENTS

    override fun getStringLiteralElements(): TokenSet = XONSH_STRINGS

    override fun createElement(node: ASTNode): PsiElement = ASTWrapperPsiElement(node)

    override fun createFile(viewProvider: FileViewProvider): PsiFile = XonshFile(viewProvider)

    @Deprecated("Required by ParserDefinition.")
    override fun spaceExistanceTypeBetweenTokens(
        left: ASTNode?,
        right: ASTNode?,
    ): ParserDefinition.SpaceRequirements = ParserDefinition.SpaceRequirements.MAY
}

class XonshFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, XonshLanguage) {
    override fun getFileType() = XonshFileType

    override fun toString(): String = "Xonsh File"
}
