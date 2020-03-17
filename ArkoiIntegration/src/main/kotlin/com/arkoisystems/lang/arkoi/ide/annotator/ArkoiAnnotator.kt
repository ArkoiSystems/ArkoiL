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

import com.arkoisystems.lang.arkoi.ArkoiTokenTypes
import com.arkoisystems.lang.arkoi.highlight.ArkoiSyntaxHighlighter
import com.arkoisystems.lang.arkoi.psi.*
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil

class ArkoiAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when (element) {
            is ArkoiFunctionCall -> {
                element.node.findChildByType(ArkoiTokenTypes.IDENTIFIER)?.apply {
                    holder.createInfoAnnotation(this, null).textAttributes = ArkoiSyntaxHighlighter.functionCall
                }
            }

//            is ArkoiVariableCall -> {
//                val isLocal = element.node.findChildByType(ArkoiTokenTypes.THIS)
//                element.node.findChildByType(ArkoiTokenTypes.IDENTIFIER)?.apply {
//                    holder.createInfoAnnotation(this, null).textAttributes =
//                        if (isLocal == null) DefaultLanguageHighlighterColors.GLOBAL_VARIABLE else DefaultLanguageHighlighterColors.LOCAL_VARIABLE
//                }
//            }

            is ArkoiVariableDeclaration -> {
                val blockDeclaration = PsiTreeUtil.getParentOfType(element, ArkoiBraceBlock::class.java, ArkoiInlinedBlock::class.java)
                element.node.findChildByType(ArkoiTokenTypes.IDENTIFIER)?.apply {
                    holder.createInfoAnnotation(this, null).textAttributes =
                        if (blockDeclaration == null) ArkoiSyntaxHighlighter.globalVariable else ArkoiSyntaxHighlighter.localVariable
                }
            }

            is ArkoiFunctionDeclaration -> {
                element.node.findChildByType(ArkoiTokenTypes.IDENTIFIER)?.apply {
                    holder.createInfoAnnotation(this, null).textAttributes = ArkoiSyntaxHighlighter.functionDeclaration
                }

                element.node.findChildByType(ArkoiTokenTypes.ARGUMENT_LIST)?.apply {
                    this.getChildren(TokenSet.forAllMatching {
                        it == ArkoiTokenTypes.IDENTIFIER
                    }).forEach {
                        it ?: return@forEach
                        holder.createInfoAnnotation(it, null).textAttributes = ArkoiSyntaxHighlighter.functionParameter
                    }
                }
            }
        }
    }

}