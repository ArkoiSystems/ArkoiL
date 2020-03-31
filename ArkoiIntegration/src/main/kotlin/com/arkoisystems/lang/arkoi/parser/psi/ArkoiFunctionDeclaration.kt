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
package com.arkoisystems.lang.arkoi.parser.psi

import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionDefinitionSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.BlockType
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.util.PsiTreeUtil

class ArkoiFunctionDeclaration(node: ASTNode, val functionDefinitionSyntaxAST: FunctionDefinitionSyntaxAST) :
    ASTWrapperPsiElement(node) {

    fun isInlined() = this.functionDefinitionSyntaxAST.functionBlock?.blockType == BlockType.INLINE

    fun isFailed() = this.functionDefinitionSyntaxAST.isFailed

    fun getBlock() = PsiTreeUtil.getChildOfType(this, ArkoiBlock::class.java)

}