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
package com.arkoisystems.lang.arkoi.lexer

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType


object ArkoiTokenTypes {

    // Keywords

    var `this`: IElementType = ArkoiTokenType("this")

    var `var`: IElementType = ArkoiTokenType("var")

    var `return`: IElementType = ArkoiTokenType("return")

    var `fun`: IElementType = ArkoiTokenType("fun")

    var `as`: IElementType = ArkoiTokenType("as")

    var import: IElementType = ArkoiTokenType("import")

    // Symbols

    var at: IElementType = ArkoiTokenType("@")


    var colon: IElementType = ArkoiTokenType(":")

    var semicolon: IElementType = ArkoiTokenType(";")


    var period: IElementType = ArkoiTokenType(".")

    var comma: IElementType = ArkoiTokenType(",")


    var openingArrow: IElementType = ArkoiTokenType("<")

    var closingArrow: IElementType = ArkoiTokenType(">")


    var openingBrace: IElementType = ArkoiTokenType("{")

    var closingBrace: IElementType = ArkoiTokenType("}")


    var openingBracket: IElementType = ArkoiTokenType("[")

    var closingBracket: IElementType = ArkoiTokenType("]")


    var openingParenthesis: IElementType = ArkoiTokenType("(")

    var closingParenthesis: IElementType = ArkoiTokenType(")")

    // Type keywords

    var char: IElementType = ArkoiTokenType("char")

    var boolean: IElementType = ArkoiTokenType("boolean")

    var byte: IElementType = ArkoiTokenType("byte")

    var int: IElementType = ArkoiTokenType("int")

    var long: IElementType = ArkoiTokenType("long")

    var short: IElementType = ArkoiTokenType("short")

    var string: IElementType = ArkoiTokenType("string")

    // Literals & Comments

    var comment: IElementType = ArkoiTokenType("COMMENT")

    var identifier: IElementType = ArkoiTokenType("IDENTIFIER")

    var numberLiteral: IElementType = ArkoiTokenType("NUMBER_LITERAL")

    var stringLiteral: IElementType = ArkoiTokenType("STRING_LITERAL")

    // Operators

    var equals: IElementType = ArkoiTokenType("=")

    var plus: IElementType = ArkoiTokenType("+")

    var minus: IElementType = ArkoiTokenType("-")

    var asterisk: IElementType = ArkoiTokenType("*")

    var div: IElementType = ArkoiTokenType("/")

    var percent: IElementType = ArkoiTokenType("%")


    var asteriskAsterisk: IElementType = ArkoiTokenType("**")

    var minusMinus: IElementType = ArkoiTokenType("--")

    var plusPlus: IElementType = ArkoiTokenType("++")


    val plusEquals: IElementType = ArkoiTokenType("+=")

    var minusEquals: IElementType = ArkoiTokenType("-=")

    var asteriskEquals: IElementType = ArkoiTokenType("*=")

    var asteriskAsteriskEquals: IElementType = ArkoiTokenType("**=")

    var divEquals: IElementType = ArkoiTokenType("/=")

    var percentEquals: IElementType = ArkoiTokenType("%=")

}