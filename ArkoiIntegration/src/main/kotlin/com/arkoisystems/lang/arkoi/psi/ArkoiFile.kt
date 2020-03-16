/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiIntegration on March 16, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.lang.arkoi.psi

import com.arkoisystems.lang.arkoi.ArkoiFileType
import com.arkoisystems.lang.arkoi.ArkoiLanguage
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider

class ArkoiFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, ArkoiLanguage) {

    override fun getFileType() = ArkoiFileType

}