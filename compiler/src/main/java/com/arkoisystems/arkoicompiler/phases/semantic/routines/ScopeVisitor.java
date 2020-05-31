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
package com.arkoisystems.arkoicompiler.phases.semantic.routines;

import com.arkoisystems.arkoicompiler.CompilerClass;
import com.arkoisystems.arkoicompiler.api.IFailed;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.errorHandling.CompilerError;
import com.arkoisystems.arkoicompiler.errorHandling.ErrorPosition;
import com.arkoisystems.arkoicompiler.phases.parser.ast.ParserNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.BlockNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.RootNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.TypeNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.OperableNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.IdentifierNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.NumberNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.StringNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.expression.ExpressionListNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.parameter.ParameterListNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.parameter.ParameterNode;
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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
@Setter
public class ScopeVisitor implements IVisitor<ParserNode>, IFailed
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
        for (final ParserNode node : rootNode.getNodes())
            this.visit(node);
        return rootNode;
    }
    
    @NotNull
    @Override
    public ParameterListNode visit(final @NotNull ParameterListNode parameterListNode) {
        for (final ParameterNode parameterNode : parameterListNode.getParameters())
            this.visit(parameterNode);
        return parameterListNode;
    }
    
    @NotNull
    @Override
    public ParameterNode visit(final @NotNull ParameterNode parameter) {
        return parameter;
    }
    
    @NotNull
    @Override
    public BlockNode visit(final @NotNull BlockNode blockNode) {
        for (final ParserNode node : blockNode.getNodes())
            this.visit(node);
        return blockNode;
    }
    
    @NotNull
    @Override
    public FunctionNode visit(final @NotNull FunctionNode functionNode) {
        Objects.requireNonNull(functionNode.getParameters(), "functionNode.parameters must not be null.");
        Objects.requireNonNull(functionNode.getReturnTypeNode(), "functionNode.returnTypeNode must not be null.");
        Objects.requireNonNull(functionNode.getBlockNode(), "functionNode.blockNode must not be null.");
    
        this.visit(functionNode.getParameters());
        this.visit(functionNode.getReturnTypeNode());
        this.visit(functionNode.getBlockNode());
        return functionNode;
    }
    
    @NotNull
    @Override
    public ImportNode visit(final @NotNull ImportNode importNode) {
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
        Objects.requireNonNull(variableNode.getExpression(), "variableNode.expression must not be null.");
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
        
        final List<ParserNode> nodes;
        if (identifierNode.isFileLocal()) {
            Objects.requireNonNull(identifierNode.getParser().getRootNode().getCurrentScope(), "rootNode.currentScope must not be null.");
            nodes = identifierNode.getParser().getRootNode().getCurrentScope().lookup(identifierNode.getIdentifier().getTokenContent());
        } else
            nodes = identifierNode.getCurrentScope().lookup(identifierNode.getIdentifier().getTokenContent());
        
        if (nodes == null || nodes.size() == 0)
            return this.addError(
                    null,
                    identifierNode.getParser().getCompilerClass(),
                    identifierNode,
                    "Cannot resolve reference '%s'.", identifierNode.getIdentifier().getTokenContent()
            );
        
        if (identifierNode.isFunctionCall()) {
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
                        functions,
                        "No function found with this identifier %s.%s",
                        identifierNode.getIdentifier().getTokenContent(),
                        !functions.isEmpty() ? " Did you mean one of these following functions?" : ""
                );

            this.visit(identifierNode.getExpressions());
        } else {
            // TODO: 6/1/20 Search for identifier
        }
        return identifierNode;
    }
    
    @Override
    public ExpressionListNode visit(final @NotNull ExpressionListNode expressionListNode) {
        for (final OperableNode operableNode : expressionListNode.getExpressions())
            this.visit(operableNode);
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
    
    public <E> E addError(@Nullable E errorSource, final @NotNull CompilerClass compilerClass, final @NotNull List<? extends ParserNode> nodes, final @NotNull String message, final @NotNull Object... arguments) {
        compilerClass.getCompiler().getErrorHandler().addError(CompilerError.builder()
                .compilerClass(compilerClass)
                .positions(nodes.stream()
                        .map(node -> ErrorPosition.builder()
                                .lineRange(Objects.requireNonNull(node.getLineRange(), "node.lineRange must not be null."))
                                .charStart(Objects.requireNonNull(node.getStartToken(), "node.startToken must not be null.").getCharStart())
                                .charEnd(Objects.requireNonNull(node.getEndToken(), "node.endToken must not be null.").getCharEnd())
                                .build())
                        .collect(Collectors.toList()))
                .message(message)
                .arguments(arguments)
                .build()
        );
        
        this.setFailed(true);
        return errorSource;
    }
    
    public <E> E addError(@Nullable E errorSource, final @NotNull CompilerClass compilerClass, final @NotNull ParserNode astNode, final @NotNull String message, final @NotNull Object... arguments) {
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
