/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on June 24, 2020
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
package com.arkoisystems.compiler.phases.parser.ast.types.statement.types;

import com.arkoisystems.compiler.phases.lexer.token.LexerToken;
import com.arkoisystems.compiler.phases.lexer.token.enums.KeywordType;
import com.arkoisystems.compiler.phases.lexer.token.enums.SymbolType;
import com.arkoisystems.compiler.phases.parser.Parser;
import com.arkoisystems.compiler.phases.parser.ParserErrorType;
import com.arkoisystems.compiler.phases.parser.SymbolTable;
import com.arkoisystems.compiler.phases.parser.ast.ParserNode;
import com.arkoisystems.compiler.phases.parser.ast.types.BlockNode;
import com.arkoisystems.compiler.phases.parser.ast.types.TypeNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.OperableNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.ExpressionNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.StatementNode;
import com.arkoisystems.compiler.visitor.IVisitor;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class IfNode extends StatementNode
{
    
    public static IfNode GLOBAL_NODE = new IfNode(null, null, null, null, null, null, null, null);
    
    @Printable(name = "expression")
    @Nullable
    private OperableNode expression;
    
    @Printable(name = "block")
    @Nullable
    private BlockNode block;
    
    @Printable(name = "next branch")
    @Nullable
    private ElseNode nextBranch;
    
    @Builder
    protected IfNode(
            @Nullable final Parser parser,
            @Nullable final ParserNode parentNode,
            @Nullable final SymbolTable currentScope,
            @Nullable final LexerToken startToken,
            @Nullable final OperableNode expression,
            @Nullable final BlockNode block,
            @Nullable final ElseNode nextBranch,
            @Nullable final LexerToken endToken
    ) {
        super(parser, parentNode, currentScope, startToken, endToken);
        
        this.nextBranch = nextBranch;
        this.expression = expression;
        this.block = block;
    }
    
    @Override
    public @NotNull IfNode parse() {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        if (this.getParser().matchesCurrentToken(KeywordType.IF) == null) {
            final LexerToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "If",
                            "'if'",
                            currentToken != null ? currentToken.getTokenContent() : "nothing"
                    )
            );
        }
        
        this.startAST(this.getParser().currentToken());
        
        if (this.getParser().matchesNextToken(SymbolType.OPENING_PARENTHESIS) == null) {
            final LexerToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "If",
                            "'('",
                            currentToken != null ? currentToken.getTokenContent() : "nothing"
                    )
            );
        }
        
        if (!ExpressionNode.GLOBAL_NODE.canParse(this.getParser(), 1)) {
            final LexerToken nextToken = this.getParser().nextToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    nextToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "If",
                            "<expression>",
                            nextToken != null ? nextToken.getTokenContent() : "nothing"
                    )
            );
        }
        
        this.getParser().nextToken();
        
        final OperableNode operableNode = ExpressionNode.expressionBuilder()
                .parser(this.getParser())
                .currentScope(this.getCurrentScope())
                .parentNode(this)
                .build()
                .parse();
        if (operableNode.isFailed()) {
            this.setFailed(true);
            return this;
        }
        
        this.expression = operableNode;
        
        if (this.getParser().matchesNextToken(SymbolType.CLOSING_PARENTHESIS) == null) {
            final LexerToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "If",
                            "')'",
                            currentToken != null ? currentToken.getTokenContent() : "nothing"
                    )
            );
        }
        
        if (!BlockNode.BRACE_NODE.canParse(this.getParser(), 1)) {
            final LexerToken nextToken = this.getParser().nextToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    nextToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "If",
                            "<braced block>",
                            nextToken != null ? nextToken.getTokenContent() : "nothing"
                    )
            );
        }
        
        this.getParser().nextToken();
        
        final BlockNode blockNode = BlockNode.builder()
                .parser(this.getParser())
                .currentScope(this.getCurrentScope())
                .parentNode(this)
                .build()
                .parse();
        if (blockNode.isFailed()) {
            this.setFailed(true);
            return this;
        }
        
        this.block = blockNode;
        
        this.endAST(this.getParser().currentToken());
        
        if (ElseNode.GLOBAL_NODE.canParse(this.getParser(), 1)) {
            this.getParser().nextToken();
            
            final ElseNode elseNode = ElseNode.builder()
                    .parser(this.getParser())
                    .currentScope(this.getCurrentScope())
                    .parentNode(this)
                    .build()
                    .parse();
            if (elseNode.isFailed()) {
                this.setFailed(true);
                return this;
            }
            
            this.nextBranch = elseNode;
        }
        return this;
    }
    
    @Override
    public void accept(@NotNull final IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @Override
    public boolean canParse(@NotNull final Parser parser, final int offset) {
        return parser.matchesPeekToken(offset, KeywordType.IF) != null;
    }
    
    @NotNull
    public TypeNode getTypeNode() {
        Objects.requireNonNull(this.getBlock(), "block must not be null.");
        return this.getBlock().getTypeNode();
    }
    
}
