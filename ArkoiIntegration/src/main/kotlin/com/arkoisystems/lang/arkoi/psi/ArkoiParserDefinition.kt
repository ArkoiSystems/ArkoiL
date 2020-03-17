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
package com.arkoisystems.lang.arkoi.psi

import com.arkoisystems.lang.arkoi.ArkoiLanguage
import com.arkoisystems.lang.arkoi.ArkoiParser
import com.arkoisystems.lang.arkoi.ArkoiTokenTypes
import com.arkoisystems.lang.arkoi.lexer.ArkoiLexerAdapter
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.ParserDefinition.SpaceRequirements
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

class ArkoiParserDefinition : ParserDefinition {

    private val whitespaces = TokenSet.create(TokenType.WHITE_SPACE)
    private val comments = TokenSet.create(ArkoiTokenTypes.COMMENT)

    private val file = IFileElementType(ArkoiLanguage)

    override fun createLexer(project: Project?) = ArkoiLexerAdapter()

    override fun getWhitespaceTokens() = whitespaces

    override fun getCommentTokens() = comments

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun createParser(project: Project?) = ArkoiParser()

    override fun getFileNodeType() = file

    override fun createFile(viewProvider: FileViewProvider) = ArkoiFile(viewProvider)

    override fun spaceExistenceTypeBetweenTokens(
        left: ASTNode?,
        right: ASTNode?
    ) = SpaceRequirements.MAY

    override fun createElement(node: ASTNode?): PsiElement = ArkoiTokenTypes.Factory.createElement(node)

}