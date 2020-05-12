/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on April 11, 2020
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
package com.arkoisystems.arkoicompiler.stage.parser.ast;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.*;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.*;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.FunctionAST;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.ImportAST;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.ReturnAST;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.VariableAST;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.Objects;
import java.util.stream.Collectors;

@Builder
public class ArkoiASTPrinter implements IVisitor<IASTNode>
{
    
    @Getter
    private final PrintStream printStream;
    
    @Getter
    @Setter
    private String indents;
    
    @NotNull
    @Override
    public TypeAST visit(@NotNull final TypeAST typeAST) {
        Objects.requireNonNull(typeAST.getTypeToken(), "typeAST.typeKeywordToken must not be null.");
        Objects.requireNonNull(typeAST.getTypeToken().getTypeKind(), "typeAST.typeKeywordToken.typeKind must not be null.");
        
        this.printFactory(typeAST);
        this.getPrintStream().printf("%s└── keyword: %s%s%n", this.getIndents(), typeAST.getTypeToken().getTypeKind().name(), typeAST.isArray() ? "[]" : "");
        return typeAST;
    }
    
    @NotNull
    @Override
    public RootAST visit(@NotNull final RootAST rootAST) {
        this.printFactory(rootAST);
        this.getPrintStream().printf("%s└── nodes: %s%n", this.getIndents(), rootAST.getAstNodes().isEmpty() ? "N/A" : "");
        for (int index = 0; index < rootAST.getAstNodes().size(); index++) {
            final IASTNode astNode = rootAST.getAstNodes().get(index);
            if (index == rootAST.getAstNodes().size() - 1) {
                this.getPrintStream().printf("%s    └── %s%n", this.getIndents(), astNode.getClass().getSimpleName());
                this.tempIndent(this.getIndents(), this.getIndents() + "        ", () -> this.visit(astNode));
            } else {
                this.getPrintStream().printf("%s    ├── %s%n", this.getIndents(), astNode.getClass().getSimpleName());
                this.tempIndent(this.getIndents(), this.getIndents() + "    │   ", () -> this.visit(astNode));
                this.getPrintStream().printf("%s    │%n", this.getIndents());
            }
        }
        return rootAST;
    }
    
    @NotNull
    @Override
    public ParameterListAST visit(@NotNull final ParameterListAST parameterListAST) {
        this.printFactory(parameterListAST);
        this.getPrintStream().printf("%s└── parameters: %s%n", this.getIndents(), parameterListAST.getParameters().isEmpty() ? "N/A" : "");
        for (int index = 0; index < parameterListAST.getParameters().size(); index++) {
            final IASTNode astNode = parameterListAST.getParameters().get(index);
            if (index == parameterListAST.getParameters().size() - 1) {
                this.getPrintStream().printf("%s    └── %s%n", this.getIndents(), astNode.getClass().getSimpleName());
                this.tempIndent(this.getIndents(), this.getIndents() + "        ", () -> this.visit(astNode));
            } else {
                this.getPrintStream().printf("%s    ├── %s%n", this.getIndents(), astNode.getClass().getSimpleName());
                this.tempIndent(this.getIndents(), this.getIndents() + "    │   ", () -> this.visit(astNode));
                this.getPrintStream().printf("%s    │%n", this.getIndents());
            }
        }
        return parameterListAST;
    }
    
    @NotNull
    @Override
    public ParameterAST visit(@NotNull final ParameterAST parameterAST) {
        Objects.requireNonNull(parameterAST.getParameterName(), "parameterAST.parameterName must not be null.");
        Objects.requireNonNull(parameterAST.getParameterType(), "parameterAST.parameterType.typeKind must not be null.");
        
        this.printFactory(parameterAST);
        this.getPrintStream().printf("%s├── name: %s%n", this.getIndents(), parameterAST.getParameterName().getData());
        this.getPrintStream().printf("%s└── type:%n", this.getIndents());
        this.tempIndent(this.getIndents(), this.getIndents() + "    ", () -> this.visit(parameterAST.getParameterType()));
        return parameterAST;
    }
    
    @NotNull
    @Override
    public BlockAST visit(@NotNull final BlockAST blockAST) {
        this.printFactory(blockAST);
        this.getPrintStream().printf("%s├── type: %s%n", this.getIndents(), blockAST.getBlockType());
        this.getPrintStream().printf("%s│%n", this.getIndents());
        this.getPrintStream().printf("%s└── nodes: %s%n", this.getIndents(), blockAST.getAstNodes().isEmpty() ? "N/A" : "");
        for (int index = 0; index < blockAST.getAstNodes().size(); index++) {
            final IASTNode astNode = blockAST.getAstNodes().get(index);
            if (index == blockAST.getAstNodes().size() - 1) {
                this.getPrintStream().printf("%s    └── %s%n", this.getIndents(), astNode.getClass().getSimpleName());
                this.tempIndent(this.getIndents(), this.getIndents() + "        ", () -> this.visit(astNode));
            } else {
                this.getPrintStream().printf("%s    ├── %s%n", this.getIndents(), astNode.getClass().getSimpleName());
                this.tempIndent(this.getIndents(), this.getIndents() + "    │   ", () -> this.visit(astNode));
                this.getPrintStream().printf("%s    │%n", this.getIndents());
            }
        }
        return blockAST;
    }
    
    @NotNull
    @Override
    public ArgumentListAST visit(@NotNull final ArgumentListAST argumentListAST) {
        this.printFactory(argumentListAST);
        this.getPrintStream().printf("%s└── arguments: %s%n", this.getIndents(), argumentListAST.getArguments().isEmpty() ? "N/A" : "");
        for (int index = 0; index < argumentListAST.getArguments().size(); index++) {
            final IASTNode astNode = argumentListAST.getArguments().get(index);
            if (index == argumentListAST.getArguments().size() - 1) {
                this.getPrintStream().printf("%s    └── %s%n", this.getIndents(), astNode.getClass().getSimpleName());
                this.tempIndent(this.getIndents(), this.getIndents() + "        ", () -> this.visit(astNode));
            } else {
                this.getPrintStream().printf("%s    ├── %s%n", this.getIndents(), astNode.getClass().getSimpleName());
                this.tempIndent(this.getIndents(), this.getIndents() + "    │   ", () -> this.visit(astNode));
                this.getPrintStream().printf("%s    │%n", this.getIndents());
            }
        }
        return argumentListAST;
    }
    
    @NotNull
    @Override
    public ArgumentAST visit(@NotNull final ArgumentAST argumentAST) {
        Objects.requireNonNull(argumentAST.getArgumentName(), "argumentAST.argumentName must not be null.");
        Objects.requireNonNull(argumentAST.getArgumentExpression(), "argumentAST.argumentExpression must not be null.");
        
        this.printFactory(argumentAST);
        this.getPrintStream().printf("%s├── name: %s%n", this.getIndents(), argumentAST.getArgumentName().getData());
        this.getPrintStream().printf("%s└── expression:%n", this.getIndents());
        this.tempIndent(this.getIndents(), this.getIndents() + "     ", () -> this.visit(argumentAST.getArgumentExpression()));
        return argumentAST;
    }
    
    @NotNull
    @Override
    public AnnotationAST visit(@NotNull final AnnotationAST annotationAST) {
        Objects.requireNonNull(annotationAST.getAnnotationName(), "annotationAST.annotationName must not be null.");
        Objects.requireNonNull(annotationAST.getAnnotationArguments(), "annotationAST.annotationArguments must not be null.");
        
        this.printFactory(annotationAST);
        this.getPrintStream().printf("%s├── name: %s%n", this.getIndents(), annotationAST.getAnnotationName().getData());
        this.getPrintStream().printf("%s└── arguments: %s%n", this.getIndents(), annotationAST.getAnnotationArguments().getArguments().isEmpty() ? "N/A" : "");
        this.tempIndent(this.getIndents(), this.getIndents() + "    ", () -> this.visit(annotationAST.getAnnotationArguments()));
        return annotationAST;
    }
    
    @NotNull
    @Override
    public FunctionAST visit(@NotNull final FunctionAST functionAST) {
        Objects.requireNonNull(functionAST.getFunctionParameters(), "functionAST.functionParameters must not be null.");
        Objects.requireNonNull(functionAST.getFunctionReturnType(), "functionAST.functionReturnType must not be null.");
        Objects.requireNonNull(functionAST.getFunctionName(), "functionAST.functionName must not be null.");
        
        this.printFactory(functionAST);
        this.getPrintStream().printf("%s├── annotations: %s%n", this.getIndents(), functionAST.getFunctionAnnotations().isEmpty() ? "N/A" : "");
        for (int index = 0; index < functionAST.getFunctionAnnotations().size(); index++) {
            final IASTNode astNode = functionAST.getFunctionAnnotations().get(index);
            if (index == functionAST.getFunctionAnnotations().size() - 1) {
                this.getPrintStream().printf("%s│   └── %s%n", this.getIndents(), astNode.getClass().getSimpleName());
                this.tempIndent(this.getIndents(), this.getIndents() + "│       ", () -> this.visit(astNode));
            } else {
                this.getPrintStream().printf("%s│   ├── %s%n", this.getIndents(), astNode.getClass().getSimpleName());
                this.tempIndent(this.getIndents(), this.getIndents() + "│   │   ", () -> this.visit(astNode));
                this.getPrintStream().printf("%s│   │%n", this.getIndents());
            }
        }
        this.getPrintStream().printf("%s│%n", this.getIndents());
        this.getPrintStream().printf("%s├── name: %s%n", this.getIndents(), functionAST.getFunctionName().getData());
        this.getPrintStream().printf("%s├── type: %n", this.getIndents());
        this.tempIndent(this.getIndents(), this.getIndents() + "│   ", () -> this.visit(functionAST.getFunctionReturnType()));
        this.getPrintStream().printf("%s│%n", this.getIndents());
        this.getPrintStream().printf("%s├── parameters: %s%n", this.getIndents(), functionAST.getFunctionParameters().getParameters().isEmpty() ? "N/A" : "");
        this.tempIndent(this.getIndents(), this.getIndents() + "│   ", () -> this.visit(functionAST.getFunctionParameters()));
        this.getPrintStream().printf("%s│%n", this.getIndents());
        this.getPrintStream().printf("%s└── block: %n", this.getIndents());
        this.tempIndent(this.getIndents(), this.getIndents() + "    ", () -> this.visit(functionAST.getFunctionReturnType()));
        return functionAST;
    }
    
    @NotNull
    @Override
    public ImportAST visit(@NotNull final ImportAST importAST) {
        Objects.requireNonNull(importAST.getImportName(), "importAST.importName must not be null.");
        Objects.requireNonNull(importAST.getImportFilePath(), "importAST.importFilePath must not be null.");
        
        this.printFactory(importAST);
        this.getPrintStream().printf("%s├── name: %s%n", this.getIndents(), importAST.getImportName().getData());
        this.getPrintStream().printf("%s└── path: %s%n", this.getIndents(), importAST.getImportFilePath().getData());
        return importAST;
    }
    
    @NotNull
    @Override
    public ReturnAST visit(@NotNull final ReturnAST returnAST) {
        Objects.requireNonNull(returnAST.getReturnExpression(), "returnAST.returnExpression must not be null.");
        
        this.printFactory(returnAST);
        this.getPrintStream().printf("%s└── expression:%n", this.getIndents());
        this.tempIndent(this.getIndents(), this.getIndents() + "    ", () -> this.visit(returnAST.getReturnExpression()));
        return returnAST;
    }
    
    @NotNull
    @Override
    public VariableAST visit(@NotNull final VariableAST variableAST) {
        Objects.requireNonNull(variableAST.getVariableName(), "variableAST.variableName must not be null.");
        Objects.requireNonNull(variableAST.getVariableExpression(), "variableAST.variableExpression must not be null.");
        
        this.printFactory(variableAST);
        this.getPrintStream().printf("%s├── annotations: %s%n", indents, variableAST.getVariableAnnotations().isEmpty() ? "N/A" : "");
        for (int index = 0; index < variableAST.getVariableAnnotations().size(); index++) {
            final IASTNode astNode = variableAST.getVariableAnnotations().get(index);
            if (index == variableAST.getVariableAnnotations().size() - 1) {
                this.getPrintStream().printf("%s│   └── %s%n", this.getIndents(), astNode.getClass().getSimpleName());
                this.tempIndent(this.getIndents(), this.getIndents() + "│       ", () -> this.visit(astNode));
            } else {
                this.getPrintStream().printf("%s│   ├── %s%n", this.getIndents(), astNode.getClass().getSimpleName());
                this.tempIndent(this.getIndents(), this.getIndents() + "│   │   ", () -> this.visit(astNode));
                this.getPrintStream().printf("%s│   │%n", this.getIndents());
            }
        }
        this.getPrintStream().printf("%s│%n", indents);
        this.getPrintStream().printf("%s├── name: %s%n", indents, variableAST.getVariableName().getData());
        this.getPrintStream().printf("%s│%n", indents);
        this.getPrintStream().printf("%s└── expression: %n", indents);
        this.tempIndent(this.getIndents(), this.getIndents() + "    ", () -> this.visit(variableAST.getVariableExpression()));
        return variableAST;
    }
    
    @NotNull
    @Override
    public StringAST visit(@NotNull final StringAST stringAST) {
        Objects.requireNonNull(stringAST.getStringToken(), "stringAST.stringToken must not be null.");
        
        this.printFactory(stringAST);
        this.getPrintStream().printf("%s└── operable: %s%n", this.getIndents(), stringAST.getStringToken().getData());
        return stringAST;
    }
    
    @NotNull
    @Override
    public NumberAST visit(@NotNull final NumberAST numberAST) {
        Objects.requireNonNull(numberAST.getNumberToken(), "numberAST.numberToken must not be null.");
        
        this.printFactory(numberAST);
        this.getPrintStream().printf("%s└── operable: %s%n", this.getIndents(), numberAST.getNumberToken().getData());
        return numberAST;
    }
    
    @NotNull
    @Override
    public IdentifierCallAST visit(@NotNull final IdentifierCallAST identifierCallAST) {
        Objects.requireNonNull(identifierCallAST.getCalledIdentifier(), "identifierCallAST.calledIdentifier must not be null.");
        
        this.printFactory(identifierCallAST);
        this.getPrintStream().printf("%s├── fileLocal: %s%n", this.getIndents(), identifierCallAST.isFileLocal());
        this.getPrintStream().printf("%s├── identifier: %s%n", this.getIndents(), identifierCallAST.getCalledIdentifier().getData());
        this.getPrintStream().printf("%s├── functionCall: %s%n", this.getIndents(), identifierCallAST.getCalledFunctionPart() == null ? "null" : "");
        if(identifierCallAST.getCalledFunctionPart() != null)
            this.tempIndent(this.getIndents(), this.getIndents() + "│   ", () -> this.visit(identifierCallAST.getCalledFunctionPart()));
        this.getPrintStream().printf("%s└── nextCall: %s%n", this.getIndents(), identifierCallAST.getNextIdentifierCall() == null ? "null" : "");
        if(identifierCallAST.getNextIdentifierCall() != null)
            this.tempIndent(this.getIndents(), this.getIndents() + "    ", () -> this.visit(identifierCallAST.getNextIdentifierCall()));
        return identifierCallAST;
    }
    
    @NotNull
    @Override
    public FunctionCallPartAST visit(@NotNull final FunctionCallPartAST functionCallPartAST) {
        this.printFactory(functionCallPartAST);
        this.getPrintStream().printf("%s└── expressions: %s%n", this.getIndents(), functionCallPartAST.getCalledExpressions().isEmpty() ? "NaN" : "");
        for (int index = 0; index < functionCallPartAST.getCalledExpressions().size(); index++) {
            final IASTNode astNode = functionCallPartAST.getCalledExpressions().get(index);
            if (index == functionCallPartAST.getCalledExpressions().size() - 1) {
                this.getPrintStream().printf("%s    └── %s%n", this.getIndents(), astNode.getClass().getSimpleName());
                this.tempIndent(this.getIndents(), this.getIndents() + "        ", () -> this.visit(astNode));
            } else {
                this.getPrintStream().printf("%s    ├── %s%n", this.getIndents(), astNode.getClass().getSimpleName());
                this.tempIndent(this.getIndents(), this.getIndents() + "    │   ", () -> this.visit(astNode));
                this.getPrintStream().printf("%s    │%n", this.getIndents());
            }
        }
        return functionCallPartAST;
    }
    
    @NotNull
    @Override
    public CollectionAST visit(@NotNull final CollectionAST collectionAST) {
        this.printFactory(collectionAST);
        this.getPrintStream().printf("%s└── expressions: %s%n", this.getIndents(), collectionAST.getCollectionExpressions().isEmpty() ? "N/A" : "");
        for (int index = 0; index < collectionAST.getCollectionExpressions().size(); index++) {
            final IASTNode astNode = collectionAST.getCollectionExpressions().get(index);
            if (index == collectionAST.getCollectionExpressions().size() - 1) {
                this.getPrintStream().printf("%s    └── %s%n", this.getIndents(), astNode.getClass().getSimpleName());
                this.tempIndent(this.getIndents(), this.getIndents() + "        ", () -> this.visit(astNode));
            } else {
                this.getPrintStream().printf("%s    ├── %s%n", this.getIndents(), astNode.getClass().getSimpleName());
                this.tempIndent(this.getIndents(), this.getIndents() + "    │   ", () -> this.visit(astNode));
                this.getPrintStream().printf("%s    │%n", this.getIndents());
            }
        }
        return collectionAST;
    }
    
    @NotNull
    @Override
    public AssignmentExpressionAST visit(@NotNull final AssignmentExpressionAST assignmentExpressionAST) {
        Objects.requireNonNull(assignmentExpressionAST.getLeftSideOperable(), "assignmentExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(assignmentExpressionAST.getRightSideOperable(), "assignmentExpressionAST.rightSideOperable must not be null.");
        
        this.printFactory(assignmentExpressionAST);
        this.getPrintStream().printf("%s├── left:%n", this.getIndents());
        this.getPrintStream().printf("%s│   └── %s%n", this.getIndents(), assignmentExpressionAST.getLeftSideOperable().getClass().getSimpleName());
        this.tempIndent(this.getIndents(), this.getIndents() + "│       ", () -> this.visit(assignmentExpressionAST.getLeftSideOperable()));
        this.getPrintStream().printf("%s├── operator: %s%n", this.getIndents(), assignmentExpressionAST.getAssignmentOperatorType());
        this.getPrintStream().printf("%s└── right:%n", this.getIndents());
        this.getPrintStream().printf("%s    └── %s%n", this.getIndents(), assignmentExpressionAST.getRightSideOperable().getClass().getSimpleName());
        this.tempIndent(this.getIndents(), this.getIndents() + "        ", () -> this.visit(assignmentExpressionAST.getRightSideOperable()));
        return assignmentExpressionAST;
    }
    
    @NotNull
    @Override
    public BinaryExpressionAST visit(@NotNull final BinaryExpressionAST binaryExpressionAST) {
        Objects.requireNonNull(binaryExpressionAST.getLeftSideOperable(), "binaryExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(binaryExpressionAST.getRightSideOperable(), "binaryExpressionAST.rightSideOperable must not be null.");
        
        this.printFactory(binaryExpressionAST);
        this.getPrintStream().printf("%s├── left:%n", this.getIndents());
        this.getPrintStream().printf("%s│   └── %s%n", this.getIndents(), binaryExpressionAST.getLeftSideOperable().getClass().getSimpleName());
        this.tempIndent(this.getIndents(), this.getIndents() + "│       ", () -> this.visit(binaryExpressionAST.getLeftSideOperable()));
        this.getPrintStream().printf("%s├── operator: %s%n", this.getIndents(), binaryExpressionAST.getBinaryOperatorType());
        this.getPrintStream().printf("%s└── right:%n", this.getIndents());
        this.getPrintStream().printf("%s    └── %s%n", this.getIndents(), binaryExpressionAST.getRightSideOperable().getClass().getSimpleName());
        this.tempIndent(this.getIndents(), this.getIndents() + "        ", () -> this.visit(binaryExpressionAST.getRightSideOperable()));
        return binaryExpressionAST;
    }
    
    @NotNull
    @Override
    public CastExpressionAST visit(@NotNull final CastExpressionAST castExpressionAST) {
        Objects.requireNonNull(castExpressionAST.getLeftSideOperable(), "castExpressionAST.leftSideOperable must not be null.");
        
        this.printFactory(castExpressionAST);
        this.getPrintStream().printf("%s├── left:%n", this.getIndents());
        this.getPrintStream().printf("%s│   └── %s%n", this.getIndents(), castExpressionAST.getLeftSideOperable().getClass().getSimpleName());
        this.tempIndent(this.getIndents(), this.getIndents() + "│       ", () -> this.visit(castExpressionAST.getLeftSideOperable()));
        this.getPrintStream().printf("%s└── typeKind: %s%n", this.getIndents(), castExpressionAST.getTypeKind());
        return castExpressionAST;
    }
    
    @NotNull
    @Override
    public EqualityExpressionAST visit(@NotNull final EqualityExpressionAST equalityExpressionAST) {
        Objects.requireNonNull(equalityExpressionAST.getLeftSideOperable(), "equalityExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(equalityExpressionAST.getRightSideOperable(), "equalityExpressionAST.rightSideOperable must not be null.");
        
        this.printFactory(equalityExpressionAST);
        this.getPrintStream().printf("%s├── left:%n", this.getIndents());
        this.getPrintStream().printf("%s│   └── %s%n", this.getIndents(), equalityExpressionAST.getLeftSideOperable().getClass().getSimpleName());
        this.tempIndent(this.getIndents(), this.getIndents() + "│       ", () -> this.visit(equalityExpressionAST.getLeftSideOperable()));
        this.getPrintStream().printf("%s├── operator: %s%n", this.getIndents(), equalityExpressionAST.getEqualityOperatorType());
        this.getPrintStream().printf("%s└── right:%n", this.getIndents());
        this.getPrintStream().printf("%s    └── %s%n", this.getIndents(), equalityExpressionAST.getRightSideOperable().getClass().getSimpleName());
        this.tempIndent(this.getIndents(), this.getIndents() + "        ", () -> this.visit(equalityExpressionAST.getRightSideOperable()));
        return equalityExpressionAST;
    }
    
    @NotNull
    @Override
    public LogicalExpressionAST visit(@NotNull final LogicalExpressionAST logicalExpressionAST) {
        Objects.requireNonNull(logicalExpressionAST.getLeftSideOperable(), "logicalExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(logicalExpressionAST.getRightSideOperable(), "logicalExpressionAST.rightSideOperable must not be null.");
        
        this.printFactory(logicalExpressionAST);
        this.getPrintStream().printf("%s├── left:%n", this.getIndents());
        this.getPrintStream().printf("%s│   └── %s%n", this.getIndents(), logicalExpressionAST.getLeftSideOperable().getClass().getSimpleName());
        this.getPrintStream().printf("%s│%n", this.getIndents());
        this.tempIndent(this.getIndents(), this.getIndents() + "│       ", () -> this.visit(logicalExpressionAST.getLeftSideOperable()));
        this.getPrintStream().printf("%s├── operator: %s%n", this.getIndents(), logicalExpressionAST.getLogicalOperatorType());
        this.getPrintStream().printf("%s│%n", this.getIndents());
        this.getPrintStream().printf("%s└── right:%n", this.getIndents());
        this.getPrintStream().printf("%s    └── %s%n", this.getIndents(), logicalExpressionAST.getRightSideOperable().getClass().getSimpleName());
        this.tempIndent(this.getIndents(), this.getIndents() + "        ", () -> this.visit(logicalExpressionAST.getRightSideOperable()));
        return logicalExpressionAST;
    }
    
    @NotNull
    @Override
    public ParenthesizedExpressionAST visit(@NotNull final ParenthesizedExpressionAST parenthesizedExpressionAST) {
        Objects.requireNonNull(parenthesizedExpressionAST.getParenthesizedExpression(), "parenthesizedExpressionAST.parenthesizedExpression must not be null.");
        
        this.printFactory(parenthesizedExpressionAST);
        this.getPrintStream().printf("%s└── operable:%n", this.getIndents());
        this.getPrintStream().printf("%s    └── %s%n", this.getIndents(), parenthesizedExpressionAST.getParenthesizedExpression().getClass().getSimpleName());
        this.tempIndent(this.getIndents(), this.getIndents() + "        ", () -> this.visit(parenthesizedExpressionAST.getParenthesizedExpression()));
        return parenthesizedExpressionAST;
    }
    
    @NotNull
    @Override
    public PostfixExpressionAST visit(@NotNull final PostfixExpressionAST postfixExpressionAST) {
        Objects.requireNonNull(postfixExpressionAST.getLeftSideOperable(), "postfixExpressionAST.leftSideOperable must not be null.");
        
        this.printFactory(postfixExpressionAST);
        this.getPrintStream().printf("%s├── left:%n", this.getIndents());
        this.getPrintStream().printf("%s│   └── %s%n", this.getIndents(), postfixExpressionAST.getLeftSideOperable().getClass().getSimpleName());
        this.tempIndent(this.getIndents(), this.getIndents() + "│       ", () -> this.visit(postfixExpressionAST.getLeftSideOperable()));
        this.getPrintStream().printf("%s└── operator: %s%n", this.getIndents(), postfixExpressionAST.getPostfixOperatorType());
        return postfixExpressionAST;
    }
    
    @NotNull
    @Override
    public PrefixExpressionAST visit(@NotNull final PrefixExpressionAST prefixExpressionAST) {
        Objects.requireNonNull(prefixExpressionAST.getRightSideOperable(), "prefixExpressionAST.rightSideOperable must not be null.");
        
        this.printFactory(prefixExpressionAST);
        this.getPrintStream().printf("%s├── operator: %s%n", this.getIndents(), prefixExpressionAST.getPrefixOperatorType());
        this.getPrintStream().printf("%s└── right:%n", this.getIndents());
        this.getPrintStream().printf("%s    └── %s%n", this.getIndents(), prefixExpressionAST.getRightSideOperable().getClass().getSimpleName());
        this.tempIndent(this.getIndents(), this.getIndents() + "        ", () -> this.visit(prefixExpressionAST.getRightSideOperable()));
        return prefixExpressionAST;
    }
    
    @NotNull
    @Override
    public RelationalExpressionAST visit(@NotNull final RelationalExpressionAST relationalExpressionAST) {
        Objects.requireNonNull(relationalExpressionAST.getLeftSideOperable(), "relationalExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(relationalExpressionAST.getRightSideOperable(), "relationalExpressionAST.rightSideOperable must not be null.");
        
        this.printFactory(relationalExpressionAST);
        this.getPrintStream().printf("%s├── left:%n", this.getIndents());
        this.getPrintStream().printf("%s│   └── %s%n", this.getIndents(), relationalExpressionAST.getLeftSideOperable().getClass().getSimpleName());
        this.tempIndent(this.getIndents(), this.getIndents() + "│       ", () -> this.visit(relationalExpressionAST.getLeftSideOperable()));
        this.getPrintStream().printf("%s├── operator: %s%n", this.getIndents(), relationalExpressionAST.getRelationalOperatorType());
        this.getPrintStream().printf("%s└── right:%n", this.getIndents());
        this.getPrintStream().printf("%s    └── %s%n", this.getIndents(), relationalExpressionAST.getRightSideOperable().getClass().getSimpleName());
        this.tempIndent(this.getIndents(), this.getIndents() + "        ", () -> this.visit(relationalExpressionAST.getRightSideOperable()));
        return relationalExpressionAST;
    }
    
    private void printFactory(@NotNull final IASTNode astNode) {
        Objects.requireNonNull(astNode.getMarkerFactory().getCurrentMarker().getStart(), "astNode.markerFactory.currentMarker.start must not be null.");
        Objects.requireNonNull(astNode.getMarkerFactory().getCurrentMarker().getEnd(), "astNode.markerFactory.currentMarker.end must not be null.");
        
        this.getPrintStream().printf("%s├── factory:%n", this.getIndents());
        this.getPrintStream().printf("%s│   ├── next: %s%n", this.getIndents(), astNode.getMarkerFactory()
                .getNextMarkerFactories()
                .stream()
                .map(markerFactory -> markerFactory.getCurrentMarker().getAstType().name())
                .collect(Collectors.joining(", "))
        );
        this.getPrintStream().printf("%s│   ├── start: %d%n", this.getIndents(), astNode.getMarkerFactory().getCurrentMarker().getStart().getCharStart());
        this.getPrintStream().printf("%s│   └── end: %d%n", this.getIndents(), astNode.getMarkerFactory().getCurrentMarker().getEnd().getCharEnd());
        this.getPrintStream().printf("%s│%n", this.getIndents());
    }
    
    private void tempIndent(final String startIndent, final String newIndent, final Runnable runnable) {
        this.setIndents(newIndent);
        runnable.run();
        this.setIndents(startIndent);
    }
    
}
