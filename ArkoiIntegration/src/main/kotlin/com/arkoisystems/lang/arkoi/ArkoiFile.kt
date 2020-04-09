/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiIntegration on March 16, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.lang.arkoi

import com.arkoisystems.arkoicompiler.ArkoiClass
import com.arkoisystems.lang.arkoi.parser.ArkoiParserDefinition
import com.arkoisystems.lang.arkoi.parser.psi.RootPSI
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider
import com.intellij.psi.util.PsiTreeUtil

class ArkoiFile(viewProvider: FileViewProvider, arkoiParserDefinition: ArkoiParserDefinition) :
    PsiFileBase(viewProvider, ArkoiLanguage) {

    var arkoiClass: ArkoiClass? = arkoiParserDefinition.arkoiClasses.remove(this.firstChild.hashCode())

    override fun getFileType() = ArkoiFileType

    fun getRoot() = PsiTreeUtil.getChildOfType(this, RootPSI::class.java)

}