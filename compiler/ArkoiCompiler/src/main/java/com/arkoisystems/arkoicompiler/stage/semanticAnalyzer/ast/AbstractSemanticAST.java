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

@Getter
@Setter
public abstract class AbstractSemanticAST<T extends AbstractSyntaxAST>
{
    
    private final AbstractSemanticAST<?> lastContainerAST;
    
    private final SemanticAnalyzer semanticAnalyzer;
    
    private final T syntaxAST;
    
    @Expose
    private final ASTType astType;
    
    @Expose
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
