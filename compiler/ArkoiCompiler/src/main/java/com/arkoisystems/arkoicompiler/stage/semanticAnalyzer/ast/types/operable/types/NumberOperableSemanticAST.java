/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.numbers.AbstractNumberToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.NumberOperableSyntaxAST;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NumberOperableSemanticAST extends AbstractOperableSemanticAST<NumberOperableSyntaxAST, AbstractNumberToken>
{
    
    public NumberOperableSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final NumberOperableSyntaxAST numberOperableSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, numberOperableSyntaxAST, ASTType.NUMBER_OPERABLE);
    }
    
    @Override
    public AbstractNumberToken getExpressionType() {
        if(this.getSyntaxAST().getOperableObject() == null) {
            this.getSemanticAnalyzer().errorHandler().addError(new SyntaxASTError<>(this.getSyntaxAST(), "Couldn't analyze this number operable because the content is null."));
            return null;
        }
        return this.getSyntaxAST().getOperableObject();
    }
    
    public AbstractNumberToken.NumberType getNumberType() {
        return this.getSyntaxAST().getOperableObject().getNumberType();
    }
    
}
