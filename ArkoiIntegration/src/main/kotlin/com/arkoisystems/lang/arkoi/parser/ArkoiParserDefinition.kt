/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiIntegration on March 16, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.lang.arkoi.parser

import com.arkoisystems.arkoicompiler.ArkoiClass
import com.arkoisystems.lang.arkoi.ArkoiFile
import com.arkoisystems.lang.arkoi.ArkoiLanguage
import com.arkoisystems.lang.arkoi.lexer.ArkoiLexer
import com.arkoisystems.lang.arkoi.lexer.ArkoiTokenTypes
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.ParserDefinition.SpaceRequirements
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

class ArkoiParserDefinition : ParserDefinition {

    val arkoiClasses = hashMapOf<Int, ArkoiClass>()


    override fun createLexer(project: Project?) = ArkoiLexer()

    override fun createParser(project: Project?) = ArkoiParser(this)

    override fun createFile(viewProvider: FileViewProvider) = ArkoiFile(viewProvider, this)


    override fun getFileNodeType() = ArkoiElementTypes.file

    override fun createElement(node: ASTNode?) = ArkoiElementTypes.createElement(node)


    override fun getWhitespaceTokens() = TokenSet.create(TokenType.WHITE_SPACE)

    override fun getCommentTokens() = TokenSet.create(ArkoiTokenTypes.comment)

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY


    override fun spaceExistenceTypeBetweenTokens(
        left: ASTNode?,
        right: ASTNode?
    ) = SpaceRequirements.MAY

}