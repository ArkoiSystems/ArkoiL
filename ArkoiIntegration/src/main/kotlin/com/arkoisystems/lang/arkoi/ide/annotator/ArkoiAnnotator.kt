/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiIntegration on March 17, 2020
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
package com.arkoisystems.lang.arkoi.ide.annotator

import com.arkoisystems.arkoicompiler.ArkoiClass
import com.arkoisystems.arkoicompiler.ArkoiCompiler
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionDefinitionSyntaxAST
import com.arkoisystems.lang.arkoi.ArkoiTokenTypes
import com.arkoisystems.lang.arkoi.psi.ArkoiFile
import com.arkoisystems.lang.arkoi.psi.ArkoiFunctionDeclaration
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.jetbrains.rd.util.string.print

class ArkoiAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is ArkoiFile)
            return
        val arkoiCompiler = ArkoiCompiler(element.project.basePath!!)
        val arkoiClass = ArkoiClass(arkoiCompiler, element.project.basePath!!, ByteArray(0))
        arkoiClass.lexicalAnalyzer
        arkoiClass.syntaxAnalyzer.rootSyntaxAST.functionStorage.addAll(generateFunctions(arkoiClass, element))

        if(!arkoiClass.semanticAnalyzer.processStage()) arkoiCompiler.printStackTrace(System.out)
        else arkoiClass.syntaxAnalyzer.rootSyntaxAST.printSyntaxAST(System.out, "")
    }

    private fun generateFunctions(arkoiClass: ArkoiClass, arkoiFile: ArkoiFile): Collection<FunctionDefinitionSyntaxAST> {
        val functionDefinitions = mutableListOf<FunctionDefinitionSyntaxAST>()
        arkoiFile.node.getChildren(TokenSet.forAllMatching {
            it == ArkoiTokenTypes.FUNCTION_DECLARATION
        }).map { it.psi as ArkoiFunctionDeclaration }.forEach {
            val functionDefinitionSyntaxAST = FunctionDefinitionSyntaxAST(arkoiClass.syntaxAnalyzer)
            functionDefinitionSyntaxAST.functionName = it.functionName
            functionDefinitionSyntaxAST.functionReturnType = it.getFunctionReturnType(arkoiClass.syntaxAnalyzer)
            functionDefinitionSyntaxAST.functionBlock = it.getFunctionBlock(arkoiClass.syntaxAnalyzer)
            functionDefinitionSyntaxAST.functionArguments = it.getFunctionParameters(arkoiClass.syntaxAnalyzer)
            functionDefinitionSyntaxAST.functionAnnotations =  it.getFunctionAnnotations(arkoiClass.syntaxAnalyzer)
            functionDefinitions.add(functionDefinitionSyntaxAST)
        }
        return functionDefinitions
    }

}