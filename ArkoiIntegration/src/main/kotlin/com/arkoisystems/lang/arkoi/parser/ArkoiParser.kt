/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiIntegration on March 27, 2020
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
package com.arkoisystems.lang.arkoi.parser

import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.marker.ArkoiMarker
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.marker.MarkerFactory
import com.arkoisystems.lang.arkoi.lexer.ArkoiLexer
import com.intellij.lang.ASTNode
import com.intellij.lang.LightPsiParser
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.lang.impl.PsiBuilderImpl
import com.intellij.psi.tree.IElementType

class ArkoiParser : PsiParser, LightPsiParser {

    override fun parseLight(root: IElementType, builder: PsiBuilder) {
        if (builder !is PsiBuilderImpl || builder.lexer !is ArkoiLexer)
            return

        val arkoiLexer = builder.lexer as ArkoiLexer
        val arkoiClass = arkoiLexer.arkoiClass ?: return

        arkoiClass.syntaxAnalyzer.processStage()

        val mark = builder.mark()
        makeTree(arkoiLexer, root, builder, arkoiClass.syntaxAnalyzer.rootSyntaxAST.markerFactory)
        mark.done(root)
    }

    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        parseLight(root, builder)
        return builder.treeBuilt
    }

    private fun makeTree(
        arkoiLexer: ArkoiLexer,
        root: IElementType,
        builder: PsiBuilderImpl,
        markerFactory: MarkerFactory<out AbstractSyntaxAST>,
        indents: String = ""
    ) {
        val mark = builder.mark()

        println(indents + markerFactory.currentMarker.astType.name + ": ")

        for (nextFactory in markerFactory.nextMarkerFactories) {
            this.toMarkerStart(builder, arkoiLexer, nextFactory.currentMarker)
//            println()
//            println(indents + "start -> " + nextFactory.currentMarker.startToken.tokenContent)
//            println(indents + "  " + arkoiLexer.tokens!![builder.rawTokenIndex()].tokenContent)
            makeTree(arkoiLexer, root, builder, nextFactory, "$indents  ")
        }

        this.toMarkerEnd(builder, arkoiLexer, markerFactory.currentMarker)
        println(indents + "end -> " + markerFactory.currentMarker.endToken.tokenContent)
        println(indents + "  " + arkoiLexer.tokens!![builder.rawTokenIndex()].tokenContent)
        mark.done(getArkoiType(root, markerFactory.currentMarker))
    }

    private fun toMarkerStart(builder: PsiBuilderImpl, arkoiLexer: ArkoiLexer, arkoiMarker: ArkoiMarker) {
        while (!builder.eof()) {
            val currentToken = arkoiLexer.tokens!![builder.rawTokenIndex()]
            if (currentToken == arkoiMarker.startToken)
                break
            builder.advanceLexer()
        }
    }

    private fun toMarkerEnd(builder: PsiBuilderImpl, arkoiLexer: ArkoiLexer, arkoiMarker: ArkoiMarker) {
        while (!builder.eof()) {
            val currentToken = arkoiLexer.tokens!![builder.rawTokenIndex()]
            if (currentToken == arkoiMarker.endToken) {
                builder.advanceLexer()
                break
            }
            builder.advanceLexer()
        }
    }

    private fun getArkoiType(root: IElementType, arkoiMarker: ArkoiMarker): IElementType {
        return when (arkoiMarker.astType) {
            ASTType.IMPORT_DEFINITION -> ArkoiElementTypes.importDeclaration
            ASTType.NUMBER_OPERABLE -> ArkoiElementTypes.numberOperable
            ASTType.CAST_EXPRESSION -> ArkoiElementTypes.castExpression
            ASTType.TYPE -> ArkoiElementTypes.type
            ASTType.ANNOTATION -> ArkoiElementTypes.annotation
            ASTType.ARGUMENT_DEFINITION -> ArkoiElementTypes.argumentDeclaration
            ASTType.ASSIGNMENT_EXPRESSION -> ArkoiElementTypes.assignmentExpression
            ASTType.BINARY_EXPRESSION -> ArkoiElementTypes.binaryExpression
            ASTType.BLOCK -> ArkoiElementTypes.block
            ASTType.COLLECTION_OPERABLE -> ArkoiElementTypes.collectionOperable
            ASTType.EQUALITY_EXPRESSION -> ArkoiElementTypes.equalityExpression
            ASTType.EXPRESSION -> ArkoiElementTypes.expression
            ASTType.FUNCTION_CALL_PART -> ArkoiElementTypes.functionCallPart
            ASTType.FUNCTION_DEFINITION -> ArkoiElementTypes.functionDeclaration
            ASTType.IDENTIFIER_CALL_OPERABLE -> ArkoiElementTypes.identifierCallOperable
            ASTType.LOGICAL_EXPRESSION -> ArkoiElementTypes.logicalExpression
            ASTType.OPERABLE -> ArkoiElementTypes.operable
            ASTType.PARAMETER_DEFINITION -> ArkoiElementTypes.parameterDeclaration
            ASTType.PARENTHESIZED_EXPRESSION -> ArkoiElementTypes.parenthesizedExpression
            ASTType.POSTFIX_EXPRESSION -> ArkoiElementTypes.postfixExpression
            ASTType.PREFIX_EXPRESSION -> ArkoiElementTypes.prefixExpression
            ASTType.RELATIONAL_EXPRESSION -> ArkoiElementTypes.relationalExpression
            ASTType.RETURN_STATEMENT -> ArkoiElementTypes.returnStatement
            ASTType.STATEMENT -> ArkoiElementTypes.statement
            ASTType.STRING_OPERABLE -> ArkoiElementTypes.stringOperable
            ASTType.VARIABLE_DEFINITION -> ArkoiElementTypes.variableDeclaration

            ASTType.ROOT -> root

            else -> TODO(arkoiMarker.astType.name)
        }
    }

}