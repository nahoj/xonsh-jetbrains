package eu.nahoj.xonsh.jetbrains

import com.intellij.lang.LanguageCommenters
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.psi.util.PsiUtilCore
import com.intellij.testFramework.PlatformTestUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class XonshCommenterTest : BasePlatformTestCase() {
    fun testLineCommentActionCommentsXshFile() {
        myFixture.configureByText(XonshFileType, "echo hi<caret>\n")
        assertSame(XonshFileType, myFixture.file.fileType)
        assertSame(XonshLanguage, myFixture.file.language)
        assertSame(XonshLanguage, PsiUtilCore.getLanguageAtOffset(myFixture.file, 0))
        assertNotNull(LanguageCommenters.INSTANCE.forLanguage(XonshLanguage))

        PlatformTestUtil.invokeNamedAction(IdeActions.ACTION_COMMENT_LINE)

        assertEquals("# echo hi\n", myFixture.editor.document.text)
    }

    fun testLineCommentActionUncommentsXshFile() {
        myFixture.configureByText(XonshFileType, "# echo hi<caret>\n")

        PlatformTestUtil.invokeNamedAction(IdeActions.ACTION_COMMENT_LINE)

        assertEquals("echo hi\n", myFixture.editor.document.text)
    }
}
