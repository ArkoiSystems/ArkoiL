/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiIntegration on March 31, 2020
 * Author єхcsє#5543 aka timo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arkoisystems.lang.arkoi

import com.arkoisystems.arkoicompiler.ArkoiClass
import com.arkoisystems.arkoicompiler.api.IClassHandler
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import java.io.File

class ArkoiCompileHandler(private val arkoiFile: ArkoiFile) : IClassHandler {

    override fun getArkoiFile(filePath: String): ArkoiClass? {
        val toFind = File(arkoiFile.project.basePath, filePath).canonicalPath

        FileTypeIndex.getFiles(ArkoiFileType, GlobalSearchScope.allScope(this.arkoiFile.project))
            .forEach { virtualFile ->
                val arkoiFile = (PsiManager.getInstance(this.arkoiFile.project).findFile(virtualFile)
                    ?: return@forEach) as ArkoiFile
                if(arkoiFile.virtualFile.canonicalPath != toFind)
                    return@forEach
                return  arkoiFile.getArkoiRoot()?.rootSyntaxAST?.syntaxAnalyzer?.arkoiClass ?: return@forEach
            }
        return null
    }

}