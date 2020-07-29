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
package com.arkoisystems.compiler.phases.parser.ast.types.argument;

import com.arkoisystems.compiler.phases.lexer.token.LexerToken;
import com.arkoisystems.compiler.phases.lexer.token.enums.SymbolType;
import com.arkoisystems.compiler.phases.parser.Parser;
import com.arkoisystems.compiler.phases.parser.ParserErrorType;
import com.arkoisystems.compiler.phases.parser.SymbolTable;
import com.arkoisystems.compiler.phases.parser.ast.ParserNode;
import com.arkoisystems.compiler.visitor.IVisitor;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class ArgumentListNode extends ParserNode
{
    
    public static ArgumentListNode ALL_NAMED_NODE = new ArgumentListNode(null, null, null, null, true, null);
    
    public static ArgumentListNode MIXED_NAMED_NODE = new ArgumentListNode(null, null, null, null, false, null);
    
    private final boolean allNamed;
    
    @Printable(name = "arguments")
    @NotNull
    private final List<ArgumentNode> arguments;
    
    @Builder
    protected ArgumentListNode(
            @Nullable final Parser parser,
            @Nullable final ParserNode parentNode,
            @Nullable final SymbolTable currentScope,
            @Nullable final LexerToken startToken,
            final boolean allNamed,
            @Nullable final LexerToken endToken
    ) {
        super(parser, parentNode, currentScope, startToken, endToken);
        
        this.allNamed = allNamed;
        this.arguments = new ArrayList<>();
    }
    
    @SneakyThrows
    @NotNull
    @Override
    public ArgumentListNode parse() {
        Objects.requireNonNull(this.getParser());
    
        this.startAST(this.getParser().currentToken());
    
        boolean mustBeNamed = this.isAllNamed();
        LexerToken mixedUp = null;
        while (this.getParser().getPosition() < this.getParser().getTokens().length) {
            final ArgumentNode argumentNode;
            if (ArgumentNode.NAMED_NODE.canParse(this.getParser(), 0)) {
                argumentNode = ArgumentNode.builder()
                        .parentNode(this)
                        .currentScope(this.getCurrentScope())
                        .parser(this.getParser())
                        .named(true)
                        .build()
                        .parse();
                mustBeNamed = true;
            } else if (ArgumentNode.UNNAMED_NODE.canParse(this.getParser(), 0)) {
                if (mustBeNamed)
                    mixedUp = this.getParser().currentToken();
            
                argumentNode = ArgumentNode.builder()
                        .parentNode(this)
                        .currentScope(this.getCurrentScope())
                        .parser(this.getParser())
                        .named(false)
                        .build()
                        .parse();
            } else break;
        
            if (argumentNode.isFailed()) {
                this.setFailed(true);
                return this;
            }
        
            this.getArguments().add(argumentNode);
        
            if (this.getParser().matchesNextToken(SymbolType.COMMA) == null)
                break;
            this.getParser().nextToken();
        }
    
        if (mixedUp != null) {
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    mixedUp,
                    this.isAllNamed() ? String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Argument list",
                            "<named argument>",
                            mixedUp.getTokenContent()
                    ) : "You can't mix up unnamed and named arguments."
            );
        }
    
        this.endAST(this.getParser().currentToken());
        return this;
    }
    
    @Override
    public boolean canParse(@NotNull final Parser parser, final int offset) {
        return this.isAllNamed() ? ArgumentNode.NAMED_NODE.canParse(parser, offset) :
                ArgumentNode.NAMED_NODE.canParse(parser, offset) || ArgumentNode.UNNAMED_NODE.canParse(parser, offset);
    }
    
    @Override
    public void accept(@NotNull final IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
}
