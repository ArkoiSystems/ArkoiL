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
import com.arkoisystems.compiler.phases.parser.ast.types.StructNode;
import com.arkoisystems.compiler.phases.parser.ast.types.TypeNode;
import com.arkoisystems.compiler.phases.parser.ast.types.argument.ArgumentListNode;
import com.arkoisystems.compiler.phases.parser.ast.types.argument.ArgumentNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.NumberNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.StringNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.ExpressionListNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.BinaryNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.ParenthesizedNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.PrefixNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier.IdentifierNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier.types.AssignNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier.types.FunctionCallNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier.types.StructCreateNode;
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

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Getter
@Setter
public class TypeVisitor implements IVisitor<TypeNode>
{
    
    public static TypeNode ERROR_NODE = TypeNode.builder()
            .dataKind(DataKind.ERROR)
            .build();
    
    @NotNull
    private final Semantic semantic;
    
    private boolean failed;
    
    @NotNull
    @Override
    public TypeNode visit(@NotNull final TypeNode typeNode) {
        if (typeNode == ERROR_NODE)
            return ERROR_NODE;
    
        Objects.requireNonNull(typeNode.getParser(), "typeNode.parser must not be null.");
    
        if (typeNode.getTargetIdentifier() != null && typeNode.getTargetNode() == null) {
            final List<ParserNode> nodes = this.getSemantic().getCompilerClass()
                    .getRootScope()
                    .lookup(typeNode.getTargetIdentifier().getTokenContent());
            final List<ParserNode> foundNodes = nodes.stream()
                    .filter(node -> {
                        if (node instanceof StructNode) {
                            final StructNode structNode = (StructNode) node;
                            final String name = Objects.requireNonNull(structNode.getName(), "structNode.name must not be null.").getTokenContent();
                            return name.equals(typeNode.getTargetIdentifier().getTokenContent());
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
            if (foundNodes.isEmpty())
                return this.addError(
                        ERROR_NODE,
                        typeNode.getParser().getCompilerClass(),
                        typeNode,
                        "No node found for this identifier."
                );
        
            final ParserNode targetNode = foundNodes.get(0);
            typeNode.setTargetNode(targetNode);
        
            final TypeNode targetType = this.visit(targetNode);
            typeNode.setDataKind(targetType.getDataKind());
        }
    
        return typeNode;
    }
    
    @NotNull
    @Override
    public TypeNode visit(@NotNull final RootNode rootNode) {
        Objects.requireNonNull(rootNode.getCurrentScope(), "rootNode.currentScope must not be null.");
    
        rootNode.getCurrentScope().getSymbolTable().values().stream()
                .flatMap((Function<List<ParserNode>, Stream<ParserNode>>) Collection::stream)
                .filter(node -> node instanceof FunctionNode)
                .map(node -> (FunctionNode) node)
                .forEach(node -> this.visit(Objects.requireNonNull(node.getParameterList(), "node.parameters must not be null.")));
    
        rootNode.getNodes().forEach(this::visit);
        return ERROR_NODE;
    }
    
    @NotNull
    @Override
    public TypeNode visit(@NotNull final ParameterListNode parameterListNode) {
        parameterListNode.getParameters().forEach(this::visit);
        return ERROR_NODE;
    }
    
    @NotNull
    @Override
    public TypeNode visit(@NotNull final ParameterNode parameter) {
        return this.visit(parameter.getTypeNode());
    }
    
    @Override
    public TypeNode visit(final @NotNull ArgumentListNode argumentListNode) {
        argumentListNode.getArguments().forEach(this::visit);
        return ERROR_NODE;
    }
    
    @Override
    public TypeNode visit(final @NotNull ArgumentNode argumentNode) {
        return this.visit(argumentNode.getTypeNode());
    }
    
    @NotNull
    @Override
    public TypeNode visit(@NotNull final BlockNode blockNode) {
        blockNode.getNodes().forEach(this::visit);
        return this.visit(blockNode.getTypeNode());
    }
    
    @NotNull
    @Override
    public TypeNode visit(@NotNull final FunctionNode functionNode) {
        Objects.requireNonNull(functionNode.getParser(), "functionNode.parser must not be null.");
        Objects.requireNonNull(functionNode.getParameterList(), "functionNode.parameters must not be null.");
    
        this.visit(functionNode.getParameterList());
    
        if (functionNode.getReturnType() == null && functionNode.getBlockNode() == null)
            return this.addError(
                    ERROR_NODE,
                    functionNode.getParser().getCompilerClass(),
                    functionNode,
                    "There must be specified a return type if no block exists."
            );
    
        TypeNode expectedType = null;
        if (functionNode.getReturnType() != null)
            expectedType = this.visit(functionNode.getReturnType());
    
        TypeNode givenType = null;
        if (functionNode.getBlockNode() != null)
            givenType = this.visit(functionNode.getBlockNode());
    
        if (expectedType == null || givenType == null)
            return this.visit(functionNode.getTypeNode());
    
        if (expectedType == ERROR_NODE || givenType == ERROR_NODE)
            return ERROR_NODE;
    
        if (!expectedType.equals(givenType))
            return this.addError(
                    ERROR_NODE,
                    functionNode.getParser().getCompilerClass(),
                    functionNode.getBlockNode(),
                    "The block type doesn't match that of the function."
            );
    
        return this.visit(functionNode.getTypeNode());
    }
    
    @NotNull
    @Override
    public TypeNode visit(@NotNull final ImportNode importNode) {
        return ERROR_NODE;
    }
    
    @NotNull
    @Override
    public TypeNode visit(@NotNull final ReturnNode returnNode) {
        if (returnNode.getExpression() != null && this.visit(returnNode.getExpression()) == ERROR_NODE)
            return ERROR_NODE;
        return this.visit(returnNode.getTypeNode());
    }
    
    @NotNull
    @Override
    public TypeNode visit(@NotNull final VariableNode variableNode) {
        Objects.requireNonNull(variableNode.getParser(), "variableNode.parser must not be null.");
    
        if (variableNode.getReturnType() != null)
            this.visit(variableNode.getReturnType());
    
        if (variableNode.getExpression() == null && variableNode.isConstant())
            return this.addError(
                    ERROR_NODE,
                    variableNode.getParser().getCompilerClass(),
                    variableNode,
                    "Constant variables need an expression."
            );
    
        if (variableNode.getExpression() == null && !variableNode.isOptional())
            return this.addError(
                    ERROR_NODE,
                    variableNode.getParser().getCompilerClass(),
                    variableNode,
                    "Variables with no default content need to be declared optional."
            );
    
        if (variableNode.getReturnType() == null && variableNode.getExpression() == null)
            return this.addError(
                    ERROR_NODE,
                    variableNode.getParser().getCompilerClass(),
                    variableNode,
                    "There must be specified a return type if no expression exists."
            );
    
        TypeNode expectedType = null;
        if (variableNode.getReturnType() != null)
            expectedType = this.visit(variableNode.getReturnType());
    
        TypeNode givenType = null;
        if (variableNode.getExpression() != null)
            givenType = this.visit(variableNode.getExpression());
    
        if (expectedType == null || givenType == null)
            return this.visit(variableNode.getTypeNode());
    
        if (expectedType == ERROR_NODE || givenType == ERROR_NODE)
            return ERROR_NODE;
    
        if (!expectedType.equals(givenType))
            return this.addError(
                    ERROR_NODE,
                    variableNode.getParser().getCompilerClass(),
                    variableNode.getExpression(),
                    "The expression type doesn't match that of the variable."
            );
    
        return this.visit(variableNode.getTypeNode());
    }
    
    @NotNull
    @Override
    public TypeNode visit(@NotNull final StringNode stringNode) {
        return this.visit(stringNode.getTypeNode());
    }
    
    @NotNull
    @Override
    public TypeNode visit(@NotNull final NumberNode numberNode) {
        return this.visit(numberNode.getTypeNode());
    }
    
    @NotNull
    @Override
    public TypeNode visit(@NotNull final IdentifierNode identifierNode) {
        return this.visit(identifierNode.getTypeNode());
    }
    
    @Override
    public TypeNode visit(@NotNull final FunctionCallNode functionCallNode) {
        Objects.requireNonNull(functionCallNode.getExpressionList(), "identifierOperable.expressionList must not be null.");
        this.visit(functionCallNode.getExpressionList());
        return this.visit(functionCallNode.getTypeNode());
    }
    
    @Override
    public TypeNode visit(@NotNull final AssignNode assignNode) {
        Objects.requireNonNull(assignNode.getExpression(), "assignNode.expression must not be null.");
        Objects.requireNonNull(assignNode.getParser(), "assignNode.parser must not be null.");
    
        final TypeNode leftHandSide = this.visit(assignNode.getTypeNode());
        final TypeNode rightHandSide = this.visit(assignNode.getExpression());
        if (leftHandSide == ERROR_NODE || rightHandSide == ERROR_NODE)
            return ERROR_NODE;
    
        if (!rightHandSide.equals(leftHandSide))
            return this.addError(
                    ERROR_NODE,
                    assignNode.getParser().getCompilerClass(),
                    assignNode,
                    "Expression doesn't match the left type."
            );
    
        return this.visit(assignNode.getTypeNode());
    }
    
    @Override
    public TypeNode visit(@NotNull final StructCreateNode structCreateNode) {
        Objects.requireNonNull(structCreateNode.getTypeNode().getTargetNode(), "structCreateNode.typeNode.targetNode must not be null.");
        Objects.requireNonNull(structCreateNode.getArgumentList(), "structCreateNode.argumentList must not be null.");
        Objects.requireNonNull(structCreateNode.getParser(), "structCreateNode.parser must not be null.");
    
        final ParserNode targetNode = structCreateNode.getTypeNode().getTargetNode();
        if (!(targetNode instanceof StructNode))
            return this.visit(structCreateNode.getTypeNode());
    
        final StructNode structNode = (StructNode) targetNode;
        for (final ArgumentNode argumentNode : structCreateNode.getArgumentList().getArguments()) {
            Objects.requireNonNull(argumentNode.getExpression(), "argumentNode.expression must not be null.");
            Objects.requireNonNull(argumentNode.getParser(), "argumentNode.parser must not be null.");
            Objects.requireNonNull(argumentNode.getName(), "argumentNode.name must not be null.");
        
            final VariableNode variableNode = structNode.getVariables().stream()
                    .filter(node -> {
                        final String name = Objects.requireNonNull(node.getName(), "node.name must not be null.").getTokenContent();
                        return name.equals(argumentNode.getName().getTokenContent());
                    })
                    .findFirst()
                    .orElse(null);
            if (variableNode == null)
                continue;
        
            final TypeNode leftHandSide = this.visit(variableNode.getTypeNode());
            final TypeNode rightHandSide = this.visit(argumentNode.getExpression());
            if (leftHandSide == ERROR_NODE || rightHandSide == ERROR_NODE)
                return ERROR_NODE;
        
            if (!rightHandSide.equals(leftHandSide))
                return this.addError(
                        ERROR_NODE,
                        argumentNode.getParser().getCompilerClass(),
                        argumentNode.getExpression(),
                        "Expression doesn't match the left type."
                );
        }
    
        return this.visit(structCreateNode.getTypeNode());
    }
    
    @NotNull
    @Override
    public TypeNode visit(@NotNull final ExpressionListNode expressionListNode) {
        expressionListNode.getExpressions().forEach(this::visit);
        return ERROR_NODE;
    }
    
    @NotNull
    @Override
    public TypeNode visit(@NotNull final BinaryNode binaryNode) {
        Objects.requireNonNull(binaryNode.getLeftHandSide(), "binaryExpressionNode.leftSideOperable must not be null.");
        Objects.requireNonNull(binaryNode.getRightHandSide(), "binaryExpressionNode.rightSideOperable must not be null.");
        Objects.requireNonNull(binaryNode.getOperatorType(), "binaryExpressionNode.binaryOperatorType must not be null.");
        Objects.requireNonNull(binaryNode.getParser(), "binaryExpressionNode.parser must not be null.");
    
        final TypeNode leftHandSide = this.visit(binaryNode.getLeftHandSide());
        final TypeNode rightHandSide = this.visit(binaryNode.getRightHandSide());
        if (leftHandSide == ERROR_NODE || rightHandSide == ERROR_NODE)
            return ERROR_NODE;
    
        Objects.requireNonNull(rightHandSide.getDataKind(), "rightHandSide.dataKind must not be null.");
        Objects.requireNonNull(leftHandSide.getDataKind(), "leftHandSide.dataKind must not be null.");
        if (!leftHandSide.getDataKind().isNumeric() && !rightHandSide.getDataKind().isNumeric())
            return this.addError(
                    ERROR_NODE,
                    binaryNode.getParser().getCompilerClass(),
                    binaryNode,
                    "Both sides are not numeric."
            );
    
        if (!leftHandSide.getDataKind().isNumeric())
            return this.addError(
                    ERROR_NODE,
                    binaryNode.getParser().getCompilerClass(),
                    binaryNode.getLeftHandSide(),
                    "Left side is not numeric."
            );
    
        if (!rightHandSide.getDataKind().isNumeric())
            return this.addError(
                    ERROR_NODE,
                    binaryNode.getParser().getCompilerClass(),
                    binaryNode.getRightHandSide(),
                    "Right side is not numeric."
            );
    
        return this.visit(binaryNode.getTypeNode());
    }
    
    @NotNull
    @Override
    public TypeNode visit(@NotNull final ParenthesizedNode parenthesizedNode) {
        Objects.requireNonNull(parenthesizedNode.getExpression(), "parenthesizedExpressionNode.expression must not be null.");
        if (this.visit(parenthesizedNode.getExpression()) == ERROR_NODE)
            return ERROR_NODE;
        return this.visit(parenthesizedNode.getTypeNode());
    }
    
    @NotNull
    @Override
    public TypeNode visit(@NotNull final PrefixNode prefixNode) {
        Objects.requireNonNull(prefixNode.getRightHandSide(), "prefixExpressionNode.rightSideOperable must not be null.");
        Objects.requireNonNull(prefixNode.getOperatorType(), "prefixExpressionNode.prefixOperatorType must not be null.");
        Objects.requireNonNull(prefixNode.getParser(), "prefixExpressionNode.parser must not be null.");
        
        final TypeNode rightHandSide = this.visit(prefixNode.getRightHandSide());
        if (rightHandSide == ERROR_NODE)
            return ERROR_NODE;
    
        Objects.requireNonNull(rightHandSide.getDataKind(), "rightHandSide.dataKind must not be null.");
        if (!rightHandSide.getDataKind().isNumeric())
            return this.addError(
                    ERROR_NODE,
                    prefixNode.getParser().getCompilerClass(),
                    prefixNode,
                    "Right side is not numeric."
            );
    
        return this.visit(prefixNode.getTypeNode());
    }
    
    @Override
    public TypeNode visit(@NotNull final StructNode structNode) {
        structNode.getVariables().forEach(this::visit);
        return this.visit(structNode.getTypeNode());
    }
    
    public <E> E addError(
            @Nullable final E errorSource,
            @NotNull final CompilerClass compilerClass,
            @NotNull final ParserNode astNode,
            @NotNull final String causeMessage
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