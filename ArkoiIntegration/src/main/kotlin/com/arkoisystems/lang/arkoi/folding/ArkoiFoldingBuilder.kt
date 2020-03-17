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
package com.arkoisystems.lang.arkoi.folding

import com.arkoisystems.lang.arkoi.psi.ArkoiBlockDeclaration
import com.arkoisystems.lang.arkoi.psi.ArkoiBraceBlock
import com.arkoisystems.lang.arkoi.psi.ArkoiFile
import com.arkoisystems.lang.arkoi.psi.ArkoiFunctionDeclaration
import com.arkoisystems.lang.arkoi.psi.impl.ArkoiBlockDeclarationImpl
import com.intellij.codeInsight.folding.CodeFoldingSettings
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.CustomFoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

class ArkoiFoldingBuilder : CustomFoldingBuilder(), DumbAware {

    private val braceDots = "{…}"
    private val dots = "…"

    override fun isRegionCollapsedByDefault(node: ASTNode): Boolean {
        val psiElement = node.psi
        val settings = CodeFoldingSettings.getInstance() ?: return false

        if (psiElement is ArkoiBraceBlock) return settings.COLLAPSE_METHODS
        return false
    }

    override fun buildLanguageFoldRegions(
        descriptors: MutableList<FoldingDescriptor>,
        root: PsiElement,
        document: Document,
        quick: Boolean
    ) {
        if (root !is ArkoiFile)
            return

        PsiTreeUtil.findChildrenOfAnyType(
            root,
            ArkoiFunctionDeclaration::class.java
        ).apply { foldFunctionBodies(descriptors, this) }
    }

    override fun getLanguagePlaceholderText(node: ASTNode, range: TextRange): String {
        val psiElement = node.psi
        if (psiElement is ArkoiBlockDeclarationImpl && psiElement.braceBlock != null && psiElement.inlinedBlock == null)
            return braceDots
        return dots
    }

    private fun foldFunctionBodies(descriptors: MutableList<FoldingDescriptor>, psiElements: Collection<PsiElement>) {
        for (arkoiComponent in psiElements) {
            if (arkoiComponent is ArkoiFunctionDeclaration) {
                val blockDeclaration =
                    PsiTreeUtil.getChildOfType(arkoiComponent, ArkoiBlockDeclaration::class.java) ?: return
                if (blockDeclaration.textLength <= 2)
                    return
                descriptors.add(FoldingDescriptor(blockDeclaration, blockDeclaration.textRange))
            }
        }
    }

}