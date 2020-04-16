/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author єхcsє#5543 aka timo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.ExpressionAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.operators.AssignmentOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class AssignmentExpressionAST extends ExpressionAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private OperableAST leftSideOperable;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private AssignmentOperatorType assignmentOperatorType;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private OperableAST rightSideOperable;
    
    
    protected AssignmentExpressionAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.ASSIGNMENT_EXPRESSION);
    }
    
    
    @NotNull
    @Override
    public AssignmentExpressionAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        Objects.requireNonNull(this.getLeftSideOperable());
    
        this.getMarkerFactory().addFactory(this.getLeftSideOperable().getMarkerFactory());
    
        this.getSyntaxAnalyzer().nextToken(2);
    
        final OperableAST operableAST = this.parseAdditive(parentAST);
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
    
    
    public static AssignmentExpressionASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new AssignmentExpressionASTBuilder(syntaxAnalyzer);
    }
    
    
    public static AssignmentExpressionASTBuilder builder() {
        return new AssignmentExpressionASTBuilder();
    }
    
    
    public static class AssignmentExpressionASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private OperableAST leftSideOperable;
        
        
        @Nullable
        private AssignmentOperatorType assignmentOperatorType;
        
        
        @Nullable
        private OperableAST rightSideOperable;
        
        
        private AbstractToken startToken, endToken;
        
        
        public AssignmentExpressionASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public AssignmentExpressionASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public AssignmentExpressionASTBuilder left(final OperableAST leftSideOperable) {
            this.leftSideOperable = leftSideOperable;
            return this;
        }
        
        
        public AssignmentExpressionASTBuilder operator(final AssignmentOperatorType assignmentOperatorType) {
            this.assignmentOperatorType = assignmentOperatorType;
            return this;
        }
        
        
        public AssignmentExpressionASTBuilder right(final OperableAST rightSideOperable) {
            this.rightSideOperable = rightSideOperable;
            return this;
        }
        
        
        public AssignmentExpressionASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public AssignmentExpressionASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public AssignmentExpressionAST build() {
            final AssignmentExpressionAST assignmentExpressionAST = new AssignmentExpressionAST(this.syntaxAnalyzer);
            if (this.leftSideOperable != null)
                assignmentExpressionAST.setLeftSideOperable(this.leftSideOperable);
            if (this.assignmentOperatorType != null)
                assignmentExpressionAST.setAssignmentOperatorType(this.assignmentOperatorType);
            if (this.rightSideOperable != null)
                assignmentExpressionAST.setRightSideOperable(this.rightSideOperable);
            assignmentExpressionAST.setStartToken(this.startToken);
            assignmentExpressionAST.getMarkerFactory().getCurrentMarker().setStart(assignmentExpressionAST.getStartToken());
            assignmentExpressionAST.setEndToken(this.endToken);
            assignmentExpressionAST.getMarkerFactory().getCurrentMarker().setEnd(assignmentExpressionAST.getEndToken());
            return assignmentExpressionAST;
        }
        
    }
    
}
