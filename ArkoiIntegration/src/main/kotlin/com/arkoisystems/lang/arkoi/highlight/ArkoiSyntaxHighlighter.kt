/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiIntegration on March 17, 2020
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
package com.arkoisystems.lang.arkoi.highlight

import com.arkoisystems.lang.arkoi.ArkoiBundle
import com.arkoisystems.lang.arkoi.ArkoiTokenTypes.*
import com.arkoisystems.lang.arkoi.lexer.ArkoiLexerAdapter
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase.pack
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

class ArkoiSyntaxHighlighter : SyntaxHighlighter {

    companion object {
        private val attributes = HashMap<IElementType, TextAttributesKey>()

        val dot = fillAttributes(
            ArkoiBundle.message("arkoi.syntaxHighlighter.dot.externalName") highlightWith DefaultLanguageHighlighterColors.DOT,
            DOT
        )

        val badCharacter = fillAttributes(
            ArkoiBundle.message("arkoi.syntaxHighlighter.badCharacter.externalName") highlightWith HighlighterColors.BAD_CHARACTER,
            TokenType.BAD_CHARACTER
        )

        val identifier = fillAttributes(
            ArkoiBundle.message("arkoi.syntaxHighlighter.identifier.externalName") highlightWith DefaultLanguageHighlighterColors.IDENTIFIER,
            IDENTIFIER
        )

        val lineComment = fillAttributes(
            ArkoiBundle.message("arkoi.syntaxHighlighter.lineComment.externalName") highlightWith DefaultLanguageHighlighterColors.LINE_COMMENT,
            COMMENT
        )

        val parentheses = fillAttributes(
            ArkoiBundle.message("arkoi.syntaxHighlighter.parentheses.externalName") highlightWith DefaultLanguageHighlighterColors.PARENTHESES,
            L_PARENTHESIS,
            R_PARENTHESIS
        )

        val brackets = fillAttributes(
            ArkoiBundle.message("arkoi.syntaxHighlighter.brackets.externalName") highlightWith DefaultLanguageHighlighterColors.BRACKETS,
            L_BRACKET,
            R_BRACKET
        )

        val braces = fillAttributes(
            ArkoiBundle.message("arkoi.syntaxHighlighter.braces.externalName") highlightWith DefaultLanguageHighlighterColors.BRACES,
            L_BRACE,
            R_BRACE
        )

        val semicolon = fillAttributes(
            ArkoiBundle.message("arkoi.syntaxHighlighter.semicolon.externalName") highlightWith DefaultLanguageHighlighterColors.SEMICOLON,
            SEMICOLON
        )

        val string = fillAttributes(
            ArkoiBundle.message("arkoi.syntaxHighlighter.string.externalName") highlightWith DefaultLanguageHighlighterColors.STRING,
            STRING_LITERAL
        )

        val number = fillAttributes(
            ArkoiBundle.message("arkoi.syntaxHighlighter.number.externalName") highlightWith DefaultLanguageHighlighterColors.STRING,
            NUMBER_LITERAL
        )

        val keyword = fillAttributes(
            ArkoiBundle.message("arkoi.syntaxHighlighter.keyword.externalName") highlightWith DefaultLanguageHighlighterColors.KEYWORD,
            FUN,
            VAR,
            RETURN,
            THIS,
            IMPORT,
            AS
        )

        val primitives = fillAttributes(
            ArkoiBundle.message("arkoi.syntaxHighlighter.primitive.externalName") highlightWith DefaultLanguageHighlighterColors.KEYWORD,
            INT,
            LONG,
            SHORT,
            BOOLEAN,
            BYTE,
            CHAR,
            STRING
        )

        val operatorSign = fillAttributes(
            ArkoiBundle.message("arkoi.syntaxHighlighter.operatorSign.externalName") highlightWith DefaultLanguageHighlighterColors.OPERATION_SIGN,
            PLUS,
            MINUS,
            ASTERISK,
            SLASH,
            PERCENT,
            ADD_ASSIGN,
            SUB_ASSIGN,
            EQUALS,
            MUL_ASSIGN,
            DIV_ASSIGN,
            EXP_ASSIGN,
            DOUBLE_ASTERISK,
            DOUBLE_PLUS,
            DOUBLE_MINUS
        )

        val functionCall =
            ArkoiBundle.message("arkoi.syntaxHighlighter.functionCall.externalName") highlightWith DefaultLanguageHighlighterColors.FUNCTION_CALL

        val functionParameter =
            ArkoiBundle.message("arkoi.syntaxHighlighter.parameter.externalName") highlightWith DefaultLanguageHighlighterColors.PARAMETER

        val functionDeclaration =
            ArkoiBundle.message("arkoi.syntaxHighlighter.functionDeclaration.externalName") highlightWith DefaultLanguageHighlighterColors.FUNCTION_DECLARATION

        val localVariable =
            ArkoiBundle.message("arkoi.syntaxHighlighter.localVariable.externalName") highlightWith DefaultLanguageHighlighterColors.LOCAL_VARIABLE

        val globalVariable =
            ArkoiBundle.message("arkoi.syntaxHighlighter.globalVariable.externalName") highlightWith DefaultLanguageHighlighterColors.GLOBAL_VARIABLE

        private fun fillAttributes(
            textAttributesKey: TextAttributesKey,
            vararg elementTypes: IElementType
        ): TextAttributesKey {
            for (elementType in elementTypes)
                attributes[elementType] = textAttributesKey
            return textAttributesKey
        }
    }

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        return pack(attributes[tokenType])
    }

    override fun getHighlightingLexer() = ArkoiLexerAdapter()

}

private infix fun String.highlightWith(textAttributesKey: TextAttributesKey): TextAttributesKey {
    return TextAttributesKey.createTextAttributesKey(this, textAttributesKey)
}
