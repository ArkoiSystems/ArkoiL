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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.*
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.*
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
    fun getAnnotationCall(
        arkoiAnnotationCall: ArkoiAnnotationCall,
        syntaxAnalyzer: SyntaxAnalyzer
    ): IdentifierCallOperableSyntaxAST? {
        return arkoiAnnotationCall.identifierCall?.getIdentifierCall(syntaxAnalyzer)
    }

    @JvmStatic
    fun getIdentifierCall(
        arkoiIdentifierCall: ArkoiIdentifierCall,
        syntaxAnalyzer: SyntaxAnalyzer
    ): IdentifierCallOperableSyntaxAST? {
        val isFileLocal = arkoiIdentifierCall.node.getChildren(TokenSet.forAllMatching {
            it == ArkoiTokenTypes.THIS
        }).size == 1
        var firstIdentifierCall: IdentifierCallOperableSyntaxAST? = null
        var parentIdentifierCall: IdentifierCallOperableSyntaxAST? = null

        arkoiIdentifierCall.identifierCallPartList.forEachIndexed { index, arkoiIdentifierCallPart ->
            val identifierCallOperableSyntaxAST = IdentifierCallOperableSyntaxAST
                .builder()
                .called(
                    IdentifierToken
                        .builder()
                        .content(arkoiIdentifierCallPart.identifier.text)
                        .start(arkoiIdentifierCallPart.identifier.textRange.startOffset)
                        .end(arkoiIdentifierCallPart.identifier.textRange.endOffset)
                        .build()
                )
                .functionPart(arkoiIdentifierCallPart.getCalledFunctionPart(syntaxAnalyzer))
                .start(arkoiIdentifierCallPart.textRange.startOffset)
                .end(arkoiIdentifierCallPart.textRange.endOffset)
                .build()

            if (index == 0) {
                firstIdentifierCall = identifierCallOperableSyntaxAST
                parentIdentifierCall = identifierCallOperableSyntaxAST
            } else {
                parentIdentifierCall?.nextIdentifierCall = identifierCallOperableSyntaxAST
                parentIdentifierCall = identifierCallOperableSyntaxAST
            }
        }

        firstIdentifierCall?.isFileLocal = isFileLocal
        return firstIdentifierCall
    }

    @JvmStatic
    fun getAnnotationArguments(
        arkoiAnnotationCall: ArkoiAnnotationCall,
        syntaxAnalyzer: SyntaxAnalyzer
    ): MutableList<ArgumentSyntaxAST> {
        val arguments = mutableListOf<ArgumentSyntaxAST>()
        arkoiAnnotationCall.argumentList?.argumentListPartList?.forEach {
            arguments.add(
                ArgumentSyntaxAST
                    .builder()
                    .name(
                        IdentifierToken
                            .builder()
                            .content(it.identifier.text)
                            .start(it.textRange.startOffset)
                            .end(it.textRange.endOffset)
                            .build()
                    )
                    .expression(getExpression(it.expression!!, syntaxAnalyzer))
                    .start(it.textRange.startOffset)
                    .end(it.textRange.endOffset)
                    .build()
            )
        }
        return arguments
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
                    .call(it.getAnnotationCall(syntaxAnalyzer))
                    .arguments(it.getAnnotationArguments(syntaxAnalyzer))
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
            .start(arkoiPrimitives.textRange.startOffset)
            .end(arkoiPrimitives.textRange.endOffset)
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
        return ExpressionSyntaxAST
            .builder(syntaxAnalyzer)
            .operable(getOperable(expression, syntaxAnalyzer))
            .start(expression.textRange.startOffset)
            .end(expression.textRange.endOffset)
            .build()
    }

    @JvmStatic
    fun getCalledFunctionPart(
        arkoiIdentifierCallPart: ArkoiIdentifierCallPart,
        syntaxAnalyzer: SyntaxAnalyzer
    ): FunctionCallPartSyntaxAST? {
        val functionCallPart = arkoiIdentifierCallPart.functionCallPart ?: return null
        return FunctionCallPartSyntaxAST
            .builder()
            .expressions(functionCallPart.expressionList?.expressionList?.map { getExpression(it, syntaxAnalyzer) }
                ?.toMutableList())
            .start(functionCallPart.textRange.startOffset)
            .end(functionCallPart.textRange.endOffset)
            .build()
    }

    private fun getOperable(
        psiElement: PsiElement?,
        syntaxAnalyzer: SyntaxAnalyzer
    ): AbstractOperableSyntaxAST<TypeKind>? {
        psiElement ?: return null

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
                    if (leftOperable is BinaryExpressionSyntaxAST) {
                        leftOperable.start = leftOperable.leftSideOperable?.start ?: 0
                        leftOperable.end = leftOperable.rightSideOperable?.end ?: 0
                    }
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
                    if (leftOperable is BinaryExpressionSyntaxAST) {
                        leftOperable.start = leftOperable.leftSideOperable?.start ?: 0
                        leftOperable.end = leftOperable.rightSideOperable?.end ?: 0
                    }
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
                    if (leftOperable is BinaryExpressionSyntaxAST) {
                        leftOperable.start = leftOperable.leftSideOperable?.start ?: 0
                        leftOperable.end = leftOperable.rightSideOperable?.end ?: 0
                    }
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
                    if (leftOperable is AssignmentExpressionSyntaxAST) {
                        leftOperable.start = leftOperable.leftSideOperable?.start ?: 0
                        leftOperable.end = leftOperable.rightSideOperable?.end ?: 0
                    }
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

            is ArkoiIdentifierCall -> return psiElement.getIdentifierCall(syntaxAnalyzer)!!

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
                    .expressions(psiElement.expressionList?.expressionList?.map { getExpression(it, syntaxAnalyzer) }
                        ?.toMutableList())
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
                val expressionOperable = getOperable(
                    psiElement.expression ?: throw NullPointerException("Strange behaviour #2"),
                    syntaxAnalyzer
                ) ?: return null
                return ParenthesizedExpressionSyntaxAST
                    .builder(syntaxAnalyzer)
                    .expression(
                        ExpressionSyntaxAST
                            .builder(syntaxAnalyzer)
                            .operable(expressionOperable)
                            .start(expressionOperable.start)
                            .end(expressionOperable.end)
                            .build()
                    )
                    .start(psiElement.textRange.startOffset)
                    .end(psiElement.textRange.endOffset)
                    .build()
            }

            else -> TODO("1# $psiElement")
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
        return when {
            psiElement is ArkoiAssignExpressionPart && psiElement.assignExpression != null -> AssignmentOperatorType.ASSIGN
            psiElement is ArkoiAssignExpressionPart && psiElement.addAssignExpression != null -> AssignmentOperatorType.ADD_ASSIGN
            psiElement is ArkoiAssignExpressionPart && psiElement.subAssignExpression != null -> AssignmentOperatorType.SUB_ASSIGN
            psiElement is ArkoiAssignExpressionPart && psiElement.mulAssignExpression != null -> AssignmentOperatorType.MUL_ASSIGN
            psiElement is ArkoiAssignExpressionPart && psiElement.divAssignExpression != null -> AssignmentOperatorType.DIV_ASSIGN
            psiElement is ArkoiAssignExpressionPart && psiElement.modAssignExpression != null -> AssignmentOperatorType.MOD_ASSIGN

            else -> throw NullPointerException("Strange behaviour #7 $psiElement ${psiElement.text}")
        }
    }

    private fun getLeftestExpression(psiElement: PsiElement?): PsiElement? {
        psiElement ?: return null

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
                    psiElement.binaryAddExpression != null -> getLeftestExpression(psiElement.binaryAddExpression?.binaryAdditiveExpression)
                    psiElement.binarySubExpression != null -> getLeftestExpression(psiElement.binarySubExpression?.binaryAdditiveExpression)
                    else -> throw NullPointerException("Strange behaviour #6")
                }
            }

            is ArkoiAssignExpressionPart -> {
                return when {
                    psiElement.assignExpression != null -> getLeftestExpression(psiElement.assignExpression?.assignmentExpression)
                    psiElement.addAssignExpression != null -> getLeftestExpression(psiElement.addAssignExpression?.assignmentExpression)
                    psiElement.subAssignExpression != null -> getLeftestExpression(psiElement.subAssignExpression?.assignmentExpression)
                    psiElement.mulAssignExpression != null -> getLeftestExpression(psiElement.mulAssignExpression?.assignmentExpression)
                    psiElement.divAssignExpression != null -> getLeftestExpression(psiElement.divAssignExpression?.assignmentExpression)
                    psiElement.modAssignExpression != null -> getLeftestExpression(psiElement.modAssignExpression?.assignmentExpression)

                    else -> throw NullPointerException("Strange behaviour #8")
                }
            }

            is ArkoiBinaryMultiplicativeExpressionPart -> {
                return when {
                    psiElement.binaryMulExpression != null -> getLeftestExpression(psiElement.binaryMulExpression?.binaryMultiplicativeExpression)
                    psiElement.binaryDivExpression != null -> getLeftestExpression(psiElement.binaryDivExpression?.binaryMultiplicativeExpression)
                    psiElement.binaryModExpression != null -> getLeftestExpression(psiElement.binaryModExpression?.binaryMultiplicativeExpression)

                    else -> throw NullPointerException("Strange behaviour #7")
                }
            }

            is ArkoiExponentialExpressionPart -> return getLeftestExpression(psiElement.exponentialExpression)

            is ArkoiOperableExpression -> psiElement

            else -> TODO("2# $psiElement")
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