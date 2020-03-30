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

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType

object ArkoiElementTypes {

    var importDeclaration: IElementType = ArkoiElementType("IMPORT_DECLARATION")

    var numberOperable: IElementType = ArkoiElementType("NUMBER_OPERABLE")

    var castExpression: IElementType = ArkoiElementType("CAST_EXPRESSION")

    var type: IElementType = ArkoiElementType("TYPE")

    var annotation: IElementType = ArkoiElementType("ANNOTATION")

    var argumentDeclaration: IElementType = ArkoiElementType("ARGUMENT_DECLARATION")

    var assignmentExpression: IElementType = ArkoiElementType("ASSIGNMENT_EXPRESSION")

    var binaryExpression: IElementType = ArkoiElementType("BINARY_EXPRESSION")

    var block: IElementType = ArkoiElementType("BLOCK")

    var collectionOperable: IElementType = ArkoiElementType("COLLECTION_OPERABLE")

    var equalityExpression: IElementType = ArkoiElementType("EQUALITY_EXPRESSION")

    var expression: IElementType = ArkoiElementType("EXPRESSION")

    var functionCallPart: IElementType = ArkoiElementType("FUNCTION_CALL_PART")

    var functionDeclaration: IElementType = ArkoiElementType("FUNCTION_DECLARATION")

    var identifierCallOperable: IElementType = ArkoiElementType("IDENTIFIER_CALL_OPERABLE")

    var logicalExpression: IElementType = ArkoiElementType("LOGICAL_EXPRESSION")

    var operable: IElementType = ArkoiElementType("OPERABLE")

    var parameterDeclaration: IElementType = ArkoiElementType("PARAMETER_DECLARATION")

    var parenthesizedExpression: IElementType = ArkoiElementType("PARENTHESIZED_EXPRESSION")

    var postfixExpression: IElementType = ArkoiElementType("POSTFIX_EXPRESSION")

    var prefixExpression: IElementType = ArkoiElementType("PREFIX_EXPRESSION")

    var relationalExpression: IElementType = ArkoiElementType("RELATIONAL_EXPRESSION")

    var returnStatement: IElementType = ArkoiElementType("RETURN_STATEMENT")

    var statement: IElementType = ArkoiElementType("STATEMENT")

    var stringOperable: IElementType = ArkoiElementType("STRING_OPERABLE")

    var variableDeclaration: IElementType = ArkoiElementType("VARIABLE_DECLARATION")

    fun createElement(astNode: ASTNode?): PsiElement {
        astNode ?: throw AssertionError("Cannot create an element with \"null\"");
        val type = astNode.elementType
        println("$type: " + astNode.getChildren(null).joinToString { it.toString() })
        return ASTWrapperPsiElement(astNode)
//        throw AssertionError("Unknown element type: $type");
    }

}