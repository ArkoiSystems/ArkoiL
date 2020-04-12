/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.NumberToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableAST;
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

public class NumberAST extends OperableAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private NumberToken numberToken;
    
    
    protected NumberAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.NUMBER);
    }
    
    
    @NotNull
    @Override
    public NumberAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.NUMBER_LITERAL) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
        
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Number", "<number>", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
    
        this.setNumberToken((NumberToken) this.getSyntaxAnalyzer().currentToken());
        
        this.setStartToken(this.getNumberToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        this.setEndToken(this.getNumberToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
    }
    
    
    @Override
    public void accept(@NotNull final IVisitor visitor) {
        visitor.visit(this);
    }
    
    
    // TODO: More advanced TypeKind checking
    @Override
    public TypeKind getTypeKind() {
        Objects.requireNonNull(this.getNumberToken(), "numberToken must not be null.");
        
        if(this.getNumberToken().getTokenContent().contains("."))
            return TypeKind.FLOAT;
        return TypeKind.DOUBLE;
    }
    
    
    public static NumberASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new NumberASTBuilder(syntaxAnalyzer);
    }
    
    
    public static NumberASTBuilder builder() {
        return new NumberASTBuilder();
    }
    
    
    public static class NumberASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private NumberToken numberToken;
        
        
        private AbstractToken startToken, endToken;
        
        
        public NumberASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public NumberASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public NumberASTBuilder literal(final NumberToken numberToken) {
            this.numberToken = numberToken;
            return this;
        }
    
    
        public NumberASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
    
    
        public NumberASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
    
    
        public NumberAST build() {
            final NumberAST numberAST = new NumberAST(this.syntaxAnalyzer);
            if (this.numberToken != null)
                numberAST.setNumberToken(this.numberToken);
            numberAST.setStartToken(this.startToken);
            numberAST.getMarkerFactory().getCurrentMarker().setStart(numberAST.getStartToken());
            numberAST.setEndToken(this.endToken);
            numberAST.getMarkerFactory().getCurrentMarker().setEnd(numberAST.getEndToken());
            return numberAST;
        }
    
    }
    
}
