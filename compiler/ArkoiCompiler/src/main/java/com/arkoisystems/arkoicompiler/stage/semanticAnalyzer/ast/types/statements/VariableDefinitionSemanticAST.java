package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.doubles.DoubleSyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.AnnotationSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types.ExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.AnnotationSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableDefinitionSyntaxAST;
import com.google.gson.annotations.Expose;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
public class VariableDefinitionSemanticAST extends AbstractSemanticAST<VariableDefinitionSyntaxAST>
{
    
    @Expose
    private List<AnnotationSemanticAST> variableAnnotations;
    
    @Expose
    private ExpressionSemanticAST variableExpression;
    
    public VariableDefinitionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final VariableDefinitionSyntaxAST variableDefinitionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, variableDefinitionSyntaxAST, ASTType.VARIABLE_DEFINITION);
    }
    
    public List<AnnotationSemanticAST> getVariableAnnotations() {
        if (this.variableAnnotations == null) {
            final List<AnnotationSemanticAST> variableAnnotations = new ArrayList<>();
            final HashMap<String, AnnotationSemanticAST> names = new HashMap<>();
            
            for (final AnnotationSyntaxAST annotationSyntaxAST : this.getSyntaxAST().getVariableAnnotations()) {
                final AnnotationSemanticAST annotationSemanticAST
                        = new AnnotationSemanticAST(this.getSemanticAnalyzer(), this, annotationSyntaxAST);
                
                final IdentifierToken annotationName = annotationSemanticAST.getAnnotationName();
                if (annotationName == null)
                    return null;
                
                if (names.containsKey(annotationName.getTokenContent())) {
                    final AbstractSemanticAST<?> abstractSemanticAST = names.get(annotationName.getTokenContent());
                    this.getSemanticAnalyzer().errorHandler().addError(new DoubleSyntaxASTError<>(annotationSemanticAST.getSyntaxAST(), abstractSemanticAST.getSyntaxAST(), "Couldn't analyze this annotation because there already exists another one with the same name."));
                    return null;
                }
                names.put(annotationName.getTokenContent(), annotationSemanticAST);
                variableAnnotations.add(annotationSemanticAST);
            }
            
            return (this.variableAnnotations = variableAnnotations);
        }
        return this.variableAnnotations;
    }
    
    public IdentifierToken getVariableName() {
        return this.getSyntaxAST().getVariableName();
    }
    
    public ExpressionSemanticAST getVariableExpression() {
        if (this.variableExpression == null) {
            final ExpressionSemanticAST expressionSemanticAST
                    = new ExpressionSemanticAST(this.getSemanticAnalyzer(), this, this.getSyntaxAST().getVariableExpression());
            
            if(expressionSemanticAST.getExpressionOperable() == null)
                return null;
            return (this.variableExpression = expressionSemanticAST);
        }
        return this.variableExpression;
    }
    
}
