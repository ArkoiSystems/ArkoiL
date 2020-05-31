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
package com.arkoisystems.arkoicompiler.phases.semantic.routines;

import com.arkoisystems.arkoicompiler.CompilerClass;
import com.arkoisystems.arkoicompiler.api.IFailed;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.errorHandling.CompilerError;
import com.arkoisystems.arkoicompiler.errorHandling.ErrorPosition;
import com.arkoisystems.arkoicompiler.phases.parser.ast.ParserNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.enums.BlockType;
import com.arkoisystems.arkoicompiler.phases.parser.ast.enums.NodeType;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.BlockNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.RootNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.TypeNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.argument.ArgumentNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.argument.ArgumentListNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.OperableNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.IdentifierNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.NumberNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.StringNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.expression.ExpressionListNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.parameter.ParameterNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.parameter.ParameterListNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.statement.types.FunctionNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.statement.types.ImportNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.statement.types.ReturnNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.statement.types.VariableNode;
import com.arkoisystems.arkoicompiler.phases.semantic.Semantic;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Objects;

@RequiredArgsConstructor
@Getter
@Setter
public class TypeVisitor implements IVisitor<NodeType>, IFailed
{
    
    @NotNull
    private final Semantic semantic;
    
    @NotNull
    private final ScopeVisitor scopeVisitor;
    
    private boolean failed;
    
    @NotNull
    @Override
    public NodeType visit(final @NotNull TypeNode typeNode) {
        return typeNode.getTypeKind();
    }
    
    @NotNull
    @Override
    public NodeType visit(final @NotNull RootNode rootNode) {
        NodeType nodeType = rootNode.getTypeKind();
        for (final ParserNode astNode : rootNode.getNodes()) {
            if (this.visit(astNode) == NodeType.ERROR)
                nodeType = NodeType.ERROR;
        }
        return nodeType;
    }
    
    @NotNull
    @Override
    public NodeType visit(final @NotNull ParameterListNode parameterListNode) {
        NodeType nodeType = parameterListNode.getTypeKind();
        for (final ParameterNode parameter : parameterListNode.getParameters()) {
            if (this.visit(parameter) == NodeType.ERROR)
                nodeType = NodeType.ERROR;
        }
        return nodeType;
    }
    
    @NotNull
    @Override
    public NodeType visit(final @NotNull ParameterNode parameter) {
        return parameter.getTypeKind();
    }
    
    @NotNull
    @Override
    public NodeType visit(final @NotNull BlockNode blockNode) {
        NodeType nodeType = blockNode.getTypeKind();
        for (final ParserNode node : blockNode.getNodes()) {
            if (this.visit(node) == NodeType.ERROR)
                nodeType = NodeType.ERROR;
        }
        return nodeType;
    }
    
    @NotNull
    @Override
    public NodeType visit(final @NotNull ArgumentListNode argumentListNode) {
        NodeType nodeType = argumentListNode.getTypeKind();
        for (final ArgumentNode argumentNode : argumentListNode.getArguments()) {
            if (this.visit(argumentNode) == NodeType.ERROR)
                nodeType = NodeType.ERROR;
        }
        return nodeType;
    }
    
    @NotNull
    @Override
    public NodeType visit(final @NotNull ArgumentNode argumentNode) {
        Objects.requireNonNull(argumentNode.getExpression(), "argumentNode.argumentExpression must not be null.");
        if (this.visit(argumentNode.getExpression()) == NodeType.ERROR)
            return NodeType.ERROR;
        return argumentNode.getTypeKind();
    }
    
    @NotNull
    @Override
    public NodeType visit(final @NotNull FunctionNode functionNode) {
        Objects.requireNonNull(functionNode.getParser(), "functionNode.parser must not be null.");
        Objects.requireNonNull(functionNode.getBlockNode(), "functionNode.block must not be null.");
        Objects.requireNonNull(functionNode.getReturnTypeNode(), "functionNode.returnType must not be null.");
        Objects.requireNonNull(functionNode.getParameters(), "functionNode.parameters must not be null.");
    
        this.visit(functionNode.getParameters());
    
        final NodeType expectedType = this.visit(functionNode.getReturnTypeNode());
        final NodeType givenType = this.visit(functionNode.getBlockNode());
        if (expectedType == NodeType.ERROR || givenType == NodeType.ERROR)
            return NodeType.ERROR;
    
        if ((expectedType != NodeType.AUTO && functionNode.getBlockNode().getBlockType() != BlockType.NATIVE) &&
                expectedType != givenType)
            return this.addError(
                    NodeType.ERROR,
                    functionNode.getParser().getCompilerClass(),
                    functionNode.getBlockNode(),
                    "The block return type doesn't match that of the function."
            );
    
        return functionNode.getTypeKind();
    }
    
    @NotNull
    @Override
    public NodeType visit(final @NotNull ImportNode importNode) {
        return importNode.getTypeKind();
    }
    
    @NotNull
    @Override
    public NodeType visit(final @NotNull ReturnNode returnNode) {
        if (returnNode.getExpression() != null && this.visit(returnNode.getExpression()) == NodeType.ERROR)
            return NodeType.ERROR;
        return returnNode.getTypeKind();
    }
    
    @NotNull
    @Override
    public NodeType visit(final @NotNull VariableNode variableNode) {
        Objects.requireNonNull(variableNode.getExpression(), "variableNode.variableExpression must not be null.");
        if (this.visit(variableNode.getExpression()) == NodeType.ERROR)
            return NodeType.ERROR;
        return variableNode.getTypeKind();
    }
    
    @NotNull
    @Override
    public NodeType visit(final @NotNull StringNode stringNode) {
        return stringNode.getTypeKind();
    }
    
    @NotNull
    @Override
    public NodeType visit(final @NotNull NumberNode numberNode) {
        return numberNode.getTypeKind();
    }
    
    @NotNull
    @Override
    public NodeType visit(final @NotNull IdentifierNode identifierNode) {
        if (identifierNode.isFunctionCall()) {
            Objects.requireNonNull(identifierNode.getExpressionListNode(), "identifierOperable.expressionList must not be null.");
            this.visit(identifierNode.getExpressionListNode());
        }
    
        return identifierNode.getTypeKind();
    }
    
    @Override
    public NodeType visit(final @NotNull ExpressionListNode expressionListNode) {
        NodeType nodeType = expressionListNode.getTypeKind();
        for (final OperableNode operableNode : expressionListNode.getExpressions()) {
            if (this.visit(operableNode) == NodeType.ERROR)
                nodeType = NodeType.ERROR;
        }
        return nodeType;
    }
    
    @Nullable
    @Override
    public NodeType visit(final @NotNull AssignmentNode assignmentNode) {
        Objects.requireNonNull(assignmentNode.getLeftHandSide(), "assignmentExpression.leftSideOperable must not be null.");
        Objects.requireNonNull(assignmentNode.getRightHandSide(), "assignmentExpression.rightSideOperable must not be null.");
        Objects.requireNonNull(assignmentNode.getParser(), "assignmentExpression.parser must not be null.");
        
        if (!(assignmentNode.getLeftHandSide() instanceof IdentifierNode) ||
                ((IdentifierNode) assignmentNode.getLeftHandSide()).isFunctionCall()) {
            // TODO: 5/29/20 Check if its mutable or not
            return this.addError(
                    NodeType.ERROR,
                    assignmentNode.getParser().getCompilerClass(),
                    assignmentNode.getLeftHandSide(),
                    "Left hand side it not an identifier operable which can be re-assigned."
            );
        }
        
        final NodeType leftHandSide = this.visit(assignmentNode.getLeftHandSide());
        final NodeType rightHandSide = this.visit(assignmentNode.getRightHandSide());
        if (leftHandSide == NodeType.ERROR || rightHandSide == NodeType.ERROR)
            return NodeType.ERROR;
        
        if (rightHandSide != leftHandSide)
            return this.addError(
                    NodeType.ERROR,
                    assignmentNode.getParser().getCompilerClass(),
                    assignmentNode.getRightHandSide(),
                    "Left type doesn't match the right one."
            );
    
        return assignmentNode.getTypeKind();
    }
    
    @Nullable
    @Override
    public NodeType visit(final @NotNull BinaryNode binaryNode) {
        Objects.requireNonNull(binaryNode.getLeftHandSide(), "binaryExpressionNode.leftSideOperable must not be null.");
        Objects.requireNonNull(binaryNode.getRightHandSide(), "binaryExpressionNode.rightSideOperable must not be null.");
        Objects.requireNonNull(binaryNode.getOperatorType(), "binaryExpressionNode.binaryOperatorType must not be null.");
        Objects.requireNonNull(binaryNode.getParser(), "binaryExpressionNode.parser must not be null.");
    
        final NodeType leftHandSide = this.visit(binaryNode.getLeftHandSide());
        final NodeType rightHandSide = this.visit(binaryNode.getRightHandSide());
        if (leftHandSide == NodeType.ERROR || rightHandSide == NodeType.ERROR)
            return NodeType.ERROR;
    
        if (!leftHandSide.isNumeric() && !rightHandSide.isNumeric())
            return this.addError(
                    NodeType.ERROR,
                    binaryNode.getParser().getCompilerClass(),
                    binaryNode,
                    "Both sides are not numeric."
            );
    
        if (!leftHandSide.isNumeric())
            return this.addError(
                    NodeType.ERROR,
                    binaryNode.getParser().getCompilerClass(),
                    binaryNode.getLeftHandSide(),
                    "Left side is not numeric."
            );
    
        if (!rightHandSide.isNumeric())
            return this.addError(
                    NodeType.ERROR,
                    binaryNode.getParser().getCompilerClass(),
                    binaryNode.getRightHandSide(),
                    "Right side is not numeric."
            );
    
        return binaryNode.getTypeKind();
    }
    
    @NotNull
    @Override
    public NodeType visit(final @NotNull ParenthesizedNode parenthesizedNode) {
        Objects.requireNonNull(parenthesizedNode.getExpression(), "parenthesizedExpressionNode.expression must not be null.");
        if (this.visit(parenthesizedNode.getExpression()) == NodeType.ERROR)
            return NodeType.ERROR;
        return parenthesizedNode.getTypeKind();
    }
    
    @Nullable
    @Override
    public NodeType visit(final @NotNull PostfixNode postfixNode) {
        Objects.requireNonNull(postfixNode.getLeftHandSide(), "postfixExpressionNode.leftSideOperable must not be null.");
        Objects.requireNonNull(postfixNode.getParser(), "postfixExpressionNode.parser must not be null.");
        
        final NodeType leftHandSide = this.visit(postfixNode.getLeftHandSide());
        if (leftHandSide == NodeType.ERROR)
            return NodeType.ERROR;
        
        if (!leftHandSide.isNumeric())
            return this.addError(
                    NodeType.ERROR,
                    postfixNode.getParser().getCompilerClass(),
                    postfixNode,
                    "Left side is not numeric."
            );
        
        return postfixNode.getTypeKind();
    }
    
    @Nullable
    @Override
    public NodeType visit(final @NotNull PrefixNode prefixNode) {
        Objects.requireNonNull(prefixNode.getRightHandSide(), "prefixExpressionNode.rightSideOperable must not be null.");
        Objects.requireNonNull(prefixNode.getOperatorType(), "prefixExpressionNode.prefixOperatorType must not be null.");
        Objects.requireNonNull(prefixNode.getParser(), "prefixExpressionNode.parser must not be null.");
        
        final NodeType rightHandSide = this.visit(prefixNode.getRightHandSide());
        if (rightHandSide == NodeType.ERROR)
            return NodeType.ERROR;
        
        if (!rightHandSide.isNumeric())
            return this.addError(
                    NodeType.ERROR,
                    prefixNode.getParser().getCompilerClass(),
                    prefixNode,
                    "Right side is not numeric."
            );
        
        return prefixNode.getTypeKind();
    }
    
    public <E> E addError(final @Nullable E errorSource, final @NotNull CompilerClass compilerClass, final @NotNull ParserNode astNode, final @NotNull String message, final @NotNull Object... arguments) {
        compilerClass.getCompiler().getErrorHandler().addError(CompilerError.builder()
                .compilerClass(compilerClass)
                .positions(Collections.singletonList(ErrorPosition.builder()
                        .lineRange(Objects.requireNonNull(astNode.getLineRange(), "astNode.lineRange must not be null."))
                        .charStart(Objects.requireNonNull(astNode.getStartToken(), "astNode.startToken must not be null.").getCharStart())
                        .charEnd(Objects.requireNonNull(astNode.getEndToken(), "astNode.endToken must not be null.").getCharEnd())
                        .build()))
                .message(message)
                .arguments(arguments)
                .build()
        );
        
        this.setFailed(true);
        return errorSource;
    }
    
}
