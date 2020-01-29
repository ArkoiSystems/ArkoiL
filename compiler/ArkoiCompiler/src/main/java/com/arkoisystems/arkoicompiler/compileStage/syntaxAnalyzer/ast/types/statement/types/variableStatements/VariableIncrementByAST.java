package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.variableStatements;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ASTError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SeparatorToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.operators.types.AssignmentOperatorToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.expressions.AbstractExpressionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.VariableStatementAST;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

/**
 * Copyright © 2019 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on the Sat Nov 09 2019 Author єхcsє#5543 aka Timo
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * <p>
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
@Getter
@Setter
public class VariableIncrementByAST extends VariableStatementAST
{
    
    @Expose
    private final VariableDefinitionAST variableDefinitionAST;
    
    @Expose
    private AbstractExpressionAST abstractExpressionAST;
    
    private int expressionPosition;
    
    public VariableIncrementByAST(final VariableDefinitionAST variableDefinitionAST) {
        super(ASTType.VARIABLE_INCREMENT_BY);
        
        this.variableDefinitionAST = variableDefinitionAST;
    }
    
    @Override
    public VariableIncrementByAST parseAST(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (this.variableDefinitionAST == null) {
            syntaxAnalyzer.errorHandler().addError(new ASTError(this, "Couldn't parse the increment statement because the given variable is null."));
            return null;
        } else this.setStart(this.variableDefinitionAST.getStart());
        
        if (syntaxAnalyzer.matchesNextToken(AssignmentOperatorToken.AssignmentOperatorType.ADDITION_ASSIGNMENT) == null) {
            syntaxAnalyzer.errorHandler().addError(new ASTError(this, "Couldn't parse the increment statement because the next token isn't an \"increment by value\" operator aka. \"+=\"."));
            return null;
        } else syntaxAnalyzer.nextToken();
        
        this.expressionPosition = syntaxAnalyzer.getPosition();
        if (!syntaxAnalyzer.findSeparator(SeparatorToken.SeparatorType.SEMICOLON)) {
            syntaxAnalyzer.errorHandler().addError(new ASTError(this, "Couldn't parse the increment statement because it doesn't end with a semicolon."));
            return null;
        }
        
        this.setEnd(syntaxAnalyzer.currentToken().getEnd());
        return parentAST.addAST(this, syntaxAnalyzer);
    }
    
    @Override
    public <T extends AbstractAST> T addAST(final T toAddAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (toAddAST instanceof AbstractExpressionAST) {
            if (this.abstractExpressionAST != null) {
                syntaxAnalyzer.errorHandler().addError(new ASTError(toAddAST, "Couldn't parse the variable increment because there was an attempt to add more then one expressions to the statement."));
                return null;
            } else this.abstractExpressionAST = (AbstractExpressionAST) toAddAST;
        }
        return super.addAST(toAddAST, syntaxAnalyzer);
    }
    
    @Override
    public boolean initialize(final SyntaxAnalyzer syntaxAnalyzer) {
        final int lastPosition = syntaxAnalyzer.getPosition();
        syntaxAnalyzer.setPosition(this.expressionPosition);
        
        final AbstractExpressionAST abstractExpressionAST = new AbstractExpressionAST().parseAST(this, syntaxAnalyzer);
        if (abstractExpressionAST == null) {
            syntaxAnalyzer.errorHandler().addError(new ASTError(this, "Couldn't parse the increment statement because an error occured during parsing the expression. Please check the stacktrace."));
            return false;
        } else this.abstractExpressionAST = abstractExpressionAST;
        
        syntaxAnalyzer.setPosition(lastPosition);
        return true;
    }
    
}
