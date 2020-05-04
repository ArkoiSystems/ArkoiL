/* Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
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

import com.arkoisystems.arkoicompiler.ArkoiClass
import com.arkoisystems.arkoicompiler.ArkoiCompiler
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.*
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.KeywordType
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.OperatorType
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind
import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

class ArkoiLexer : LexerBase() {

    var tokens: Array<out AbstractToken>? = null

    val arkoiClass = ArkoiClass(ArkoiCompiler(), "", byteArrayOf())


    private var buffer: CharSequence? = null

    private var tokenStartOffset = 0

    private var tokenEndOffset = 0

    private var startOffset = 0

    private var tokenIndex = 0

    private var endOffset = 0

    override fun start(
        buffer: CharSequence,
        startOffset: Int,
        endOffset: Int,
        initialState: Int
    ) {
        this.tokenStartOffset = startOffset
        this.tokenEndOffset = startOffset
        this.startOffset = startOffset
        this.endOffset = endOffset
        this.buffer = buffer

        this.tokenIndex = 0

        this.arkoiClass.content = String(this.buffer.toString().toByteArray()).toCharArray()

        this.arkoiClass.lexicalAnalyzer.position = this.startOffset
        this.arkoiClass.lexicalAnalyzer.processStage()
        this.tokens = arkoiClass.lexicalAnalyzer.tokens
    }

    override fun advance() {
        this.tokenIndex++
    }

    override fun getTokenType(): IElementType? {
        if (this.tokens == null)
            return null

        val token = (if (this.tokenIndex > this.tokens!!.size - 1) null
        else this.tokens!![this.tokenIndex]) ?: return null
        this.tokenStartOffset = token.start
        this.tokenEndOffset = token.end

        return when (token) {
            is CommentToken -> ArkoiTokenTypes.comment

            is IdentifierToken -> ArkoiTokenTypes.identifier

            is NumberToken -> ArkoiTokenTypes.numberLiteral

            is StringToken -> ArkoiTokenTypes.stringLiteral

            is WhitespaceToken -> TokenType.WHITE_SPACE

            is SymbolToken -> when (token.symbolType) {
                SymbolType.AT_SIGN -> ArkoiTokenTypes.at

                SymbolType.COLON -> ArkoiTokenTypes.colon
//                SymbolType.SEMICOLON -> ArkoiTokenTypes.semicolon

                SymbolType.PERIOD -> ArkoiTokenTypes.period
                SymbolType.COMMA -> ArkoiTokenTypes.comma

                SymbolType.OPENING_ARROW -> ArkoiTokenTypes.openingArrow
                SymbolType.CLOSING_ARROW -> ArkoiTokenTypes.closingArrow

                SymbolType.OPENING_BRACE -> ArkoiTokenTypes.openingBrace
                SymbolType.CLOSING_BRACE -> ArkoiTokenTypes.closingBrace

                SymbolType.OPENING_BRACKET -> ArkoiTokenTypes.openingBracket
                SymbolType.CLOSING_BRACKET -> ArkoiTokenTypes.closingBracket

                SymbolType.OPENING_PARENTHESIS -> ArkoiTokenTypes.openingParenthesis
                SymbolType.CLOSING_PARENTHESIS -> ArkoiTokenTypes.closingParenthesis

                else -> TokenType.BAD_CHARACTER
            }

            is OperatorToken -> when (token.operatorType) {
                OperatorType.EQUALS -> ArkoiTokenTypes.equals

                OperatorType.PLUS -> ArkoiTokenTypes.plus
                OperatorType.MINUS -> ArkoiTokenTypes.minus
                OperatorType.ASTERISK -> ArkoiTokenTypes.asterisk
                OperatorType.DIV -> ArkoiTokenTypes.div
                OperatorType.PERCENT -> ArkoiTokenTypes.percent

                OperatorType.PLUS_PLUS -> ArkoiTokenTypes.plusPlus
                OperatorType.PLUS_EQUALS -> ArkoiTokenTypes.plusEquals
                OperatorType.MINUS_MINUS -> ArkoiTokenTypes.minusMinus
                OperatorType.MINUS_EQUALS -> ArkoiTokenTypes.minusEquals
                OperatorType.ASTERISK_ASTERISK -> ArkoiTokenTypes.asteriskAsterisk
                OperatorType.ASTERISK_EQUALS -> ArkoiTokenTypes.asteriskEquals
                OperatorType.DIV_EQUALS -> ArkoiTokenTypes.divEquals
                OperatorType.PERCENT_EQUALS -> ArkoiTokenTypes.percentEquals

                else -> TokenType.BAD_CHARACTER
            }

            is KeywordToken -> when (token.keywordType) {
                KeywordType.AS -> ArkoiTokenTypes.`as`
                KeywordType.FUN -> ArkoiTokenTypes.`fun`
                KeywordType.IMPORT -> ArkoiTokenTypes.import
                KeywordType.RETURN -> ArkoiTokenTypes.`return`
                KeywordType.THIS -> ArkoiTokenTypes.`this`
                KeywordType.VAR -> ArkoiTokenTypes.`var`

                else -> TokenType.BAD_CHARACTER
            }

            is TypeKeywordToken -> when(token.typeKind) {
                TypeKind.BOOLEAN -> ArkoiTokenTypes.boolean
                TypeKind.BYTE -> ArkoiTokenTypes.byte
                TypeKind.CHAR -> ArkoiTokenTypes.char
                TypeKind.INTEGER -> ArkoiTokenTypes.integer
                TypeKind.LONG -> ArkoiTokenTypes.long
                TypeKind.SHORT -> ArkoiTokenTypes.short
                TypeKind.STRING -> ArkoiTokenTypes.string

                else -> TokenType.BAD_CHARACTER
            }

            else -> TokenType.BAD_CHARACTER
        }
    }

    override fun getState() = 0

    override fun getBufferSequence() = this.buffer!!

    override fun getTokenStart() = this.tokenStartOffset

    override fun getTokenEnd() = this.tokenEndOffset

    override fun getBufferEnd() = this.endOffset

}