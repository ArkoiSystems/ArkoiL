/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticErrorType;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.AnnotationSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types.ExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.AnnotationSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class VariableDefinitionSemanticAST extends AbstractSemanticAST<VariableDefinitionSyntaxAST>
{
    
    @Nullable
    private List<AnnotationSemanticAST> variableAnnotations;
    
    
    @Nullable
    private ExpressionSemanticAST variableExpression;
    
    
    public VariableDefinitionSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final AbstractSemanticAST<?> lastContainerAST, @NotNull final VariableDefinitionSyntaxAST variableDefinitionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, variableDefinitionSyntaxAST, ASTType.VARIABLE_DEFINITION);
    }
    
    
    // TODO: Check for null safety.
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "├── annotations: " + (this.getVariableAnnotations().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getVariableAnnotations().size(); index++) {
            final AbstractSemanticAST<?> abstractSemanticAST = this.getVariableAnnotations().get(index);
            if (index == this.getVariableAnnotations().size() - 1) {
                printStream.println(indents + "│   └── " + abstractSemanticAST.getClass().getSimpleName());
                abstractSemanticAST.printSemanticAST(printStream, indents + "│       ");
            } else {
                printStream.println(indents + "│   ├── " + abstractSemanticAST.getClass().getSimpleName());
                abstractSemanticAST.printSemanticAST(printStream, indents + "│   │   ");
                printStream.println(indents + "│   │");
            }
        }
        printStream.println(indents + "│");
        printStream.println(indents + "├── name: " + this.getVariableName().getTokenContent());
        printStream.println(indents + "│");
        printStream.println(indents + "└── expression: " + (this.getVariableExpression() == null ? null : ""));
        if (this.getVariableExpression() != null)
            this.getVariableExpression().printSemanticAST(printStream, indents + "    ");
    }
    
    
    public List<AnnotationSemanticAST> getVariableAnnotations() {
        Objects.requireNonNull(this.getSemanticAnalyzer());
    
        if (this.variableAnnotations == null) {
            this.variableAnnotations = new ArrayList<>();
        
            final HashMap<String, AnnotationSemanticAST> names = new HashMap<>();
            for (final AnnotationSyntaxAST annotationSyntaxAST : this.getSyntaxAST().getVariableAnnotations()) {
                final AnnotationSemanticAST annotationSemanticAST
                        = new AnnotationSemanticAST(this.getSemanticAnalyzer(), this, annotationSyntaxAST);
    
                final IdentifierToken annotationName = annotationSemanticAST.getAnnotationName();
                if (annotationName != null) {
                    if (names.containsKey(annotationName.getTokenContent())) {
                        final AbstractSemanticAST<?> alreadyExistAST = names.get(annotationName.getTokenContent());
                        this.addError(
                                this.getSemanticAnalyzer().getArkoiClass(),
                                new AbstractSemanticAST[] {
                                        this,
                                        alreadyExistAST
                                },
                                SemanticErrorType.VARIABLE_ANNOTATION_SAME_NAME
                        );
                        continue;
                    } else
                        names.put(annotationName.getTokenContent(), annotationSemanticAST);
                } else this.failed();
    
                annotationSemanticAST.getAnnotationArguments();
                if (annotationSemanticAST.isFailed())
                    this.failed();
    
                this.variableAnnotations.add(annotationSemanticAST);
            }
        }
        return this.variableAnnotations;
    }
    
    
    @NotNull
    public IdentifierToken getVariableName() {
        return this.getSyntaxAST().getVariableName();
    }
    
    
    @Nullable
    public ExpressionSemanticAST getVariableExpression() {
        if (this.variableExpression == null) {
            if (this.getSyntaxAST().getVariableExpression() == null)
                return null;
    
            this.variableExpression = new ExpressionSemanticAST(this.getSemanticAnalyzer(), this, this.getSyntaxAST().getVariableExpression());
            this.variableExpression.getTypeKind();
    
            if (this.variableExpression.isFailed())
                this.failed();
        }
        return this.variableExpression;
    }
    
}
