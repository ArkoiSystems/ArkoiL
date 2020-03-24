/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiIntegration on March 18, 2020
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
package com.arkoisystems.lang.arkoi.psi.impl

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.NumberToken
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.StringToken
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.AnnotationSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.BlockSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.ParameterSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.TypeSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.CollectionOperableSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.NumberOperableSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.StringOperableSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.AssignmentExpressionSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.BinaryExpressionSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ParenthesizedExpressionSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.AssignmentOperatorType
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.BinaryOperatorType
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ReturnStatementSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.BlockType
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind
import com.arkoisystems.lang.arkoi.ArkoiTokenTypes
import com.arkoisystems.lang.arkoi.psi.*
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet

object ArkoiPsiImplUtil {

    @JvmStatic
    fun getParameterName(arkoiParameterListPart: ArkoiParameterListPart): IdentifierToken {
        return IdentifierToken
            .builder()
            .content(arkoiParameterListPart.identifier.text)
            .start(arkoiParameterListPart.identifier.textRange.startOffset)
            .end(arkoiParameterListPart.identifier.textRange.endOffset)
            .build()
    }

    @JvmStatic
    fun getFunctionName(functionDeclaration: ArkoiFunctionDeclaration): IdentifierToken? {
        val functionName = functionDeclaration.identifier ?: return null
        return IdentifierToken
            .builder()
            .content(functionName.text)
            .start(functionName.textRange.startOffset)
            .end(functionName.textRange.endOffset)
            .build()
    }

    @JvmStatic
    fun getAnnotationName(arkoiAnnotationCall: ArkoiAnnotationCall): IdentifierToken? {
        val identifierCallPartList =
            arkoiAnnotationCall.identifierCall?.identifierCallPartList ?: return null
        val identifierCallPart = identifierCallPartList[identifierCallPartList.size - 1]
        return IdentifierToken
            .builder()
            .content(identifierCallPart.text)
            .start(identifierCallPart.textRange.startOffset)
            .end(identifierCallPart.textRange.endOffset)
            .build()
    }

    @JvmStatic
    fun getAnnotationArguments(arkoiAnnotationCall: ArkoiAnnotationCall): MutableList<IdentifierToken> {
        TODO()
    }

    @JvmStatic
    fun getFunctionAnnotations(
        functionDeclaration: ArkoiFunctionDeclaration,
        syntaxAnalyzer: SyntaxAnalyzer
    ): MutableList<AnnotationSyntaxAST> {
        val annotations = mutableListOf<AnnotationSyntaxAST>()
        functionDeclaration.annotationCallList.forEach {
            annotations.add(
                AnnotationSyntaxAST
                    .builder(syntaxAnalyzer)
                    .name(it.annotationName)
                    .arguments(it.annotationArguments)
                    .start(it.textRange.startOffset)
                    .end(it.textRange.endOffset)
                    .build()
            )
        }
        return annotations
    }

    @JvmStatic
    fun getFunctionReturnType(
        functionDeclaration: ArkoiFunctionDeclaration,
        syntaxAnalyzer: SyntaxAnalyzer
    ): TypeSyntaxAST? {
        val functionReturnType =
            functionDeclaration.primitives ?: return TypeSyntaxAST
                .builder(syntaxAnalyzer)
                .typeKind(TypeKind.VOID)
                .array(false)
                .build()
        return getPrimitiveReturnType(functionReturnType, syntaxAnalyzer)
    }

    @JvmStatic
    fun getFunctionParameters(
        functionDeclaration: ArkoiFunctionDeclaration,
        syntaxAnalyzer: SyntaxAnalyzer
    ): MutableList<ParameterSyntaxAST> {
        val parameters = mutableListOf<ParameterSyntaxAST>()
        functionDeclaration.parameterList?.parameterListPartList?.forEach {
            parameters.add(
                ParameterSyntaxAST
                    .builder(syntaxAnalyzer)
                    .name(it.parameterName)
                    .type(it.primitives?.getPrimitiveReturnType(syntaxAnalyzer))
                    .start(it.textRange.startOffset)
                    .end(it.textRange.endOffset)
                    .build()
            )
        }
        return parameters
    }

    @JvmStatic
    fun getPrimitiveReturnType(
        arkoiPrimitives: ArkoiPrimitives,
        syntaxAnalyzer: SyntaxAnalyzer
    ): TypeSyntaxAST {
        return TypeSyntaxAST
            .builder(syntaxAnalyzer)
            .typeKind(
                when {
                    arkoiPrimitives.text.startsWith("int") -> TypeKind.INTEGER
                    arkoiPrimitives.text.startsWith("string") -> TypeKind.STRING
                    arkoiPrimitives.text.startsWith("boolean") -> TypeKind.BOOLEAN
                    arkoiPrimitives.text.startsWith("byte") -> TypeKind.BYTE
                    arkoiPrimitives.text.startsWith("double") -> TypeKind.DOUBLE
                    arkoiPrimitives.text.startsWith("float") -> TypeKind.FLOAT
                    arkoiPrimitives.text.startsWith("short") -> TypeKind.SHORT
                    else -> TypeKind.UNDEFINED
                }
            )
            .array(arkoiPrimitives.text.contains("[]"))
            .build()
    }

    @JvmStatic
    fun getFunctionBlock(
        functionDeclaration: ArkoiFunctionDeclaration,
        syntaxAnalyzer: SyntaxAnalyzer
    ): BlockSyntaxAST? {
        val blockDeclaration = functionDeclaration.blockDeclaration ?: return null
        val blockStorage = mutableListOf<AbstractSyntaxAST?>()

        blockDeclaration.braceBlock?.apply {
            blockStorage.addAll(this.returnDeclarationList.map {
                it.getReturnDeclaration(syntaxAnalyzer)
            }.toMutableList())
        }

        blockDeclaration.inlinedBlock?.expression?.apply {
            blockStorage.add(this.getExpression(syntaxAnalyzer))
        }

        return BlockSyntaxAST.builder(syntaxAnalyzer)
            .type(
                if (functionDeclaration.blockDeclaration?.braceBlock != null) BlockType.BLOCK
                else BlockType.INLINE
            )
            .storage(blockStorage)
            .start(blockDeclaration.textRange.startOffset)
            .end(blockDeclaration.textRange.endOffset)
            .build()
    }

    @JvmStatic
    fun getExpression(
        expression: ArkoiExpression,
        syntaxAnalyzer: SyntaxAnalyzer
    ): ExpressionSyntaxAST {
        val expressionOperable = getOperable(expression, syntaxAnalyzer)
        return ExpressionSyntaxAST
            .builder(syntaxAnalyzer)
            .operable(expressionOperable)
            .build()
    }

    private fun getOperable(
        psiElement: PsiElement,
        syntaxAnalyzer: SyntaxAnalyzer
    ): AbstractOperableSyntaxAST<TypeKind> {
        when (psiElement) {
            is ArkoiExpression -> return getOperable(getLeftestExpression(psiElement), syntaxAnalyzer)

            is ArkoiBinaryAdditiveExpression -> {
                var leftOperable =
                    getOperable(getLeftestExpression(psiElement.binaryMultiplicativeExpression), syntaxAnalyzer)
                for (arkoiBinaryAdditiveExpressionPart in psiElement.binaryAdditiveExpressionPartList) {
                    leftOperable = BinaryExpressionSyntaxAST
                        .builder(syntaxAnalyzer)
                        .left(leftOperable)
                        .operator(getBinaryOperator(arkoiBinaryAdditiveExpressionPart))
                        .right(getOperable(getLeftestExpression(arkoiBinaryAdditiveExpressionPart), syntaxAnalyzer))
                        .build()
                }
                return leftOperable
            }

            is ArkoiBinaryMultiplicativeExpression -> {
                var leftOperable = getOperable(getLeftestExpression(psiElement.exponentialExpression), syntaxAnalyzer)
                for (arkoiBinaryAdditiveExpressionPart in psiElement.binaryMultiplicativeExpressionPartList) {
                    leftOperable = BinaryExpressionSyntaxAST
                        .builder(syntaxAnalyzer)
                        .left(leftOperable)
                        .operator(getBinaryOperator(arkoiBinaryAdditiveExpressionPart))
                        .right(getOperable(getLeftestExpression(arkoiBinaryAdditiveExpressionPart), syntaxAnalyzer))
                        .build()
                }
                return leftOperable
            }

            is ArkoiExponentialExpression -> {
                var leftOperable = getOperable(getLeftestExpression(psiElement.operableExpression), syntaxAnalyzer)
                for (arkoiExponentialExpressionPart in psiElement.exponentialExpressionPartList) {
                    leftOperable = BinaryExpressionSyntaxAST
                        .builder(syntaxAnalyzer)
                        .left(leftOperable)
                        .operator(getBinaryOperator(arkoiExponentialExpressionPart))
                        .right(getOperable(getLeftestExpression(arkoiExponentialExpressionPart), syntaxAnalyzer))
                        .build()
                }
                return leftOperable
            }

            is ArkoiAssignmentExpression -> {
                var leftOperable =
                    getOperable(getLeftestExpression(psiElement.binaryAdditiveExpression), syntaxAnalyzer)
                for (arkoiAssignExpressionPart in psiElement.assignExpressionPartList) {
                    leftOperable = AssignmentExpressionSyntaxAST
                        .builder(syntaxAnalyzer)
                        .left(leftOperable)
                        .operator(getAssignmentOperator(arkoiAssignExpressionPart))
                        .right(getOperable(getLeftestExpression(arkoiAssignExpressionPart), syntaxAnalyzer))
                        .build()
                }
                return leftOperable
            }

            is ArkoiOperableExpression -> {
                return run<AbstractOperableSyntaxAST<TypeKind>?> {
                    psiElement.parenthesizedExpression?.apply {
                        return@run getOperable(this, syntaxAnalyzer)
                    }
                    psiElement.operable?.apply {
                        return@run getOperable(this, syntaxAnalyzer)
                    }
                    null
                } ?: throw NullPointerException("Strange behaviour #1")
            }

            is ArkoiIdentifierCall -> {

                TODO()
            }

            is ArkoiOperable -> {
                return when {
                    psiElement.collection != null -> getOperable(psiElement.collection!!, syntaxAnalyzer)
                    psiElement.literals != null -> getOperable(psiElement.literals!!, syntaxAnalyzer)
                    psiElement.identifierCall != null -> getOperable(psiElement.identifierCall!!, syntaxAnalyzer)
                    else -> throw NullPointerException("Strange behaviour #3")
                }
            }

            is ArkoiCollection -> {
                return CollectionOperableSyntaxAST
                    .builder(syntaxAnalyzer)
                    .expressions(
                        psiElement.expressionList?.expressionList?.map {
                            ExpressionSyntaxAST
                                .builder(syntaxAnalyzer)
                                .operable(getOperable(it, syntaxAnalyzer))
                                .start(it.textRange.startOffset)
                                .end(it.textRange.endOffset)
                                .build()
                        }?.toMutableList()
                    )
                    .start(psiElement.textRange.startOffset)
                    .end(psiElement.textRange.endOffset)
                    .build()
            }

            is ArkoiLiterals -> {
                return when {
                    psiElement.stringLiteral != null -> StringOperableSyntaxAST
                        .builder(syntaxAnalyzer)
                        .literal(
                            StringToken
                                .builder()
                                .content(
                                    psiElement.stringLiteral!!.text.substring(
                                        1,
                                        psiElement.stringLiteral!!.textLength - 1
                                    )
                                )
                                .start(psiElement.stringLiteral!!.textRange.startOffset)
                                .end(psiElement.stringLiteral!!.textRange.endOffset)
                                .build()
                        )
                        .start(psiElement.stringLiteral!!.textRange.startOffset)
                        .end(psiElement.stringLiteral!!.textRange.endOffset)
                        .build()

                    psiElement.numberLiteral != null -> NumberOperableSyntaxAST
                        .builder(syntaxAnalyzer)
                        .literal(
                            NumberToken
                                .builder()
                                .content(psiElement.numberLiteral!!.text)
                                .start(psiElement.numberLiteral!!.textRange.startOffset)
                                .end(psiElement.numberLiteral!!.textRange.endOffset)
                                .build()
                        )
                        .start(psiElement.numberLiteral!!.textRange.startOffset)
                        .end(psiElement.numberLiteral!!.textRange.endOffset)
                        .build()

                    else -> throw NullPointerException("Strange behaviour #4")
                }
            }

            is ArkoiParenthesizedExpression -> {
                val parenthesis = psiElement.node.getChildren(TokenSet.forAllMatching {
                    it == ArkoiTokenTypes.L_PARENTHESIS || it == ArkoiTokenTypes.R_PARENTHESIS
                })
                val expressionOperable = getOperable(
                    psiElement.expression ?: throw NullPointerException("Strange behaviour #2"),
                    syntaxAnalyzer
                )
                return ParenthesizedExpressionSyntaxAST
                    .builder(syntaxAnalyzer)
                    .open(
                        SymbolToken
                            .builder()
                            .content(parenthesis[0].text)
                            .start(parenthesis[0].textRange.startOffset)
                            .end(parenthesis[0].textRange.endOffset)
                            .build()
                    )
                    .expression(
                        ExpressionSyntaxAST
                            .builder(syntaxAnalyzer)
                            .operable(expressionOperable)
                            .start(expressionOperable.start)
                            .end(expressionOperable.end)
                            .build()
                    )
                    .close(
                        SymbolToken
                            .builder()
                            .content(parenthesis[1].text)
                            .start(parenthesis[1].textRange.startOffset)
                            .end(parenthesis[1].textRange.endOffset)
                            .build()
                    )
                    .build()
            }

            else -> {
                println("1# $psiElement")
                TODO()
            }
        }
    }

    private fun getBinaryOperator(psiElement: PsiElement): BinaryOperatorType {
        return when {
            psiElement is ArkoiBinaryAdditiveExpressionPart && psiElement.binaryAddExpression != null -> BinaryOperatorType.ADDITION
            psiElement is ArkoiBinaryAdditiveExpressionPart && psiElement.binarySubExpression != null -> BinaryOperatorType.SUBTRACTION

            psiElement is ArkoiBinaryMultiplicativeExpressionPart && psiElement.binaryMulExpression != null -> BinaryOperatorType.MULTIPLICATION
            psiElement is ArkoiBinaryMultiplicativeExpressionPart && psiElement.binaryDivExpression != null -> BinaryOperatorType.DIVISION
            psiElement is ArkoiBinaryMultiplicativeExpressionPart && psiElement.binaryModExpression != null -> BinaryOperatorType.MODULO

            psiElement is ArkoiExponentialExpressionPart -> BinaryOperatorType.EXPONENTIAL

            else -> throw NullPointerException("Strange behaviour #5")
        }
    }

    private fun getAssignmentOperator(psiElement: PsiElement): AssignmentOperatorType {
        return when (psiElement) {
            is ArkoiAssignmentExpression -> AssignmentOperatorType.ASSIGN
            is ArkoiAddAssignExpression -> AssignmentOperatorType.ADD_ASSIGN
            is ArkoiSubAssignExpression -> AssignmentOperatorType.SUB_ASSIGN
            is ArkoiMulAssignExpression -> AssignmentOperatorType.MUL_ASSIGN
            is ArkoiDivAssignExpression -> AssignmentOperatorType.DIV_ASSIGN
            is ArkoiModAssignExpression -> AssignmentOperatorType.MOD_ASSIGN

            else -> throw NullPointerException("Strange behaviour #7")
        }
    }

    private fun getLeftestExpression(psiElement: PsiElement): PsiElement {
        return when (psiElement) {
            is ArkoiExpression -> getLeftestExpression(psiElement.assignmentExpression)

            is ArkoiAssignmentExpression -> if (psiElement.assignExpressionPartList.isNotEmpty()) psiElement
            else getLeftestExpression(psiElement.binaryAdditiveExpression)

            is ArkoiBinaryAdditiveExpression -> if (psiElement.binaryAdditiveExpressionPartList.isNotEmpty()) psiElement
            else getLeftestExpression(psiElement.binaryMultiplicativeExpression)

            is ArkoiBinaryMultiplicativeExpression -> if (psiElement.binaryMultiplicativeExpressionPartList.isNotEmpty()) psiElement
            else getLeftestExpression(psiElement.exponentialExpression)

            is ArkoiExponentialExpression -> if (psiElement.exponentialExpressionPartList.isNotEmpty()) psiElement
            else getLeftestExpression(psiElement.operableExpression)

            is ArkoiBinaryAdditiveExpressionPart -> {
                return when {
                    psiElement.binaryAddExpression != null -> getLeftestExpression(psiElement.binaryAddExpression!!.binaryAdditiveExpression!!)
                    psiElement.binarySubExpression != null -> getLeftestExpression(psiElement.binarySubExpression!!.binaryAdditiveExpression!!)
                    else -> throw NullPointerException("Strange behaviour #6")
                }
            }

            is ArkoiBinaryMultiplicativeExpressionPart -> {
                return when {
                    psiElement.binaryMulExpression != null -> getLeftestExpression(psiElement.binaryMulExpression!!.binaryMultiplicativeExpression!!)
                    psiElement.binaryDivExpression != null -> getLeftestExpression(psiElement.binaryDivExpression!!.binaryMultiplicativeExpression!!)
                    psiElement.binaryModExpression != null -> getLeftestExpression(psiElement.binaryModExpression!!.binaryMultiplicativeExpression!!)
                    else -> throw NullPointerException("Strange behaviour #7")
                }
            }

            is ArkoiExponentialExpressionPart -> return getLeftestExpression(psiElement.exponentialExpression)

            is ArkoiOperableExpression -> psiElement

            else -> {
                println("2# $psiElement")
                TODO()
            }
        }
    }

    @JvmStatic
    fun getReturnDeclaration(
        returnDeclaration: ArkoiReturnDeclaration,
        syntaxAnalyzer: SyntaxAnalyzer
    ): ReturnStatementSyntaxAST {
        return ReturnStatementSyntaxAST
            .builder(syntaxAnalyzer)
            .expression(returnDeclaration.expression?.getExpression(syntaxAnalyzer))
            .start(returnDeclaration.textRange.startOffset)
            .end(returnDeclaration.textRange.endOffset)
            .build()
    }

}