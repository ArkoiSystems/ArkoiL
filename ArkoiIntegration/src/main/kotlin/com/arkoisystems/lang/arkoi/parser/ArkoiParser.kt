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

import com.arkoisystems.arkoicompiler.ArkoiClass
import com.arkoisystems.arkoicompiler.api.ICompilerMarker
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.marker.MarkerFactory
import com.arkoisystems.lang.arkoi.lexer.ArkoiLexer
import com.intellij.lang.ASTNode
import com.intellij.lang.LightPsiParser
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.lang.impl.PsiBuilderImpl
import com.intellij.psi.tree.IElementType
import java.lang.reflect.Field

class ArkoiParser(private val arkoiParserDefinition: ArkoiParserDefinition) : PsiParser, LightPsiParser {

    private lateinit var myCurrentLexeme: Field

    private lateinit var arkoiClass: ArkoiClass

    override fun parseLight(root: IElementType, builder: PsiBuilder) {
        if (builder !is PsiBuilderImpl || builder.lexer !is ArkoiLexer)
            return

        this.myCurrentLexeme = builder.javaClass.getDeclaredField("myCurrentLexeme")
        this.myCurrentLexeme.isAccessible = true

        val arkoiLexer = builder.lexer as ArkoiLexer
        this.arkoiClass = arkoiLexer.arkoiClass

        this.arkoiClass.content = builder.originalText.toString().toCharArray()
        this.arkoiClass.lexicalAnalyzer.processStage()
        this.arkoiClass.syntaxAnalyzer.processStage()

        val mark = builder.mark()
        makeTree(arkoiLexer, root, builder, this.arkoiClass.syntaxAnalyzer.rootAST.markerFactory)
        mark.done(root)
    }

    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        parseLight(root, builder)
        val fileNode = builder.treeBuilt
        this.arkoiParserDefinition.arkoiClasses[fileNode.firstChildNode.psi.hashCode()] = this.arkoiClass
        return fileNode
    }

    private fun makeTree(
        arkoiLexer: ArkoiLexer,
        root: IElementType,
        builder: PsiBuilderImpl,
        markerFactory: MarkerFactory<*, *, *>
    ) {
        this.setLexemeIndex(builder, arkoiLexer.tokens!!.indexOf(markerFactory.currentMarker.start))

        val mark = builder.mark()

        for (nextFactory in markerFactory.nextMarkerFactories)
            this.makeTree(arkoiLexer, root, builder, nextFactory)

        this.setLexemeIndex(builder, arkoiLexer.tokens!!.indexOf(markerFactory.currentMarker.end) + 1)
        if (markerFactory.currentMarker.errorMessage != null) {
//            println(String.format(markerFactory.currentMarker.errorMessage!!, markerFactory.currentMarker.errorArguments))
//            println(markerFactory.currentMarker.errorMessage!! + ", " + markerFactory.currentMarker.errorArguments?.contentToString())
            mark.error(markerFactory.currentMarker.errorMessage!!)
        } else mark.done(getArkoiType(markerFactory.currentMarker))
    }

    private fun setLexemeIndex(builder: PsiBuilderImpl, index: Int) = this.myCurrentLexeme.set(builder, index)

    private fun getArkoiType(compilerMarker: ICompilerMarker<*, *>): IElementType {
        return when (compilerMarker.astType) {
            ASTType.IMPORT -> ArkoiElementTypes.import
            ASTType.NUMBER -> ArkoiElementTypes.number
            ASTType.CAST_EXPRESSION -> ArkoiElementTypes.castExpression
            ASTType.TYPE -> ArkoiElementTypes.type
            ASTType.ANNOTATION -> ArkoiElementTypes.annotation
            ASTType.ARGUMENT -> ArkoiElementTypes.argument
            ASTType.ASSIGNMENT_EXPRESSION -> ArkoiElementTypes.assignmentExpression
            ASTType.BINARY_EXPRESSION -> ArkoiElementTypes.binaryExpression
            ASTType.BLOCK -> ArkoiElementTypes.block
            ASTType.COLLECTION -> ArkoiElementTypes.collection
            ASTType.EQUALITY_EXPRESSION -> ArkoiElementTypes.equalityExpression
            ASTType.FUNCTION_CALL_PART -> ArkoiElementTypes.functionCallPart
            ASTType.FUNCTION -> ArkoiElementTypes.function
            ASTType.IDENTIFIER_CALL -> ArkoiElementTypes.identifierCall
            ASTType.LOGICAL_EXPRESSION -> ArkoiElementTypes.logicalExpression
            ASTType.OPERABLE -> ArkoiElementTypes.operable
            ASTType.PARAMETER -> ArkoiElementTypes.parameter
            ASTType.PARENTHESIZED_EXPRESSION -> ArkoiElementTypes.parenthesizedExpression
            ASTType.POSTFIX_EXPRESSION -> ArkoiElementTypes.postfixExpression
            ASTType.PREFIX_EXPRESSION -> ArkoiElementTypes.prefixExpression
            ASTType.RELATIONAL_EXPRESSION -> ArkoiElementTypes.relationalExpression
            ASTType.RETURN -> ArkoiElementTypes.`return`
            ASTType.STATEMENT -> ArkoiElementTypes.statement
            ASTType.STRING -> ArkoiElementTypes.string
            ASTType.VARIABLE -> ArkoiElementTypes.variable
            ASTType.EXPRESSION -> ArkoiElementTypes.expression
            ASTType.PARAMETER_LIST -> ArkoiElementTypes.parameterList
            ASTType.ARGUMENT_LIST -> ArkoiElementTypes.argumentList
            ASTType.ROOT -> ArkoiElementTypes.root

            else -> TODO(compilerMarker.astType.name)
        }
    }

}