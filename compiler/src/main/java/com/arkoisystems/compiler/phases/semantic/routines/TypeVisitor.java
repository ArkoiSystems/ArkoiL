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
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.BinaryNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.ParenthesizedNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.UnaryNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier.IdentifierNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier.types.AssignNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier.types.FunctionCallNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier.types.StructCreateNode;
import com.arkoisystems.compiler.phases.parser.ast.types.parameter.ParameterListNode;
import com.arkoisystems.compiler.phases.parser.ast.types.parameter.ParameterNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.*;
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
    
    public static TypeNode LOOPING_NODE = TypeNode.builder()
            .dataKind(DataKind.ERROR)
            .build();
    
    public static TypeNode ERROR_NODE = TypeNode.builder()
            .dataKind(DataKind.ERROR)
            .build();
    
    @NotNull
    private final Semantic semantic;
    
    private boolean failed;
    
    @NotNull
    @Override
    public TypeNode visit(@NotNull final TypeNode typeNode) {
        if (typeNode.getDataKind() == DataKind.ERROR)
            return typeNode;
        
        Objects.requireNonNull(typeNode.getParser());
        
        if (typeNode.getTargetIdentifier() != null && typeNode.getTargetNode() == null) {
            final List<ParserNode> nodes = this.getSemantic().getCompilerClass()
                    .getRootScope()
                    .lookup(typeNode.getTargetIdentifier().getTokenContent());
            final List<ParserNode> foundNodes = nodes.stream()
                    .filter(node -> {
                        if (node instanceof StructNode) {
                            final StructNode structNode = (StructNode) node;
                            final String name = Objects.requireNonNull(structNode.getName()).getTokenContent();
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
        Objects.requireNonNull(rootNode.getCurrentScope());
    
        rootNode.getCurrentScope().getSymbolTable().values().stream()
                .flatMap((Function<List<ParserNode>, Stream<ParserNode>>) Collection::stream)
                .filter(node -> node instanceof FunctionNode)
                .map(node -> (FunctionNode) node)
                .forEach(node -> this.visit(Objects.requireNonNull(node.getParameterList())));
    
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
    public TypeNode visit(@NotNull final ArgumentListNode argumentListNode) {
        argumentListNode.getArguments().forEach(this::visit);
        return ERROR_NODE;
    }
    
    @Override
    public TypeNode visit(@NotNull final ArgumentNode argumentNode) {
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
        Objects.requireNonNull(functionNode.getParameterList());
        Objects.requireNonNull(functionNode.getReturnType());
        Objects.requireNonNull(functionNode.getParser());
    
        this.visit(functionNode.getParameterList());
        this.visit(functionNode.getReturnType());
    
        if (functionNode.getBlockNode() != null)
            this.visit(functionNode.getBlockNode());
    
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
        Objects.requireNonNull(returnNode.getParser());
    
        if (returnNode.getExpression() != null &&
                this.visit(returnNode.getExpression()).getDataKind() == DataKind.ERROR)
            return ERROR_NODE;
    
        final FunctionNode functionNode = returnNode.getParent(FunctionNode.class);
        if (functionNode == null)
            throw new NullPointerException();
    
        final TypeNode expectedType = this.visit(functionNode.getTypeNode());
        Objects.requireNonNull(expectedType.getDataKind());
        final TypeNode givenType = this.visit(returnNode.getTypeNode());
        Objects.requireNonNull(givenType.getDataKind());
    
        if (expectedType.getDataKind() == DataKind.ERROR)
            return expectedType;
        if (givenType.getDataKind() == DataKind.ERROR)
            return givenType;
    
        if (!expectedType.equals(givenType))
            return this.addError(
                    ERROR_NODE,
                    returnNode.getParser().getCompilerClass(),
                    returnNode,
                    "The return type doesn't match that of the function."
            );
    
        return this.visit(returnNode.getTypeNode());
    }
    
    @NotNull
    @Override
    public TypeNode visit(@NotNull final VariableNode variableNode) {
        Objects.requireNonNull(variableNode.getParser());
    
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
    
        final TypeNode givenType = this.visit(variableNode.getTypeNode());
        Objects.requireNonNull(givenType.getDataKind());
    
        if (givenType == LOOPING_NODE)
            return this.addError(
                    ERROR_NODE,
                    variableNode.getParser().getCompilerClass(),
                    variableNode,
                    "This variable causes an endless loop."
            );
    
        if (variableNode.getReturnType() == null)
            return givenType;
    
        final TypeNode expectedType = this.visit(variableNode.getReturnType());
        Objects.requireNonNull(expectedType.getDataKind());
    
        if (expectedType.getDataKind() == DataKind.ERROR)
            return expectedType;
        if (givenType.getDataKind() == DataKind.ERROR)
            return givenType;
    
        if (!expectedType.equals(givenType))
            return this.addError(
                    ERROR_NODE,
                    variableNode.getParser().getCompilerClass(),
                    variableNode.getExpression(),
                    "The expression type doesn't match that of the variable."
            );
    
        return givenType;
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
        Objects.requireNonNull(identifierNode.getParser());
        return this.visit(identifierNode.getTypeNode());
    }
    
    @Override
    public TypeNode visit(@NotNull final FunctionCallNode functionCallNode) {
        Objects.requireNonNull(functionCallNode.getArgumentList());
        this.visit(functionCallNode.getArgumentList());
        return this.visit(functionCallNode.getTypeNode());
    }
    
    @Override
    public TypeNode visit(@NotNull final AssignNode assignNode) {
        Objects.requireNonNull(assignNode.getExpression());
        Objects.requireNonNull(assignNode.getParser());
    
        final TypeNode leftHandSide = this.visit(assignNode.getTypeNode());
        Objects.requireNonNull(leftHandSide.getDataKind());
        final TypeNode rightHandSide = this.visit(assignNode.getExpression());
        Objects.requireNonNull(rightHandSide.getDataKind());
    
        if (leftHandSide.getDataKind() == DataKind.ERROR)
            return leftHandSide;
        if (rightHandSide.getDataKind() == DataKind.ERROR)
            return rightHandSide;
    
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
        Objects.requireNonNull(structCreateNode.getArgumentList());
        Objects.requireNonNull(structCreateNode.getParser());
    
        final ParserNode targetNode = this.visit(structCreateNode.getTypeNode()).getTargetNode();
        if (!(targetNode instanceof StructNode))
            return this.visit(structCreateNode.getTypeNode());
    
        final StructNode structNode = (StructNode) targetNode;
        for (final ArgumentNode argumentNode : structCreateNode.getArgumentList().getArguments()) {
            Objects.requireNonNull(argumentNode.getExpression());
            Objects.requireNonNull(argumentNode.getParser());
            Objects.requireNonNull(argumentNode.getName());
    
            final VariableNode variableNode = structNode.getVariables().stream()
                    .filter(node -> {
                        final String name = Objects.requireNonNull(node.getName()).getTokenContent();
                        return name.equals(argumentNode.getName().getTokenContent());
                    })
                    .findFirst()
                    .orElse(null);
            if (variableNode == null)
                continue;
    
            final TypeNode expectedType = this.visit(variableNode.getTypeNode());
            Objects.requireNonNull(expectedType.getDataKind());
            final TypeNode givenType = this.visit(argumentNode.getExpression());
            Objects.requireNonNull(givenType.getDataKind());
    
            if (expectedType.getDataKind() == DataKind.ERROR)
                return expectedType;
            if (givenType.getDataKind() == DataKind.ERROR)
                return givenType;
    
            if (!givenType.equals(expectedType))
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
    public TypeNode visit(@NotNull final BinaryNode binaryNode) {
        Objects.requireNonNull(binaryNode.getLeftHandSide());
        Objects.requireNonNull(binaryNode.getRightHandSide());
        Objects.requireNonNull(binaryNode.getOperatorType());
        Objects.requireNonNull(binaryNode.getParser());
    
        final TypeNode leftHandSide = this.visit(binaryNode.getLeftHandSide());
        Objects.requireNonNull(leftHandSide.getDataKind());
        final TypeNode rightHandSide = this.visit(binaryNode.getRightHandSide());
        Objects.requireNonNull(rightHandSide.getDataKind());
    
        if (leftHandSide.getDataKind() == DataKind.ERROR)
            return leftHandSide;
        if (rightHandSide.getDataKind() == DataKind.ERROR)
            return rightHandSide;
    
        switch (binaryNode.getOperatorType()) {
            case LESS_EQUAL_THAN:
            case LESS_THAN:
            case GREATER_EQUAL_THAN:
            case GREATER_THAN:
            case EQUAL:
            case NOT_EQUAL:
        
            case ADDITION:
            case MULTIPLICATION:
            case SUBTRACTION:
            case DIVISION:
            case REMAINING:
                if (!leftHandSide.getDataKind().isNumeric() || leftHandSide.getPointers() > 0)
                    return this.addError(
                            ERROR_NODE,
                            binaryNode.getParser().getCompilerClass(),
                            binaryNode.getLeftHandSide(),
                            "Left side is not numeric."
                    );
            
                if (!rightHandSide.getDataKind().isNumeric() || rightHandSide.getPointers() > 0)
                    return this.addError(
                            ERROR_NODE,
                            binaryNode.getParser().getCompilerClass(),
                            binaryNode.getRightHandSide(),
                            "Right side is not numeric."
                    );
                break;
            default:
                throw new NullPointerException();
        }
    
        return this.visit(binaryNode.getTypeNode());
    }
    
    @NotNull
    @Override
    public TypeNode visit(@NotNull final ParenthesizedNode parenthesizedNode) {
        Objects.requireNonNull(parenthesizedNode.getExpression());
    
        final TypeNode expressionType = this.visit(parenthesizedNode.getExpression());
        Objects.requireNonNull(expressionType.getDataKind());
    
        if (expressionType.getDataKind() == DataKind.ERROR)
            return expressionType;
    
        return this.visit(parenthesizedNode.getTypeNode());
    }
    
    @NotNull
    @Override
    public TypeNode visit(@NotNull final UnaryNode unaryNode) {
        Objects.requireNonNull(unaryNode.getRightHandSide());
        Objects.requireNonNull(unaryNode.getOperatorType());
        Objects.requireNonNull(unaryNode.getParser());
    
        final TypeNode rightHandSide = this.visit(unaryNode.getRightHandSide());
        Objects.requireNonNull(rightHandSide.getDataKind());
    
        if (rightHandSide.getDataKind() == DataKind.ERROR)
            return rightHandSide;
    
        if (!rightHandSide.getDataKind().isNumeric() || rightHandSide.getPointers() > 0)
            return this.addError(
                    ERROR_NODE,
                    unaryNode.getParser().getCompilerClass(),
                    unaryNode,
                    "Right side is not numeric."
            );
    
        return this.visit(unaryNode.getTypeNode());
    }
    
    @Override
    public TypeNode visit(@NotNull final StructNode structNode) {
        structNode.getVariables().forEach(this::visit);
        return this.visit(structNode.getTypeNode());
    }
    
    @Override
    public TypeNode visit(@NotNull final IfNode ifNode) {
        Objects.requireNonNull(ifNode.getExpression());
        Objects.requireNonNull(ifNode.getParser());
        Objects.requireNonNull(ifNode.getBlock());
    
        final TypeNode typeNode = this.visit(ifNode.getExpression());
        if (typeNode.getDataKind() != DataKind.INTEGER || typeNode.getBits() != 1)
            return this.addError(
                    ERROR_NODE,
                    ifNode.getParser().getCompilerClass(),
                    ifNode.getExpression(),
                    "The branch condition needs to be from a boolean type."
            );
    
        this.visit(ifNode.getBlock());
    
        if (ifNode.getNextBranch() != null)
            this.visit(ifNode.getNextBranch());
        return this.visit(ifNode.getTypeNode());
    }
    
    @Override
    public TypeNode visit(@NotNull final ElseNode elseNode) {
        Objects.requireNonNull(elseNode.getParser());
        Objects.requireNonNull(elseNode.getBlock());
    
        if (elseNode.getExpression() != null) {
            final TypeNode typeNode = this.visit(elseNode.getExpression());
            if (typeNode.getDataKind() != DataKind.INTEGER || typeNode.getBits() != 1)
                return this.addError(
                        ERROR_NODE,
                        elseNode.getParser().getCompilerClass(),
                        elseNode.getExpression(),
                        "The branch condition needs to be from a boolean type."
                );
        }
    
        this.visit(elseNode.getBlock());
    
        if (elseNode.getNextBranch() != null)
            this.visit(elseNode.getNextBranch());
        return this.visit(elseNode.getTypeNode());
    }
    
    public <E> E addError(
            @Nullable final E errorSource,
            @NotNull final CompilerClass compilerClass,
            @NotNull final ParserNode astNode,
            @NotNull final String causeMessage
    ) {
        compilerClass.getCompiler().getErrorHandler().addError(CompilerError.builder()
                .causePosition(ErrorPosition.builder()
                        .sourceCode(Objects.requireNonNull(astNode.getParser()).getCompilerClass().getContent())
                        .filePath(Objects.requireNonNull(astNode.getParser()).getCompilerClass().getFilePath())
                        .lineRange(Objects.requireNonNull(astNode.getLineRange()))
                        .charStart(Objects.requireNonNull(astNode.getStartToken()).getCharStart())
                        .charEnd(Objects.requireNonNull(astNode.getEndToken()).getCharEnd())
                        .build())
                .causeMessage(causeMessage)
                .build()
        );
        
        this.setFailed(true);
        return errorSource;
    }
    
}