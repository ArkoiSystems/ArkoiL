package com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.types.operable.types.expressions;

import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.AbstractSemantic;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.TypeAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.types.expression.types.LogicalExpressionAST;
import lombok.SneakyThrows;

/**
 * Copyright © 2019 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on the Sat Nov 09 2019 Author єхcsє#5543 aka timo
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
public class LogicalExpressionSemantic extends AbstractSemantic<LogicalExpressionAST>
{
    
    public LogicalExpressionSemantic(final AbstractSemantic<?> abstractSemantic, final LogicalExpressionAST logicalExpressionAST) {
        super(abstractSemantic, logicalExpressionAST);
    }
    
    @SneakyThrows
    @Override
    public boolean analyse(final SemanticAnalyzer semanticAnalyzer) {
        System.out.println("Logical Expression Semantic");
        if(this.getAbstractAST().getLeftSideAST() instanceof AbstractExpressionAST) {
            if (!semanticAnalyzer.analyseSemanticClass(this.getLastContainerSemantic(), this.getAbstractAST().getLeftSideAST()))
                return false;
        }
        if(this.getAbstractAST().getRightSideAST() instanceof AbstractExpressionAST) {
            if (!semanticAnalyzer.analyseSemanticClass(this.getLastContainerSemantic(), this.getAbstractAST().getRightSideAST()))
                return false;
        }
    
        final TypeAST.TypeKind resultKind;
        switch (this.getAbstractAST().getLogicalOperator()) {
            case LOGICAL_AND:
                resultKind = this.getAbstractAST().getLeftSideAST().logicalAnd(semanticAnalyzer, this.getAbstractAST().getRightSideAST());
                break;
            case LOGICAL_OR:
                resultKind = this.getAbstractAST().getLeftSideAST().logicalOr(semanticAnalyzer, this.getAbstractAST().getRightSideAST());
                break;
            default:
                resultKind = null;
                break;
        }
        
        System.out.println(resultKind);
        return true;
    }
    
}
