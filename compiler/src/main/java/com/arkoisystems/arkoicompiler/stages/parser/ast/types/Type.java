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
package com.arkoisystems.arkoicompiler.stages.parser.ast.types;

import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stages.lexer.token.LexerToken;
import com.arkoisystems.arkoicompiler.stages.lexer.token.enums.SymbolType;
import com.arkoisystems.arkoicompiler.stages.lexer.token.enums.TokenType;
import com.arkoisystems.arkoicompiler.stages.lexer.token.types.TypeToken;
import com.arkoisystems.arkoicompiler.stages.parser.Parser;
import com.arkoisystems.arkoicompiler.stages.parser.ParserErrorType;
import com.arkoisystems.arkoicompiler.stages.parser.ast.ParserNode;
import com.arkoisystems.arkoicompiler.stages.parser.ast.enums.ASTType;
import com.arkoisystems.arkoicompiler.stages.parser.ast.enums.TypeKind;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class Type extends ParserNode
{
    
    public static Type GLOBAL_NODE = new Type(null, null, null, null, false);
    
    @Printable(name = "type")
    @Nullable
    private TypeToken typeToken;
    
    @Printable(name = "array")
    private boolean isArray;
    
    @Builder
    protected Type(
            final @Nullable Parser parser,
            final @Nullable TypeToken typeToken,
            final @Nullable LexerToken startToken,
            final @Nullable LexerToken endToken,
            final boolean isArray
    ) {
        super(parser, ASTType.TYPE, startToken, endToken);
        
        this.typeToken = typeToken;
        this.isArray = isArray;
    }
    
    @NotNull
    @Override
    public Type parseAST(final @Nullable ParserNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        if (this.getParser().matchesCurrentToken(TokenType.TYPE) == null) {
            final LexerToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Type", "<type keyword>", currentToken != null ? currentToken.getTokenContent() : "nothing"
            );
        }
        
        this.startAST(this.getParser().currentToken());
        this.typeToken = (TypeToken) this.getParser().currentToken();
        
        if (this.getParser().matchesPeekToken(1, SymbolType.OPENING_BRACKET) != null && this.getParser().matchesPeekToken(2, SymbolType.CLOSING_BRACKET) != null) {
            this.getParser().nextToken(2);
            this.isArray = true;
        }
        this.endAST(this.getParser().currentToken());
        return this;
    }
    
    @Override
    public boolean canParse(final @NotNull Parser parser, final int offset) {
        return parser.matchesPeekToken(offset, TokenType.TYPE) != null;
    }
    
    @Override
    public void accept(final @NotNull IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @Override
    @NotNull
    public TypeKind getTypeKind() {
        Objects.requireNonNull(this.getTypeToken(), "typeToken must not be null.");
        return this.getTypeToken().getTypeKind();
    }
    
}
