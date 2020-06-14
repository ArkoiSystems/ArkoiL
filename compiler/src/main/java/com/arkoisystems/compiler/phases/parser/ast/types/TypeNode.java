/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
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
package com.arkoisystems.compiler.phases.parser.ast.types;

import com.arkoisystems.compiler.phases.lexer.token.LexerToken;
import com.arkoisystems.compiler.phases.lexer.token.enums.OperatorType;
import com.arkoisystems.compiler.phases.lexer.token.enums.TokenType;
import com.arkoisystems.compiler.phases.lexer.token.types.IdentifierToken;
import com.arkoisystems.compiler.phases.lexer.token.types.TypeToken;
import com.arkoisystems.compiler.phases.parser.Parser;
import com.arkoisystems.compiler.phases.parser.ParserErrorType;
import com.arkoisystems.compiler.phases.parser.SymbolTable;
import com.arkoisystems.compiler.phases.parser.ast.DataKind;
import com.arkoisystems.compiler.phases.parser.ast.ParserNode;
import com.arkoisystems.compiler.visitor.IVisitor;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class TypeNode extends ParserNode
{
    
    public static TypeNode GLOBAL_NODE = new TypeNode(null, null, null, null, null, null, false, 0, 0, null, null);
    
    @Printable(name = "target node")
    @Setter
    @Nullable
    private ParserNode targetNode;
    
    @Printable(name = "target identifier")
    @Nullable
    private IdentifierToken targetIdentifier;
    
    @Printable(name = "data kind")
    @Setter
    @NotNull
    private DataKind dataKind;
    
    @Printable(name = "signed")
    private boolean signed;
    
    @Printable(name = "pointers")
    private int pointers;
    
    @Printable(name = "bits")
    private int bits;
    
    @Builder
    protected TypeNode(
            @Nullable final Parser parser,
            @Nullable final ParserNode parentNode,
            @Nullable final SymbolTable currentScope,
            @Nullable final ParserNode targetNode,
            @Nullable final IdentifierToken targetIdentifier,
            @Nullable final DataKind dataKind,
            final boolean signed,
            final int pointers,
            final int bits,
            @Nullable final LexerToken startToken,
            @Nullable final LexerToken endToken
    ) {
        super(parser, parentNode, currentScope, startToken, endToken);
    
        this.targetIdentifier = targetIdentifier;
        this.targetNode = targetNode;
        this.pointers = pointers;
        this.signed = signed;
        this.bits = bits;
    
        this.dataKind = dataKind == null ? DataKind.UNDEFINED : dataKind;
    }
    
    @NotNull
    @Override
    public TypeNode parse() {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
    
        if (this.getParser().matchesCurrentToken(TokenType.TYPE) == null &&
                this.getParser().matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            final LexerToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Type",
                            "<data type> or <identifier>",
                            currentToken != null ? currentToken.getTokenContent() : "nothing"
                    )
            );
        }
    
        this.startAST(this.getParser().currentToken());
    
        if (this.getParser().matchesCurrentToken(TokenType.TYPE) != null) {
            final TypeToken typeToken = (TypeToken) this.getParser().currentToken();
            this.dataKind = Objects.requireNonNull(typeToken).getDataKind();
            this.signed = typeToken.isSigned();
            this.bits = typeToken.getBits();
        } else {
            this.targetIdentifier = (IdentifierToken) this.getParser().currentToken();
        }
    
        while (this.getParser().getPosition() < this.getParser().getTokens().length) {
            if (this.getParser().matchesPeekToken(1, OperatorType.ASTERISK) == null)
                break;
        
            this.getParser().nextToken();
            this.pointers++;
        }
    
        this.endAST(this.getParser().currentToken());
        return this;
    }
    
    @Override
    public boolean canParse(@NotNull final Parser parser, final int offset) {
        return parser.matchesPeekToken(offset, TokenType.TYPE) != null ||
                parser.matchesPeekToken(offset, TokenType.IDENTIFIER) != null;
    }
    
    @Override
    public void accept(@NotNull final IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (!(other instanceof TypeNode)) return false;
        
        final TypeNode typeNode = (TypeNode) other;
        if (this.getPointers() != typeNode.getPointers()) return false;
        if (this.isSigned() && !typeNode.isSigned()) return false;
        if (this.getTargetNode() != typeNode.getTargetNode()) return false;
        return this.getDataKind() == typeNode.getDataKind();
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.getDataKind().hashCode();
        result = 31 * result + this.getPointers();
        return result;
    }
    
}
