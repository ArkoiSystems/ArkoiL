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
import java.lang.reflect.Field

class ArkoiParser : PsiParser, LightPsiParser {

    private lateinit var myCurrentLexeme: Field

    override fun parseLight(root: IElementType, builder: PsiBuilder) {
        if (builder !is PsiBuilderImpl || builder.lexer !is ArkoiLexer)
            return

        this.myCurrentLexeme = builder.javaClass.getDeclaredField("myCurrentLexeme")
        this.myCurrentLexeme.isAccessible = true

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
        markerFactory: MarkerFactory<out AbstractSyntaxAST>
    ) {
        println(markerFactory.currentMarker.astType.name + ": " + markerFactory.currentMarker.errorMessage)
        println(markerFactory.abstractSyntaxAST)
        println(markerFactory.currentMarker.startToken.start)
        println(markerFactory.currentMarker.endToken.end)

        this.setLexemeIndex(builder, arkoiLexer.tokens!!.indexOf(markerFactory.currentMarker.startToken))
        val mark = builder.mark()

        for (nextFactory in markerFactory.nextMarkerFactories)
            this.makeTree(arkoiLexer, root, builder, nextFactory)

        this.setLexemeIndex(builder, arkoiLexer.tokens!!.indexOf(markerFactory.currentMarker.endToken) + 1)
        if (markerFactory.currentMarker.errorMessage != null) {
            mark.error(
                String.format(
                    markerFactory.currentMarker.errorMessage!!,
                    markerFactory.currentMarker.errorArguments
                )
            )
        } else mark.done(getArkoiType(root, markerFactory.currentMarker))
    }

    private fun setLexemeIndex(builder: PsiBuilderImpl, index: Int) = this.myCurrentLexeme.set(builder, index)

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
            ASTType.EXPRESSION -> ArkoiElementTypes.expression

            ASTType.ROOT -> root

            else -> TODO(arkoiMarker.astType.name)
        }
    }

}