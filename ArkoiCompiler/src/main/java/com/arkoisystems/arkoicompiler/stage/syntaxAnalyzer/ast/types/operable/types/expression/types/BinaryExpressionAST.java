/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.ExpressionAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.operators.BinaryOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class BinaryExpressionAST extends ExpressionAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private OperableAST leftSideOperable;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private BinaryOperatorType binaryOperatorType;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private OperableAST rightSideOperable;
    
    
    protected BinaryExpressionAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.BINARY_EXPRESSION);
    }
    
    
    @NotNull
    @Override
    public BinaryExpressionAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        Objects.requireNonNull(this.getLeftSideOperable());
        
        this.getMarkerFactory().addFactory(this.getLeftSideOperable().getMarkerFactory());
        
        this.getSyntaxAnalyzer().nextToken(2);
        
        final OperableAST operableAST = this.parseMultiplicative(parentAST);
        this.getMarkerFactory().addFactory(operableAST.getMarkerFactory());
        
        if (operableAST.isFailed()) {
            this.failed();
            return this;
        }
        
        this.setRightSideOperable(operableAST);
        
        this.setEndToken(this.getRightSideOperable().getEndToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
    }
    
    
    @Override
    public void accept(@NotNull final IVisitor visitor) {
        visitor.visit(this);
    }
    
    
    public static BinaryExpressionBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new BinaryExpressionBuilder(syntaxAnalyzer);
    }
    
    
    public static BinaryExpressionBuilder builder() {
        return new BinaryExpressionBuilder();
    }
    
    
    public static class BinaryExpressionBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
    
    
        @Nullable
        private OperableAST leftSideOperable;
        
        
        @Nullable
        private BinaryOperatorType binaryOperatorType;
    
    
        @Nullable
        private OperableAST rightSideOperable;
        
        
        private AbstractToken startToken, endToken;
    
    
        public BinaryExpressionBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
    
    
        public BinaryExpressionBuilder() {
            this.syntaxAnalyzer = null;
        }
    
    
        public BinaryExpressionBuilder left(final OperableAST leftSideOperable) {
            this.leftSideOperable = leftSideOperable;
            return this;
        }
    
    
        public BinaryExpressionBuilder operator(final BinaryOperatorType binaryOperatorType) {
            this.binaryOperatorType = binaryOperatorType;
            return this;
        }
    
    
        public BinaryExpressionBuilder right(final OperableAST rightSideOperable) {
            this.rightSideOperable = rightSideOperable;
            return this;
        }
    
    
        public BinaryExpressionBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public BinaryExpressionBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public BinaryExpressionAST build() {
            final BinaryExpressionAST binaryExpressionAST = new BinaryExpressionAST(this.syntaxAnalyzer);
            if (this.leftSideOperable != null)
                binaryExpressionAST.setLeftSideOperable(this.leftSideOperable);
            if (this.binaryOperatorType != null)
                binaryExpressionAST.setBinaryOperatorType(this.binaryOperatorType);
            if (this.rightSideOperable != null)
                binaryExpressionAST.setRightSideOperable(this.rightSideOperable);
            binaryExpressionAST.setStartToken(this.startToken);
            binaryExpressionAST.getMarkerFactory().getCurrentMarker().setStart(binaryExpressionAST.getStartToken());
            binaryExpressionAST.setEndToken(this.endToken);
            binaryExpressionAST.getMarkerFactory().getCurrentMarker().setEnd(binaryExpressionAST.getEndToken());
            return binaryExpressionAST;
        }
        
    }
    
}
