/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.ArgumentDefinitionSyntaxAST;
import com.google.gson.annotations.Expose;
import lombok.Setter;

@Setter
public class ArgumentDefinitionSemanticAST extends AbstractSemanticAST<ArgumentDefinitionSyntaxAST>
{
    
    @Expose
    private IdentifierToken argumentName;
    
    @Expose
    private TypeSemanticAST argumentType;
    
    public ArgumentDefinitionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final ArgumentDefinitionSyntaxAST argumentDefinitionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, argumentDefinitionSyntaxAST, ASTType.ARGUMENT_DEFINITION);
    }
    
    public IdentifierToken getArgumentName() {
        if(this.argumentName == null)
            return (this.argumentName = this.getSyntaxAST().getArgumentName());
        return this.argumentName;
    }
    
    public TypeSemanticAST getArgumentType() {
        if(this.argumentType == null)
            return (this.argumentType = new TypeSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), this.getSyntaxAST().getArgumentType()));
        return this.argumentType;
    }
    
}
