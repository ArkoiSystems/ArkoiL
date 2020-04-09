/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on April 08, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements;

import com.arkoisystems.arkoicompiler.api.ICompilerSemanticAST;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticErrorType;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.ArkoiSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.AnnotationSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.ExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class VariableSemanticAST extends ArkoiSemanticAST<VariableSyntaxAST>
{
    
    @Getter
    @NotNull
    private final List<AnnotationSemanticAST> variableAnnotations = this.checkVariableAnnotations();
    
    
    @Getter
    @NotNull
    private final IdentifierToken variableName = this.checkVariableName();
    
    
    @Getter
    @NotNull
    private final ExpressionSemanticAST<?> variableExpression = this.checkVariableExpression();
    
    
    public VariableSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final ICompilerSemanticAST<?> lastContainerAST, @NotNull final VariableSyntaxAST syntaxAST) {
        super(semanticAnalyzer, lastContainerAST, syntaxAST, ASTType.VARIABLE);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.printf("%s├── annotations: %s%n", indents, this.getVariableAnnotations().isEmpty() ? "N/A" : "");
        for (int index = 0; index < this.getVariableAnnotations().size(); index++) {
            final ArkoiSemanticAST<?> arkoiSemanticAST = this.getVariableAnnotations().get(index);
            if (index == this.getVariableAnnotations().size() - 1) {
                printStream.printf("%s│   └── %s%n", indents, arkoiSemanticAST.getClass().getSimpleName());
                arkoiSemanticAST.printSemanticAST(printStream, indents + "│       ");
            } else {
                printStream.printf("%s│   ├── %s%n", indents, arkoiSemanticAST.getClass().getSimpleName());
                arkoiSemanticAST.printSemanticAST(printStream, indents + "│   │   ");
                printStream.printf("%s│   │%n", indents);
            }
        }
        printStream.printf("%s│%n", indents);
        printStream.printf("%s├── name: %s%n", indents, this.getVariableName().getTokenContent());
        printStream.printf("%s│%n", indents);
        printStream.printf("%s└── expression:%n", indents);
        this.getVariableExpression().printSemanticAST(printStream, indents + "    ");
    }
    
    
    @NotNull
    private List<AnnotationSemanticAST> checkVariableAnnotations() {
        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null."));
        Objects.requireNonNull(this.getSemanticAnalyzer().getArkoiClass(), this.getFailedSupplier("semanticAnalyzer.arkoiClass must not be null."));
        
        final List<AnnotationSemanticAST> variableAnnotations = new ArrayList<>();
        final HashMap<String, AnnotationSemanticAST> names = new HashMap<>();
        
        for (int index = 0; index < this.getSyntaxAST().getVariableAnnotations().size(); index++) {
            final AnnotationSemanticAST annotationSemanticAST = new AnnotationSemanticAST(
                    this.getSemanticAnalyzer(),
                    this,
                    this.getSyntaxAST().getVariableAnnotations().get(index)
            );
            
            final IdentifierToken annotationName = annotationSemanticAST.getAnnotationName();
            if (names.containsKey(annotationName.getTokenContent())) {
                this.addError(
                        annotationName,
                        this.getSemanticAnalyzer().getArkoiClass(),
                        new ArkoiSemanticAST[] {
                                this,
                                names.get(annotationName.getTokenContent())
                        },
                        SemanticErrorType.VARIABLE_ANNOTATION_SAME_NAME
                );
            } else names.put(annotationName.getTokenContent(), annotationSemanticAST);
            
            if (annotationSemanticAST.isFailed())
                this.failed();
            variableAnnotations.add(annotationSemanticAST);
        }
        return variableAnnotations;
    }
    
    
    @NotNull
    private IdentifierToken checkVariableName() {
        Objects.requireNonNull(this.getSyntaxAST().getVariableName(), this.getFailedSupplier("syntaxAST.variableName must not be null."));
        return this.getSyntaxAST().getVariableName();
    }
    
    
    @NotNull
    private ExpressionSemanticAST<?> checkVariableExpression() {
        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null."));
        Objects.requireNonNull(this.getSemanticAnalyzer().getArkoiClass(), this.getFailedSupplier("semanticAnalyzer.arkoiClass must not be null."));
        Objects.requireNonNull(this.getSyntaxAST().getVariableExpression(), this.getFailedSupplier("syntaxAST.variableExpression must not be null."));
        
        final ExpressionSemanticAST<?> expressionSemanticAST = new ExpressionSemanticAST<>(
                this.getSemanticAnalyzer(),
                this.getLastContainerAST(),
                this.getSyntaxAST().getVariableExpression()
        );
        
        if (expressionSemanticAST.isFailed())
            this.failed();
        return expressionSemanticAST;
    }
    
}
