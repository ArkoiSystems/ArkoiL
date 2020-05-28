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
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.Block;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.Root;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.Type;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.argument.Argument;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.argument.ArgumentList;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.Operable;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.CollectionOperable;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.IdentifierOperable;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.NumberOperable;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.StringOperable;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.ExpressionList;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.parameter.Parameter;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.parameter.ParameterList;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.FunctionStatement;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.ImportStatement;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.ReturnStatement;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.VariableStatement;
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
    private final List<HashMap<java.lang.String, ArkoiNode>> scopeStack = new ArrayList<>();
    
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
    public Type visit(final @NotNull Type typeAST) {
        return typeAST;
    }
    
    @NotNull
    @Override
    public Root visit(final @NotNull Root rootAST) {
        this.getScopeStack().add(new HashMap<>());
        this.setCurrentIndex(0);
        
        for (final ArkoiNode astNode : rootAST.getNodes()) {
            if (astNode instanceof VariableStatement)
                this.preVisit((VariableStatement) astNode);
            else if (astNode instanceof FunctionStatement)
                this.preVisit((FunctionStatement) astNode);
            else if (astNode instanceof ImportStatement)
                this.preVisit((ImportStatement) astNode);
        }
        
        for (final ArkoiNode astNode : rootAST.getNodes())
            this.visit(astNode);
        return rootAST;
    }
    
    @NotNull
    @Override
    public ParameterList visit(final @NotNull ParameterList parameterListAST) {
        for (final Parameter parameterAST : parameterListAST.getParameters())
            this.visit(parameterAST);
        return parameterListAST;
    }
    
    @NotNull
    @Override
    public Parameter visit(final @NotNull Parameter parameterAST) {
        Objects.requireNonNull(parameterAST.getName(), "parameterAST.parameterName must not be null.");
        Objects.requireNonNull(parameterAST.getParser(), "parameterAST.parser must not be null.");
        
        final HashMap<java.lang.String, ArkoiNode> currentScope = this.getScopeIndexes().containsKey(parameterAST.hashCode()) ?
                this.getScopeStack().get(this.getScopeIndexes().get(parameterAST.hashCode())) :
                this.getScopeStack().get(this.getCurrentIndex());
        if (!this.getScopeIndexes().containsKey(parameterAST.hashCode()))
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
    public Block visit(final @NotNull Block blockAST) {
        for (final ArkoiNode astNode : blockAST.getNodes()) {
            if (astNode instanceof VariableStatement)
                this.preVisit((VariableStatement) astNode);
            this.visit(astNode);
        }
        return blockAST;
    }
    
    @NotNull
    @Override
    public ArgumentList visit(final @NotNull ArgumentList argumentListAST) {
        for (final Argument argumentAST : argumentListAST.getArguments())
            this.visit(argumentAST);
        return argumentListAST;
    }
    
    @NotNull
    @Override
    public Argument visit(final @NotNull Argument argumentAST) {
        Objects.requireNonNull(argumentAST.getName(), "argumentAST.argumentName must not be null.");
        Objects.requireNonNull(argumentAST.getParser(), "argumentAST.parser must not be null.");
        
        final HashMap<java.lang.String, ArkoiNode> currentScope = this.getScopeIndexes().containsKey(argumentAST.hashCode()) ?
                this.getScopeStack().get(this.getScopeIndexes().get(argumentAST.hashCode())) :
                this.getScopeStack().get(this.getCurrentIndex());
        if (!this.getScopeIndexes().containsKey(argumentAST.hashCode()))
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
    
    public void preVisit(final @NotNull FunctionStatement functionStatement) {
        Objects.requireNonNull(functionStatement.getName(), "functionAST.functionName must not be null.");
        Objects.requireNonNull(functionStatement.getParser(), "functionAST.parser must not be null.");
        
        final HashMap<java.lang.String, ArkoiNode> rootScope = this.getScopeStack().get(0);
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
    public FunctionStatement visit(final @NotNull FunctionStatement functionStatement) {
        Objects.requireNonNull(functionStatement.getParameters(), "functionAST.parameters must not be null.");
        Objects.requireNonNull(functionStatement.getBlock(), "functionAST.block must not be null.");
        
        this.getScopeStack().add(new HashMap<>());
        this.setCurrentIndex(this.getCurrentIndex() + 1);
        if (!this.getScopeIndexes().containsKey(functionStatement.hashCode()))
            this.getScopeIndexes().put(functionStatement.hashCode(), this.getCurrentIndex());
        
        this.visit(functionStatement.getParameters());
        this.visit(functionStatement.getBlock());
        return functionStatement;
    }
    
    public void preVisit(final @NotNull ImportStatement importStatement) {
        Objects.requireNonNull(importStatement.getName(), "importAST.importName must not be null.");
        Objects.requireNonNull(importStatement.getParser(), "importAST.parser must not be null.");
        
        final HashMap<java.lang.String, ArkoiNode> rootScope = this.getScopeStack().get(0);
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
    public ImportStatement visit(final @NotNull ImportStatement importStatement) {
        this.resolveClass(importStatement);
        return importStatement;
    }
    
    @NotNull
    @Override
    public ReturnStatement visit(final @NotNull ReturnStatement returnStatement) {
        if (returnStatement.getExpression() != null)
            this.visit(returnStatement.getExpression());
        return returnStatement;
    }
    
    public void preVisit(final @NotNull VariableStatement variableStatement) {
        Objects.requireNonNull(variableStatement.getName(), "variableAST.variableName must not be null.");
        Objects.requireNonNull(variableStatement.getParser(), "variableAST.parser must not be null.");
        
        final HashMap<java.lang.String, ArkoiNode> currentScope = this.getScopeIndexes().containsKey(variableStatement.hashCode()) ?
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
    public VariableStatement visit(final @NotNull VariableStatement variableStatement) {
        Objects.requireNonNull(variableStatement.getExpression(), "variableAST.variableExpression must not be null.");
        
        this.visit(variableStatement.getExpression());
        return variableStatement;
    }
    
    @NotNull
    @Override
    public StringOperable visit(final @NotNull StringOperable stringOperable) {
        return stringOperable;
    }
    
    @NotNull
    @Override
    public NumberOperable visit(final @NotNull NumberOperable numberOperable) {
        return numberOperable;
    }
    
    @Nullable
    @Override
    public ArkoiNode visit(final @NotNull IdentifierOperable identifierOperable) {
        Objects.requireNonNull(identifierOperable.getIdentifier(), "identifierCallAST.calledIdentifier must not be null.");
        Objects.requireNonNull(identifierOperable.getParser(), "identifierCallAST.parser must not be null.");
        
        final HashMap<java.lang.String, ArkoiNode> currentScope = this.getScopeIndexes().containsKey(identifierOperable.hashCode()) ?
                this.getScopeStack().get(this.getScopeIndexes().get(identifierOperable.hashCode())) :
                this.getScopeStack().get(this.getCurrentIndex());
        if (!this.getScopeIndexes().containsKey(identifierOperable.hashCode()))
            this.getScopeIndexes().put(identifierOperable.hashCode(), this.getCurrentIndex());
        
        ArkoiNode foundAST = null;
        if (!identifierOperable.isFileLocal() && currentScope.containsKey(identifierOperable.getDescriptor()))
            foundAST = currentScope.get(identifierOperable.getDescriptor());
        if (foundAST == null && this.getScopeStack().get(0).containsKey(identifierOperable.getDescriptor()))
            foundAST = this.getScopeStack().get(0).get(identifierOperable.getDescriptor());
        
        // TODO: 5/27/20 Do better resolving (variadic)
        if (foundAST == null) {
            final LineRange lineRange;
            final int charStart, charEnd;
            
            if (identifierOperable.isFunctionCall()) {
                Objects.requireNonNull(identifierOperable.getExpressionList(), "identifierOperable.expressionList must not be null.");
                Objects.requireNonNull(identifierOperable.getExpressionList().getLineRange(), "identifierOperable.expressionList.lineRange must not be null.");
                Objects.requireNonNull(identifierOperable.getExpressionList().getEndToken(), "identifierOperable.expressionList.endToken must not be null.");
                
                lineRange = LineRange.make(
                        identifierOperable.getParser().getCompilerClass(),
                        identifierOperable.getIdentifier().getLineRange().getStartLine(),
                        identifierOperable.getExpressionList().getLineRange().getEndLine()
                );
                charStart = identifierOperable.getIdentifier().getCharStart();
                charEnd = identifierOperable.getExpressionList().getEndToken().getCharEnd();
            } else {
                Objects.requireNonNull(identifierOperable.getLineRange(), "identifierCallAST.lineRange must not be null.");
                
                lineRange = identifierOperable.getLineRange();
                charStart = identifierOperable.getIdentifier().getCharStart();
                charEnd = identifierOperable.getIdentifier().getCharEnd();
            }
        
            return this.addError(
                    null,
                    identifierOperable.getParser().getCompilerClass(),
        
                    charStart,
                    charEnd,
                    lineRange,
        
                    "Cannot resolve reference '%s'.", identifierOperable.getIdentifier().getTokenContent()
            );
        }
        
        if (identifierOperable.isFunctionCall()) {
            Objects.requireNonNull(identifierOperable.getExpressionList(), "identifierOperable.expressionList must not be null.");
            this.visit(identifierOperable.getExpressionList());
        }
        
        if (identifierOperable.getIdentifierOperable() == null)
            return foundAST;
        
        ArkoiClass resolvedClass = this.resolveClass(foundAST);
        if (resolvedClass == null)
            return foundAST;
        
        final ScopeVisitor scopeVisitor = new ScopeVisitor(resolvedClass.getSemantic());
        scopeVisitor.visit(resolvedClass.getParser().getRootAST());
        foundAST = scopeVisitor.visit(identifierOperable.getIdentifierOperable());
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
    
        if (foundAST instanceof ImportStatement) {
            final ImportStatement importStatement = (ImportStatement) foundAST;
        
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
    public CollectionOperable visit(final @NotNull CollectionOperable collectionOperable) {
        for (final Operable operableAST : collectionOperable.getExpressions())
            this.visit(operableAST);
        return collectionOperable;
    }
    
    @Override
    public ExpressionList visit(final @NotNull ExpressionList expressionList) {
        for (final Operable operable : expressionList.getExpressions())
            this.visit(operable);
        return expressionList;
    }
    
    @NotNull
    @Override
    public AssignmentExpression visit(final @NotNull AssignmentExpression assignmentExpressionAST) {
        Objects.requireNonNull(assignmentExpressionAST.getLeftHandSide(), "assignmentExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(assignmentExpressionAST.getRightHandSide(), "assignmentExpressionAST.rightSideOperable must not be null.");
        
        this.visit(assignmentExpressionAST.getLeftHandSide());
        this.visit(assignmentExpressionAST.getRightHandSide());
        return assignmentExpressionAST;
    }
    
    @NotNull
    @Override
    public BinaryExpression visit(final @NotNull BinaryExpression binaryExpressionAST) {
        Objects.requireNonNull(binaryExpressionAST.getLeftHandSide(), "binaryExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(binaryExpressionAST.getRightHandSide(), "binaryExpressionAST.rightSideOperable must not be null.");
        
        this.visit(binaryExpressionAST.getLeftHandSide());
        this.visit(binaryExpressionAST.getRightHandSide());
        return binaryExpressionAST;
    }
    
    @NotNull
    @Override
    public EqualityExpression visit(final @NotNull EqualityExpression equalityExpressionAST) {
        Objects.requireNonNull(equalityExpressionAST.getLeftHandSide(), "equalityExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(equalityExpressionAST.getRightHandSide(), "equalityExpressionAST.rightSideOperable must not be null.");
        
        this.visit(equalityExpressionAST.getLeftHandSide());
        this.visit(equalityExpressionAST.getRightHandSide());
        return equalityExpressionAST;
    }
    
    @NotNull
    @Override
    public LogicalExpression visit(final @NotNull LogicalExpression logicalExpressionAST) {
        Objects.requireNonNull(logicalExpressionAST.getLeftHandSide(), "logicalExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(logicalExpressionAST.getRightHandSide(), "logicalExpressionAST.rightSideOperable must not be null.");
        
        this.visit(logicalExpressionAST.getLeftHandSide());
        this.visit(logicalExpressionAST.getRightHandSide());
        return logicalExpressionAST;
    }
    
    @NotNull
    @Override
    public ParenthesizedExpression visit(final @NotNull ParenthesizedExpression parenthesizedExpressionAST) {
        Objects.requireNonNull(parenthesizedExpressionAST.getExpression(), "parenthesizedExpressionAST.parenthesizedExpression must not be null.");
        
        this.visit(parenthesizedExpressionAST.getExpression());
        return parenthesizedExpressionAST;
    }
    
    @NotNull
    @Override
    public PostfixExpression visit(final @NotNull PostfixExpression postfixExpressionAST) {
        Objects.requireNonNull(postfixExpressionAST.getLeftHandSide(), "postfixExpressionAST.leftSideOperable must not be null.");
        
        this.visit(postfixExpressionAST.getLeftHandSide());
        return postfixExpressionAST;
    }
    
    @NotNull
    @Override
    public PrefixExpression visit(final @NotNull PrefixExpression prefixExpressionAST) {
        Objects.requireNonNull(prefixExpressionAST.getRightHandSide(), "prefixExpressionAST.rightSideOperable must not be null.");
        
        this.visit(prefixExpressionAST.getRightHandSide());
        return prefixExpressionAST;
    }
    
    @NotNull
    @Override
    public RelationalExpression visit(final @NotNull RelationalExpression relationalExpressionAST) {
        Objects.requireNonNull(relationalExpressionAST.getLeftHandSide(), "relationalExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(relationalExpressionAST.getRightHandSide(), "relationalExpressionAST.rightSideOperable must not be null.");
        
        this.visit(relationalExpressionAST.getLeftHandSide());
        this.visit(relationalExpressionAST.getRightHandSide());
        return relationalExpressionAST;
    }
    
    @Nullable
    public <E> E addError(@Nullable E errorSource, final @NotNull ArkoiClass compilerClass, final @NotNull ArkoiNode astNode, final @NotNull java.lang.String message, final @NotNull Object... arguments) {
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
    public <E> E addError(@Nullable E errorSource, final @NotNull ArkoiClass compilerClass, final @NotNull ArkoiToken arkoiToken, final @NotNull java.lang.String message, final @NotNull Object... arguments) {
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
    public <E> E addError(@Nullable E errorSource, final @NotNull ArkoiClass compilerClass, final int start, final int end, final @NotNull LineRange lineRange, final @NotNull java.lang.String message, final @NotNull Object... arguments) {
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
