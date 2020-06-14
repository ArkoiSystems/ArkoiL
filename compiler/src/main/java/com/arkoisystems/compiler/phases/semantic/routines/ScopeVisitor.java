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
import com.arkoisystems.compiler.phases.parser.ast.ParserNode;
import com.arkoisystems.compiler.phases.parser.ast.types.BlockNode;
import com.arkoisystems.compiler.phases.parser.ast.types.RootNode;
import com.arkoisystems.compiler.phases.parser.ast.types.StructNode;
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
    public TypeNode visit(final @NotNull TypeNode typeNode) {
        return typeNode;
    }
    
    @NotNull
    @Override
    public RootNode visit(final @NotNull RootNode rootNode) {
        rootNode.getNodes().forEach(this::visit);
        return rootNode;
    }
    
    @NotNull
    @Override
    public ParameterListNode visit(final @NotNull ParameterListNode parameterListNode) {
        parameterListNode.getParameters().forEach(this::visit);
        return parameterListNode;
    }
    
    @NotNull
    @Override
    public ParameterNode visit(final @NotNull ParameterNode parameter) {
        Objects.requireNonNull(parameter.getCurrentScope(), "parameter.currentScope must not be null.");
        Objects.requireNonNull(parameter.getParser(), "parameter.parser must not be null.");
        Objects.requireNonNull(parameter.getName(), "parameter.name must not be null.");
        
        final List<ParserNode> identifiers = parameter.getCurrentScope().lookupScope(parameter.getName().getTokenContent());
        if (identifiers != null && !identifiers.isEmpty() && identifiers.size() != 1) {
            return this.addError(
                    null,
                    parameter.getParser().getCompilerClass(),
                    identifiers.get(0),
                    "There already exists an identifier with the same name is this scope.",
                    identifiers.subList(1, identifiers.size()),
                    "Edit these identifiers to fix the problem."
            );
        }
        return parameter;
    }
    
    @NotNull
    @Override
    public BlockNode visit(final @NotNull BlockNode blockNode) {
        blockNode.getNodes().forEach(this::visit);
        return blockNode;
    }
    
    @NotNull
    @Override
    public FunctionNode visit(final @NotNull FunctionNode functionNode) {
        Objects.requireNonNull(functionNode.getParameters(), "functionNode.parameters must not be null.");
        Objects.requireNonNull(functionNode.getCurrentScope(), "functionNode.currentScope must not be null.");
        Objects.requireNonNull(functionNode.getParser(), "functionNode.parser must not be null.");
        Objects.requireNonNull(functionNode.getName(), "functionNode.name must not be null.");
        
        this.visit(functionNode.getParameters());
        
        if (functionNode.getReturnType() != null)
            this.visit(functionNode.getReturnType());
        
        if (functionNode.getBlockNode() != null)
            this.visit(functionNode.getBlockNode());
        
        final List<ParserNode> nodes = functionNode.getParser().getCompilerClass().getRootScope().lookupScope(functionNode.getName().getTokenContent());
        if (nodes == null)
            return functionNode;
        
        final List<FunctionNode> functions = nodes.stream()
                .filter(node -> node instanceof FunctionNode)
                .map(node -> (FunctionNode) node)
                .filter(node -> node.equalsToFunction(functionNode))
                .collect(Collectors.toList());
        if (!functions.isEmpty() && functions.size() != 1) {
            return this.addError(
                    null,
                    functionNode.getParser().getCompilerClass(),
                    functions.get(0),
                    "There already exists a function with the same name and arguments.",
                    functions.subList(1, nodes.size()),
                    "Edit these functions to fix the problem."
            );
        }
        
        return functionNode;
    }
    
    @NotNull
    @Override
    public ImportNode visit(final @NotNull ImportNode importNode) {
        Objects.requireNonNull(importNode.getCurrentScope(), "importNode.currentScope must not be null.");
        Objects.requireNonNull(importNode.getFilePath(), "importNode.filePath must not be null.");
        Objects.requireNonNull(importNode.getParser(), "importNode.parser must not be null.");
    
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
                .filter(node -> Objects.requireNonNull(node.getFilePath(), "node.filePath must not be null.").getTokenContent().equals(importNode.getFilePath().getTokenContent()))
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
    public ReturnNode visit(final @NotNull ReturnNode returnNode) {
        if (returnNode.getExpression() != null)
            this.visit(returnNode.getExpression());
        return returnNode;
    }
    
    @NotNull
    @Override
    public VariableNode visit(final @NotNull VariableNode variableNode) {
        Objects.requireNonNull(variableNode.getCurrentScope(), "variableNode.currentScope must not be null.");
        Objects.requireNonNull(variableNode.getParser(), "variableNode.parser must not be null.");
        Objects.requireNonNull(variableNode.getName(), "variableNode.name must not be null.");
        
        final List<ParserNode> nodes = variableNode.getCurrentScope().lookupScope(variableNode.getName().getTokenContent());
        if (nodes != null && !nodes.isEmpty() && nodes.size() != 1) {
            return this.addError(
                    null,
                    variableNode.getParser().getCompilerClass(),
                    nodes.get(0),
                    "There already exists an identifier with the same name is this scope.",
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
    public StringNode visit(final @NotNull StringNode stringNode) {
        return stringNode;
    }
    
    @NotNull
    @Override
    public NumberNode visit(final @NotNull NumberNode numberNode) {
        return numberNode;
    }
    
    @Override
    public IdentifierNode visit(final @NotNull IdentifierNode identifierNode) {
        Objects.requireNonNull(identifierNode.getCurrentScope(), "identifierNode.currentScope must not be null.");
        Objects.requireNonNull(identifierNode.getIdentifier(), "identifierNode.identifier must not be null.");
        Objects.requireNonNull(identifierNode.getParser(), "identifierNode.parser must not be null.");
    
    
        final ParserNode foundNode;
        if (identifierNode.isFunctionCall()) {
            final List<ParserNode> nodes = identifierNode.getParser().getCompilerClass().getRootScope().lookup(
                    identifierNode.getIdentifier().getTokenContent()
            );
            if (nodes == null || nodes.size() == 0)
                return this.addError(
                        null,
                        identifierNode.getParser().getCompilerClass(),
                        identifierNode,
                        String.format(
                                "Cannot resolve reference '%s'.",
                                identifierNode.getIdentifier().getTokenContent()
                        )
                );
        
            Objects.requireNonNull(identifierNode.getExpressions(), "identifierNode.expressions must not be null.");
            final List<FunctionNode> functions = nodes.stream()
                    .filter(node -> node instanceof FunctionNode)
                    .map(node -> (FunctionNode) node)
                    .collect(Collectors.toList());
            final List<FunctionNode> matchingFunctions = functions.stream()
                    .filter(node -> node.equalsToIdentifier(identifierNode))
                    .collect(Collectors.toList());
        
            if (matchingFunctions.size() == 0)
                return this.addError(
                        null,
                        identifierNode.getParser().getCompilerClass(),
                        identifierNode,
                        "No matching function found with this name and arguments.",
                        functions,
                        "These functions matches with the identifier name. Did you mean one of those?"
                );
            
            this.visit(identifierNode.getExpressions());
            foundNode = matchingFunctions.get(0);
        } else {
            final List<ParserNode> nodes = identifierNode.getCurrentScope().lookup(identifierNode.getIdentifier().getTokenContent());
            if (nodes == null || nodes.size() == 0)
                return this.addError(
                        null,
                        identifierNode.getParser().getCompilerClass(),
                        identifierNode,
                        String.format(
                                "Cannot resolve reference '%s'.",
                                identifierNode.getIdentifier().getTokenContent()
                        )
                );
        
            nodes.sort((o1, o2) -> o2.getStartLine() - o1.getStartLine());
            foundNode = nodes.get(0);
        }
        
        if (identifierNode.getNextIdentifier() != null) {
            if (foundNode instanceof ImportNode) {
                final ImportNode importNode = (ImportNode) foundNode;
                final CompilerClass compilerClass = Objects.requireNonNull(importNode.resolveClass(), "importNode.resolveClass must not be null.");
                identifierNode.getNextIdentifier().setCurrentScope(compilerClass.getRootScope());
                identifierNode.getNextIdentifier().setParser(compilerClass.getParser());
            } else throw new NullPointerException(foundNode.toString());
            
            this.visit(identifierNode.getNextIdentifier());
        }
        return identifierNode;
    }
    
    @Override
    public ExpressionListNode visit(final @NotNull ExpressionListNode expressionListNode) {
        expressionListNode.getExpressions().forEach(this::visit);
        return expressionListNode;
    }
    
    @Override
    public AssignmentNode visit(final @NotNull AssignmentNode assignmentNode) {
        Objects.requireNonNull(assignmentNode.getLeftHandSide(), "assignmentNode.leftHandSide must not be null.");
        Objects.requireNonNull(assignmentNode.getRightHandSide(), "assignmentNode.rightHandSide must not be null.");
        
        this.visit(assignmentNode.getLeftHandSide());
        this.visit(assignmentNode.getRightHandSide());
        return assignmentNode;
    }
    
    @Override
    public BinaryNode visit(final @NotNull BinaryNode binaryNode) {
        Objects.requireNonNull(binaryNode.getLeftHandSide(), "binaryNode.leftHandSide must not be null.");
        Objects.requireNonNull(binaryNode.getRightHandSide(), "binaryNode.rightHandSide must not be null.");
        
        this.visit(binaryNode.getLeftHandSide());
        this.visit(binaryNode.getRightHandSide());
        return binaryNode;
    }
    
    @Override
    public ParenthesizedNode visit(final @NotNull ParenthesizedNode parenthesizedNode) {
        Objects.requireNonNull(parenthesizedNode.getExpression(), "parenthesizedNode.expression must not be null.");
        this.visit(parenthesizedNode.getExpression());
        return parenthesizedNode;
    }
    
    @Override
    public PostfixNode visit(final @NotNull PostfixNode postfixNode) {
        Objects.requireNonNull(postfixNode.getLeftHandSide(), "postfixNode.leftHandSide must not be null.");
        this.visit(postfixNode.getLeftHandSide());
        return postfixNode;
    }
    
    @Override
    public PrefixNode visit(final @NotNull PrefixNode prefixNode) {
        Objects.requireNonNull(prefixNode.getRightHandSide(), "prefixNode.rightHandSide must not be null.");
        this.visit(prefixNode.getRightHandSide());
        return prefixNode;
    }
    
    @Override
    public StructNode visit(final @NotNull StructNode structNode) {
        return structNode;
    }
    
    public <E> E addError(
            final @Nullable E errorSource,
            final @NotNull CompilerClass compilerClass,
            final @NotNull ParserNode causeNode,
            final @NotNull String causeMessage,
            final @NotNull List<? extends ParserNode> nodes,
            final @NotNull String otherMessage
    ) {
        compilerClass.getCompiler().getErrorHandler().addError(CompilerError.builder()
                .causePosition(ErrorPosition.builder()
                        .sourceCode(Objects.requireNonNull(causeNode.getParser(), "causeNode.parser must not be null.").getCompilerClass().getContent())
                        .filePath(Objects.requireNonNull(causeNode.getParser(), "causeNode.parser must not be null.").getCompilerClass().getFilePath())
                        .lineRange(Objects.requireNonNull(causeNode.getLineRange(), "causeNode.lineRange must not be null."))
                        .charStart(Objects.requireNonNull(causeNode.getStartToken(), "causeNode.startToken must not be null.").getCharStart())
                        .charEnd(Objects.requireNonNull(causeNode.getEndToken(), "causeNode.endToken must not be null.").getCharEnd())
                        .build())
                .causeMessage(causeMessage)
                .otherPositions(nodes.stream()
                        .map(node -> ErrorPosition.builder()
                                .sourceCode(Objects.requireNonNull(node.getParser(), "node.parser must not be null.").getCompilerClass().getContent())
                                .filePath(Objects.requireNonNull(node.getParser(), "node.parser must not be null.").getCompilerClass().getFilePath())
                                .lineRange(Objects.requireNonNull(node.getLineRange(), "node.lineRange must not be null."))
                                .charStart(Objects.requireNonNull(node.getStartToken(), "node.startToken must not be null.").getCharStart())
                                .charEnd(Objects.requireNonNull(node.getEndToken(), "node.endToken must not be null.").getCharEnd())
                                .build())
                        .collect(Collectors.toList()))
                .otherMessage(otherMessage)
                .build()
        );
        
        this.setFailed(true);
        return errorSource;
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
