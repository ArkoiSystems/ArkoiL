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
package com.arkoisystems.arkoicompiler.phases.parser.ast;

import com.arkoisystems.arkoicompiler.CompilerClass;
import com.arkoisystems.arkoicompiler.api.IFailed;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.errorHandling.CompilerError;
import com.arkoisystems.arkoicompiler.errorHandling.ErrorPosition;
import com.arkoisystems.arkoicompiler.errorHandling.LineRange;
import com.arkoisystems.arkoicompiler.phases.lexer.token.LexerToken;
import com.arkoisystems.arkoicompiler.phases.lexer.token.enums.SymbolType;
import com.arkoisystems.arkoicompiler.phases.parser.Parser;
import com.arkoisystems.arkoicompiler.phases.parser.SymbolTable;
import com.arkoisystems.arkoicompiler.phases.parser.ast.enums.TypeKind;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Objects;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public class ParserNode implements IFailed, Cloneable
{
    
    @EqualsAndHashCode.Include
    @Setter
    @Nullable
    private LexerToken startToken, endToken;
    
    @Setter
    @Nullable
    private SymbolTable currentScope;
    
    @Nullable
    private LineRange lineRange;
    
    private boolean failed;
    
    @Setter
    @Nullable
    private Parser parser;
    
    private int startLine;
    
    protected ParserNode(
            final @Nullable Parser parser,
            final @Nullable SymbolTable currentScope,
            final @Nullable LexerToken startToken,
            final @Nullable LexerToken endToken
    ) {
        this.currentScope = currentScope;
        this.parser = parser;
        
        this.startToken = startToken;
        this.endToken = endToken;
        
        this.startAST(startToken);
        this.endAST(endToken);
    }
    
    @NotNull
    public ParserNode parseAST(final ParserNode parentAST) {
        throw new NullPointerException("Not implemented.");
    }
    
    public void accept(final @NotNull IVisitor<?> visitor) {
        throw new NullPointerException("Not implemented.");
    }
    
    public boolean canParse(final @NotNull Parser parser, final int offset) {
        throw new NullPointerException("Not implemented.");
    }
    
    @NotNull
    public TypeKind getTypeKind() {
        throw new NullPointerException("Not implemented.");
    }
    
    @Override
    public void setFailed(final boolean failed) {
        if (!failed) {
            this.failed = false;
            return;
        }
        
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        this.endAST(this.getParser().currentToken());
        this.failed = true;
    }
    
    @Override
    public ParserNode clone() throws CloneNotSupportedException {
        return (ParserNode) super.clone();
    }
    
    public void startAST(final @Nullable LexerToken token) {
        if (token == null)
            return;
        
        this.startToken = token;
        this.startLine = token.getLineRange().getStartLine();
    }
    
    public void endAST(final @Nullable LexerToken token) {
        if (token == null)
            return;
        
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        this.endToken = token;
        this.lineRange = LineRange.make(
                this.getParser().getCompilerClass(),
                this.startLine,
                token.getLineRange().getEndLine()
        );
    }
    
    public <E> E addError(final @Nullable E errorSource, final @NotNull CompilerClass compilerClass, final @Nullable ParserNode astNode, final @NotNull String message, final @NotNull Object... arguments) {
        final LineRange lineRange;
        final int charStart, charEnd;
        if (astNode != null) {
            lineRange = Objects.requireNonNull(astNode.getLineRange(), "astNode.lineRange must not be null.");
            charStart = Objects.requireNonNull(astNode.getStartToken(), "astNode.startToken must not be null.").getCharStart();
            charEnd = Objects.requireNonNull(astNode.getStartToken(), "astNode.startToken must not be null.").getCharEnd();
        } else {
            final String[] sourceSplit = compilerClass.getContent().split(System.getProperty("line.separator"));
            lineRange = LineRange.make(compilerClass, sourceSplit.length - 1, sourceSplit.length - 1);
            charStart = sourceSplit[sourceSplit.length - 1].length() - 1;
            charEnd = sourceSplit[sourceSplit.length - 1].length();
        }
    
        compilerClass.getCompiler().getErrorHandler().addError(CompilerError.builder()
                .compilerClass(compilerClass)
                .positions(Collections.singletonList(ErrorPosition.builder()
                        .lineRange(lineRange)
                        .charStart(charStart)
                        .charEnd(charEnd)
                        .build()))
                .message(message)
                .arguments(arguments)
                .build()
        );
        
        this.setFailed(true);
        return errorSource;
    }
    
    public <E> E addError(final @Nullable E errorSource, final @NotNull CompilerClass compilerClass, final @Nullable LexerToken lexerToken, final @NotNull String message, final @NotNull Object... arguments) {
        final LineRange lineRange;
        final int charStart, charEnd;
        if (lexerToken != null) {
            lineRange = lexerToken.getLineRange();
            charStart = lexerToken.getCharStart();
            charEnd = lexerToken.getCharEnd();
        } else {
            final String[] sourceSplit = compilerClass.getContent().split(System.getProperty("line.separator"));
            lineRange = LineRange.make(compilerClass, sourceSplit.length - 1, sourceSplit.length - 1);
            charStart = sourceSplit[sourceSplit.length - 1].length() - 1;
            charEnd = sourceSplit[sourceSplit.length - 1].length();
        }
    
        compilerClass.getCompiler().getErrorHandler().addError(CompilerError.builder()
                .compilerClass(compilerClass)
                .positions(Collections.singletonList(ErrorPosition.builder()
                        .lineRange(lineRange)
                        .charStart(charStart)
                        .charEnd(charEnd)
                        .build()))
                .message(message)
                .arguments(arguments)
                .build()
        );
        
        this.setFailed(true);
        return errorSource;
    }
    
    protected void skipToNextValidToken() {
        this.setFailed(true);
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        int openBraces = 0;
        while (this.getParser().getPosition() < this.getParser().getTokens().length) {
            if (this.getParser().matchesCurrentToken(SymbolType.OPENING_BRACE) != null)
                openBraces++;
            else if (this.getParser().matchesCurrentToken(SymbolType.CLOSING_BRACE) != null) {
                openBraces--;
                if (openBraces <= 0) {
                    this.getParser().nextToken();
                    break;
                }
            }
            this.getParser().nextToken();
        }
    }
    
    @SafeVarargs
    @Nullable
    public final <T extends ParserNode> T getValidNode(final @NotNull T... nodes) {
        Objects.requireNonNull(this.getParser(), "parser must not be null");
        
        for (final T node : nodes)
            if (node.canParse(this.getParser(), 0))
                return node;
        return null;
    }
    
}
