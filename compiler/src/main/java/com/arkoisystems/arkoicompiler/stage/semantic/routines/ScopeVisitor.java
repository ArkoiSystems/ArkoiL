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
package com.arkoisystems.arkoicompiler.stage.semantic.routines;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.ArkoiCompiler;
import com.arkoisystems.arkoicompiler.api.IFailed;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.error.ArkoiError;
import com.arkoisystems.arkoicompiler.error.ErrorPosition;
import com.arkoisystems.arkoicompiler.error.LineRange;
import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.parser.ast.ArkoiNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.*;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.OperableNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.*;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.FunctionNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.ImportNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.ReturnNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.VariableNode;
import com.arkoisystems.arkoicompiler.stage.semantic.Semantic;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

public class ScopeVisitor implements IVisitor<ArkoiNode>, IFailed
{
    
    @NotNull
    @Getter
    private final List<HashMap<String, ArkoiNode>> scopeStack = new ArrayList<>();
    
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
    public TypeNode visit(final @NotNull TypeNode typeAST) {
        return typeAST;
    }
    
    @NotNull
    @Override
    public RootNode visit(final @NotNull RootNode rootAST) {
        this.getScopeStack().add(new HashMap<>());
        this.setCurrentIndex(0);
        
        for (final ArkoiNode astNode : rootAST.getNodes()) {
            if (astNode instanceof VariableNode)
                this.preVisit((VariableNode) astNode);
            else if (astNode instanceof FunctionNode)
                this.preVisit((FunctionNode) astNode);
            else if (astNode instanceof ImportNode)
                this.preVisit((ImportNode) astNode);
        }
        
        for (final ArkoiNode astNode : rootAST.getNodes())
            this.visit(astNode);
        return rootAST;
    }
    
    @NotNull
    @Override
    public ParameterListNode visit(final @NotNull ParameterListNode parameterListAST) {
        for (final ParameterNode parameterAST : parameterListAST.getParameters())
            this.visit(parameterAST);
        return parameterListAST;
    }
    
    @NotNull
    @Override
    public ParameterNode visit(final @NotNull ParameterNode parameterAST) {
        Objects.requireNonNull(parameterAST.getName(), "parameterAST.parameterName must not be null.");
        Objects.requireNonNull(parameterAST.getParser(), "parameterAST.parser must not be null.");
        
        final HashMap<String, ArkoiNode> currentScope = this.getScopeIndexes().containsKey(parameterAST.hashCode()) ?
                this.getScopeStack().get(this.getScopeIndexes().get(parameterAST.hashCode())) :
                this.getScopeStack().get(this.getCurrentIndex());
        if(!this.getScopeIndexes().containsKey(parameterAST.hashCode()))
            this.getScopeIndexes().put(parameterAST.hashCode(), this.getCurrentIndex());
        
        if (currentScope.containsKey(parameterAST.getName().getTokenContent())) {
            this.addError(
                    parameterAST,
                    parameterAST.getParser().getCompilerClass(),
        
                    parameterAST.getName(),
                    "Variable '%s' is already defined in the scope.", parameterAST.getName().getTokenContent()
            );
        } else
            currentScope.put(parameterAST.getName().getTokenContent(), parameterAST);
        return parameterAST;
    }
    
    @NotNull
    @Override
    public BlockNode visit(final @NotNull BlockNode blockAST) {
        for (final ArkoiNode astNode : blockAST.getNodes()) {
            if (astNode instanceof VariableNode)
                this.preVisit((VariableNode) astNode);
            this.visit(astNode);
        }
        return blockAST;
    }
    
    @NotNull
    @Override
    public ArgumentListNode visit(final @NotNull ArgumentListNode argumentListAST) {
        for (final ArgumentNode argumentAST : argumentListAST.getArguments())
            this.visit(argumentAST);
        return argumentListAST;
    }
    
    @NotNull
    @Override
    public ArgumentNode visit(final @NotNull ArgumentNode argumentAST) {
        Objects.requireNonNull(argumentAST.getName(), "argumentAST.argumentName must not be null.");
        Objects.requireNonNull(argumentAST.getParser(), "argumentAST.parser must not be null.");
        
        final HashMap<String, ArkoiNode> currentScope = this.getScopeIndexes().containsKey(argumentAST.hashCode()) ?
                this.getScopeStack().get(this.getScopeIndexes().get(argumentAST.hashCode())) :
                this.getScopeStack().get(this.getCurrentIndex());
        if(!this.getScopeIndexes().containsKey(argumentAST.hashCode()))
            this.getScopeIndexes().put(argumentAST.hashCode(), this.getCurrentIndex());
        
        if (currentScope.containsKey(argumentAST.getName().getTokenContent())) {
            this.addError(
                    argumentAST,
                    argumentAST.getParser().getCompilerClass(),
        
                    argumentAST.getName(),
                    "Variable '%s' is already defined in the scope.", argumentAST.getName().getTokenContent()
            );
        } else
            currentScope.put(argumentAST.getName().getTokenContent(), argumentAST);
        return argumentAST;
    }
    
    public void preVisit(final @NotNull FunctionNode functionAST) {
        Objects.requireNonNull(functionAST.getName(), "functionAST.functionName must not be null.");
        Objects.requireNonNull(functionAST.getParser(), "functionAST.parser must not be null.");
        
        final HashMap<String, ArkoiNode> rootScope = this.getScopeStack().get(0);
        if (rootScope.containsKey(functionAST.getFunctionDescription())) {
            this.addError(
                    functionAST,
                    functionAST.getParser().getCompilerClass(),
        
                    functionAST.getName(),
                    "Function '%s' is already defined in the scope.", functionAST.getFunctionDescription()
            );
        } else rootScope.put(functionAST.getFunctionDescription(), functionAST);
    }
    
    @NotNull
    @Override
    public FunctionNode visit(final @NotNull FunctionNode functionAST) {
        Objects.requireNonNull(functionAST.getParameters(), "functionAST.parameters must not be null.");
        Objects.requireNonNull(functionAST.getBlock(), "functionAST.block must not be null.");
        
        this.getScopeStack().add(new HashMap<>());
        this.setCurrentIndex(this.getCurrentIndex() + 1);
        if(!this.getScopeIndexes().containsKey(functionAST.hashCode()))
            this.getScopeIndexes().put(functionAST.hashCode(), this.getCurrentIndex());
        
        this.visit(functionAST.getParameters());
        this.visit(functionAST.getBlock());
        return functionAST;
    }
    
    public void preVisit(final @NotNull ImportNode importAST) {
        Objects.requireNonNull(importAST.getName(), "importAST.importName must not be null.");
        Objects.requireNonNull(importAST.getParser(), "importAST.parser must not be null.");
        
        final HashMap<String, ArkoiNode> rootScope = this.getScopeStack().get(0);
        if (rootScope.containsKey(importAST.getName().getTokenContent())) {
            this.addError(
                    importAST,
                    importAST.getParser().getCompilerClass(),
        
                    importAST.getName(),
                    "Variable '%s' is already defined in the scope.", importAST.getName().getTokenContent()
            );
        } else rootScope.put(importAST.getName().getTokenContent(), importAST);
    }
    
    @NotNull
    @Override
    public ImportNode visit(final @NotNull ImportNode importAST) {
        return importAST;
    }
    
    @NotNull
    @Override
    public ReturnNode visit(final @NotNull ReturnNode returnAST) {
        Objects.requireNonNull(returnAST.getExpression(), "returnAST.returnExpression must not be null.");
        
        this.visit(returnAST.getExpression());
        return returnAST;
    }
    
    public void preVisit(final @NotNull VariableNode variableAST) {
        Objects.requireNonNull(variableAST.getName(), "variableAST.variableName must not be null.");
        Objects.requireNonNull(variableAST.getParser(), "variableAST.parser must not be null.");
        
        final HashMap<String, ArkoiNode> currentScope = this.getScopeIndexes().containsKey(variableAST.hashCode()) ?
                this.getScopeStack().get(this.getScopeIndexes().get(variableAST.hashCode())) :
                this.getScopeStack().get(this.getCurrentIndex());
        if(!this.getScopeIndexes().containsKey(variableAST.hashCode()))
            this.getScopeIndexes().put(variableAST.hashCode(), this.getCurrentIndex());
        
        if (currentScope.containsKey(variableAST.getName().getTokenContent())) {
            this.addError(
                    variableAST,
                    variableAST.getParser().getCompilerClass(),
        
                    variableAST.getName(),
                    "Variable '%s' is already defined in the scope.", variableAST.getName().getTokenContent()
            );
        } else
            currentScope.put(variableAST.getName().getTokenContent(), variableAST);
    }
    
    @NotNull
    @Override
    public VariableNode visit(final @NotNull VariableNode variableAST) {
        Objects.requireNonNull(variableAST.getExpression(), "variableAST.variableExpression must not be null.");
        
        this.visit(variableAST.getExpression());
        return variableAST;
    }
    
    @NotNull
    @Override
    public StringNode visit(final @NotNull StringNode stringAST) {
        return stringAST;
    }
    
    @NotNull
    @Override
    public NumberNode visit(final @NotNull NumberNode numberAST) {
        return numberAST;
    }
    
    @Nullable
    @Override
    public ArkoiNode visit(final @NotNull IdentifierCallNode identifierCallAST) {
        Objects.requireNonNull(identifierCallAST.getIdentifier(), "identifierCallAST.calledIdentifier must not be null.");
        Objects.requireNonNull(identifierCallAST.getParser(), "identifierCallAST.parser must not be null.");
        
        final HashMap<String, ArkoiNode> currentScope = this.getScopeIndexes().containsKey(identifierCallAST.hashCode()) ?
                this.getScopeStack().get(this.getScopeIndexes().get(identifierCallAST.hashCode())) :
                this.getScopeStack().get(this.getCurrentIndex());
        if (!this.getScopeIndexes().containsKey(identifierCallAST.hashCode()))
            this.getScopeIndexes().put(identifierCallAST.hashCode(), this.getCurrentIndex());
    
        ArkoiNode foundAST = null;
        if (!identifierCallAST.isFileLocal() && currentScope.containsKey(identifierCallAST.getDescriptor()))
            foundAST = currentScope.get(identifierCallAST.getDescriptor());
        if (foundAST == null && this.getScopeStack().get(0).containsKey(identifierCallAST.getDescriptor()))
            foundAST = this.getScopeStack().get(0).get(identifierCallAST.getDescriptor());
    
        // TODO: 5/27/20 Do better resolving (variadic)
        if (foundAST == null) {
            final LineRange lineRange;
            final int charStart, charEnd;
        
            if (identifierCallAST.getFunctionPart() != null) {
                Objects.requireNonNull(identifierCallAST.getFunctionPart().getLineRange(), "identifierCallAST.functionPart.lineRange must not be null.");
                Objects.requireNonNull(identifierCallAST.getFunctionPart().getEndToken(), "identifierCallAST.functionPart.endToken must not be null.");
            
                lineRange = LineRange.make(
                        identifierCallAST.getParser().getCompilerClass(),
                        identifierCallAST.getIdentifier().getLineRange().getStartLine(),
                        Objects.requireNonNull(
                                identifierCallAST.getFunctionPart().getLineRange(),
                                "identifierCallAST.functionPart.lineRange must not be null."
                        ).getEndLine()
                );
                charStart = identifierCallAST.getIdentifier().getCharStart();
                charEnd = identifierCallAST.getFunctionPart().getEndToken().getCharEnd();
            } else {
                Objects.requireNonNull(identifierCallAST.getLineRange(), "identifierCallAST.lineRange must not be null.");
            
                lineRange = identifierCallAST.getLineRange();
                charStart = identifierCallAST.getIdentifier().getCharStart();
                charEnd = identifierCallAST.getIdentifier().getCharEnd();
            }
        
            return this.addError(
                    null,
                    identifierCallAST.getParser().getCompilerClass(),
                
                    charStart,
                    charEnd,
                    lineRange,
                
                    "Cannot resolve reference '%s'.", identifierCallAST.getIdentifier().getTokenContent()
            );
        }
        
        if (identifierCallAST.getFunctionPart() != null)
            this.visit(identifierCallAST.getFunctionPart());
    
        if (identifierCallAST.getNextIdentifierCall() == null)
            return foundAST;
    
        ArkoiClass resolvedClass = this.resolveClass(foundAST);
        if (resolvedClass == null)
            return foundAST;
    
        final ScopeVisitor scopeVisitor = new ScopeVisitor(resolvedClass.getSemantic());
        scopeVisitor.visit(resolvedClass.getParser().getRootAST());
        foundAST = scopeVisitor.visit(identifierCallAST.getNextIdentifierCall());
        if (scopeVisitor.isFailed())
            this.setFailed(true);
        if (foundAST == null)
            return null;
        return this.visit(foundAST);
    }
    
    @SneakyThrows
    @Nullable
    private ArkoiClass resolveClass(final @NotNull ArkoiNode foundAST) {
        Objects.requireNonNull(foundAST.getParser(), "foundAST.parser must not be null.");
        
        if (foundAST instanceof ImportNode) {
            final ImportNode importAST = (ImportNode) foundAST;
    
            Objects.requireNonNull(importAST.getFilePath(), "importAST.importFilePath must not be null.");
            Objects.requireNonNull(importAST.getParser(), "importAST.parser must not be null.");
    
            File targetFile = new File(importAST.getFilePath().getTokenContent() + ".ark");
            if (!targetFile.isAbsolute()) {
                targetFile = new File(new File(this.getSemantic().getCompilerClass().getFilePath()).getParent(), importAST.getFilePath().getTokenContent() + ".ark");
                
                if (!targetFile.exists()) {
                    for (final File libraryDirectory : this.getSemantic().getCompilerClass().getCompiler().getLibraryPaths()) {
                        final File file = new File(libraryDirectory.getPath(), importAST.getFilePath().getTokenContent() + ".ark");
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
                        importAST.getParser().getCompilerClass(),
                        importAST.getFilePath(),
                        "Path doesn't lead to file '%s'.", importAST.getFilePath().getTokenContent()
                );
    
            final ArkoiCompiler arkoiCompiler = this.getSemantic().getCompilerClass().getCompiler();
            for (final ArkoiClass compilerClass : arkoiCompiler.getArkoiClasses())
                if (compilerClass.getFilePath().equals(targetFile.getCanonicalPath()))
                    return compilerClass;
    
            final ArkoiClass arkoiClass = new ArkoiClass(arkoiCompiler, targetFile.getCanonicalPath(), Files.readAllBytes(targetFile.toPath()), this.getSemantic().getCompilerClass().isDetailed());
            arkoiCompiler.getArkoiClasses().add(arkoiClass);
    
            arkoiClass.getLexer().processStage();
            arkoiClass.getParser().processStage();
            arkoiClass.getSemantic().processStage();
            return arkoiClass;
        }
        return null;
    }
    
    @NotNull
    @Override
    public FunctionCallPartNode visit(final @NotNull FunctionCallPartNode functionCallPartAST) {
        for (final OperableNode operableAST : functionCallPartAST.getExpressions())
            this.visit(operableAST);
        return functionCallPartAST;
    }
    
    @NotNull
    @Override
    public CollectionNode visit(final @NotNull CollectionNode collectionAST) {
        for (final OperableNode operableAST : collectionAST.getExpressions())
            this.visit(operableAST);
        return collectionAST;
    }
    
    @NotNull
    @Override
    public AssignmentExpressionNode visit(final @NotNull AssignmentExpressionNode assignmentExpressionAST) {
        Objects.requireNonNull(assignmentExpressionAST.getLeftHandSide(), "assignmentExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(assignmentExpressionAST.getRightHandSide(), "assignmentExpressionAST.rightSideOperable must not be null.");
        
        this.visit(assignmentExpressionAST.getLeftHandSide());
        this.visit(assignmentExpressionAST.getRightHandSide());
        return assignmentExpressionAST;
    }
    
    @NotNull
    @Override
    public BinaryExpressionNode visit(final @NotNull BinaryExpressionNode binaryExpressionAST) {
        Objects.requireNonNull(binaryExpressionAST.getLeftHandSide(), "binaryExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(binaryExpressionAST.getRightHandSide(), "binaryExpressionAST.rightSideOperable must not be null.");
        
        this.visit(binaryExpressionAST.getLeftHandSide());
        this.visit(binaryExpressionAST.getRightHandSide());
        return binaryExpressionAST;
    }
    
    @NotNull
    @Override
    public EqualityExpressionNode visit(final @NotNull EqualityExpressionNode equalityExpressionAST) {
        Objects.requireNonNull(equalityExpressionAST.getLeftHandSide(), "equalityExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(equalityExpressionAST.getRightHandSide(), "equalityExpressionAST.rightSideOperable must not be null.");
        
        this.visit(equalityExpressionAST.getLeftHandSide());
        this.visit(equalityExpressionAST.getRightHandSide());
        return equalityExpressionAST;
    }
    
    @NotNull
    @Override
    public LogicalExpressionNode visit(final @NotNull LogicalExpressionNode logicalExpressionAST) {
        Objects.requireNonNull(logicalExpressionAST.getLeftHandSide(), "logicalExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(logicalExpressionAST.getRightHandSide(), "logicalExpressionAST.rightSideOperable must not be null.");
        
        this.visit(logicalExpressionAST.getLeftHandSide());
        this.visit(logicalExpressionAST.getRightHandSide());
        return logicalExpressionAST;
    }
    
    @NotNull
    @Override
    public ParenthesizedExpressionNode visit(final @NotNull ParenthesizedExpressionNode parenthesizedExpressionAST) {
        Objects.requireNonNull(parenthesizedExpressionAST.getExpression(), "parenthesizedExpressionAST.parenthesizedExpression must not be null.");
        
        this.visit(parenthesizedExpressionAST.getExpression());
        return parenthesizedExpressionAST;
    }
    
    @NotNull
    @Override
    public PostfixExpressionNode visit(final @NotNull PostfixExpressionNode postfixExpressionAST) {
        Objects.requireNonNull(postfixExpressionAST.getLeftHandSide(), "postfixExpressionAST.leftSideOperable must not be null.");
        
        this.visit(postfixExpressionAST.getLeftHandSide());
        return postfixExpressionAST;
    }
    
    @NotNull
    @Override
    public PrefixExpressionNode visit(final @NotNull PrefixExpressionNode prefixExpressionAST) {
        Objects.requireNonNull(prefixExpressionAST.getRightHandSide(), "prefixExpressionAST.rightSideOperable must not be null.");
        
        this.visit(prefixExpressionAST.getRightHandSide());
        return prefixExpressionAST;
    }
    
    @NotNull
    @Override
    public RelationalExpressionNode visit(final @NotNull RelationalExpressionNode relationalExpressionAST) {
        Objects.requireNonNull(relationalExpressionAST.getLeftHandSide(), "relationalExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(relationalExpressionAST.getRightHandSide(), "relationalExpressionAST.rightSideOperable must not be null.");
        
        this.visit(relationalExpressionAST.getLeftHandSide());
        this.visit(relationalExpressionAST.getRightHandSide());
        return relationalExpressionAST;
    }
    
    @Nullable
    public <E> E addError(@Nullable E errorSource, final @NotNull ArkoiClass compilerClass, final @NotNull ArkoiNode astNode, final @NotNull String message, final @NotNull Object... arguments) {
        Objects.requireNonNull(astNode.getLineRange(), "astNode.lineRange must not be null.");
        
        compilerClass.getSemantic().getErrorHandler().addError(ArkoiError.builder()
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
    public <E> E addError(@Nullable E errorSource, final @NotNull ArkoiClass compilerClass, final @NotNull ArkoiToken arkoiToken, final @NotNull String message, final @NotNull Object... arguments) {
        compilerClass.getSemantic().getErrorHandler().addError(ArkoiError.builder()
                .compilerClass(compilerClass)
                .positions(Collections.singletonList(ErrorPosition.builder()
                        .lineRange(arkoiToken.getLineRange())
                        .charStart(arkoiToken.getCharStart())
                        .charEnd(arkoiToken.getCharEnd())
                        .build()))
                .message(message)
                .arguments(arguments)
                .build()
        );
    
        this.setFailed(true);
        return errorSource;
    }
    
    @Nullable
    public <E> E addError(@Nullable E errorSource, final @NotNull ArkoiClass compilerClass, final int start, final int end, final @NotNull LineRange lineRange, final @NotNull String message, final @NotNull Object... arguments) {
        compilerClass.getSemantic().getErrorHandler().addError(ArkoiError.builder()
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
