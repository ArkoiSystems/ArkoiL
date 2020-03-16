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
import lombok.NonNull;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Objects;

public abstract class AbstractSemanticAST<T extends AbstractSyntaxAST>
{
    
    @Getter
    private final AbstractSemanticAST<?> lastContainerAST;
    
    
    @Getter
    private final HashSet<String> errorList;
    
    
    @Getter
    private final SemanticAnalyzer semanticAnalyzer;
    
    
    @Getter
    private final T syntaxAST;
    
    
    @Getter
    private final ASTType astType;
    
    
    @Getter
    private boolean failed;
    
    
    @Getter
    @Setter
    private int start, end;
    
    
    public AbstractSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final T syntaxAST, final ASTType astType) {
        this.lastContainerAST = lastContainerAST;
        this.semanticAnalyzer = semanticAnalyzer;
        this.syntaxAST = syntaxAST;
        this.astType = astType;
        
        this.errorList = new HashSet<>();
        
        this.start = syntaxAST.getStart();
        this.end = syntaxAST.getEnd();
    }
    
    
    public AbstractSemanticAST<?> initialize() {
        return null;
    }
    
    
    public abstract void printSemanticAST(final PrintStream printStream, final String indents);
    
    
    public void addError(@NonNull final ArkoiClass arkoiClass, @NonNull final AbstractSyntaxAST[] abstractSyntaxASTs, @NonNull final String message, final Object... arguments) {
        if (!this.getErrorList().contains(message)) {
            this.getErrorList().add(message);
            this.getSemanticAnalyzer().getErrorHandler().addError(new ArkoiError(
                    arkoiClass,
                    abstractSyntaxASTs,
                    message,
                    arguments
            ));
        }
        this.failed();
    }
    
    
    public void addError(@NonNull final ArkoiClass arkoiClass, @NonNull final AbstractSyntaxAST abstractSyntaxAST, @NonNull final String message, final Object... arguments) {
        if (!this.getErrorList().contains(message)) {
            this.getErrorList().add(message);
            this.getSemanticAnalyzer().getErrorHandler().addError(new ArkoiError(
                    arkoiClass,
                    abstractSyntaxAST,
                    message,
                    arguments
            ));
        }
        this.failed();
    }
    
    
    public void addError(@NonNull final ArkoiClass arkoiClass, @NonNull final AbstractSemanticAST<?>[] abstractSemanticASTS, @NonNull final String message, final Object... arguments) {
        if (!this.getErrorList().contains(message)) {
            this.getErrorList().add(message);
            this.getSemanticAnalyzer().getErrorHandler().addError(new ArkoiError(
                    arkoiClass,
                    abstractSemanticASTS,
                    message,
                    arguments
            ));
        }
        this.failed();
    }
    
    
    public void addError(@NonNull final ArkoiClass arkoiClass, @NonNull final AbstractSemanticAST<?> abstractSemanticAST, @NonNull final String message, final Object... arguments) {
        if (!this.getErrorList().contains(message)) {
            this.getErrorList().add(message);
            this.getSemanticAnalyzer().getErrorHandler().addError(new ArkoiError(
                    arkoiClass,
                    abstractSemanticAST,
                    message,
                    arguments
            ));
        }
        this.failed();
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
