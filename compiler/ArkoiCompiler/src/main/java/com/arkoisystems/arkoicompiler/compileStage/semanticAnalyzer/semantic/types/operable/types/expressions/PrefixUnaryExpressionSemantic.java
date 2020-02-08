package com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.types.operable.types.expressions;

import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.AbstractSemantic;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.TypeAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.types.expression.types.PrefixUnaryExpressionAST;
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
public class PrefixUnaryExpressionSemantic extends AbstractSemantic<PrefixUnaryExpressionAST>
{
    
    public PrefixUnaryExpressionSemantic(final AbstractSemantic<?> abstractSemantic, final PrefixUnaryExpressionAST prefixUnaryExpressionAST) {
        super(abstractSemantic, prefixUnaryExpressionAST);
    }
    
    @SneakyThrows
    @Override
    public boolean analyse(final SemanticAnalyzer semanticAnalyzer) {
        System.out.println("Prefix Expression Semantic");
        if(this.getAbstractAST().getRightSideAST() instanceof AbstractExpressionAST) {
            if (!semanticAnalyzer.analyseSemanticClass(this.getLastContainerSemantic(), this.getAbstractAST().getRightSideAST()))
                return false;
        }
    
        final TypeAST.TypeKind resultKind;
        switch (this.getAbstractAST().getPrefixUnaryOperator()) {
            case AFFIRM:
                resultKind = this.getAbstractAST().getRightSideAST().prefixAffirm(semanticAnalyzer);
                break;
            case NEGATE:
                resultKind = this.getAbstractAST().getRightSideAST().prefixNegate(semanticAnalyzer);
                break;
            case PREFIX_ADD:
                resultKind = this.getAbstractAST().getRightSideAST().prefixAdd(semanticAnalyzer);
                break;
            case PREFIX_SUB:
                resultKind = this.getAbstractAST().getRightSideAST().prefixSub(semanticAnalyzer);
                break;
            default:
                resultKind = null;
                break;
        }
        
        System.out.println(resultKind);
        return true;
    }
    
}
