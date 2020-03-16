/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiIntegration on March 16, 2020
 * Author timo aka. єхcsє#5543
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