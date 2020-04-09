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

import com.arkoisystems.lang.arkoi.ArkoiLanguage
import com.arkoisystems.lang.arkoi.parser.psi.*
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IFileElementType

object ArkoiElementTypes {

    val file = IFileElementType(ArkoiLanguage)

    var import = ArkoiElementType("IMPORT")

    var number = ArkoiElementType("NUMBER")

    var castExpression = ArkoiElementType("CAST_EXPRESSION")

    var parameterList = ArkoiElementType("PARAMETER_LIST")

    var type = ArkoiElementType("TYPE")

    var annotation = ArkoiElementType("ANNOTATION")

    var argument = ArkoiElementType("ARGUMENT")

    var assignmentExpression = ArkoiElementType("ASSIGNMENT_EXPRESSION")

    var binaryExpression = ArkoiElementType("BINARY_EXPRESSION")

    var block = ArkoiElementType("BLOCK")

    var collection = ArkoiElementType("COLLECTION")

    var equalityExpression = ArkoiElementType("EQUALITY_EXPRESSION")

    var expression = ArkoiElementType("EXPRESSION")

    var functionCallPart = ArkoiElementType("FUNCTION_CALL_PART")

    var function = ArkoiElementType("FUNCTION")

    var identifierCall = ArkoiElementType("IDENTIFIER_CALL")

    var logicalExpression = ArkoiElementType("LOGICAL_EXPRESSION")

    var operable = ArkoiElementType("OPERABLE")

    var parameter = ArkoiElementType("PARAMETER")

    var parenthesizedExpression = ArkoiElementType("PARENTHESIZED_EXPRESSION")

    var postfixExpression = ArkoiElementType("POSTFIX_EXPRESSION")

    var prefixExpression = ArkoiElementType("PREFIX_EXPRESSION")

    var relationalExpression = ArkoiElementType("RELATIONAL_EXPRESSION")

    var `return` = ArkoiElementType("RETURN_STATEMENT")

    var statement = ArkoiElementType("STATEMENT")

    var string = ArkoiElementType("STRING")

    var variable = ArkoiElementType("VARIABLE")

    var argumentList = ArkoiElementType("ARGUMENT_LIST")

    var root = ArkoiElementType("ROOT")

    fun createElement(astNode: ASTNode?): PsiElement {
        astNode ?: throw AssertionError("Cannot create an element with \"null\"")

        return when (astNode.elementType) {
            import -> ImportPSI(astNode)
            number -> NumberPSI(astNode)
            castExpression -> CastExpressionPSI(astNode)
            type -> TypePSI(astNode)
            annotation -> AnnotationPSI(astNode)
            argument -> ArgumentPSI(astNode)
            assignmentExpression -> AssignmentExpressionPSI(astNode)
            binaryExpression -> BinaryExpressionPSI(astNode)
            block -> BlockPSI(astNode)
            collection -> CollectionPSI(astNode)
            equalityExpression -> EqualityExpressionPSI(astNode)
            expression -> ExpressionPSI(astNode)
            functionCallPart -> FunctionCallPSI(astNode)
            function -> FunctionPSI(astNode)
            identifierCall -> IdentifierCallPSI(astNode)
            logicalExpression -> LogicalExpressionPSI(astNode)
            operable -> OperablePSI(astNode)
            parameter -> ParameterPSI(astNode)
            parenthesizedExpression -> ParenthesizedExpressionPSI(astNode)
            postfixExpression -> PostfixExpressionPSI(astNode)
            prefixExpression -> PrefixExpressionPSI(astNode)
            relationalExpression -> RelationalExpressionPSI(astNode)
            `return` -> ReturnPSI(astNode)
            statement -> StatementPSI(astNode)
            string -> StringPSI(astNode)
            variable -> VariablePSI(astNode)
            parameterList -> ParameterListPSI(astNode)
            argumentList -> ArgumentListPSI(astNode)
            root -> RootPSI(astNode)

            else -> TODO(astNode.elementType.toString())
        }
    }

}