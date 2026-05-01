package eu.nahoj.xonsh.jetbrains

import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

/**
 * Best-effort lexer for highlighting only — not a full xonsh parser.
 * Recognises Python tokens plus xonsh shell escapes ($, @, !) and env vars ($NAME, ${...}).
 * Triple-quoted, f-strings, byte strings etc. are treated as plain strings.
 */
class XonshLexer : LexerBase() {
    private lateinit var buffer: CharSequence
    private var endOffset = 0
    private var pos = 0
    private var tokenStart = 0
    private var tokenEnd = 0
    private var tokenType: IElementType? = null

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.endOffset = endOffset
        this.pos = startOffset
        advance()
    }

    override fun getState(): Int = 0
    override fun getTokenType(): IElementType? = tokenType
    override fun getTokenStart(): Int = tokenStart
    override fun getTokenEnd(): Int = tokenEnd
    override fun getBufferSequence(): CharSequence = buffer
    override fun getBufferEnd(): Int = endOffset

    override fun advance() {
        tokenStart = pos
        if (pos >= endOffset) {
            tokenEnd = pos
            tokenType = null
            return
        }
        val c = buffer[pos]
        when {
            c == ' ' || c == '\t' || c == '\n' || c == '\r' -> readWhile { it == ' ' || it == '\t' || it == '\n' || it == '\r' }.also { tokenType = TokenType.WHITE_SPACE }
            c == '#' -> readWhile { it != '\n' }.also { tokenType = XonshTokenTypes.COMMENT }
            c == '"' || c == '\'' -> readString(c)
            c.isDigit() -> readNumber()
            c == '$' -> readDollar()
            c == '@' -> readAt()
            c == '!' -> readBang()
            isIdStart(c) -> readIdentifier()
            isOperator(c) -> readWhile { isOperator(it) }.also { tokenType = XonshTokenTypes.OPERATOR }
            else -> { pos++; tokenEnd = pos; tokenType = XonshTokenTypes.BAD_CHARACTER }
        }
    }

    private inline fun readWhile(pred: (Char) -> Boolean) {
        while (pos < endOffset && pred(buffer[pos])) pos++
        tokenEnd = pos
    }

    private fun readString(quote: Char) {
        pos++ // opening quote
        // Triple-quoted?
        val triple = pos + 1 < endOffset && buffer[pos] == quote && buffer[pos + 1] == quote
        if (triple) {
            pos += 2
            while (pos + 2 < endOffset) {
                if (buffer[pos] == quote && buffer[pos + 1] == quote && buffer[pos + 2] == quote) {
                    pos += 3
                    break
                }
                if (buffer[pos] == '\\' && pos + 1 < endOffset) pos++
                pos++
            }
            // EOF without close: tolerate
            if (pos > endOffset) pos = endOffset
        } else {
            while (pos < endOffset) {
                val ch = buffer[pos]
                if (ch == '\\' && pos + 1 < endOffset) { pos += 2; continue }
                if (ch == quote) { pos++; break }
                if (ch == '\n') break
                pos++
            }
        }
        tokenEnd = pos
        tokenType = XonshTokenTypes.STRING
    }

    private fun readNumber() {
        // hex / bin / oct prefixes
        if (buffer[pos] == '0' && pos + 1 < endOffset) {
            val n = buffer[pos + 1]
            if (n == 'x' || n == 'X' || n == 'b' || n == 'B' || n == 'o' || n == 'O') {
                pos += 2
                readWhile { it.isLetterOrDigit() || it == '_' }
                tokenType = XonshTokenTypes.NUMBER
                return
            }
        }
        readWhile { it.isDigit() || it == '_' }
        if (pos < endOffset && buffer[pos] == '.') {
            pos++
            readWhile { it.isDigit() || it == '_' }
        }
        if (pos < endOffset && (buffer[pos] == 'e' || buffer[pos] == 'E')) {
            pos++
            if (pos < endOffset && (buffer[pos] == '+' || buffer[pos] == '-')) pos++
            readWhile { it.isDigit() }
        }
        if (pos < endOffset && (buffer[pos] == 'j' || buffer[pos] == 'J')) pos++
        tokenEnd = pos
        tokenType = XonshTokenTypes.NUMBER
    }

    private fun readDollar() {
        // $VAR, ${...}, $(...), $[...]
        pos++ // consume $
        if (pos >= endOffset) {
            tokenEnd = pos; tokenType = XonshTokenTypes.SHELL_OP; return
        }
        val n = buffer[pos]
        if (n == '(' || n == '[' || n == '{') {
            // emit just the $ as a shell op; parens follow as separate tokens
            tokenEnd = pos; tokenType = XonshTokenTypes.SHELL_OP; return
        }
        if (isIdStart(n)) {
            // $IDENT — env var
            pos++
            readWhile { isIdPart(it) }
            tokenType = XonshTokenTypes.ENV_VAR
            return
        }
        tokenEnd = pos; tokenType = XonshTokenTypes.SHELL_OP
    }

    private fun readAt() {
        pos++ // consume @
        if (pos >= endOffset) {
            tokenEnd = pos; tokenType = XonshTokenTypes.OPERATOR; return
        }
        val n = buffer[pos]
        // @( ... ), @$( ... ) — shell op
        if (n == '(' || n == '$') {
            tokenEnd = pos; tokenType = XonshTokenTypes.SHELL_OP; return
        }
        // @decorator on its own line: rest is identifier — but we'll just emit @ as DECORATOR marker
        // Caller will see following IDENTIFIER as a decorator visually if convention matches.
        tokenEnd = pos; tokenType = XonshTokenTypes.DECORATOR
    }

    private fun readBang() {
        pos++ // consume !
        if (pos < endOffset) {
            val n = buffer[pos]
            if (n == '(' || n == '[') {
                tokenEnd = pos; tokenType = XonshTokenTypes.SHELL_OP; return
            }
            if (n == '=') { pos++; tokenEnd = pos; tokenType = XonshTokenTypes.OPERATOR; return }
        }
        tokenEnd = pos; tokenType = XonshTokenTypes.OPERATOR
    }

    private fun readIdentifier() {
        readWhile { isIdPart(it) }
        val text = buffer.subSequence(tokenStart, pos).toString()
        tokenType = when (text) {
            in PYTHON_KEYWORDS -> XonshTokenTypes.KEYWORD
            in XONSH_KEYWORDS -> XonshTokenTypes.KEYWORD
            in PYTHON_BUILTINS -> XonshTokenTypes.BUILTIN
            else -> XonshTokenTypes.IDENTIFIER
        }
    }

    private fun isIdStart(c: Char) = c.isLetter() || c == '_'
    private fun isIdPart(c: Char) = c.isLetterOrDigit() || c == '_'
    private fun isOperator(c: Char) = c in "+-*/%=<>&|^~,;:.()[]{}"
}
