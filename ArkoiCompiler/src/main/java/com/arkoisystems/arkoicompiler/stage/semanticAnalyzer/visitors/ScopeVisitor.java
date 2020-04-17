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
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.visitors;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.ArkoiCompiler;
import com.arkoisystems.arkoicompiler.ArkoiError;
import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.ICompilerClass;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.api.utils.IFailed;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ImportAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ReturnAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableAST;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Objects;
import java.util.Stack;

public class ScopeVisitor implements IVisitor<IASTNode>, IFailed
{
    
    
    @NotNull
    @Getter
    private final Stack<HashMap<String, IASTNode>> scopeStack = new Stack<>();
    
    
    @NotNull
    @Getter
    private final HashMap<String, IASTNode> rootScope = new HashMap<>();
    
    
    @NotNull
    @Getter
    private final SemanticAnalyzer semanticAnalyzer;
    
    
    @Getter
    private boolean failed;
    
    
    public ScopeVisitor(@NotNull final SemanticAnalyzer semanticAnalyzer) {
        this.semanticAnalyzer = semanticAnalyzer;
        
        this.scopeStack.add(rootScope);
    }
    
    
    @Override
    public TypeAST visit(@NotNull final TypeAST typeAST) {
        return typeAST;
    }
    
    
    @Override
    public RootAST visit(@NotNull final RootAST rootAST) {
        for (final IASTNode astNode : rootAST.getAstNodes()) {
            if (astNode instanceof VariableAST)
                this.preVisit((VariableAST) astNode);
            else if (astNode instanceof FunctionAST)
                this.preVisit((FunctionAST) astNode);
            else if (astNode instanceof ImportAST)
                this.preVisit((ImportAST) astNode);
        }
        
        for (final IASTNode astNode : rootAST.getAstNodes())
            this.visit(astNode);
        return rootAST;
    }
    
    
    @Override
    public ParameterListAST visit(@NotNull final ParameterListAST parameterListAST) {
        for (final ParameterAST parameterAST : parameterListAST.getParameters())
            this.visit(parameterAST);
        return parameterListAST;
    }
    
    
    @Override
    public ParameterAST visit(@NotNull final ParameterAST parameterAST) {
        Objects.requireNonNull(parameterAST.getParameterName(), "parameterAST.parameterName must not be null.");
        Objects.requireNonNull(parameterAST.getSyntaxAnalyzer(), "parameterAST.syntaxAnalyzer must not be null.");
        
        final HashMap<String, IASTNode> currentScope = this.getScopeStack().peek();
        if (currentScope.containsKey(parameterAST.getParameterName().getTokenContent())) {
            this.addError(
                    parameterAST.getSyntaxAnalyzer().getCompilerClass(),
                    
                    parameterAST.getParameterName(),
                    "Variable '%s' is already defined in the scope.", parameterAST.getParameterName().getTokenContent()
            );
        } else
            currentScope.put(parameterAST.getParameterName().getTokenContent(), parameterAST);
        return parameterAST;
    }
    
    
    @Override
    public BlockAST visit(@NotNull final BlockAST blockAST) {
        for (final IASTNode astNode : blockAST.getAstNodes()) {
            if (astNode instanceof VariableAST)
                this.preVisit((VariableAST) astNode);
            this.visit(astNode);
        }
        return blockAST;
    }
    
    
    @Override
    public ArgumentListAST visit(@NotNull final ArgumentListAST argumentListAST) {
        for (final ArgumentAST argumentAST : argumentListAST.getArguments())
            this.visit(argumentAST);
        return argumentListAST;
    }
    
    
    @Override
    public ArgumentAST visit(@NotNull final ArgumentAST argumentAST) {
        Objects.requireNonNull(argumentAST.getArgumentName(), "argumentAST.argumentName must not be null.");
        Objects.requireNonNull(argumentAST.getSyntaxAnalyzer(), "argumentAST.syntaxAnalyzer must not be null.");
        
        final HashMap<String, IASTNode> currentScope = this.getScopeStack().peek();
        if (currentScope.containsKey(argumentAST.getArgumentName().getTokenContent())) {
            this.addError(
                    argumentAST.getSyntaxAnalyzer().getCompilerClass(),
                    
                    argumentAST.getArgumentName(),
                    "Variable '%s' is already defined in the scope.", argumentAST.getArgumentName().getTokenContent()
            );
        } else
            currentScope.put(argumentAST.getArgumentName().getTokenContent(), argumentAST);
        return argumentAST;
    }
    
    
    @Override
    public AnnotationAST visit(@NotNull final AnnotationAST annotationAST) {
        Objects.requireNonNull(annotationAST.getAnnotationArguments(), "annotationAST.annotationArguments must not be null.");
        
        this.getScopeStack().add(new HashMap<>());
        this.visit(annotationAST.getAnnotationArguments());
        this.getScopeStack().pop();
        return annotationAST;
    }
    
    
    public void preVisit(@NotNull final FunctionAST functionAST) {
        Objects.requireNonNull(functionAST.getFunctionName(), "functionAST.functionName must not be null.");
        Objects.requireNonNull(functionAST.getSyntaxAnalyzer(), "functionAST.syntaxAnalyzer must not be null.");
        
        final HashMap<String, IASTNode> currentScope = this.getScopeStack().peek();
        if (currentScope.containsKey(functionAST.getFunctionDescription())) {
            this.addError(
                    functionAST.getSyntaxAnalyzer().getCompilerClass(),
                    
                    functionAST.getFunctionName(),
                    "Function '%s' is already defined in the scope.", functionAST.getFunctionDescription()
            );
        } else currentScope.put(functionAST.getFunctionDescription(), functionAST);
    }
    
    
    @Override
    public FunctionAST visit(@NotNull final FunctionAST functionAST) {
        Objects.requireNonNull(functionAST.getFunctionParameters(), "functionAST.functionParameters must not be null.");
        Objects.requireNonNull(functionAST.getFunctionBlock(), "functionAST.functionBlock must not be null.");
        
        this.getScopeStack().add(new HashMap<>());
        this.visit(functionAST.getFunctionParameters());
        this.visit(functionAST.getFunctionBlock());
        this.getScopeStack().pop();
        return functionAST;
    }
    
    
    public void preVisit(@NotNull final ImportAST importAST) {
        Objects.requireNonNull(importAST.getImportName(), "importAST.importName must not be null.");
        Objects.requireNonNull(importAST.getSyntaxAnalyzer(), "importAST.syntaxAnalyzer must not be null.");
        
        final HashMap<String, IASTNode> currentScope = this.getScopeStack().peek();
        if (currentScope.containsKey(importAST.getImportName().getTokenContent())) {
            this.addError(
                    importAST.getSyntaxAnalyzer().getCompilerClass(),
                    
                    importAST.getImportName(),
                    "Variable '%s' is already defined in the scope.", importAST.getImportName().getTokenContent()
            );
        } else currentScope.put(importAST.getImportName().getTokenContent(), importAST);
    }
    
    
    @Override
    public ImportAST visit(@NotNull final ImportAST importAST) {
        return importAST;
    }
    
    
    @Override
    public ReturnAST visit(@NotNull final ReturnAST returnAST) {
        Objects.requireNonNull(returnAST.getReturnExpression(), "returnAST.returnExpression must not be null.");
        
        this.visit(returnAST.getReturnExpression());
        return returnAST;
    }
    
    
    public void preVisit(@NotNull final VariableAST variableAST) {
        Objects.requireNonNull(variableAST.getVariableName(), "variableAST.variableName must not be null.");
        Objects.requireNonNull(variableAST.getSyntaxAnalyzer(), "variableAST.syntaxAnalyzer must not be null.");
        
        final HashMap<String, IASTNode> currentScope = this.getScopeStack().peek();
        if (currentScope.containsKey(variableAST.getVariableName().getTokenContent())) {
            this.addError(
                    variableAST.getSyntaxAnalyzer().getCompilerClass(),
                    
                    variableAST.getVariableName(),
                    "Variable '%s' is already defined in the scope.", variableAST.getVariableName().getTokenContent()
            );
        } else
            currentScope.put(variableAST.getVariableName().getTokenContent(), variableAST);
    }
    
    
    @Override
    public VariableAST visit(@NotNull final VariableAST variableAST) {
        Objects.requireNonNull(variableAST.getVariableExpression(), "variableAST.variableExpression must not be null.");
        
        this.visit(variableAST.getVariableExpression());
        return variableAST;
    }
    
    
    @Override
    public StringAST visit(@NotNull final StringAST stringAST) {
        return stringAST;
    }
    
    
    @Override
    public NumberAST visit(@NotNull final NumberAST numberAST) {
        return numberAST;
    }
    
    
    @Nullable
    @Override
    public IASTNode visit(@NotNull final IdentifierCallAST identifierCallAST) {
        Objects.requireNonNull(identifierCallAST.getCalledIdentifier(), "identifierCallAST.calledIdentifier must not be null.");
        Objects.requireNonNull(identifierCallAST.getSyntaxAnalyzer(), "identifierCallAST.syntaxAnalyzer must not be null.");
        
        final HashMap<String, IASTNode> currentScope = this.getScopeStack().peek();
//        if(identifierCallAST.getCalledIdentifier().getTokenContent().equals("test_argument"))
//            System.out.println(this.getRootScope().keySet() + ", " + currentScope.keySet());
        
        IASTNode foundAST = null;
        if (!identifierCallAST.isFileLocal() && currentScope.containsKey(identifierCallAST.getDescriptor()))
            foundAST = currentScope.get(identifierCallAST.getDescriptor());
        if (foundAST == null && this.getRootScope().containsKey(identifierCallAST.getDescriptor()))
            foundAST = this.getRootScope().get(identifierCallAST.getDescriptor());
        if (foundAST == null) {
            final ICompilerClass[] compilerClasses = this.getSemanticAnalyzer().getCompilerClass().getArkoiCompiler().getArkoiClasses().stream()
                    .filter(ICompilerClass::isNative)
                    .filter(compilerClass -> compilerClass != getSemanticAnalyzer().getCompilerClass())
                    .toArray(ICompilerClass[]::new);
            
            for (final ICompilerClass compilerClass : compilerClasses) {
                final ScopeVisitor scopeVisitor = new ScopeVisitor(compilerClass.getSemanticAnalyzer());
                scopeVisitor.visit(compilerClass.getSyntaxAnalyzer().getRootAST());
                if (!scopeVisitor.getRootScope().containsKey(identifierCallAST.getDescriptor()))
                    continue;
                
                foundAST = scopeVisitor.getRootScope().get(identifierCallAST.getDescriptor());
                break;
            }
        }
        
        if (identifierCallAST.getCalledFunctionPart() != null)
            this.visit(identifierCallAST.getCalledFunctionPart());
        
        if (foundAST == null) {
//            System.out.println(identifierCallAST.getCalledIdentifier().getTokenContent() + "," + this.getRootScope().keySet() + ", " + currentScope.keySet());
            this.addError(
                    identifierCallAST.getSyntaxAnalyzer().getCompilerClass(),
                    
                    identifierCallAST.getCalledIdentifier(),
                    "Cannot resolve reference '%s'.", identifierCallAST.getCalledIdentifier().getTokenContent()
            );
            return null;
        }
        
        if (identifierCallAST.getNextIdentifierCall() == null)
            return foundAST;
        
        final ICompilerClass resolvedClass = this.resolveClass(foundAST);
        if (resolvedClass == null)
            return foundAST;
        
        final ScopeVisitor scopeVisitor = new ScopeVisitor(resolvedClass.getSemanticAnalyzer());
        scopeVisitor.visit(resolvedClass.getSyntaxAnalyzer().getRootAST());
        foundAST = scopeVisitor.visit(identifierCallAST.getNextIdentifierCall());
        if (foundAST == null)
            return null;
        
        if (scopeVisitor.isFailed())
            this.failed();
        return this.visit(foundAST);
    }
    
    
    @SneakyThrows
    @Nullable
    private ICompilerClass resolveClass(@NotNull final IASTNode foundAST) {
        Objects.requireNonNull(foundAST.getSyntaxAnalyzer(), "foundAST.syntaxAnalyzer must not be null.");
        
        if (foundAST instanceof ImportAST) {
            final ImportAST importAST = (ImportAST) foundAST;
            Objects.requireNonNull(importAST.getImportFilePath(), "importAST.importFilePath must not be null.");
            
            File file = new File(importAST.getImportFilePath().getTokenContent() + ".ark");
            if (!file.isAbsolute())
                file = new File(new File(this.getSemanticAnalyzer().getCompilerClass().getFilePath()).getParent(), file.getPath());
            
            if (!file.exists()) {
                this.addError(
                        foundAST.getSyntaxAnalyzer().getCompilerClass(),
                        
                        importAST.getImportFilePath(),
                        "Path doesn't lead to file '%s'.", importAST.getImportFilePath().getTokenContent()
                );
                return null;
            }
            
            final ArkoiCompiler arkoiCompiler = this.getSemanticAnalyzer().getCompilerClass().getArkoiCompiler();
            for (final ICompilerClass compilerClass : arkoiCompiler.getArkoiClasses())
                if (compilerClass.getFilePath().equals(file.getCanonicalPath()))
                    return compilerClass;
            
            final ArkoiClass arkoiClass = new ArkoiClass(arkoiCompiler, file.getCanonicalPath(), Files.readAllBytes(file.toPath()));
            arkoiCompiler.addClass(arkoiClass);
            arkoiClass.getLexicalAnalyzer().processStage();
            arkoiClass.getSyntaxAnalyzer().processStage();
            arkoiClass.getSemanticAnalyzer().processStage();
            return arkoiClass;
        }
        return null;
    }
    
    
    @Override
    public FunctionCallPartAST visit(@NotNull final FunctionCallPartAST functionCallPartAST) {
        for (final OperableAST operableAST : functionCallPartAST.getCalledExpressions())
            this.visit(operableAST);
        return functionCallPartAST;
    }
    
    
    @Override
    public CollectionAST visit(@NotNull final CollectionAST collectionAST) {
        for (final OperableAST operableAST : collectionAST.getCollectionExpressions())
            this.visit(operableAST);
        return collectionAST;
    }
    
    
    @Override
    public AssignmentExpressionAST visit(@NotNull final AssignmentExpressionAST assignmentExpressionAST) {
        Objects.requireNonNull(assignmentExpressionAST.getLeftSideOperable(), "assignmentExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(assignmentExpressionAST.getRightSideOperable(), "assignmentExpressionAST.rightSideOperable must not be null.");
        
        this.visit(assignmentExpressionAST.getLeftSideOperable());
        this.visit(assignmentExpressionAST.getRightSideOperable());
        return assignmentExpressionAST;
    }
    
    
    @Override
    public BinaryExpressionAST visit(@NotNull final BinaryExpressionAST binaryExpressionAST) {
        Objects.requireNonNull(binaryExpressionAST.getLeftSideOperable(), "binaryExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(binaryExpressionAST.getRightSideOperable(), "binaryExpressionAST.rightSideOperable must not be null.");
        
        this.visit(binaryExpressionAST.getLeftSideOperable());
        this.visit(binaryExpressionAST.getRightSideOperable());
        return binaryExpressionAST;
    }
    
    
    @Override
    public CastExpressionAST visit(@NotNull final CastExpressionAST castExpressionAST) {
        Objects.requireNonNull(castExpressionAST.getLeftSideOperable(), "castExpressionAST.leftSideOperable must not be null.");
        
        this.visit(castExpressionAST.getLeftSideOperable());
        return castExpressionAST;
    }
    
    
    @Override
    public EqualityExpressionAST visit(@NotNull final EqualityExpressionAST equalityExpressionAST) {
        Objects.requireNonNull(equalityExpressionAST.getLeftSideOperable(), "equalityExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(equalityExpressionAST.getRightSideOperable(), "equalityExpressionAST.rightSideOperable must not be null.");
        
        this.visit(equalityExpressionAST.getLeftSideOperable());
        this.visit(equalityExpressionAST.getRightSideOperable());
        return equalityExpressionAST;
    }
    
    
    @Override
    public LogicalExpressionAST visit(@NotNull final LogicalExpressionAST logicalExpressionAST) {
        Objects.requireNonNull(logicalExpressionAST.getLeftSideOperable(), "logicalExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(logicalExpressionAST.getRightSideOperable(), "logicalExpressionAST.rightSideOperable must not be null.");
        
        this.visit(logicalExpressionAST.getLeftSideOperable());
        this.visit(logicalExpressionAST.getRightSideOperable());
        return logicalExpressionAST;
    }
    
    
    @Override
    public ParenthesizedExpressionAST visit(@NotNull final ParenthesizedExpressionAST parenthesizedExpressionAST) {
        Objects.requireNonNull(parenthesizedExpressionAST.getParenthesizedExpression(), "parenthesizedExpressionAST.parenthesizedExpression must not be null.");
        
        this.visit(parenthesizedExpressionAST.getParenthesizedExpression());
        return parenthesizedExpressionAST;
    }
    
    
    @Override
    public PostfixExpressionAST visit(@NotNull final PostfixExpressionAST postfixExpressionAST) {
        Objects.requireNonNull(postfixExpressionAST.getLeftSideOperable(), "postfixExpressionAST.leftSideOperable must not be null.");
        
        this.visit(postfixExpressionAST.getLeftSideOperable());
        return postfixExpressionAST;
    }
    
    
    @Override
    public PrefixExpressionAST visit(@NotNull final PrefixExpressionAST prefixExpressionAST) {
        Objects.requireNonNull(prefixExpressionAST.getRightSideOperable(), "prefixExpressionAST.rightSideOperable must not be null.");
        
        this.visit(prefixExpressionAST.getRightSideOperable());
        return prefixExpressionAST;
    }
    
    
    @Override
    public RelationalExpressionAST visit(@NotNull final RelationalExpressionAST relationalExpressionAST) {
        Objects.requireNonNull(relationalExpressionAST.getLeftSideOperable(), "relationalExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(relationalExpressionAST.getRightSideOperable(), "relationalExpressionAST.rightSideOperable must not be null.");
        
        this.visit(relationalExpressionAST.getLeftSideOperable());
        this.visit(relationalExpressionAST.getRightSideOperable());
        return relationalExpressionAST;
    }
    
    
    @Override
    public void failed() {
        this.failed = true;
    }
    
    
    public void addError(@NotNull final ICompilerClass compilerClass, @NotNull final IASTNode astNode, @NotNull final String message, @NotNull final Object... arguments) {
        Objects.requireNonNull(astNode.getSyntaxAnalyzer());
        Objects.requireNonNull(astNode.getSyntaxAnalyzer().getCompilerClass().getSemanticAnalyzer());
    
        astNode.getSyntaxAnalyzer().getCompilerClass().getSemanticAnalyzer().getErrorHandler().addError(ArkoiError.builder()
                .compilerClass(compilerClass)
                .positions(new int[][] { {
                        Objects.requireNonNull(astNode.getStartToken()).getStart(),
                        Objects.requireNonNull(astNode.getEndToken()).getEnd()
                } })
                .message(message)
                .arguments(arguments)
                .build()
        );
    
        this.failed();
    }
    
    
    public void addError(@NotNull final ICompilerClass compilerClass, @NotNull final ArkoiToken arkoiToken, @NotNull final String message, @NotNull final Object... arguments) {
        Objects.requireNonNull(arkoiToken.getLexicalAnalyzer());
        Objects.requireNonNull(arkoiToken.getLexicalAnalyzer().getCompilerClass().getSemanticAnalyzer());
        
        arkoiToken.getLexicalAnalyzer().getCompilerClass().getSemanticAnalyzer().getErrorHandler().addError(ArkoiError.builder()
                .compilerClass(compilerClass)
                .positions(new int[][] { {
                        arkoiToken.getStart(),
                        arkoiToken.getEnd()
                } })
                .message(message)
                .arguments(arguments)
                .build()
        );
        
        this.failed();
    }
    
}
