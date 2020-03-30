/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.OperatorType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.AnnotationSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.BlockSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.RootSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class VariableDefinitionSyntaxAST extends AbstractStatementSyntaxAST
{
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private List<AnnotationSyntaxAST> variableAnnotations = new ArrayList<>();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private IdentifierToken variableName = IdentifierToken
            .builder()
            .content("Undefined identifier for \"variableName\"")
            .crash()
            .build();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private ExpressionSyntaxAST variableExpression;
    
    
    public VariableDefinitionSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer, @NotNull final List<AnnotationSyntaxAST> variableAnnotations) {
        super(syntaxAnalyzer, ASTType.VARIABLE_DEFINITION);
        
        this.variableAnnotations = variableAnnotations;
    }
    
    
    public VariableDefinitionSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.VARIABLE_DEFINITION);
    }
    
    
    @Override
    public Optional<VariableDefinitionSyntaxAST> parseAST(@NotNull final AbstractSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (!(parentAST instanceof RootSyntaxAST) && !(parentAST instanceof BlockSyntaxAST)) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.VARIABLE_DEFINITION_WRONG_PARENT
            );
            return Optional.empty();
        }
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.VAR) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.VARIABLE_DEFINITION_WRONG_STAR
            );
            return Optional.empty();
        }
    
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        if (this.getSyntaxAnalyzer().matchesNextToken(TokenType.IDENTIFIER) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.VARIABLE_DEFINITION_NO_NAME
            );
            return Optional.empty();
        }
        this.variableName = (IdentifierToken) this.getSyntaxAnalyzer().currentToken();
    
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.EQUALS) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.VARIABLE_DEFINITION_NO_EQUAL_SIGN
            );
            return Optional.empty();
        } else this.getSyntaxAnalyzer().nextToken(2);
        
        if (!AbstractExpressionSyntaxAST.EXPRESSION_PARSER.canParse(this, this.getSyntaxAnalyzer())) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.VARIABLE_DEFINITION_ERROR_DURING_EXPRESSION_PARSING
            );
            return Optional.empty();
        }
        
        final Optional<ExpressionSyntaxAST> optionalExpressionSyntaxAST = AbstractExpressionSyntaxAST.EXPRESSION_PARSER.parse(this, this.getSyntaxAnalyzer());
        if (optionalExpressionSyntaxAST.isEmpty())
            return Optional.empty();
        
        this.getMarkerFactory().addFactory(optionalExpressionSyntaxAST.get().getMarkerFactory());
        this.variableExpression = optionalExpressionSyntaxAST.get();
        
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        return Optional.of(this);
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "├── annotations: " + (this.getVariableAnnotations().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getVariableAnnotations().size(); index++) {
            final AnnotationSyntaxAST annotationSyntaxAST = this.getVariableAnnotations().get(index);
            if (index == this.getVariableAnnotations().size() - 1) {
                printStream.println(indents + "│   └── " + annotationSyntaxAST.getClass().getSimpleName());
                annotationSyntaxAST.printSyntaxAST(printStream, indents + "│       ");
            } else {
                printStream.println(indents + "│   ├── " + annotationSyntaxAST.getClass().getSimpleName());
                annotationSyntaxAST.printSyntaxAST(printStream, indents + "│   │   ");
                printStream.println(indents + "│   │");
            }
        }
        printStream.println(indents + "│");
        printStream.println(indents + "├── name: " + this.getVariableName().getTokenContent());
        printStream.println(indents + "│");
        printStream.println(indents + "└── expression: " + (this.getVariableExpression() == null ? null : ""));
        if (this.getVariableExpression() != null)
            this.getVariableExpression().printSyntaxAST(printStream, indents + "    ");
    }
    
}
