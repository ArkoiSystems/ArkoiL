/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiIntegration on April 04, 2020
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
package com.arkoisystems.lang.arkoi.parser.psi

import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.BlockType
import com.arkoisystems.lang.arkoi.ArkoiFile
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.util.PsiTreeUtil

class FunctionPSI(node: ASTNode) : ASTWrapperPsiElement(node) {

    private val arkoiClass = PsiTreeUtil.getParentOfType(this, ArkoiFile::class.java)?.arkoiClass

    val block = PsiTreeUtil.getChildOfType(this, BlockPSI::class.java)


    private fun getFunctionSyntaxAST() = arkoiClass!!.syntaxAnalyzer.rootAST.astNodes.filter {
        it.astType == ASTType.FUNCTION
    }.map { it as FunctionAST }.find {
        it.startToken?.start == node.textRange.startOffset && it.endToken?.end == node.textRange.endOffset
    }

    fun isInlined() = getFunctionSyntaxAST()?.functionBlock?.blockType == BlockType.INLINE

    fun isFailed() = getFunctionSyntaxAST()?.isFailed

}