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
package com.arkoisystems.compiler.phases.parser.ast;

import com.arkoisystems.compiler.CompilerClass;
import com.arkoisystems.compiler.error.CompilerError;
import com.arkoisystems.compiler.error.ErrorPosition;
import com.arkoisystems.compiler.error.LineRange;
import com.arkoisystems.compiler.phases.lexer.token.LexerToken;
import com.arkoisystems.compiler.phases.parser.Parser;
import com.arkoisystems.compiler.phases.parser.SymbolTable;
import com.arkoisystems.compiler.visitor.IVisitor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public class ParserNode implements Cloneable
{
    
    @EqualsAndHashCode.Include
    @Setter
    @Nullable
    private LexerToken startToken, endToken;
    
    @Setter
    @Nullable
    private SymbolTable currentScope;
    
    @Setter
    @Nullable
    private ParserNode parentNode;
    
    @Nullable
    private LineRange lineRange;
    
    private boolean failed;
    
    @Setter
    @Nullable
    private Parser parser;
    
    private int startLine;
    
    protected ParserNode(
            @Nullable final Parser parser,
            @Nullable final ParserNode parentNode,
            @Nullable final SymbolTable currentScope,
            @Nullable final LexerToken startToken,
            @Nullable final LexerToken endToken
    ) {
        this.currentScope = currentScope;
        this.parentNode = parentNode;
        this.parser = parser;
        
        this.startToken = startToken;
        this.endToken = endToken;
        
        this.startAST(startToken);
        this.endAST(endToken);
    }
    
    @NotNull
    public ParserNode parse() {
        throw new NullPointerException("Not implemented.");
    }
    
    public void accept(@NotNull final IVisitor<?> visitor) {
        throw new NullPointerException("Not implemented.");
    }
    
    public boolean canParse(@NotNull final Parser parser, final int offset) {
        throw new NullPointerException("Not implemented.");
    }
    
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
    
    public void startAST(@Nullable final LexerToken token) {
        if (token == null)
            return;
        
        this.startToken = token;
        this.startLine = token.getLineRange().getStartLine();
    }
    
    public void endAST(@Nullable final LexerToken token) {
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
    
    public <E> E addError(
            @Nullable final E errorSource,
            @NotNull final CompilerClass compilerClass,
            @Nullable final LexerToken lexerToken,
            @NotNull final String causeMessage
    ) {
        final LineRange lineRange;
        final CompilerClass tokenClass;
        final int charStart, charEnd;
        if (lexerToken != null) {
            tokenClass = lexerToken.getLexer().getCompilerClass();
            lineRange = lexerToken.getLineRange();
            charStart = lexerToken.getCharStart();
            charEnd = lexerToken.getCharEnd();
        } else {
            final String[] sourceSplit = compilerClass.getContent().split(System.getProperty("line.separator"));
            tokenClass = compilerClass;
            lineRange = LineRange.make(compilerClass, sourceSplit.length - 1, sourceSplit.length - 1);
            charStart = sourceSplit[sourceSplit.length - 1].length() - 1;
            charEnd = sourceSplit[sourceSplit.length - 1].length();
        }
        
        compilerClass.getCompiler().getErrorHandler().addError(CompilerError.builder()
                .causePosition(ErrorPosition.builder()
                        .sourceCode(tokenClass.getContent())
                        .filePath(tokenClass.getFilePath())
                        .lineRange(lineRange)
                        .charStart(charStart)
                        .charEnd(charEnd)
                        .build())
                .causeMessage(causeMessage)
                .build()
        );
        
        this.setFailed(true);
        return errorSource;
    }
    
    @Nullable
    public <T extends ParserNode> T getParent(@NotNull Class<T> clazz) {
        if (this.getParentNode() == null)
            return null;
        
        if (this.getParentNode().getClass().equals(clazz))
            return (T) this.getParentNode();
        return this.getParentNode().getParent(clazz);
    }
    
    @SafeVarargs
    @Nullable
    public final <T extends ParserNode> T getValidNode(@NotNull final T... nodes) {
        Objects.requireNonNull(this.getParser(), "parser must not be null");
        
        for (final T node : nodes) {
            if (node.canParse(this.getParser(), 0))
                return node;
        }
        
        return null;
    }
    
}
