package io.github.xonsh.jetbrains

import com.intellij.psi.tree.IElementType

class XonshTokenType(debugName: String) : IElementType(debugName, XonshLanguage)

object XonshTokenTypes {
    val COMMENT = XonshTokenType("COMMENT")
    val STRING = XonshTokenType("STRING")
    val NUMBER = XonshTokenType("NUMBER")
    val KEYWORD = XonshTokenType("KEYWORD")
    val BUILTIN = XonshTokenType("BUILTIN")
    val IDENTIFIER = XonshTokenType("IDENTIFIER")
    val OPERATOR = XonshTokenType("OPERATOR")
    val SHELL_OP = XonshTokenType("SHELL_OP")
    val ENV_VAR = XonshTokenType("ENV_VAR")
    val DECORATOR = XonshTokenType("DECORATOR")
    val BAD_CHARACTER = XonshTokenType("BAD_CHARACTER")
}

internal val PYTHON_KEYWORDS = setOf(
    "and", "as", "assert", "async", "await", "break", "class", "continue",
    "def", "del", "elif", "else", "except", "finally", "for", "from",
    "global", "if", "import", "in", "is", "lambda", "nonlocal", "not",
    "or", "pass", "raise", "return", "try", "while", "with", "yield",
    "True", "False", "None",
)

internal val XONSH_KEYWORDS = setOf("xontrib", "aliases", "source")

internal val PYTHON_BUILTINS = setOf(
    "abs", "all", "any", "ascii", "bin", "bool", "bytearray", "bytes",
    "callable", "chr", "classmethod", "compile", "complex", "delattr",
    "dict", "dir", "divmod", "enumerate", "eval", "exec", "filter", "float",
    "format", "frozenset", "getattr", "globals", "hasattr", "hash", "help",
    "hex", "id", "input", "int", "isinstance", "issubclass", "iter", "len",
    "list", "locals", "map", "max", "memoryview", "min", "next", "object",
    "oct", "open", "ord", "pow", "print", "property", "range", "repr",
    "reversed", "round", "set", "setattr", "slice", "sorted", "staticmethod",
    "str", "sum", "super", "tuple", "type", "vars", "zip", "__import__",
)
