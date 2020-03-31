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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.*
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.*
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.*
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionDefinitionSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ImportDefinitionSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ReturnStatementSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableDefinitionSyntaxAST
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
        } else mark.done(getArkoiType(markerFactory))
    }

    private fun setLexemeIndex(builder: PsiBuilderImpl, index: Int) = this.myCurrentLexeme.set(builder, index)

    private fun getArkoiType(markerFactory: MarkerFactory<*>): IElementType {
        return when (markerFactory.currentMarker.astType) {
            ASTType.IMPORT_DEFINITION -> ArkoiElementTypes.ImportDeclarationElement(markerFactory.abstractSyntaxAST as ImportDefinitionSyntaxAST)
            ASTType.NUMBER_OPERABLE -> ArkoiElementTypes.NumberOperableElement(markerFactory.abstractSyntaxAST as NumberOperableSyntaxAST)
            ASTType.CAST_EXPRESSION -> ArkoiElementTypes.CastExpressionElement(markerFactory.abstractSyntaxAST as CastExpressionSyntaxAST)
            ASTType.TYPE -> ArkoiElementTypes.TypeElement(markerFactory.abstractSyntaxAST as TypeSyntaxAST)
            ASTType.ANNOTATION -> ArkoiElementTypes.AnnotationElement(markerFactory.abstractSyntaxAST as AnnotationSyntaxAST)
            ASTType.ARGUMENT_DEFINITION -> ArkoiElementTypes.ArgumentElement(markerFactory.abstractSyntaxAST as ArgumentSyntaxAST)
            ASTType.ASSIGNMENT_EXPRESSION -> ArkoiElementTypes.AssignmentExpressionElement(markerFactory.abstractSyntaxAST as AssignmentExpressionSyntaxAST)
            ASTType.BINARY_EXPRESSION -> ArkoiElementTypes.BinaryExpressionElement(markerFactory.abstractSyntaxAST as BinaryExpressionSyntaxAST)
            ASTType.BLOCK -> ArkoiElementTypes.BlockElement(markerFactory.abstractSyntaxAST as BlockSyntaxAST)
            ASTType.COLLECTION_OPERABLE -> ArkoiElementTypes.CollectionOperableElement(markerFactory.abstractSyntaxAST as CollectionOperableSyntaxAST)
            ASTType.EQUALITY_EXPRESSION -> ArkoiElementTypes.EqualityExpressionElement(markerFactory.abstractSyntaxAST as EqualityExpressionSyntaxAST)
            ASTType.FUNCTION_CALL_PART -> ArkoiElementTypes.FunctionCallPartElement(markerFactory.abstractSyntaxAST as FunctionCallPartSyntaxAST)
            ASTType.FUNCTION_DEFINITION -> ArkoiElementTypes.FunctionDeclarationElement(markerFactory.abstractSyntaxAST as FunctionDefinitionSyntaxAST)
            ASTType.IDENTIFIER_CALL_OPERABLE -> ArkoiElementTypes.IdentifierCallOperableElement(markerFactory.abstractSyntaxAST as IdentifierCallOperableSyntaxAST)
            ASTType.LOGICAL_EXPRESSION -> ArkoiElementTypes.LogicalExpressionElement(markerFactory.abstractSyntaxAST as LogicalExpressionSyntaxAST)
            ASTType.OPERABLE -> ArkoiElementTypes.OperableElement(markerFactory.abstractSyntaxAST as AbstractOperableSyntaxAST<*>)
            ASTType.PARAMETER_DEFINITION -> ArkoiElementTypes.ParameterElement(markerFactory.abstractSyntaxAST as ParameterSyntaxAST)
            ASTType.PARENTHESIZED_EXPRESSION -> ArkoiElementTypes.ParenthesizedExpressionElement(markerFactory.abstractSyntaxAST as ParenthesizedExpressionSyntaxAST)
            ASTType.POSTFIX_EXPRESSION -> ArkoiElementTypes.PostfixExpressionElement(markerFactory.abstractSyntaxAST as PostfixExpressionSyntaxAST)
            ASTType.PREFIX_EXPRESSION -> ArkoiElementTypes.PrefixExpressionElement(markerFactory.abstractSyntaxAST as PrefixExpressionSyntaxAST)
            ASTType.RELATIONAL_EXPRESSION -> ArkoiElementTypes.RelationalExpressionElement(markerFactory.abstractSyntaxAST as RelationalExpressionSyntaxAST)
            ASTType.RETURN_STATEMENT -> ArkoiElementTypes.ReturnStatementElement(markerFactory.abstractSyntaxAST as ReturnStatementSyntaxAST)
            ASTType.STATEMENT -> ArkoiElementTypes.StatementElement(markerFactory.abstractSyntaxAST as AbstractStatementSyntaxAST)
            ASTType.STRING_OPERABLE -> ArkoiElementTypes.StringOperableElement(markerFactory.abstractSyntaxAST as StringOperableSyntaxAST)
            ASTType.VARIABLE_DEFINITION -> ArkoiElementTypes.VariableDeclarationElement(markerFactory.abstractSyntaxAST as VariableDefinitionSyntaxAST)
            ASTType.ROOT -> ArkoiElementTypes.RootElement(markerFactory.abstractSyntaxAST as RootSyntaxAST)

            else -> TODO(markerFactory.currentMarker.astType.name)
        }
    }

}