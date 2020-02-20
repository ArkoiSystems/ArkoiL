/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.NumberToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.NumberOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.Getter;
import lombok.Setter;

import java.io.PrintStream;

public class NumberOperableSemanticAST extends AbstractOperableSemanticAST<NumberOperableSyntaxAST, TypeKind>
{
    
    @Setter
    private TypeKind operableType;
    
    
    public NumberOperableSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final NumberOperableSyntaxAST numberOperableSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, numberOperableSyntaxAST, ASTType.NUMBER_OPERABLE);
    }
    
    
    @Override
    public TypeKind getOperableObject() {
        if (this.operableType == null) {
            final NumberToken numberToken = this.getSyntaxAST().getOperableObject();
            if(numberToken.getTokenContent().contains("."))
                return (this.operableType = TypeKind.FLOAT);
            return (this.operableType = TypeKind.INTEGER);
        } else return this.operableType;
    }
    
}
