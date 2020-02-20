/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast;

import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.utils.Variables;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.io.PrintStream;

public abstract class AbstractSemanticAST<T extends AbstractSyntaxAST>
{
    
    @Getter
    private final AbstractSemanticAST<?> lastContainerAST;
    
    
    @Getter
    private final SemanticAnalyzer semanticAnalyzer;
    
    
    @Getter
    private final T syntaxAST;
    
    
    @Getter
    private final ASTType astType;
    
    
    @Getter
    @Setter
    private int start, end;
    
    public AbstractSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final T syntaxAST, final ASTType astType) {
        this.lastContainerAST = lastContainerAST;
        this.semanticAnalyzer = semanticAnalyzer;
        this.syntaxAST = syntaxAST;
        this.astType = astType;
    }
    
    public AbstractSemanticAST<?> initialize() {
        return null;
    }
    
    @Override
    public String toString() {
        return Variables.GSON.toJson(this.getSyntaxAST());
    }
    
}
