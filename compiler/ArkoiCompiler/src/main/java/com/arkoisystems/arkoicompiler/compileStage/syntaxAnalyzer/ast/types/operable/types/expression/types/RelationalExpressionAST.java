package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.types.operable.types.expressions.RelationalExpressionSemantic;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.AbstractOperableAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionAST;
import com.google.gson.annotations.Expose;
import lombok.Getter;

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
public class RelationalExpressionAST extends AbstractExpressionAST<RelationalExpressionSemantic>
{
    
    @Expose
    private final AbstractOperableAST<?, ?> leftSideAST;
    
    @Expose
    private final RelationalOperator relationalOperator;
    
    @Expose
    private final AbstractOperableAST<?, ?> rightSideAST;
    
    
    public RelationalExpressionAST(final AbstractOperableAST<?, ?> leftSideAST, final RelationalOperator relationalOperator, final AbstractOperableAST<?, ?> rightSideAST) {
        super(ASTType.RELATIONAL_EXPRESSION);
        
        this.relationalOperator = relationalOperator;
        this.rightSideAST = rightSideAST;
        this.leftSideAST = leftSideAST;
        
        this.setStart(leftSideAST.getStart());
        this.setEnd(rightSideAST.getEnd());
    }
    
    @Override
    public RelationalExpressionAST parseAST(final AbstractAST<?> parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return parentAST.addAST(this, syntaxAnalyzer);
    }
    
    @Override
    public <T extends AbstractAST<?>> T addAST(final T toAddAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return toAddAST;
    }
    
    public enum RelationalOperator
    {
        
        LESS_THAN,
        GREATER_THAN,
        LESS_EQUAL_THAN,
        GREATER_EQUAL_THAN,
        IS
        
    }
    
}
