/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on June 13, 2020
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
import com.arkoisystems.compiler.phases.lexer.token.enums.KeywordType;
import com.arkoisystems.compiler.phases.lexer.token.enums.SymbolType;
import com.arkoisystems.compiler.phases.lexer.token.enums.TokenType;
import com.arkoisystems.compiler.phases.lexer.token.types.IdentifierToken;
import com.arkoisystems.compiler.phases.parser.Parser;
import com.arkoisystems.compiler.phases.parser.ParserErrorType;
import com.arkoisystems.compiler.phases.parser.SymbolTable;
import com.arkoisystems.compiler.phases.parser.ast.DataKind;
import com.arkoisystems.compiler.phases.parser.ast.ParserNode;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Getter;
import lombok.Setter;
import org.bytedeco.llvm.LLVM.LLVMTypeRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class StructNode extends ParserNode
{
    
    public static StructNode GLOBAL_NODE = new StructNode(null, null, null, null, false, null, null, null);
    
    @Printable(name = "struct")
    @Setter
    @Nullable
    private LLVMTypeRef structRef;
    
    @Printable(name = "built in")
    private boolean builtin;
    
    @Printable(name = "name")
    @Nullable
    private IdentifierToken name;
    
    protected StructNode(
            @Nullable final Parser parser,
            @Nullable final ParserNode parentNode,
            @Nullable final SymbolTable currentScope,
            @Nullable final LLVMTypeRef structRef,
            final boolean builtin,
            @Nullable final IdentifierToken name,
            @Nullable final LexerToken startToken,
            @Nullable final LexerToken endToken
    ) {
        super(parser, parentNode, currentScope, startToken, endToken);
        
        this.structRef = structRef;
        this.builtin = builtin;
        this.name = name;
    }
    
    @Override
    public @NotNull StructNode parse() {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        if (this.getParser().matchesCurrentToken(KeywordType.STRUCT) == null) {
            final LexerToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Struct",
                            "'struct'",
                            currentToken != null ? currentToken.getTokenContent() : "nothing"
                    )
            );
        }
        
        this.startAST(this.getParser().currentToken());
        
        if (this.getParser().matchesPeekToken(1, SymbolType.AT_SIGN) != null) {
            this.getParser().nextToken();
            this.builtin = true;
        }
        
        if (this.getParser().matchesPeekToken(1, TokenType.IDENTIFIER) == null) {
            final LexerToken nextToken = this.getParser().nextToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    nextToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Struct",
                            "<identifier>",
                            nextToken != null ? nextToken.getTokenContent() : "nothing"
                    )
            );
        }
        
        final IdentifierToken identifierToken = (IdentifierToken) this.getParser().nextToken();
        if (identifierToken == null)
            throw new NullPointerException();
        
        this.name = identifierToken;
        
        Objects.requireNonNull(this.getCurrentScope(), "currentScope must not be null.");
        this.getCurrentScope().insert(identifierToken.getTokenContent(), this);
        this.setCurrentScope(new SymbolTable(this.getCurrentScope()));
        
        this.endAST(this.getParser().currentToken());
        return this;
    }
    
    @Override
    public boolean canParse(final @NotNull Parser parser, final int offset) {
        return parser.matchesPeekToken(offset, KeywordType.STRUCT) != null;
    }
    
    @NotNull
    public TypeNode getTypeNode() {
        return TypeNode.builder()
                .parentNode(this)
                .currentScope(this.getCurrentScope())
                .parser(this.getParser())
                .dataKind(DataKind.STRUCT)
                .startToken(this.getStartToken())
                .endToken(this.getEndToken())
                .build();
    }
    
}
