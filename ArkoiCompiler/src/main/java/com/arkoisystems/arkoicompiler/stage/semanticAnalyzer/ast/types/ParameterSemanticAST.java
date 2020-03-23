/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.ParameterSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;

import java.io.PrintStream;

public class ParameterSemanticAST extends AbstractSemanticAST<ParameterSyntaxAST>
{
    
    private TypeSemanticAST parameterType;
    
    public ParameterSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final ParameterSyntaxAST parameterSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, parameterSyntaxAST, ASTType.PARAMETER_DEFINITION);
    }
    
    
    // TODO: Check for null safety.
    @Override
    public void printSemanticAST(final PrintStream printStream, final String indents) {
        printStream.println(indents + "├── name: " + this.getParameterName().getTokenContent());
        printStream.println(indents + "└── type: " + this.getParameterType().getTypeKind().getName() + (this.getParameterType().isArray() ? "[]" : ""));
    }
    
    
    public IdentifierToken getParameterName() {
        return this.getSyntaxAST().getParameterName();
    }
    
    
    public TypeSemanticAST getParameterType() {
        if(this.parameterType == null)
            return (this.parameterType = new TypeSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), this.getSyntaxAST().getParameterType()));
        return this.parameterType;
    }
    
}
