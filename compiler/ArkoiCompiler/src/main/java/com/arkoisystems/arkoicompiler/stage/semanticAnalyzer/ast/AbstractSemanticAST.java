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
    @Setter
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
    
    
    public void addError(final ArkoiClass arkoiClass, final AbstractSyntaxAST[] abstractSyntaxASTs, final String message, final Object... arguments) {
        if (!this.getErrorList().contains(message)) {
            this.getErrorList().add(message);
            this.getSemanticAnalyzer().getErrorHandler().addError(new ArkoiError(
                    arkoiClass,
                    abstractSyntaxASTs,
                    message,
                    arguments
            ));
        }
        this.setFailed(true);
    }
    
    
    public void addError(final ArkoiClass arkoiClass, final AbstractSyntaxAST abstractSyntaxAST, final String message, final Object... arguments) {
        if (!this.getErrorList().contains(message)) {
            this.getErrorList().add(message);
            this.getSemanticAnalyzer().getErrorHandler().addError(new ArkoiError(
                    arkoiClass,
                    abstractSyntaxAST,
                    message,
                    arguments
            ));
        }
        this.setFailed(true);
    }
    
    
    public void addError(final ArkoiClass arkoiClass, final AbstractSemanticAST<?>[] abstractSemanticASTS, final String message, final Object... arguments) {
        if (!this.getErrorList().contains(message)) {
            this.getErrorList().add(message);
            this.getSemanticAnalyzer().getErrorHandler().addError(new ArkoiError(
                    arkoiClass,
                    abstractSemanticASTS,
                    message,
                    arguments
            ));
        }
        this.setFailed(true);
    }
    
    
    public void addError(final ArkoiClass arkoiClass, final AbstractSemanticAST<?> abstractSemanticAST, final String message, final Object... arguments) {
        if (!this.getErrorList().contains(message)) {
            this.getErrorList().add(message);
            this.getSemanticAnalyzer().getErrorHandler().addError(new ArkoiError(
                    arkoiClass,
                    abstractSemanticAST,
                    message,
                    arguments
            ));
        }
        this.setFailed(true);
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
