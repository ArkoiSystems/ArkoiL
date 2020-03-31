/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiIntegration on March 16, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.lang.arkoi

import com.arkoisystems.lang.arkoi.parser.psi.ArkoiRoot
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider
import com.intellij.psi.util.PsiTreeUtil

class ArkoiFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, ArkoiLanguage) {

    init {
        val arkoiClass = this.getArkoiRoot()!!.rootSyntaxAST.syntaxAnalyzer?.arkoiClass!!
        arkoiClass.customHandler = ArkoiCompileHandler(this)
        arkoiClass.filePath = viewProvider.virtualFile.canonicalPath!!
    }

    override fun getFileType() = ArkoiFileType

    fun getArkoiRoot() = PsiTreeUtil.getChildOfType(this, ArkoiRoot::class.java)

}