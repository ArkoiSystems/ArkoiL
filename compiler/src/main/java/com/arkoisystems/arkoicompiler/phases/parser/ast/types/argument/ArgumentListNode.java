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
package com.arkoisystems.arkoicompiler.phases.parser.ast.types.argument;

import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.phases.lexer.token.LexerToken;
import com.arkoisystems.arkoicompiler.phases.lexer.token.enums.SymbolType;
import com.arkoisystems.arkoicompiler.phases.parser.Parser;
import com.arkoisystems.arkoicompiler.phases.parser.ParserErrorType;
import com.arkoisystems.arkoicompiler.phases.parser.ast.ParserNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.enums.NodeType;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class ArgumentListNode extends ParserNode
{
    
    public static ArgumentListNode GLOBAL_NODE = new ArgumentListNode(null, null, null);
    
    @Printable(name = "arguments")
    @NotNull
    private final List<ArgumentNode> arguments;
    
    @Builder
    protected ArgumentListNode(
            final @Nullable Parser parser,
            final @Nullable LexerToken startToken,
            final @Nullable LexerToken endToken
    ) {
        super(parser, startToken, endToken);
        
        this.arguments = new ArrayList<>();
    }
    
    @NotNull
    @Override
    public ArgumentListNode parseAST(final @NotNull ParserNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        if (this.getParser().matchesCurrentToken(SymbolType.OPENING_BRACKET) == null) {
            final LexerToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Argument list", "'['", currentToken != null ? currentToken.getTokenContent() : "nothing"
            );
        }
        
        this.startAST(this.getParser().currentToken());
        this.getParser().nextToken();
        
        while (this.getParser().getPosition() < this.getParser().getTokens().length) {
            if (!ArgumentNode.GLOBAL_NODE.canParse(this.getParser(), 0))
                break;
            
            final ArgumentNode argumentNode = ArgumentNode.builder()
                    .parser(this.getParser())
                    .build()
                    .parseAST(parentAST);
            if (argumentNode.isFailed()) {
                this.setFailed(true);
                return this;
            }
            
            this.getArguments().add(argumentNode);
            
            if (this.getParser().matchesNextToken(SymbolType.COMMA) == null)
                break;
            else this.getParser().nextToken();
        }
        
        if (this.getParser().matchesCurrentToken(SymbolType.CLOSING_BRACKET) == null) {
            final LexerToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Argument list", "']'", currentToken != null ? currentToken.getTokenContent() : "nothing"
            );
        }
        
        this.endAST(this.getParser().currentToken());
        return this;
    }
    
    @Override
    public boolean canParse(final @NotNull Parser parser, final int offset) {
        return parser.matchesPeekToken(offset, SymbolType.OPENING_BRACKET) != null;
    }
    
    @Override
    public void accept(final @NotNull IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @Override
    @NotNull
    public NodeType getTypeKind() {
        return NodeType.UNDEFINED;
    }
    
}
