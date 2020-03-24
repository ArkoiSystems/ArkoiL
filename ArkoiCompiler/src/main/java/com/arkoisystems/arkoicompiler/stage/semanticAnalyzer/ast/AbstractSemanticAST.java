/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.stage.errorHandler.ArkoiError;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractSemanticAST<T extends AbstractSyntaxAST>
{
    
    @Getter
    @NotNull
    private final AbstractSemanticAST<?> lastContainerAST;
    
    
    @Getter
    @NotNull
    private final HashSet<String> errorList = new HashSet<>();
    
    
    @Getter
    @Nullable
    private final SemanticAnalyzer semanticAnalyzer;
    
    
    @Getter
    @NotNull
    private final T syntaxAST;
    
    
    @Getter
    @NotNull
    private final ASTType astType;
    
    
    @Getter
    private boolean failed;
    
    
    @Getter
    @Setter
    private int start, end;
    
    
    public AbstractSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @NotNull final AbstractSemanticAST<?> lastContainerAST, @NotNull final T syntaxAST, @NotNull final ASTType astType) {
        this.lastContainerAST = lastContainerAST;
        this.semanticAnalyzer = semanticAnalyzer;
        this.syntaxAST = syntaxAST;
        this.astType = astType;
        
        this.start = syntaxAST.getStart();
        this.end = syntaxAST.getEnd();
    }
    
    
    @NotNull
    public Optional<AbstractSemanticAST<?>> initialize() {
        return Optional.empty();
    }
    
    
    public abstract void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents);
    
    
    public void addError(@NotNull final ArkoiClass arkoiClass, @NotNull final AbstractSyntaxAST[] abstractSyntaxASTs, @NotNull final String message, final Object... arguments) {
        this.failed();
        
        if (!this.getErrorList().contains(message)) {
            this.getErrorList().add(message);
            Objects.requireNonNull(this.getSemanticAnalyzer()).getErrorHandler().addError(new ArkoiError(
                    arkoiClass,
                    abstractSyntaxASTs,
                    message,
                    arguments
            ));
        }
    }
    
    
    public void addError(@NotNull final ArkoiClass arkoiClass, @NotNull final AbstractSyntaxAST abstractSyntaxAST, @NotNull final String message, final Object... arguments) {
        this.failed();
        
        if (!this.getErrorList().contains(message)) {
            this.getErrorList().add(message);
            Objects.requireNonNull(this.getSemanticAnalyzer()).getErrorHandler().addError(new ArkoiError(
                    arkoiClass,
                    abstractSyntaxAST,
                    message,
                    arguments
            ));
        }
    }
    
    
    public void addError(@NotNull final ArkoiClass arkoiClass, @NotNull final AbstractSemanticAST<?>[] abstractSemanticASTS, @NotNull final String message, final Object... arguments) {
        this.failed();
        
        if (!this.getErrorList().contains(message)) {
            this.getErrorList().add(message);
            Objects.requireNonNull(this.getSemanticAnalyzer()).getErrorHandler().addError(new ArkoiError(
                    arkoiClass,
                    abstractSemanticASTS,
                    message,
                    arguments
            ));
        }
    }
    
    
    public void addError(@NotNull final ArkoiClass arkoiClass, @NotNull final AbstractSemanticAST<?> abstractSemanticAST, @NotNull final String message, final Object... arguments) {
        this.failed();
        
        if (!this.getErrorList().contains(message)) {
            this.getErrorList().add(message);
            Objects.requireNonNull(this.getSemanticAnalyzer()).getErrorHandler().addError(new ArkoiError(
                    arkoiClass,
                    abstractSemanticAST,
                    message,
                    arguments
            ));
        }
    }
    
    
    public void failed() {
        this.failed = true;
    }
    
    
    @Override
    public String toString() {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(byteArrayOutputStream);
        this.printSemanticAST(printStream, "");
        return byteArrayOutputStream.toString();
    }
    
    
    /**
     * Returns a unique hash for this {@link AbstractSemanticAST}. This hash is used when
     * comparing for errors etc.
     *
     * @return a unique hash for this {@link AbstractSemanticAST}.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getLastContainerAST(), this.getSyntaxAST(), this.getAstType(), this.getStart(), this.getEnd());
    }
    
}
