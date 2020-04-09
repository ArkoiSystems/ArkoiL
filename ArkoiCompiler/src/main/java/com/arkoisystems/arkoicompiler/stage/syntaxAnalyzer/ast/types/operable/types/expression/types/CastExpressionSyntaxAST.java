/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.api.ICompilerSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.stream.Collectors;

public class CastExpressionSyntaxAST extends ExpressionSyntaxAST
{
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private TypeKind typeKind;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private OperableSyntaxAST leftSideOperable;
    
    
    protected CastExpressionSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.CAST_EXPRESSION);
    }
    
    
    @NotNull
    @Override
    public CastExpressionSyntaxAST parseAST(@NotNull final ICompilerSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        Objects.requireNonNull(this.getLeftSideOperable());
        
        this.getMarkerFactory().addFactory(this.getLeftSideOperable().getMarkerFactory());
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER, false) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().peekToken(1),
                    "Couldn't parse the cast expression because it doesn't start with an identifier."
            );
        }
        
        final IdentifierToken identifierToken = (IdentifierToken) this.getSyntaxAnalyzer().nextToken(false);
        switch (identifierToken.getTokenContent()) {
            case "i":
            case "I":
                this.typeKind = TypeKind.INTEGER;
                break;
            case "d":
            case "D":
                this.typeKind = TypeKind.DOUBLE;
                break;
            case "f":
            case "F":
                this.typeKind = TypeKind.FLOAT;
                break;
            case "b":
            case "B":
                this.typeKind = TypeKind.BYTE;
                break;
            case "s":
            case "S":
                this.typeKind = TypeKind.SHORT;
                break;
            default:
                return this.addError(
                        this,
                        this.getSyntaxAnalyzer().getCompilerClass(),
                        identifierToken,
                        SyntaxErrorType.EXPRESSION_CAST_WRONG_IDENTIFIER
                );
        }
        
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getStart());
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getEnd());
        Objects.requireNonNull(this.getLeftSideOperable());
    
        printStream.println(indents + "├── factory:");
        printStream.println(indents + "│    ├── next: " + this.getMarkerFactory()
                .getNextMarkerFactories()
                .stream()
                .map(markerFactory -> markerFactory.getCurrentMarker().getAstType().name())
                .collect(Collectors.joining(", "))
        );
        printStream.println(indents + "│    ├── start: " + this.getMarkerFactory().getCurrentMarker().getStart().getStart());
        printStream.println(indents + "│    └── end: " + this.getMarkerFactory().getCurrentMarker().getEnd().getEnd());
        printStream.println(indents + "│");
        printStream.println(indents + "├── left:");
        printStream.println(indents + "│   └── " + this.getLeftSideOperable().getClass().getSimpleName());
        this.getLeftSideOperable().printSyntaxAST(printStream, indents + "│       ");
        printStream.println(indents + "└── operator: " + this.getTypeKind());
    }
    
    
    public static CastExpressionSyntaxASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new CastExpressionSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static CastExpressionSyntaxASTBuilder builder() {
        return new CastExpressionSyntaxASTBuilder();
    }
    
    
    public static class CastExpressionSyntaxASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private TypeKind typeKind;
        
        
        @Nullable
        private OperableSyntaxAST leftSideOperable;
        
        
        private AbstractToken startToken, endToken;
        
        
        public CastExpressionSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public CastExpressionSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public CastExpressionSyntaxASTBuilder left(final OperableSyntaxAST leftSideOperable) {
            this.leftSideOperable = leftSideOperable;
            return this;
        }
        
        
        public CastExpressionSyntaxASTBuilder operator(final TypeKind typeKind) {
            this.typeKind = typeKind;
            return this;
        }
        
        
        public CastExpressionSyntaxASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public CastExpressionSyntaxASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public CastExpressionSyntaxAST build() {
            final CastExpressionSyntaxAST castExpressionSyntaxAST = new CastExpressionSyntaxAST(this.syntaxAnalyzer);
            if (this.leftSideOperable != null)
                castExpressionSyntaxAST.setLeftSideOperable(this.leftSideOperable);
            if (this.typeKind != null)
                castExpressionSyntaxAST.setTypeKind(this.typeKind);
            castExpressionSyntaxAST.setStartToken(this.startToken);
            castExpressionSyntaxAST.getMarkerFactory().getCurrentMarker().setStart(castExpressionSyntaxAST.getStartToken());
            castExpressionSyntaxAST.setEndToken(this.endToken);
            castExpressionSyntaxAST.getMarkerFactory().getCurrentMarker().setEnd(castExpressionSyntaxAST.getEndToken());
            return castExpressionSyntaxAST;
        }
        
    }
    
}
