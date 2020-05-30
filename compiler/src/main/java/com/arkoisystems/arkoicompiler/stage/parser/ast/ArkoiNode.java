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
package com.arkoisystems.arkoicompiler.stage.parser.ast;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.api.IFailed;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.error.ArkoiError;
import com.arkoisystems.arkoicompiler.error.ErrorPosition;
import com.arkoisystems.arkoicompiler.error.LineRange;
import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.SymbolType;
import com.arkoisystems.arkoicompiler.stage.parser.Parser;
import com.arkoisystems.arkoicompiler.stage.parser.ast.enums.ASTType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.enums.TypeKind;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Objects;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public class ArkoiNode implements IFailed, Cloneable
{
    
    @Printable(name = "type")
    @EqualsAndHashCode.Include
    @NotNull
    private final ASTType astType;
    
    @Nullable
    private LineRange lineRange;
    
    @EqualsAndHashCode.Include
    @Setter
    @Nullable
    private ArkoiToken startToken, endToken;
    
    private boolean failed;
    
    @Setter
    @Nullable
    private Parser parser;
    
    private int startLine;
    
    protected ArkoiNode(
            final @Nullable Parser parser,
            final @NotNull ASTType astType,
            final @Nullable ArkoiToken startToken,
            final @Nullable ArkoiToken endToken
    ) {
        this.parser = parser;
        this.astType = astType;
        
        this.startToken = startToken;
        this.endToken = endToken;
        
        this.startAST(startToken);
        this.endAST(endToken);
    }
    
    @NotNull
    public ArkoiNode parseAST(final ArkoiNode parentAST) {
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
    public ArkoiNode clone() throws CloneNotSupportedException {
        return (ArkoiNode) super.clone();
    }
    
    public void startAST(final @Nullable ArkoiToken token) {
        if (token == null)
            return;
        
        this.startToken = token;
        this.startLine = token.getLineRange().getStartLine();
    }
    
    public void endAST(final @Nullable ArkoiToken token) {
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
    
    public <E> E addError(final @Nullable E errorSource, final @NotNull ArkoiClass compilerClass, final @Nullable ArkoiNode astNode, final @NotNull String message, final @NotNull Object... arguments) {
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
        
        compilerClass.getParser().getErrorHandler().addError(ArkoiError.builder()
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
    
    public <E> E addError(final @Nullable E errorSource, final @NotNull ArkoiClass compilerClass, final @Nullable ArkoiToken arkoiToken, final @NotNull String message, final @NotNull Object... arguments) {
        final LineRange lineRange;
        final int charStart, charEnd;
        if (arkoiToken != null) {
            lineRange = arkoiToken.getLineRange();
            charStart = arkoiToken.getCharStart();
            charEnd = arkoiToken.getCharEnd();
        } else {
            final String[] sourceSplit = compilerClass.getContent().split(System.getProperty("line.separator"));
            lineRange = LineRange.make(compilerClass, sourceSplit.length - 1, sourceSplit.length - 1);
            charStart = sourceSplit[sourceSplit.length - 1].length() - 1;
            charEnd = sourceSplit[sourceSplit.length - 1].length();
        }
        
        compilerClass.getParser().getErrorHandler().addError(ArkoiError.builder()
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
    public final <T extends ArkoiNode> T getValidNode(final @NotNull T... nodes) {
        Objects.requireNonNull(this.getParser(), "parser must not be null");
        
        for (final T node : nodes)
            if (node.canParse(this.getParser(), 0))
                return node;
        return null;
    }
    
}
