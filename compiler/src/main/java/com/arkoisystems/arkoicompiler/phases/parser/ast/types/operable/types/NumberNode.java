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
package com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.phases.lexer.token.LexerToken;
import com.arkoisystems.arkoicompiler.phases.lexer.token.enums.TokenType;
import com.arkoisystems.arkoicompiler.phases.lexer.token.types.NumberToken;
import com.arkoisystems.arkoicompiler.phases.parser.Parser;
import com.arkoisystems.arkoicompiler.phases.parser.ParserErrorType;
import com.arkoisystems.arkoicompiler.phases.parser.ast.ParserNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.enums.NodeType;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.OperableNode;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class NumberNode extends OperableNode
{
    
    public static NumberNode GLOBAL_NODE = new NumberNode(null, null, null, null);
    
    @Printable(name = "number")
    @Nullable
    private NumberToken numberToken;
    
    @Builder
    protected NumberNode(
            final @Nullable Parser parser,
            final @Nullable NumberToken numberToken,
            final @Nullable LexerToken startToken,
            final @Nullable LexerToken endToken
    ) {
        super(parser, startToken, endToken);
        
        this.numberToken = numberToken;
    }
    
    @NotNull
    @Override
    public NumberNode parseAST(final @NotNull ParserNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        if (this.getParser().matchesCurrentToken(TokenType.NUMBER) == null) {
            final LexerToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Number", "<number>", currentToken != null ? currentToken.getTokenContent() : "nothing"
            );
        }
        
        this.startAST(this.getParser().currentToken());
        this.numberToken = (NumberToken) this.getParser().currentToken();
        this.endAST(this.getParser().currentToken());
        return this;
    }
    
    @Override
    public boolean canParse(final @NotNull Parser parser, final int offset) {
        return parser.matchesPeekToken(offset, TokenType.NUMBER) != null;
    }
    
    @Override
    public void accept(final @NotNull IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @Override
    @NotNull
    public NodeType getTypeKind() {
        Objects.requireNonNull(this.getNumberToken(), "numberToken must not be null.");
        return this.getNumberToken().getNodeType();
    }
    
}
