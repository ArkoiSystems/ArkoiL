/*
 * Copyright © 2019-2020 ArykoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
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
package com.arkoisystems.lang.arkoi.folding

import com.arkoisystems.lang.arkoi.ArkoiFile
import com.arkoisystems.lang.arkoi.parser.psi.BlockPSI
import com.intellij.codeInsight.folding.CodeFoldingSettings
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.CustomFoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

class ArkoiFoldingBuilder : CustomFoldingBuilder(), DumbAware {

    private val braceDots = "{…}"

    private val dots = "…"

    override fun isRegionCollapsedByDefault(node: ASTNode): Boolean {
        if (node.psi !is BlockPSI)
            return false
        return CodeFoldingSettings.getInstance().COLLAPSE_METHODS
    }

    override fun buildLanguageFoldRegions(
        descriptors: MutableList<FoldingDescriptor>,
        root: PsiElement,
        document: Document,
        quick: Boolean
    ) {
        if (root !is ArkoiFile)
            return

        root.getRoot()?.getFunctions()?.forEach {
            if (it.isInlined() || (it.isFailed() ?: return@forEach))
                return@forEach
            val block = it.block ?: return@forEach
            if (block.textLength <= 2)
                return@forEach
            descriptors.add(FoldingDescriptor(block, block.textRange))
        }
    }

    override fun getLanguagePlaceholderText(node: ASTNode, range: TextRange): String {
        if (node.psi is BlockPSI)
            return braceDots
        return dots
    }

}