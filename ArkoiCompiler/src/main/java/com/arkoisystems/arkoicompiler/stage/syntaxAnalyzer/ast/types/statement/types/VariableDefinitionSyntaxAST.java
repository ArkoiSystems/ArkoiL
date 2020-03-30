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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
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
    private AbstractOperableSyntaxAST<?> variableExpression;
    
    
    public VariableDefinitionSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer, @NotNull final List<AnnotationSyntaxAST> variableAnnotations) {
        super(syntaxAnalyzer, ASTType.VARIABLE_DEFINITION);
        
        this.variableAnnotations = variableAnnotations;
    }
    
    
    public VariableDefinitionSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.VARIABLE_DEFINITION);
    }
    
    
    @NotNull
    @Override
    public VariableDefinitionSyntaxAST parseAST(@NotNull final AbstractSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (!(parentAST instanceof RootSyntaxAST) && !(parentAST instanceof BlockSyntaxAST)) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.VARIABLE_DEFINITION_WRONG_PARENT
            );
            return this;
        }
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.VAR) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.VARIABLE_DEFINITION_WRONG_STAR
            );
            return this;
        }
    
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        if (this.getSyntaxAnalyzer().matchesNextToken(TokenType.IDENTIFIER) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.VARIABLE_DEFINITION_NO_NAME
            );
            return this;
        }
        this.variableName = (IdentifierToken) this.getSyntaxAnalyzer().currentToken();
    
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.EQUALS) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.VARIABLE_DEFINITION_NO_EQUAL_SIGN
            );
            return this;
        } else this.getSyntaxAnalyzer().nextToken(2);
        
        if (!AbstractExpressionSyntaxAST.EXPRESSION_PARSER.canParse(this, this.getSyntaxAnalyzer())) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.VARIABLE_DEFINITION_ERROR_DURING_EXPRESSION_PARSING
            );
            return this;
        }
        
        final AbstractOperableSyntaxAST<?> abstractOperableSyntaxAST = AbstractExpressionSyntaxAST.EXPRESSION_PARSER.parse(this, this.getSyntaxAnalyzer());
        this.getMarkerFactory().addFactory(abstractOperableSyntaxAST.getMarkerFactory());
        
        if (abstractOperableSyntaxAST.isFailed()) {
            this.failed();
            return this;
        } this.variableExpression = abstractOperableSyntaxAST;
        
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
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
