/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SemanticASTError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.AnnotationSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types.ExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.AnnotationSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VariableDefinitionSemanticAST extends AbstractSemanticAST<VariableDefinitionSyntaxAST>
{
    
    private List<AnnotationSemanticAST> variableAnnotations;
    
    
    private ExpressionSemanticAST variableExpression;
    
    
    public VariableDefinitionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final VariableDefinitionSyntaxAST variableDefinitionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, variableDefinitionSyntaxAST, ASTType.VARIABLE_DEFINITION);
        
        this.setStart(this.getSyntaxAST().getStart());
        this.setEnd(this.getSyntaxAST().getEnd());
    }
    
    
    public List<AnnotationSemanticAST> getVariableAnnotations() {
        if (this.variableAnnotations == null) {
            this.variableAnnotations = new ArrayList<>();
            
            final HashMap<String, AnnotationSemanticAST> names = new HashMap<>();
            for (final AnnotationSyntaxAST annotationSyntaxAST : this.getSyntaxAST().getVariableAnnotations()) {
                final AnnotationSemanticAST annotationSemanticAST
                        = new AnnotationSemanticAST(this.getSemanticAnalyzer(), this, annotationSyntaxAST);
                
                final IdentifierToken annotationName = annotationSemanticAST.getAnnotationName();
                if (annotationName == null)
                    return null;
                
                if (names.containsKey(annotationName.getTokenContent())) {
                    final AbstractSemanticAST<?> alreadyExistAST = names.get(annotationName.getTokenContent());
                    this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                            this.getSemanticAnalyzer().getArkoiClass(),
                            new AbstractSemanticAST[]{alreadyExistAST},
                            alreadyExistAST.getStart(),
                            alreadyExistAST.getEnd(),
                            "Couldn't analyze this annotation because there already exists another one with the same name."
                    ));
                    return null;
                }
                names.put(annotationName.getTokenContent(), annotationSemanticAST);
                this.variableAnnotations.add(annotationSemanticAST);
            }
        }
        return this.variableAnnotations;
    }
    
    
    public IdentifierToken getVariableName() {
        return this.getSyntaxAST().getVariableName();
    }
    
    
    public ExpressionSemanticAST getVariableExpression() {
        if (this.variableExpression == null) {
            this.variableExpression
                    = new ExpressionSemanticAST(this.getSemanticAnalyzer(), this, this.getSyntaxAST().getVariableExpression());
            if (this.variableExpression.getOperableObject() == null)
                return null;
        }
        return this.variableExpression;
    }
    
}
