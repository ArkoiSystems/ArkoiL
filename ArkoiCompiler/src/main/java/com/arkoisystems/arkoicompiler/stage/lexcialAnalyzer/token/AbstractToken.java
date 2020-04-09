/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.ArkoiError;
import com.arkoisystems.arkoicompiler.api.utils.IFailed;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.NumberToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class AbstractToken implements IFailed
{
    
    @Getter
    @Nullable
    private final LexicalAnalyzer lexicalAnalyzer;
    
    
    @EqualsAndHashCode.Include
    @Setter(AccessLevel.PROTECTED)
    @Getter
    @NotNull
    private TokenType tokenType;
    
    
    @Getter
    private boolean failed;
    
    
    @EqualsAndHashCode.Include
    @Setter
    @Getter
    @NotNull
    private String tokenContent;
    
    
    @EqualsAndHashCode.Include
    @Setter(AccessLevel.PROTECTED)
    @Getter
    private int start, end;
    
    
    public AbstractToken(@Nullable final LexicalAnalyzer lexicalAnalyzer, @NotNull final TokenType tokenType) {
        this.lexicalAnalyzer = lexicalAnalyzer;
        this.tokenType = tokenType;
    }
    
    
    @NotNull
    public abstract Optional<? extends AbstractToken> parseToken();
    
    
    @Override
    public void failed() {
        this.failed = true;
    }
    
    
    public void addError(@NotNull final ArkoiClass arkoiClass, final int position, @NotNull final String message, final Object... arguments) {
        Objects.requireNonNull(this.getLexicalAnalyzer());
        
        this.getLexicalAnalyzer().getErrorHandler().addError(ArkoiError.builder()
                .compilerClass(arkoiClass)
                .positions(new int[][] { { position, position + 1 } })
                .message(message)
                .arguments(arguments)
                .build()
        );
        
        this.failed();
    }
    
    
    public void addError(@NotNull final ArkoiClass arkoiClass, final int start, final int end, @NotNull final String message, final Object... arguments) {
        Objects.requireNonNull(this.getLexicalAnalyzer());
        
        this.getLexicalAnalyzer().getErrorHandler().addError(ArkoiError.builder()
                .compilerClass(arkoiClass)
                .positions(new int[][] { { start, end } })
                .message(message)
                .arguments(arguments)
                .build()
        );
        
        this.failed();
    }
    
}
