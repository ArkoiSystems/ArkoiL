package com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.ast.types.operable.types.expressions;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.ast.types.operable.types.NumberOperableSemanticAST;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.ast.types.operable.types.StringOperableSemanticAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.types.NumberOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.types.StringOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;
import com.google.gson.annotations.Expose;
import lombok.Getter;
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
@Getter
@Setter
public class ExpressionSemanticAST extends AbstractSemanticAST<ExpressionSyntaxAST>
{
    
    @Expose
    private AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST;
    
    public ExpressionSemanticAST(final AbstractSemanticAST<?> lastContainerAST, final ExpressionSyntaxAST expressionSyntaxAST) {
        super(lastContainerAST, expressionSyntaxAST, ASTType.BASIC_EXPRESSION);
    }
    
    @Override
    public ExpressionSemanticAST analyse(final SemanticAnalyzer semanticAnalyzer) {
        if (this.getSyntaxAST().getAbstractOperableSyntaxAST() instanceof StringOperableSyntaxAST) {
            final StringOperableSyntaxAST stringOperableSyntaxAST = (StringOperableSyntaxAST) this.getSyntaxAST().getAbstractOperableSyntaxAST();
            final StringOperableSemanticAST stringOperableSemanticAST
                    = new StringOperableSemanticAST(this.getLastContainerAST(), stringOperableSyntaxAST).analyse(semanticAnalyzer);
            this.abstractOperableSemanticAST = stringOperableSemanticAST;
            return stringOperableSemanticAST == null ? null : this;
        } else if (this.getSyntaxAST().getAbstractOperableSyntaxAST() instanceof NumberOperableSyntaxAST) {
            final NumberOperableSyntaxAST numberOperableSyntaxAST = (NumberOperableSyntaxAST) this.getSyntaxAST().getAbstractOperableSyntaxAST();
            final NumberOperableSemanticAST numberOperableSemanticAST
                    = new NumberOperableSemanticAST(this.getLastContainerAST(), numberOperableSyntaxAST).analyse(semanticAnalyzer);
            this.abstractOperableSemanticAST = numberOperableSemanticAST;
            return numberOperableSemanticAST == null ? null : this;
        } else {
            semanticAnalyzer.errorHandler().addError(new SyntaxASTError<>(this.getSyntaxAST().getAbstractOperableSyntaxAST(), "Couldn't analyze this expression because the operable isn't supported."));
            return null;
        }
    }
    
}
