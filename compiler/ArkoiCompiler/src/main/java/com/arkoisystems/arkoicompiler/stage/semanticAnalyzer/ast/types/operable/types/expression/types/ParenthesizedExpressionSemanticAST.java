package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.AbstractExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.TypeSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ParenthesizedExpressionSyntaxAST;
import com.google.gson.annotations.Expose;
import lombok.Setter;

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
@Setter
public class ParenthesizedExpressionSemanticAST extends AbstractExpressionSemanticAST<ParenthesizedExpressionSyntaxAST>
{
    
    @Expose
    private AbstractExpressionSemanticAST<?> parenthesizedExpression;
    
    public ParenthesizedExpressionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final ParenthesizedExpressionSyntaxAST parenthesizedExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, parenthesizedExpressionSyntaxAST, ASTType.PARENTHESIZED_EXPRESSION);
    }
    
    @Override
    public TypeSyntaxAST.TypeKind getExpressionType() {
        if(this.getParenthesizedExpression() == null)
            return null;
        return this.getParenthesizedExpression().getExpressionType();
    }
    
    public AbstractExpressionSemanticAST<?> getParenthesizedExpression() {
        if(this.parenthesizedExpression == null) {
            final ExpressionSyntaxAST expressionSyntaxAST = (ExpressionSyntaxAST) this.getSyntaxAST().getParenthesizedExpression();
            this.parenthesizedExpression
                    = new ExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), expressionSyntaxAST);
            if(this.parenthesizedExpression.getExpressionType() == null)
                return null;
        }
        return this.parenthesizedExpression;
    }
    
}
