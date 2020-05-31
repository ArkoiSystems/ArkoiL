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
import com.arkoisystems.arkoicompiler.Compiler;
import com.arkoisystems.arkoicompiler.api.IFailed;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.errorHandling.CompilerError;
import com.arkoisystems.arkoicompiler.errorHandling.ErrorPosition;
import com.arkoisystems.arkoicompiler.errorHandling.LineRange;
import com.arkoisystems.arkoicompiler.phases.lexer.token.LexerToken;
import com.arkoisystems.arkoicompiler.phases.parser.ast.ParserNode;
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
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

public class ScopeVisitor implements IVisitor<ParserNode>, IFailed
{
    
    @NotNull
    @Getter
    private final List<HashMap<String, ParserNode>> scopeStack = new ArrayList<>();
    
    @Getter
    @NotNull
    private final HashMap<Integer, Integer> scopeIndexes = new HashMap<>();
    
    @NotNull
    @Getter
    private final Semantic semantic;
    
    @Getter
    @Setter
    private int currentIndex;
    
    @Getter
    @Setter
    private boolean failed;
    
    public ScopeVisitor(final @NotNull Semantic semantic) {
        this.semantic = semantic;
    }
    
    @NotNull
    @Override
    public TypeNode visit(final @NotNull TypeNode typeNode) {
        return typeNode;
    }
    
    @NotNull
    @Override
    public RootNode visit(final @NotNull RootNode rootNode) {
        this.getScopeStack().add(new HashMap<>());
        this.setCurrentIndex(0);
        
        for (final ParserNode astNode : rootNode.getNodes()) {
            if (astNode instanceof VariableNode)
                this.preVisit((VariableNode) astNode);
            else if (astNode instanceof FunctionNode)
                this.preVisit((FunctionNode) astNode);
            else if (astNode instanceof ImportNode)
                this.preVisit((ImportNode) astNode);
        }
        
        for (final ParserNode astNode : rootNode.getNodes())
            this.visit(astNode);
        return rootNode;
    }
    
    @NotNull
    @Override
    public ParameterListNode visit(final @NotNull ParameterListNode parameterListNode) {
        for (final ParameterNode parameterAST : parameterListNode.getParameters())
            this.visit(parameterAST);
        return parameterListNode;
    }
    
    @NotNull
    @Override
    public ParameterNode visit(final @NotNull ParameterNode parameter) {
        Objects.requireNonNull(parameter.getName(), "parameterAST.parameterName must not be null.");
        Objects.requireNonNull(parameter.getParser(), "parameterAST.parser must not be null.");
        
        final HashMap<String, ParserNode> currentScope = this.getScopeIndexes().containsKey(parameter.hashCode()) ?
                this.getScopeStack().get(this.getScopeIndexes().get(parameter.hashCode())) :
                this.getScopeStack().get(this.getCurrentIndex());
        if (!this.getScopeIndexes().containsKey(parameter.hashCode()))
            this.getScopeIndexes().put(parameter.hashCode(), this.getCurrentIndex());
        
        if (currentScope.containsKey(parameter.getName().getTokenContent())) {
            this.addError(
                    parameter,
                    parameter.getParser().getCompilerClass(),
        
                    parameter.getName(),
                    "Variable '%s' is already defined in the scope.", parameter.getName().getTokenContent()
            );
        } else
            currentScope.put(parameter.getName().getTokenContent(), parameter);
        return parameter;
    }
    
    @NotNull
    @Override
    public BlockNode visit(final @NotNull BlockNode blockNode) {
        for (final ParserNode astNode : blockNode.getNodes()) {
            if (astNode instanceof VariableNode)
                this.preVisit((VariableNode) astNode);
            this.visit(astNode);
        }
        return blockNode;
    }
    
    @NotNull
    @Override
    public ArgumentListNode visit(final @NotNull ArgumentListNode argumentListNode) {
        for (final ArgumentNode argumentNode : argumentListNode.getArguments())
            this.visit(argumentNode);
        return argumentListNode;
    }
    
    @NotNull
    @Override
    public ArgumentNode visit(final @NotNull ArgumentNode argumentNode) {
        Objects.requireNonNull(argumentNode.getName(), "argumentAST.argumentName must not be null.");
        Objects.requireNonNull(argumentNode.getParser(), "argumentAST.parser must not be null.");
        
        final HashMap<String, ParserNode> currentScope = this.getScopeIndexes().containsKey(argumentNode.hashCode()) ?
                this.getScopeStack().get(this.getScopeIndexes().get(argumentNode.hashCode())) :
                this.getScopeStack().get(this.getCurrentIndex());
        if (!this.getScopeIndexes().containsKey(argumentNode.hashCode()))
            this.getScopeIndexes().put(argumentNode.hashCode(), this.getCurrentIndex());
        
        if (currentScope.containsKey(argumentNode.getName().getTokenContent())) {
            this.addError(
                    argumentNode,
                    argumentNode.getParser().getCompilerClass(),
        
                    argumentNode.getName(),
                    "Variable '%s' is already defined in the scope.", argumentNode.getName().getTokenContent()
            );
        } else
            currentScope.put(argumentNode.getName().getTokenContent(), argumentNode);
        return argumentNode;
    }
    
    public void preVisit(final @NotNull FunctionNode functionStatement) {
        Objects.requireNonNull(functionStatement.getName(), "functionAST.functionName must not be null.");
        Objects.requireNonNull(functionStatement.getParser(), "functionAST.parser must not be null.");
        
        final HashMap<String, ParserNode> rootScope = this.getScopeStack().get(0);
        if (rootScope.containsKey(functionStatement.getFunctionDescription())) {
            this.addError(
                    functionStatement,
                    functionStatement.getParser().getCompilerClass(),
                    
                    functionStatement.getName(),
                    "Function '%s' is already defined in the scope.", functionStatement.getFunctionDescription()
            );
        } else
            rootScope.put(functionStatement.getFunctionDescription(), functionStatement);
    }
    
    @NotNull
    @Override
    public FunctionNode visit(final @NotNull FunctionNode functionNode) {
        Objects.requireNonNull(functionNode.getParameters(), "functionAST.parameters must not be null.");
        Objects.requireNonNull(functionNode.getBlockNode(), "functionAST.block must not be null.");
        
        this.getScopeStack().add(new HashMap<>());
        this.setCurrentIndex(this.getCurrentIndex() + 1);
        if (!this.getScopeIndexes().containsKey(functionNode.hashCode()))
            this.getScopeIndexes().put(functionNode.hashCode(), this.getCurrentIndex());
        
        this.visit(functionNode.getParameters());
        this.visit(functionNode.getBlockNode());
        return functionNode;
    }
    
    public void preVisit(final @NotNull ImportNode importStatement) {
        Objects.requireNonNull(importStatement.getName(), "importAST.importName must not be null.");
        Objects.requireNonNull(importStatement.getParser(), "importAST.parser must not be null.");
        
        final HashMap<String, ParserNode> rootScope = this.getScopeStack().get(0);
        if (rootScope.containsKey(importStatement.getName().getTokenContent())) {
            this.addError(
                    importStatement,
                    importStatement.getParser().getCompilerClass(),
                    
                    importStatement.getName(),
                    "Variable '%s' is already defined in the scope.", importStatement.getName().getTokenContent()
            );
        } else
            rootScope.put(importStatement.getName().getTokenContent(), importStatement);
    }
    
    @NotNull
    @Override
    public ImportNode visit(final @NotNull ImportNode importNode) {
        this.resolveClass(importNode);
        return importNode;
    }
    
    @NotNull
    @Override
    public ReturnNode visit(final @NotNull ReturnNode returnNode) {
        if (returnNode.getExpression() != null)
            this.visit(returnNode.getExpression());
        return returnNode;
    }
    
    public void preVisit(final @NotNull VariableNode variableStatement) {
        Objects.requireNonNull(variableStatement.getName(), "variableAST.variableName must not be null.");
        Objects.requireNonNull(variableStatement.getParser(), "variableAST.parser must not be null.");
        
        final HashMap<String, ParserNode> currentScope = this.getScopeIndexes().containsKey(variableStatement.hashCode()) ?
                this.getScopeStack().get(this.getScopeIndexes().get(variableStatement.hashCode())) :
                this.getScopeStack().get(this.getCurrentIndex());
        if (!this.getScopeIndexes().containsKey(variableStatement.hashCode()))
            this.getScopeIndexes().put(variableStatement.hashCode(), this.getCurrentIndex());
        
        if (currentScope.containsKey(variableStatement.getName().getTokenContent())) {
            this.addError(
                    variableStatement,
                    variableStatement.getParser().getCompilerClass(),
                    
                    variableStatement.getName(),
                    "Variable '%s' is already defined in the scope.", variableStatement.getName().getTokenContent()
            );
        } else
            currentScope.put(variableStatement.getName().getTokenContent(), variableStatement);
    }
    
    @NotNull
    @Override
    public VariableNode visit(final @NotNull VariableNode variableNode) {
        Objects.requireNonNull(variableNode.getExpression(), "variableAST.variableExpression must not be null.");
        
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
    
    @Nullable
    @Override
    public ParserNode visit(final @NotNull IdentifierNode identifierNode) {
        Objects.requireNonNull(identifierNode.getIdentifier(), "identifierCallAST.calledIdentifier must not be null.");
        Objects.requireNonNull(identifierNode.getParser(), "identifierCallAST.parser must not be null.");
        
        final HashMap<String, ParserNode> currentScope = this.getScopeIndexes().containsKey(identifierNode.hashCode()) ?
                this.getScopeStack().get(this.getScopeIndexes().get(identifierNode.hashCode())) :
                this.getScopeStack().get(this.getCurrentIndex());
        if (!this.getScopeIndexes().containsKey(identifierNode.hashCode()))
            this.getScopeIndexes().put(identifierNode.hashCode(), this.getCurrentIndex());
        
        ParserNode foundAST = null;
        if (!identifierNode.isFileLocal() && currentScope.containsKey(identifierNode.getDescriptor()))
            foundAST = currentScope.get(identifierNode.getDescriptor());
        if (foundAST == null && this.getScopeStack().get(0).containsKey(identifierNode.getDescriptor()))
            foundAST = this.getScopeStack().get(0).get(identifierNode.getDescriptor());
        
        // TODO: 5/27/20 Do better resolving (variadic)
        if (foundAST == null) {
            final LineRange lineRange;
            final int charStart, charEnd;
            
            if (identifierNode.isFunctionCall()) {
                Objects.requireNonNull(identifierNode.getExpressionListNode(), "identifierOperable.expressionList must not be null.");
                Objects.requireNonNull(identifierNode.getExpressionListNode().getLineRange(), "identifierOperable.expressionList.lineRange must not be null.");
                Objects.requireNonNull(identifierNode.getExpressionListNode().getEndToken(), "identifierOperable.expressionList.endToken must not be null.");
                
                lineRange = LineRange.make(
                        identifierNode.getParser().getCompilerClass(),
                        identifierNode.getIdentifier().getLineRange().getStartLine(),
                        identifierNode.getExpressionListNode().getLineRange().getEndLine()
                );
                charStart = identifierNode.getIdentifier().getCharStart();
                charEnd = identifierNode.getExpressionListNode().getEndToken().getCharEnd();
            } else {
                Objects.requireNonNull(identifierNode.getLineRange(), "identifierCallAST.lineRange must not be null.");
                
                lineRange = identifierNode.getLineRange();
                charStart = identifierNode.getIdentifier().getCharStart();
                charEnd = identifierNode.getIdentifier().getCharEnd();
            }
        
            return this.addError(
                    null,
                    identifierNode.getParser().getCompilerClass(),
        
                    charStart,
                    charEnd,
                    lineRange,
        
                    "Cannot resolve reference '%s'.", identifierNode.getIdentifier().getTokenContent()
            );
        }
        
        if (identifierNode.isFunctionCall()) {
            Objects.requireNonNull(identifierNode.getExpressionListNode(), "identifierOperable.expressionList must not be null.");
            this.visit(identifierNode.getExpressionListNode());
        }
        
        if (identifierNode.getNextIdentifier() == null)
            return foundAST;
        
        CompilerClass resolvedClass = this.resolveClass(foundAST);
        if (resolvedClass == null)
            return foundAST;
        
        final ScopeVisitor scopeVisitor = new ScopeVisitor(resolvedClass.getSemantic());
        scopeVisitor.visit(resolvedClass.getParser().getRootNodeAST());
        foundAST = scopeVisitor.visit(identifierNode.getNextIdentifier());
        if (scopeVisitor.isFailed())
            this.setFailed(true);
        if (foundAST == null)
            return null;
        return this.visit(foundAST);
    }
    
    @SneakyThrows
    @Nullable
    private CompilerClass resolveClass(final @NotNull ParserNode foundAST) {
        Objects.requireNonNull(foundAST.getParser(), "foundAST.parser must not be null.");
    
        if (foundAST instanceof ImportNode) {
            final ImportNode importStatement = (ImportNode) foundAST;
        
            Objects.requireNonNull(importStatement.getFilePath(), "importAST.importFilePath must not be null.");
            Objects.requireNonNull(importStatement.getParser(), "importAST.parser must not be null.");
        
            File targetFile = new File(importStatement.getFilePath().getTokenContent() + ".ark");
            if (!targetFile.isAbsolute()) {
                targetFile = new File(new File(this.getSemantic().getCompilerClass().getFilePath()).getParent(), importStatement.getFilePath().getTokenContent() + ".ark");
            
                if (!targetFile.exists()) {
                    for (final File libraryDirectory : this.getSemantic().getCompilerClass().getCompiler().getLibraryPaths()) {
                        final File file = new File(libraryDirectory.getPath(), importStatement.getFilePath().getTokenContent() + ".ark");
                        if (!file.exists())
                            continue;
                    
                        targetFile = file;
                        break;
                    }
                }
            }
    
            if (!targetFile.exists())
                return this.addError(
                        null,
                        importStatement.getParser().getCompilerClass(),
                        importStatement.getFilePath(),
                        "Path doesn't lead to file '%s'.", importStatement.getFilePath().getTokenContent()
                );
    
            final Compiler compiler = this.getSemantic().getCompilerClass().getCompiler();
            for (final CompilerClass compilerClass : compiler.getClasses())
                if (compilerClass.getFilePath().equals(targetFile.getCanonicalPath()))
                    return compilerClass;
    
            final CompilerClass compilerClass = new CompilerClass(compiler, targetFile.getCanonicalPath(), Files.readAllBytes(targetFile.toPath()));
            compiler.getClasses().add(compilerClass);
    
            compilerClass.getLexer().processStage();
            compilerClass.getParser().processStage();
            compilerClass.getSemantic().processStage();
            return compilerClass;
        }
        return null;
    }
    
    @Override
    public ExpressionListNode visit(final @NotNull ExpressionListNode expressionListNode) {
        for (final OperableNode operableNode : expressionListNode.getExpressions())
            this.visit(operableNode);
        return expressionListNode;
    }
    
    @NotNull
    @Override
    public AssignmentNode visit(final @NotNull AssignmentNode assignmentNode) {
        Objects.requireNonNull(assignmentNode.getLeftHandSide(), "assignmentExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(assignmentNode.getRightHandSide(), "assignmentExpressionAST.rightSideOperable must not be null.");
        
        this.visit(assignmentNode.getLeftHandSide());
        this.visit(assignmentNode.getRightHandSide());
        return assignmentNode;
    }
    
    @NotNull
    @Override
    public BinaryNode visit(final @NotNull BinaryNode binaryNode) {
        Objects.requireNonNull(binaryNode.getLeftHandSide(), "binaryExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(binaryNode.getRightHandSide(), "binaryExpressionAST.rightSideOperable must not be null.");
        
        this.visit(binaryNode.getLeftHandSide());
        this.visit(binaryNode.getRightHandSide());
        return binaryNode;
    }
    
    @NotNull
    @Override
    public ParenthesizedNode visit(final @NotNull ParenthesizedNode parenthesizedNode) {
        Objects.requireNonNull(parenthesizedNode.getExpression(), "parenthesizedExpressionAST.parenthesizedExpression must not be null.");
        
        this.visit(parenthesizedNode.getExpression());
        return parenthesizedNode;
    }
    
    @NotNull
    @Override
    public PostfixNode visit(final @NotNull PostfixNode postfixNode) {
        Objects.requireNonNull(postfixNode.getLeftHandSide(), "postfixExpressionAST.leftSideOperable must not be null.");
        
        this.visit(postfixNode.getLeftHandSide());
        return postfixNode;
    }
    
    @NotNull
    @Override
    public PrefixNode visit(final @NotNull PrefixNode prefixNode) {
        Objects.requireNonNull(prefixNode.getRightHandSide(), "prefixExpressionAST.rightSideOperable must not be null.");
        
        this.visit(prefixNode.getRightHandSide());
        return prefixNode;
    }
    
    @Nullable
    public <E> E addError(@Nullable E errorSource, final @NotNull CompilerClass compilerClass, final @NotNull ParserNode astNode, final @NotNull String message, final @NotNull Object... arguments) {
        Objects.requireNonNull(astNode.getLineRange(), "astNode.lineRange must not be null.");
        
        compilerClass.getCompiler().getErrorHandler().addError(CompilerError.builder()
                .compilerClass(compilerClass)
                .positions(Collections.singletonList(ErrorPosition.builder()
                        .lineRange(astNode.getLineRange())
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
    
    @Nullable
    public <E> E addError(@Nullable E errorSource, final @NotNull CompilerClass compilerClass, final @NotNull LexerToken lexerToken, final @NotNull String message, final @NotNull Object... arguments) {
        compilerClass.getCompiler().getErrorHandler().addError(CompilerError.builder()
                .compilerClass(compilerClass)
                .positions(Collections.singletonList(ErrorPosition.builder()
                        .lineRange(lexerToken.getLineRange())
                        .charStart(lexerToken.getCharStart())
                        .charEnd(lexerToken.getCharEnd())
                        .build()))
                .message(message)
                .arguments(arguments)
                .build()
        );
    
        this.setFailed(true);
        return errorSource;
    }
    
    @Nullable
    public <E> E addError(@Nullable E errorSource, final @NotNull CompilerClass compilerClass, final int start, final int end, final @NotNull LineRange lineRange, final @NotNull String message, final @NotNull Object... arguments) {
        compilerClass.getCompiler().getErrorHandler().addError(CompilerError.builder()
                .compilerClass(compilerClass)
                .positions(Collections.singletonList(ErrorPosition.builder()
                        .lineRange(lineRange)
                        .charStart(start)
                        .charEnd(end)
                        .build()))
                .message(message)
                .arguments(arguments)
                .build()
        );
        
        this.setFailed(true);
        return errorSource;
    }
    
}
