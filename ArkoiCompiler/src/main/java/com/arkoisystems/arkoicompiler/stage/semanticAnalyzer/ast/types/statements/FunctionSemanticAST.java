/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements;

import com.arkoisystems.arkoicompiler.api.ICompilerSemanticAST;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticErrorType;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.ArkoiSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.AnnotationSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.BlockSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.ParameterSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.TypeSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.AnnotationSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.ParameterSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class FunctionSemanticAST extends ArkoiSemanticAST<FunctionSyntaxAST>
{
    
    
    @Getter
    @NotNull
    private final List<AnnotationSemanticAST> functionAnnotations = this.checkFunctionAnnotations();
    
    
    @Getter
    @NotNull
    private final IdentifierToken functionName = this.checkFunctionName();
    
    
    @Getter
    @NotNull
    private final TypeSemanticAST functionReturnType = this.checkFunctionReturnType();
    
    
    @Getter
    @NotNull
    private final List<ParameterSemanticAST> functionParameters = this.checkFunctionParameters();
    
    
    @Getter
    @NotNull
    private final BlockSemanticAST functionBlock = this.checkFunctionBlock();
    
    
    @Getter
    @NotNull
    private final String functionDescription = this.generateFunctionDescription();
    
    
    public FunctionSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final ICompilerSemanticAST<?> lastContainerAST, @NonNull final FunctionSyntaxAST functionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, functionSyntaxAST, ASTType.FUNCTION);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.printf("%s├── annotations: %s%n", indents, this.getFunctionAnnotations().isEmpty() ? "N/A" : "");
        for (int index = 0; index < this.getFunctionAnnotations().size(); index++) {
            final ArkoiSemanticAST<?> arkoiSemanticAST = this.getFunctionAnnotations().get(index);
            if (index == this.getFunctionAnnotations().size() - 1) {
                printStream.printf("%s│   └── %s%n", indents, arkoiSemanticAST.getClass().getSimpleName());
                arkoiSemanticAST.printSemanticAST(printStream, indents + "│       ");
            } else {
                printStream.printf("%s│   ├── %s%n", indents, arkoiSemanticAST.getClass().getSimpleName());
                arkoiSemanticAST.printSemanticAST(printStream, indents + "│   │   ");
                printStream.printf("%s│   │%n", indents);
            }
        }
        printStream.printf("%s│%n", indents);
        printStream.printf("%s├── name: %s%n", indents, this.getFunctionName().getTokenContent());
        printStream.printf("%s├── type:%n", indents);
        this.getFunctionReturnType().printSemanticAST(printStream, indents + "│       ");
        printStream.printf("%s│%n", indents);
        printStream.printf("%s├── arguments: %s%n", indents, this.getFunctionParameters().isEmpty() ? "N/A" : "");
        for (int index = 0; index < this.getFunctionParameters().size(); index++) {
            final ArkoiSemanticAST<?> arkoiSemanticAST = this.getFunctionParameters().get(index);
            if (index == this.getFunctionParameters().size() - 1) {
                printStream.printf("%s│   └── %s%n", indents, arkoiSemanticAST.getClass().getSimpleName());
                arkoiSemanticAST.printSemanticAST(printStream, indents + "│       ");
            } else {
                printStream.printf("%s│   ├── %s%n", indents, arkoiSemanticAST.getClass().getSimpleName());
                arkoiSemanticAST.printSemanticAST(printStream, indents + "│   │   ");
                printStream.printf("%s│   │%n", indents);
            }
        }
        printStream.printf("%s│%n", indents);
        printStream.printf("%s└── block:%n", indents);
        this.getFunctionBlock().printSemanticAST(printStream, indents + "     ");
    }
    
    
    @NotNull
    private List<AnnotationSemanticAST> checkFunctionAnnotations() {
        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null."));
        Objects.requireNonNull(this.getSemanticAnalyzer().getArkoiClass(), this.getFailedSupplier("semanticAnalyzer.arkoiClass must not be null."));
        Objects.requireNonNull(this.getSyntaxAST().getFunctionAnnotations(), this.getFailedSupplier("syntaxAST.functionAnnotations must not be null."));
        
        final List<AnnotationSemanticAST> functionAnnotations = new ArrayList<>();
        final HashMap<String, AnnotationSemanticAST> names = new HashMap<>();
        
        for (final AnnotationSyntaxAST annotationSyntaxAST : this.getSyntaxAST().getFunctionAnnotations()) {
            final AnnotationSemanticAST annotationSemanticAST = new AnnotationSemanticAST(
                    this.getSemanticAnalyzer(),
                    this,
                    annotationSyntaxAST
            );
            
            final IdentifierToken annotationName = annotationSemanticAST.getAnnotationName();
            if (names.containsKey(annotationName.getTokenContent())) {
                this.addError(
                        null,
                        this.getSemanticAnalyzer().getArkoiClass(),
                        new ArkoiSemanticAST[] {
                                this,
                                names.get(annotationName.getTokenContent())
                        },
                        SemanticErrorType.FUNCTION_ANNOTATION_SAME_NAME
                );
            } else names.put(annotationName.getTokenContent(), annotationSemanticAST);
            
            if (annotationSemanticAST.isFailed())
                this.isFailed();
            functionAnnotations.add(annotationSemanticAST);
        }
        return functionAnnotations;
    }
    
    
    @NotNull
    private IdentifierToken checkFunctionName() {
        Objects.requireNonNull(this.getSyntaxAST().getFunctionName(), this.getFailedSupplier("syntaxAST.functionName must not be null."));
        return this.getSyntaxAST().getFunctionName();
    }
    
    
    @NotNull
    private TypeSemanticAST checkFunctionReturnType() {
        Objects.requireNonNull(this.getSyntaxAST().getFunctionReturnType(), this.getFailedSupplier("syntaxAST.functionReturnType must not be null."));
        
        final TypeSemanticAST typeSemanticAST = new TypeSemanticAST(
                this.getSemanticAnalyzer(),
                this.getLastContainerAST(),
                this.getSyntaxAST().getFunctionReturnType()
        );
        
        if (typeSemanticAST.isFailed())
            this.failed();
        return typeSemanticAST;
    }
    
    
    @NotNull
    private List<ParameterSemanticAST> checkFunctionParameters() {
        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null."));
        Objects.requireNonNull(this.getSemanticAnalyzer().getArkoiClass(), this.getFailedSupplier("semanticAnalyzer.arkoiClass must not be null."));
        Objects.requireNonNull(this.getSyntaxAST().getFunctionParameters(), this.getFailedSupplier("syntaxAST.functionParameters must not be null."));
        
        final List<ParameterSemanticAST> functionParameters = new ArrayList<>();
        final HashMap<String, ParameterSemanticAST> names = new HashMap<>();
        
        for (final ParameterSyntaxAST parameterSyntaxAST : this.getSyntaxAST().getFunctionParameters().getParameters()) {
            final ParameterSemanticAST parameterSemanticAST = new ParameterSemanticAST(
                    this.getSemanticAnalyzer(),
                    this,
                    parameterSyntaxAST
            );
            
            final IdentifierToken parameterName = parameterSemanticAST.getParameterName();
            if (names.containsKey(parameterName.getTokenContent())) {
                this.addError(
                        null,
                        this.getSemanticAnalyzer().getArkoiClass(),
                        new ArkoiSemanticAST[] {
                                this,
                                names.get(parameterName.getTokenContent())
                        },
                        SemanticErrorType.FUNCTION_ARGUMENT_SAME_NAME
                );
            } else names.put(parameterName.getTokenContent(), parameterSemanticAST);
            
            if (parameterSemanticAST.isFailed())
                this.failed();
            functionParameters.add(parameterSemanticAST);
        }
        
        return functionParameters;
    }
    
    
    @NotNull
    private BlockSemanticAST checkFunctionBlock() {
        Objects.requireNonNull(this.getSyntaxAST().getFunctionBlock(), this.getFailedSupplier("syntaxAST.functionBlock must not be null."));
        
        final BlockSemanticAST blockSemanticAST = new BlockSemanticAST(
                this.getSemanticAnalyzer(),
                this,
                this.getSyntaxAST().getFunctionBlock()
        );
        
        final HashMap<String, ArkoiSemanticAST<?>> names = new HashMap<>();
        for (final ParameterSemanticAST parameterSemanticAST : this.getFunctionParameters())
            names.put(parameterSemanticAST.getParameterName().getTokenContent(), parameterSemanticAST);
        
        blockSemanticAST.getBlockType();
        blockSemanticAST.getBlockStorage(names);
        
        if (blockSemanticAST.isFailed())
            this.failed();
        return blockSemanticAST;
    }
    
    
    @NotNull
    private String generateFunctionDescription() {
        final StringBuilder descriptionBuilder = new StringBuilder(this.getFunctionName().getTokenContent());
        for (final ParameterSemanticAST parameterSemanticAST : this.getFunctionParameters())
            descriptionBuilder.append(parameterSemanticAST.getParameterType().getTypeKind());
        return descriptionBuilder.toString();
    }
    
    
    @Nullable
    public ArkoiSemanticAST<?> findIdentifier(@NotNull final IdentifierToken identifierToken) {
        final ArkoiSemanticAST<?> foundIdentifier = this.getFunctionBlock().findIdentifier(identifierToken);
        if (foundIdentifier != null)
            return foundIdentifier;
        
        for (final ParameterSemanticAST parameterSemanticAST : this.getFunctionParameters()) {
            if (parameterSemanticAST.getParameterName().getTokenContent().equals(identifierToken.getTokenContent()))
                return parameterSemanticAST;
        }
        return null;
    }
    
}
