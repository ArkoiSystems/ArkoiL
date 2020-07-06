/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on April 12, 2020
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
import com.arkoisystems.compiler.phases.lexer.token.LexerToken;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
@Setter
public class ScopeVisitor implements IVisitor<ParserNode>
{
    
    @NotNull
    private final Semantic semantic;
    
    public boolean failed;
    
    @NotNull
    @Override
    public TypeNode visit(@NotNull final TypeNode typeNode) {
        return typeNode;
    }
    
    @NotNull
    @Override
    public RootNode visit(@NotNull final RootNode rootNode) {
        rootNode.getNodes().forEach(this::visit);
        return rootNode;
    }
    
    @NotNull
    @Override
    public ParameterListNode visit(@NotNull final ParameterListNode parameterListNode) {
        parameterListNode.getParameters().forEach(this::visit);
        return parameterListNode;
    }
    
    @NotNull
    @Override
    public ParameterNode visit(@NotNull final ParameterNode parameter) {
        Objects.requireNonNull(parameter.getCurrentScope());
        Objects.requireNonNull(parameter.getParser());
        Objects.requireNonNull(parameter.getName());
    
        final List<ParserNode> identifiers = Objects.requireNonNullElse(
                parameter.getCurrentScope().lookupScope(parameter.getName().getTokenContent()),
                new ArrayList<>()
        );
        if (identifiers.size() > 1)
            return this.addError(
                    null,
                    parameter.getParser().getCompilerClass(),
                    identifiers.get(0),
                    "There already exists an identifier with the same name is this scope.",
                    identifiers.subList(1, identifiers.size()),
                    "Edit these identifiers to fix the problem."
            );
    
        return parameter;
    }
    
    @Override
    public ArgumentListNode visit(@NotNull final ArgumentListNode argumentListNode) {
        argumentListNode.getArguments().forEach(this::visit);
        return argumentListNode;
    }
    
    @Override
    public ParserNode visit(@NotNull final ArgumentNode argumentNode) {
        Objects.requireNonNull(argumentNode.getCurrentScope());
        Objects.requireNonNull(argumentNode.getExpression());
        Objects.requireNonNull(argumentNode.getParser());
    
        if (argumentNode.getName() == null) {
            this.visit(argumentNode.getExpression());
            return argumentNode;
        }
    
        final List<ParserNode> identifiers = Objects.requireNonNullElse(
                argumentNode.getCurrentScope().lookupScope(argumentNode.getName().getTokenContent()),
                new ArrayList<>()
        );
        if (identifiers.size() > 1)
            return this.addError(
                    null,
                    argumentNode.getParser().getCompilerClass(),
                    identifiers.get(0),
                    "There already exists an identifier with the same name is this scope.",
                    identifiers.subList(1, identifiers.size()),
                    "Edit these identifiers to fix the problem."
            );
    
        this.visit(argumentNode.getExpression());
        return argumentNode;
    }
    
    @NotNull
    @Override
    public BlockNode visit(@NotNull final BlockNode blockNode) {
        Objects.requireNonNull(blockNode.getParser());
    
        blockNode.getNodes().forEach(this::visit);
    
        final List<ReturnNode> returns = blockNode.getNodes().stream()
                .filter(node -> node instanceof ReturnNode)
                .map(node -> (ReturnNode) node)
                .collect(Collectors.toList());
        if (returns.isEmpty())
            return blockNode;
    
        final ReturnNode firstNode = returns.get(0);
        final int returnIndex = blockNode.getNodes().indexOf(firstNode);
        if (blockNode.getNodes().indexOf(firstNode) != blockNode.getNodes().size() - 1)
            return this.addError(
                    null,
                    blockNode.getParser().getCompilerClass(),
                    firstNode,
                    "Everything after a return statement is unreachable.",
                    blockNode.getNodes().subList(returnIndex + 1, blockNode.getNodes().size()),
                    "Remove these statements to fix the problem."
            );
    
        return blockNode;
    }
    
    @NotNull
    @Override
    public FunctionNode visit(@NotNull final FunctionNode functionNode) {
        Objects.requireNonNull(functionNode.getCurrentScope());
        Objects.requireNonNull(functionNode.getParameterList());
        Objects.requireNonNull(functionNode.getReturnType());
        Objects.requireNonNull(functionNode.getParser());
        Objects.requireNonNull(functionNode.getName());
    
        this.visit(functionNode.getParameterList());
        this.visit(functionNode.getReturnType());
    
        if (functionNode.getBlockNode() != null) {
            this.visit(functionNode.getBlockNode());
        
            final boolean hasReturn = functionNode.getBlockNode().getNodes().stream()
                    .filter(node -> node instanceof ReturnNode)
                    .count() == 1;
            if (!hasReturn && functionNode.getReturnType().getDataKind() != DataKind.VOID)
                return this.addError(
                        null,
                        functionNode.getParser().getCompilerClass(),
                        functionNode,
                        "Non void functions always need a return statement."
                );
        }
    
        final List<ParserNode> nodes = Objects.requireNonNullElse(functionNode.getParser()
                        .getCompilerClass()
                        .getRootScope()
                        .lookupScope(functionNode.getName().getTokenContent()),
                new ArrayList<>()
        );
    
        final List<FunctionNode> functions = nodes.stream()
                .filter(node -> node instanceof FunctionNode)
                .map(node -> (FunctionNode) node)
                .filter(node -> node.equalsToFunction(functionNode))
                .collect(Collectors.toList());
        if (functions.size() > 1)
            return this.addError(
                    null,
                    functionNode.getParser().getCompilerClass(),
                    functions.get(0),
                    "There already exists a function with the same name and arguments.",
                    functions.subList(1, nodes.size()),
                    "Edit these functions to fix the problem."
            );
        
        return functionNode;
    }
    
    @NotNull
    @Override
    public ImportNode visit(@NotNull final ImportNode importNode) {
        Objects.requireNonNull(importNode.getCurrentScope());
        Objects.requireNonNull(importNode.getFilePath());
        Objects.requireNonNull(importNode.getParser());
    
        if (importNode.resolveClass() == null)
            return this.addError(
                    null,
                    importNode.getParser().getCompilerClass(),
                    importNode.getFilePath(),
                    String.format(
                            "Path doesn't lead to any file %s.",
                            importNode.getFilePath().getTokenContent()
                    )
            );
    
        final List<ImportNode> imports = importNode.getParser().getRootNode().getNodes().stream()
                .filter(node -> node instanceof ImportNode)
                .map(node -> (ImportNode) node)
                .filter(node -> Objects.requireNonNull(node.getFilePath())
                        .getTokenContent().equals(importNode.getFilePath().getTokenContent()))
                .sorted(Comparator.comparingInt(ParserNode::getStartLine))
                .collect(Collectors.toList());
        if (imports.size() > 1)
            return this.addError(
                    null,
                    importNode.getParser().getCompilerClass(),
                    imports.get(0),
                    "There already exists another import with the same file path.",
                    imports.subList(1, imports.size()),
                    "Edit these imports to fix the problem."
            );
        
        return importNode;
    }
    
    @NotNull
    @Override
    public ReturnNode visit(@NotNull final ReturnNode returnNode) {
        if (returnNode.getExpression() != null)
            this.visit(returnNode.getExpression());
        return returnNode;
    }
    
    @NotNull
    @Override
    public VariableNode visit(@NotNull final VariableNode variableNode) {
        Objects.requireNonNull(variableNode.getCurrentScope());
        Objects.requireNonNull(variableNode.getParser());
        Objects.requireNonNull(variableNode.getName());
    
        final List<ParserNode> nodes = Objects.requireNonNullElse(
                variableNode.getCurrentScope().lookupScope(variableNode.getName().getTokenContent()),
                new ArrayList<>()
        );
        if (nodes.size() > 1) {
            return this.addError(
                    null,
                    variableNode.getParser().getCompilerClass(),
                    nodes.get(0),
                    "There already exists an identifier with the same name is this scope.3",
                    nodes.subList(1, nodes.size()),
                    "Edit these identifiers to fix the problem."
            );
        }
    
        if (variableNode.getExpression() != null)
            this.visit(variableNode.getExpression());
        return variableNode;
    }
    
    @NotNull
    @Override
    public StringNode visit(@NotNull final StringNode stringNode) {
        return stringNode;
    }
    
    @NotNull
    @Override
    public NumberNode visit(@NotNull final NumberNode numberNode) {
        return numberNode;
    }
    
    @Nullable
    @Override
    public ParserNode visit(@NotNull final IdentifierNode identifierNode) {
        Objects.requireNonNull(identifierNode.getCurrentScope());
        Objects.requireNonNull(identifierNode.getIdentifier());
        Objects.requireNonNull(identifierNode.getParser());
    
        final List<ParserNode> nodes = identifierNode.getCurrentScope().lookup(
                identifierNode.getIdentifier().getTokenContent(),
                node -> {
                    if (node instanceof FunctionNode)
                        return false;
                    if (node instanceof ArgumentNode)
                        return false;
                    if (node instanceof VariableNode) {
                        final VariableNode variableNode = (VariableNode) node;
                        if (!variableNode.isLocal())
                            return true;
                    }
                
                    return this.comesBefore(node, identifierNode);
                }
        );
        if (nodes.size() == 0)
            return this.addError(
                    null,
                    identifierNode.getParser().getCompilerClass(),
                    identifierNode.getIdentifier(),
                    String.format(
                            "Cannot resolve reference '%s'.",
                            identifierNode.getIdentifier().getTokenContent()
                    )
            );
        
        nodes.sort((o1, o2) -> o2.getStartLine() - o1.getStartLine());
        final ParserNode foundNode = nodes.get(0);
        identifierNode.setTargetNode(foundNode);

        if (identifierNode.getNextIdentifier() != null) {
            if (foundNode instanceof ImportNode) {
                final ImportNode importNode = (ImportNode) foundNode;
                final CompilerClass compilerClass = Objects.requireNonNull(importNode.resolveClass());
                identifierNode.getNextIdentifier().setCurrentScope(compilerClass.getRootScope());
                identifierNode.getNextIdentifier().setParser(compilerClass.getParser());
            } else if (foundNode instanceof VariableNode) {
                final VariableNode variableNode = (VariableNode) foundNode;
                final TypeNode typeNode = variableNode.getTypeNode();
    
                if (typeNode.getTargetNode() == null)
                    return null;
                
                identifierNode.getNextIdentifier().setCurrentScope(typeNode.getTargetNode().getCurrentScope());
                identifierNode.getNextIdentifier().setParser(typeNode.getTargetNode().getParser());
            }
            
            return this.visit(identifierNode.getNextIdentifier());
        }
        
        return foundNode;
    }
    
    @Override
    public ParserNode visit(@NotNull final FunctionCallNode functionCallNode) {
        Objects.requireNonNull(functionCallNode.getCurrentScope());
        Objects.requireNonNull(functionCallNode.getIdentifier());
        Objects.requireNonNull(functionCallNode.getParser());
    
        final List<ParserNode> nodes = functionCallNode.getParser().getCompilerClass()
                .getRootScope()
                .lookup(
                        functionCallNode.getIdentifier().getTokenContent(),
                        node -> node instanceof FunctionNode
                );
        if (nodes.size() == 0)
            return this.addError(
                    null,
                    functionCallNode.getParser().getCompilerClass(),
                    functionCallNode,
                    String.format(
                            "Cannot resolve reference '%s'.",
                            functionCallNode.getIdentifier().getTokenContent()
                    )
            );
    
        Objects.requireNonNull(functionCallNode.getArgumentList());
        
        final List<FunctionNode> functions = nodes.stream()
                .map(node -> (FunctionNode) node)
                .collect(Collectors.toList());
        final List<FunctionNode> matchingFunctions = functions.stream()
                .filter(node -> node.equalsToCall(functionCallNode))
                .collect(Collectors.toList());
        if (matchingFunctions.size() == 0)
            return this.addError(
                    null,
                    functionCallNode.getParser().getCompilerClass(),
                    functionCallNode,
                    "No matching function found with this name and arguments.",
                    functions,
                    "These functions matches with the identifier name. Did you mean one of those?"
            );
    
        this.visit(functionCallNode.getArgumentList());
        
        if (functionCallNode.getNextIdentifier() != null)
            throw new NullPointerException("Not implemented.");
        return functionCallNode;
    }
    
    @Override
    public AssignNode visit(@NotNull final AssignNode assignNode) {
        Objects.requireNonNull(assignNode.getExpression());
        Objects.requireNonNull(assignNode.getIdentifier());
        Objects.requireNonNull(assignNode.getParser());
    
        final ParserNode targetNode = this.visit((IdentifierNode) assignNode);
        if (targetNode instanceof ParameterNode) {
            this.visit(assignNode.getExpression());
            return assignNode;
        } else if (targetNode instanceof VariableNode) {
            final VariableNode variableNode = (VariableNode) targetNode;
            if (variableNode.isConstant())
                return this.addError(
                        null,
                        assignNode.getParser().getCompilerClass(),
                        assignNode,
                        "Can't re-assign a constant variable."
                );
        
            this.visit(assignNode.getExpression());
            return assignNode;
        } else return this.addError(
                null,
                assignNode.getParser().getCompilerClass(),
                assignNode,
                "Left side can't be assigned."
        );
    }
    
    @Override
    public StructCreateNode visit(@NotNull final StructCreateNode structCreateNode) {
        Objects.requireNonNull(structCreateNode.getArgumentList());
        Objects.requireNonNull(structCreateNode.getParser());
    
        if (this.visit((IdentifierNode) structCreateNode) == null)
            return null;
    
        final ParserNode targetNode = structCreateNode.getTypeNode().getTargetNode();
        if (!(targetNode instanceof StructNode))
            return this.addError(
                    null,
                    structCreateNode.getParser().getCompilerClass(),
                    structCreateNode,
                    "The target node is'nt a struct."
            );
    
        this.visit(structCreateNode.getArgumentList());
    
        final StructNode structNode = (StructNode) targetNode;
        for (final ArgumentNode argumentNode : structCreateNode.getArgumentList().getArguments()) {
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
                return this.addError(
                        null,
                        argumentNode.getParser().getCompilerClass(),
                        argumentNode,
                        String.format(
                                "Cannot resolve reference '%s'.",
                                argumentNode.getName().getTokenContent()
                        )
                );
        
            if (variableNode.isConstant())
                return this.addError(
                        null,
                        argumentNode.getParser().getCompilerClass(),
                        argumentNode,
                        "Can't re-assign a constant variable."
                );
        }
    
        return structCreateNode;
    }
    
    @Override
    public BinaryNode visit(@NotNull final BinaryNode binaryNode) {
        Objects.requireNonNull(binaryNode.getLeftHandSide());
        Objects.requireNonNull(binaryNode.getRightHandSide());
    
        this.visit(binaryNode.getLeftHandSide());
        this.visit(binaryNode.getRightHandSide());
        return binaryNode;
    }
    
    @Override
    public ParenthesizedNode visit(@NotNull final ParenthesizedNode parenthesizedNode) {
        Objects.requireNonNull(parenthesizedNode.getExpression());
        
        this.visit(parenthesizedNode.getExpression());
        return parenthesizedNode;
    }
    
    @Override
    public UnaryNode visit(@NotNull final UnaryNode unaryNode) {
        Objects.requireNonNull(unaryNode.getRightHandSide());
        
        this.visit(unaryNode.getRightHandSide());
        return unaryNode;
    }
    
    @Override
    public StructNode visit(@NotNull final StructNode structNode) {
        Objects.requireNonNull(structNode.getParser());
        Objects.requireNonNull(structNode.getParser().getRootNode().getCurrentScope());
        Objects.requireNonNull(structNode.getName());
    
        final List<ParserNode> nodes = structNode.getParser().getRootNode()
                .getCurrentScope()
                .lookup(structNode.getName().getTokenContent());
        if (nodes.size() > 1)
            return this.addError(
                    null,
                    structNode.getParser().getCompilerClass(),
                    nodes.get(0),
                    "There already exists an identifier with the same name is this scope.",
                    nodes.subList(1, nodes.size()),
                    "Edit these identifiers to fix the problem."
            );
    
        structNode.getVariables().forEach(this::visit);
        return structNode;
    }
    
    @Override
    public IfNode visit(@NotNull final IfNode ifNode) {
        Objects.requireNonNull(ifNode.getExpression());
        Objects.requireNonNull(ifNode.getBlock());
    
        this.visit(ifNode.getExpression());
        this.visit(ifNode.getBlock());
    
        if (ifNode.getNextBranch() != null)
            this.visit(ifNode.getNextBranch());
        return ifNode;
    }
    
    @Override
    public ElseNode visit(@NotNull final ElseNode elseNode) {
        Objects.requireNonNull(elseNode.getBlock());
    
        if (elseNode.getExpression() != null)
            this.visit(elseNode.getExpression());
        this.visit(elseNode.getBlock());
    
        if (elseNode.getNextBranch() != null)
            this.visit(elseNode.getNextBranch());
        return elseNode;
    }
    
    private boolean comesBefore(
            @NotNull final ParserNode firstNode,
            @NotNull final ParserNode secondNode
    ) {
        Objects.requireNonNull(secondNode.getStartToken());
        Objects.requireNonNull(firstNode.getStartToken());
        
        if (firstNode.getStartLine() == secondNode.getStartLine())
            return firstNode.getStartToken().getCharStart() < secondNode.getStartToken().getCharStart();
        return firstNode.getStartLine() < secondNode.getStartLine();
    }
    
    public <E> E addError(
            @Nullable final E errorSource,
            @NotNull final CompilerClass compilerClass,
            @NotNull final ParserNode causeNode,
            @NotNull final String causeMessage,
            @NotNull final List<? extends ParserNode> nodes,
            @NotNull final String otherMessage
    ) {
        compilerClass.getCompiler().getErrorHandler().addError(CompilerError.builder()
                .causePosition(ErrorPosition.builder()
                        .sourceCode(Objects.requireNonNull(causeNode.getParser()).getCompilerClass().getContent())
                        .filePath(Objects.requireNonNull(causeNode.getParser()).getCompilerClass().getFilePath())
                        .lineRange(Objects.requireNonNull(causeNode.getLineRange()))
                        .charStart(Objects.requireNonNull(causeNode.getStartToken()).getCharStart())
                        .charEnd(Objects.requireNonNull(causeNode.getEndToken()).getCharEnd())
                        .build())
                .causeMessage(causeMessage)
                .otherPositions(nodes.stream()
                        .map(node -> ErrorPosition.builder()
                                .sourceCode(Objects.requireNonNull(node.getParser()).getCompilerClass().getContent())
                                .filePath(Objects.requireNonNull(node.getParser()).getCompilerClass().getFilePath())
                                .lineRange(Objects.requireNonNull(node.getLineRange()))
                                .charStart(Objects.requireNonNull(node.getStartToken()).getCharStart())
                                .charEnd(Objects.requireNonNull(node.getEndToken()).getCharEnd())
                                .build())
                        .collect(Collectors.toList()))
                .otherMessage(otherMessage)
                .build()
        );
        
        this.setFailed(true);
        return errorSource;
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
    
    public <E> E addError(
            @Nullable final E errorSource,
            @NotNull final CompilerClass compilerClass,
            @NotNull final LexerToken lexerToken,
            @NotNull final String causeMessage
    ) {
        compilerClass.getCompiler().getErrorHandler().addError(CompilerError.builder()
                .causePosition(ErrorPosition.builder()
                        .sourceCode(lexerToken.getLexer().getCompilerClass().getContent())
                        .filePath(lexerToken.getLexer().getCompilerClass().getFilePath())
                        .lineRange(lexerToken.getLineRange())
                        .charStart(lexerToken.getCharStart())
                        .charEnd(lexerToken.getCharEnd())
                        .build())
                .causeMessage(causeMessage)
                .build()
        );
        
        this.setFailed(true);
        return errorSource;
    }
    
}
