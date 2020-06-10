/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on April 17, 2020
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
package com.arkoisystems.compiler.phases.semantic.routines;

import com.arkoisystems.compiler.CompilerClass;
import com.arkoisystems.compiler.error.CompilerError;
import com.arkoisystems.compiler.error.ErrorPosition;
import com.arkoisystems.compiler.phases.parser.ast.DataKind;
import com.arkoisystems.compiler.phases.parser.ast.ParserNode;
import com.arkoisystems.compiler.phases.parser.ast.types.BlockNode;
import com.arkoisystems.compiler.phases.parser.ast.types.RootNode;
import com.arkoisystems.compiler.phases.parser.ast.types.TypeNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.IdentifierNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.NumberNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.StringNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.ExpressionListNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.*;
import com.arkoisystems.compiler.phases.parser.ast.types.parameter.ParameterListNode;
import com.arkoisystems.compiler.phases.parser.ast.types.parameter.ParameterNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.FunctionNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.ImportNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.ReturnNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.VariableNode;
import com.arkoisystems.compiler.phases.semantic.Semantic;
import com.arkoisystems.compiler.visitor.IVisitor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
@Setter
public class TypeVisitor implements IVisitor<DataKind>
{
    
    @NotNull
    private final Semantic semantic;
    
    private boolean failed;
    
    @NotNull
    @Override
    public DataKind visit(final @NotNull TypeNode typeNode) {
        return typeNode.getDataKind();
    }
    
    @NotNull
    @Override
    public DataKind visit(final @NotNull RootNode rootNode) {
        rootNode.getNodes().forEach(this::visit);
        return DataKind.UNDEFINED;
    }
    
    @NotNull
    @Override
    public DataKind visit(final @NotNull ParameterListNode parameterListNode) {
        parameterListNode.getParameters().forEach(this::visit);
        return DataKind.UNDEFINED;
    }
    
    @NotNull
    @Override
    public DataKind visit(final @NotNull ParameterNode parameter) {
        return parameter.getTypeNode().getDataKind();
    }
    
    @NotNull
    @Override
    public DataKind visit(final @NotNull BlockNode blockNode) {
        blockNode.getNodes().forEach(this::visit);
        return blockNode.getTypeNode().getDataKind();
    }
    
    @NotNull
    @Override
    public DataKind visit(final @NotNull FunctionNode functionNode) {
        Objects.requireNonNull(functionNode.getParser(), "functionNode.parser must not be null.");
        Objects.requireNonNull(functionNode.getParameters(), "functionNode.parameters must not be null.");
        
        this.visit(functionNode.getParameters());
        
        if (functionNode.getBlockNode() == null || functionNode.getReturnType() == null)
            return functionNode.getTypeNode().getDataKind();
        
        final DataKind expectedType = this.visit(functionNode.getReturnType());
        final DataKind givenType = this.visit(functionNode.getBlockNode());
        if (expectedType == DataKind.ERROR || givenType == DataKind.ERROR)
            return DataKind.ERROR;
        
        if (expectedType != givenType)
            return this.addError(
                    DataKind.ERROR,
                    functionNode.getParser().getCompilerClass(),
                    functionNode.getBlockNode(),
                    "The block type doesn't match that of the function."
            );
        
        return functionNode.getTypeNode().getDataKind();
    }
    
    @NotNull
    @Override
    public DataKind visit(final @NotNull ImportNode importNode) {
        return DataKind.UNDEFINED;
    }
    
    @NotNull
    @Override
    public DataKind visit(final @NotNull ReturnNode returnNode) {
        if (returnNode.getExpression() != null && this.visit(returnNode.getExpression()) == DataKind.ERROR)
            return DataKind.ERROR;
        return returnNode.getTypeNode().getDataKind();
    }
    
    @NotNull
    @Override
    public DataKind visit(final @NotNull VariableNode variableNode) {
        Objects.requireNonNull(variableNode.getParser(), "variableNode.parser must not be null.");
        
        if (variableNode.getExpression() == null && variableNode.isConstant())
            return this.addError(
                    DataKind.ERROR,
                    variableNode.getParser().getCompilerClass(),
                    variableNode,
                    "Constant variables need an expression."
            );
        
        if (variableNode.getReturnType() == null && variableNode.getExpression() == null)
            return this.addError(
                    DataKind.ERROR,
                    variableNode.getParser().getCompilerClass(),
                    variableNode,
                    "A type must be given if there is no expression."
            );
        
        if (variableNode.getExpression() == null || variableNode.getReturnType() == null)
            return variableNode.getTypeNode().getDataKind();
        
        final DataKind expectedType = this.visit(variableNode.getReturnType());
        final DataKind givenType = this.visit(variableNode.getExpression());
        if (expectedType == DataKind.ERROR || givenType == DataKind.ERROR)
            return DataKind.ERROR;
        
        if (expectedType != givenType)
            return this.addError(
                    DataKind.ERROR,
                    variableNode.getParser().getCompilerClass(),
                    variableNode.getExpression(),
                    "The expression type doesn't match that of the function."
            );
        
        return variableNode.getTypeNode().getDataKind();
    }
    
    @NotNull
    @Override
    public DataKind visit(final @NotNull StringNode stringNode) {
        return stringNode.getTypeNode().getDataKind();
    }
    
    @NotNull
    @Override
    public DataKind visit(final @NotNull NumberNode numberNode) {
        return numberNode.getTypeNode().getDataKind();
    }
    
    @NotNull
    @Override
    public DataKind visit(final @NotNull IdentifierNode identifierNode) {
        if (identifierNode.isFunctionCall()) {
            Objects.requireNonNull(identifierNode.getExpressions(), "identifierOperable.expressionList must not be null.");
            this.visit(identifierNode.getExpressions());
        }
        return identifierNode.getTypeNode().getDataKind();
    }
    
    @Override
    public DataKind visit(final @NotNull ExpressionListNode expressionListNode) {
        expressionListNode.getExpressions().forEach(this::visit);
        return DataKind.UNDEFINED;
    }
    
    @Nullable
    @Override
    public DataKind visit(final @NotNull AssignmentNode assignmentNode) {
        Objects.requireNonNull(assignmentNode.getLeftHandSide(), "assignmentExpression.leftSideOperable must not be null.");
        Objects.requireNonNull(assignmentNode.getRightHandSide(), "assignmentExpression.rightSideOperable must not be null.");
        Objects.requireNonNull(assignmentNode.getParser(), "assignmentExpression.parser must not be null.");
        
        if (!(assignmentNode.getLeftHandSide() instanceof IdentifierNode) ||
                ((IdentifierNode) assignmentNode.getLeftHandSide()).isFunctionCall()) {
            // TODO: 5/29/20 Check if its mutable or not
            return this.addError(
                    DataKind.ERROR,
                    assignmentNode.getParser().getCompilerClass(),
                    assignmentNode.getLeftHandSide(),
                    "Left side isn't an identifier operable which can be re-assigned."
            );
        }
        
        final DataKind leftHandSide = this.visit(assignmentNode.getLeftHandSide());
        final DataKind rightHandSide = this.visit(assignmentNode.getRightHandSide());
        if (leftHandSide == DataKind.ERROR || rightHandSide == DataKind.ERROR)
            return DataKind.ERROR;
        
        if (rightHandSide != leftHandSide)
            return this.addError(
                    DataKind.ERROR,
                    assignmentNode.getParser().getCompilerClass(),
                    assignmentNode.getRightHandSide(),
                    "Right side doesn't match the left one."
            );
        
        return assignmentNode.getTypeNode().getDataKind();
    }
    
    @Nullable
    @Override
    public DataKind visit(final @NotNull BinaryNode binaryNode) {
        Objects.requireNonNull(binaryNode.getLeftHandSide(), "binaryExpressionNode.leftSideOperable must not be null.");
        Objects.requireNonNull(binaryNode.getRightHandSide(), "binaryExpressionNode.rightSideOperable must not be null.");
        Objects.requireNonNull(binaryNode.getOperatorType(), "binaryExpressionNode.binaryOperatorType must not be null.");
        Objects.requireNonNull(binaryNode.getParser(), "binaryExpressionNode.parser must not be null.");
        
        final DataKind leftHandSide = this.visit(binaryNode.getLeftHandSide());
        final DataKind rightHandSide = this.visit(binaryNode.getRightHandSide());
        if (leftHandSide == DataKind.ERROR || rightHandSide == DataKind.ERROR)
            return DataKind.ERROR;
        
        if (!leftHandSide.isNumeric() && !rightHandSide.isNumeric())
            return this.addError(
                    DataKind.ERROR,
                    binaryNode.getParser().getCompilerClass(),
                    binaryNode,
                    "Both sides are not numeric."
            );
        
        if (!leftHandSide.isNumeric())
            return this.addError(
                    DataKind.ERROR,
                    binaryNode.getParser().getCompilerClass(),
                    binaryNode.getLeftHandSide(),
                    "Left side is not numeric."
            );
        
        if (!rightHandSide.isNumeric())
            return this.addError(
                    DataKind.ERROR,
                    binaryNode.getParser().getCompilerClass(),
                    binaryNode.getRightHandSide(),
                    "Right side is not numeric."
            );
        
        return binaryNode.getTypeNode().getDataKind();
    }
    
    @NotNull
    @Override
    public DataKind visit(final @NotNull ParenthesizedNode parenthesizedNode) {
        Objects.requireNonNull(parenthesizedNode.getExpression(), "parenthesizedExpressionNode.expression must not be null.");
        if (this.visit(parenthesizedNode.getExpression()) == DataKind.ERROR)
            return DataKind.ERROR;
        return parenthesizedNode.getTypeNode().getDataKind();
    }
    
    @Nullable
    @Override
    public DataKind visit(final @NotNull PostfixNode postfixNode) {
        Objects.requireNonNull(postfixNode.getLeftHandSide(), "postfixExpressionNode.leftSideOperable must not be null.");
        Objects.requireNonNull(postfixNode.getParser(), "postfixExpressionNode.parser must not be null.");
        
        final DataKind leftHandSide = this.visit(postfixNode.getLeftHandSide());
        if (leftHandSide == DataKind.ERROR)
            return DataKind.ERROR;
        
        if (!leftHandSide.isNumeric())
            return this.addError(
                    DataKind.ERROR,
                    postfixNode.getParser().getCompilerClass(),
                    postfixNode,
                    "Left side is not numeric."
            );
        
        return postfixNode.getTypeNode().getDataKind();
    }
    
    @Nullable
    @Override
    public DataKind visit(final @NotNull PrefixNode prefixNode) {
        Objects.requireNonNull(prefixNode.getRightHandSide(), "prefixExpressionNode.rightSideOperable must not be null.");
        Objects.requireNonNull(prefixNode.getOperatorType(), "prefixExpressionNode.prefixOperatorType must not be null.");
        Objects.requireNonNull(prefixNode.getParser(), "prefixExpressionNode.parser must not be null.");
        
        final DataKind rightHandSide = this.visit(prefixNode.getRightHandSide());
        if (rightHandSide == DataKind.ERROR)
            return DataKind.ERROR;
        
        if (!rightHandSide.isNumeric())
            return this.addError(
                    DataKind.ERROR,
                    prefixNode.getParser().getCompilerClass(),
                    prefixNode,
                    "Right side is not numeric."
            );
        
        return prefixNode.getTypeNode().getDataKind();
    }
    
    public <E> E addError(
            final @Nullable E errorSource,
            final @NotNull CompilerClass compilerClass,
            final @NotNull ParserNode astNode,
            final @NotNull String causeMessage
    ) {
        compilerClass.getCompiler().getErrorHandler().addError(CompilerError.builder()
                .causePosition(ErrorPosition.builder()
                        .sourceCode(Objects.requireNonNull(astNode.getParser(), "astNode.parser must not be null.").getCompilerClass().getContent())
                        .filePath(Objects.requireNonNull(astNode.getParser(), "astNode.parser must not be null.").getCompilerClass().getFilePath())
                        .lineRange(Objects.requireNonNull(astNode.getLineRange(), "astNode.lineRange must not be null."))
                        .charStart(Objects.requireNonNull(astNode.getStartToken(), "astNode.startToken must not be null.").getCharStart())
                        .charEnd(Objects.requireNonNull(astNode.getEndToken(), "astNode.endToken must not be null.").getCharEnd())
                        .build())
                .causeMessage(causeMessage)
                .build()
        );
        
        this.setFailed(true);
        return errorSource;
    }
    
}