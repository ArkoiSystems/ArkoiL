/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiIntegration on March 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.lang.arkoi.highlight

import com.arkoisystems.lang.arkoi.ArkoiBundle
import com.arkoisystems.lang.arkoi.lexer.ArkoiTokenTypes
import com.arkoisystems.lang.arkoi.lexer.ArkoiLexer
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
            ArkoiTokenTypes.period
        )

        val badCharacter = fillAttributes(
            ArkoiBundle.message("arkoi.syntaxHighlighter.badCharacter.externalName") highlightWith HighlighterColors.BAD_CHARACTER,
            TokenType.BAD_CHARACTER
        )

        val identifier = fillAttributes(
            ArkoiBundle.message("arkoi.syntaxHighlighter.identifier.externalName") highlightWith DefaultLanguageHighlighterColors.IDENTIFIER,
            ArkoiTokenTypes.identifier
        )

        val lineComment = fillAttributes(
            ArkoiBundle.message("arkoi.syntaxHighlighter.lineComment.externalName") highlightWith DefaultLanguageHighlighterColors.LINE_COMMENT,
            ArkoiTokenTypes.comment
        )

        val parentheses = fillAttributes(
            ArkoiBundle.message("arkoi.syntaxHighlighter.parentheses.externalName") highlightWith DefaultLanguageHighlighterColors.PARENTHESES,
            ArkoiTokenTypes.openingParenthesis,
            ArkoiTokenTypes.closingParenthesis
        )

        val brackets = fillAttributes(
            ArkoiBundle.message("arkoi.syntaxHighlighter.brackets.externalName") highlightWith DefaultLanguageHighlighterColors.BRACKETS,
            ArkoiTokenTypes.openingBracket,
            ArkoiTokenTypes.closingBracket
        )

        val braces = fillAttributes(
            ArkoiBundle.message("arkoi.syntaxHighlighter.braces.externalName") highlightWith DefaultLanguageHighlighterColors.BRACES,
            ArkoiTokenTypes.openingBrace,
            ArkoiTokenTypes.closingBrace
        )

        val semicolon = fillAttributes(
            ArkoiBundle.message("arkoi.syntaxHighlighter.semicolon.externalName") highlightWith DefaultLanguageHighlighterColors.SEMICOLON,
            ArkoiTokenTypes.semicolon
        )

        val string = fillAttributes(
            ArkoiBundle.message("arkoi.syntaxHighlighter.string.externalName") highlightWith DefaultLanguageHighlighterColors.STRING,
            ArkoiTokenTypes.stringLiteral
        )

        val number = fillAttributes(
            ArkoiBundle.message("arkoi.syntaxHighlighter.number.externalName") highlightWith DefaultLanguageHighlighterColors.STRING,
            ArkoiTokenTypes.numberLiteral
        )

        val keyword = fillAttributes(
            ArkoiBundle.message("arkoi.syntaxHighlighter.keyword.externalName") highlightWith DefaultLanguageHighlighterColors.KEYWORD,
            ArkoiTokenTypes.`fun`,
            ArkoiTokenTypes.`var`,
            ArkoiTokenTypes.`return`,
            ArkoiTokenTypes.`this`,
            ArkoiTokenTypes.import,
            ArkoiTokenTypes.`as`
        )

        val primitives = fillAttributes(
            ArkoiBundle.message("arkoi.syntaxHighlighter.primitive.externalName") highlightWith DefaultLanguageHighlighterColors.KEYWORD,
            ArkoiTokenTypes.int,
            ArkoiTokenTypes.long,
            ArkoiTokenTypes.short,
            ArkoiTokenTypes.boolean,
            ArkoiTokenTypes.byte,
            ArkoiTokenTypes.char,
            ArkoiTokenTypes.string
        )

        val operatorSign = fillAttributes(
            ArkoiBundle.message("arkoi.syntaxHighlighter.operatorSign.externalName") highlightWith DefaultLanguageHighlighterColors.OPERATION_SIGN,
            ArkoiTokenTypes.plus,
            ArkoiTokenTypes.minus,
            ArkoiTokenTypes.asterisk,
            ArkoiTokenTypes.div,
            ArkoiTokenTypes.percent,
            ArkoiTokenTypes.plusEquals,
            ArkoiTokenTypes.minusEquals,
            ArkoiTokenTypes.equals,
            ArkoiTokenTypes.asteriskEquals,
            ArkoiTokenTypes.divEquals,
            ArkoiTokenTypes.asteriskAsteriskEquals,
            ArkoiTokenTypes.asteriskAsterisk,
            ArkoiTokenTypes.plusPlus,
            ArkoiTokenTypes.minusMinus,
            ArkoiTokenTypes.percentEquals
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

        val validStringEscape =
            ArkoiBundle.message("arkoi.syntaxHighlighter.validStringEscape.externalName") highlightWith DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE

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

    override fun getHighlightingLexer() = ArkoiLexer()

}

private infix fun String.highlightWith(textAttributesKey: TextAttributesKey): TextAttributesKey {
    return TextAttributesKey.createTextAttributesKey(this, textAttributesKey)
}
