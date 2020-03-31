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

import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.*
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.*
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.*
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionDefinitionSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ImportDefinitionSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ReturnStatementSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableDefinitionSyntaxAST
import com.arkoisystems.lang.arkoi.parser.psi.*
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IFileElementType

object ArkoiElementTypes {

    class ImportDeclarationElement(importDefinitionSyntaxAST: ImportDefinitionSyntaxAST) :
        ArkoiElementType<ImportDefinitionSyntaxAST>("IMPORT_DECLARATION", importDefinitionSyntaxAST)

    class NumberOperableElement(numberOperableSyntaxAST: NumberOperableSyntaxAST) :
        ArkoiElementType<NumberOperableSyntaxAST>("NUMBER_OPERABLE", numberOperableSyntaxAST)

    class CastExpressionElement(castExpressionSyntaxAST: CastExpressionSyntaxAST) :
        ArkoiElementType<CastExpressionSyntaxAST>("CAST_EXPRESSION", castExpressionSyntaxAST)

    class TypeElement(typeSyntaxAST: TypeSyntaxAST) :
        ArkoiElementType<TypeSyntaxAST>("TYPE", typeSyntaxAST)

    class AnnotationElement(annotationSyntaxAST: AnnotationSyntaxAST) :
        ArkoiElementType<AnnotationSyntaxAST>("ANNOTATION", annotationSyntaxAST)

    class ArgumentElement(argumentSyntaxAST: ArgumentSyntaxAST) :
        ArkoiElementType<ArgumentSyntaxAST>("ARGUMENT", argumentSyntaxAST)

    class AssignmentExpressionElement(assignmentExpressionSyntaxAST: AssignmentExpressionSyntaxAST) :
        ArkoiElementType<AssignmentExpressionSyntaxAST>("ASSIGNMENT_EXPRESSION", assignmentExpressionSyntaxAST)

    class BinaryExpressionElement(binaryExpressionSyntaxAST: BinaryExpressionSyntaxAST) :
        ArkoiElementType<BinaryExpressionSyntaxAST>("BINARY_EXPRESSION", binaryExpressionSyntaxAST)

    class BlockElement(blockSyntaxAST: BlockSyntaxAST) :
        ArkoiElementType<BlockSyntaxAST>("BLOCK", blockSyntaxAST)

    class CollectionOperableElement(collectionOperableSyntaxAST: CollectionOperableSyntaxAST) :
        ArkoiElementType<CollectionOperableSyntaxAST>("COLLECTION_OPERABLE", collectionOperableSyntaxAST)

    class EqualityExpressionElement(equalityExpressionSyntaxAST: EqualityExpressionSyntaxAST) :
        ArkoiElementType<EqualityExpressionSyntaxAST>("EQUALITY_EXPRESSION", equalityExpressionSyntaxAST)

    class FunctionCallPartElement(functionCallPartSyntaxAST: FunctionCallPartSyntaxAST) :
        ArkoiElementType<FunctionCallPartSyntaxAST>("FUNCTION_CALL_PART", functionCallPartSyntaxAST)

    class FunctionDeclarationElement(functionDefinitionSyntaxAST: FunctionDefinitionSyntaxAST) :
        ArkoiElementType<FunctionDefinitionSyntaxAST>("FUNCTION_DECLARATION", functionDefinitionSyntaxAST)

    class IdentifierCallOperableElement(identifierCallOperableSyntaxAST: IdentifierCallOperableSyntaxAST) :
        ArkoiElementType<IdentifierCallOperableSyntaxAST>("IDENTIFIER_CALL_OPERABLE", identifierCallOperableSyntaxAST)

    class LogicalExpressionElement(logicalExpressionSyntaxAST: LogicalExpressionSyntaxAST) :
        ArkoiElementType<LogicalExpressionSyntaxAST>("LOGICAL_EXPRESSION", logicalExpressionSyntaxAST)

    class OperableElement(abstractOperableSyntaxAST: AbstractOperableSyntaxAST<*>) :
        ArkoiElementType<AbstractOperableSyntaxAST<*>>("OPERABLE", abstractOperableSyntaxAST)

    class ParameterElement(parameterSyntaxAST: ParameterSyntaxAST) :
        ArkoiElementType<ParameterSyntaxAST>("PARAMETER", parameterSyntaxAST)

    class ParenthesizedExpressionElement(parenthesizedExpressionSyntaxAST: ParenthesizedExpressionSyntaxAST) :
        ArkoiElementType<ParenthesizedExpressionSyntaxAST>("PARENTHESIZED_EXPRESSION", parenthesizedExpressionSyntaxAST)

    class PostfixExpressionElement(postfixExpressionSyntaxAST: PostfixExpressionSyntaxAST) :
        ArkoiElementType<PostfixExpressionSyntaxAST>("POSTFIX_EXPRESSION", postfixExpressionSyntaxAST)

    class PrefixExpressionElement(prefixExpressionSyntaxAST: PrefixExpressionSyntaxAST) :
        ArkoiElementType<PrefixExpressionSyntaxAST>("PREFIX_EXPRESSION", prefixExpressionSyntaxAST)

    class RelationalExpressionElement(relationalExpressionSyntaxAST: RelationalExpressionSyntaxAST) :
        ArkoiElementType<RelationalExpressionSyntaxAST>("RELATIONAL_EXPRESSION", relationalExpressionSyntaxAST)

    class ReturnStatementElement(returnStatementSyntaxAST: ReturnStatementSyntaxAST) :
        ArkoiElementType<ReturnStatementSyntaxAST>("RETURN_STATEMENT", returnStatementSyntaxAST)

    class StatementElement(abstractStatementSyntaxAST: AbstractStatementSyntaxAST) :
        ArkoiElementType<AbstractStatementSyntaxAST>("STATEMENT", abstractStatementSyntaxAST)

    class StringOperableElement(stringOperableSyntaxAST: StringOperableSyntaxAST) :
        ArkoiElementType<StringOperableSyntaxAST>("STRING_OPERABLE", stringOperableSyntaxAST)

    class VariableDeclarationElement(variableDefinitionSyntaxAST: VariableDefinitionSyntaxAST) :
        ArkoiElementType<VariableDefinitionSyntaxAST>("VARIABLE_DECLARATION", variableDefinitionSyntaxAST)

    class RootElement(rootSyntaxAST: RootSyntaxAST) :
        ArkoiElementType<RootSyntaxAST>("ROOT", rootSyntaxAST)

    fun createElement(astNode: ASTNode?): PsiElement {
        astNode ?: throw AssertionError("Cannot create an element with \"null\"")
        return when (astNode.elementType) {
            is RootElement -> ArkoiRoot(
                astNode,
                (astNode.elementType as RootElement).abstractSyntaxAST!!
            )
            is ImportDeclarationElement -> ArkoiImportDeclaration(
                astNode,
                (astNode.elementType as ImportDeclarationElement).abstractSyntaxAST!!
            )
            is NumberOperableElement -> ArkoiNumberOperable(
                astNode,
                (astNode.elementType as NumberOperableElement).abstractSyntaxAST!!
            )
            is CastExpressionElement -> ArkoiCastExpression(
                astNode,
                (astNode.elementType as CastExpressionElement).abstractSyntaxAST!!
            )
            is TypeElement -> ArkoiType(
                astNode,
                (astNode.elementType as TypeElement).abstractSyntaxAST!!
            )
            is AnnotationElement -> ArkoiAnnotation(
                astNode,
                (astNode.elementType as AnnotationElement).abstractSyntaxAST!!
            )
            is ArgumentElement -> ArkoiArgument(
                astNode,
                (astNode.elementType as ArgumentElement).abstractSyntaxAST!!
            )
            is AssignmentExpressionElement -> ArkoiAssignmentExpression(
                astNode,
                (astNode.elementType as AssignmentExpressionElement).abstractSyntaxAST!!
            )
            is BinaryExpressionElement -> ArkoiBinaryExpression(
                astNode,
                (astNode.elementType as BinaryExpressionElement).abstractSyntaxAST!!
            )
            is BlockElement -> ArkoiBlock(
                astNode,
                (astNode.elementType as BlockElement).abstractSyntaxAST!!
            )
            is CollectionOperableElement -> ArkoiCollectionOperable(
                astNode,
                (astNode.elementType as CollectionOperableElement).abstractSyntaxAST!!
            )
            is EqualityExpressionElement -> ArkoiEqualityExpression(
                astNode,
                (astNode.elementType as EqualityExpressionElement).abstractSyntaxAST!!
            )
            is FunctionCallPartElement -> ArkoiFunctionCallPart(
                astNode,
                (astNode.elementType as FunctionCallPartElement).abstractSyntaxAST!!
            )
            is FunctionDeclarationElement -> ArkoiFunctionDeclaration(
                astNode,
                (astNode.elementType as FunctionDeclarationElement).abstractSyntaxAST!!
            )
            is IdentifierCallOperableElement -> ArkoiIdentifierCallOperable(
                astNode,
                (astNode.elementType as IdentifierCallOperableElement).abstractSyntaxAST!!
            )
            is LogicalExpressionElement -> ArkoiLogicalExpression(
                astNode,
                (astNode.elementType as LogicalExpressionElement).abstractSyntaxAST!!
            )
            is OperableElement -> ArkoiOperable(
                astNode,
                (astNode.elementType as OperableElement).abstractSyntaxAST!!
            )
            is ParameterElement -> ArkoiParameter(
                astNode,
                (astNode.elementType as ParameterElement).abstractSyntaxAST!!
            )
            is ParenthesizedExpressionElement -> ArkoiParenthesizedExpression(
                astNode,
                (astNode.elementType as ParenthesizedExpressionElement).abstractSyntaxAST!!
            )
            is PostfixExpressionElement -> ArkoiPostExpression(
                astNode,
                (astNode.elementType as PostfixExpressionElement).abstractSyntaxAST!!
            )
            is PrefixExpressionElement -> ArkoiPrefixExpression(
                astNode,
                (astNode.elementType as PrefixExpressionElement).abstractSyntaxAST!!
            )
            is RelationalExpressionElement -> ArkoiRelationalExpression(
                astNode,
                (astNode.elementType as RelationalExpressionElement).abstractSyntaxAST!!
            )
            is ReturnStatementElement -> ArkoiReturnStatement(
                astNode,
                (astNode.elementType as ReturnStatementElement).abstractSyntaxAST!!
            )
            is StatementElement -> ArkoiStatement(
                astNode,
                (astNode.elementType as StatementElement).abstractSyntaxAST!!
            )
            is StringOperableElement -> ArkoiStringOperable(
                astNode,
                (astNode.elementType as StringOperableElement).abstractSyntaxAST!!
            )
            is VariableDeclarationElement -> ArkoiVariableDeclaration(
                astNode,
                (astNode.elementType as VariableDeclarationElement).abstractSyntaxAST!!
            )

            else -> TODO(astNode.elementType.javaClass.simpleName)
        }
    }

}