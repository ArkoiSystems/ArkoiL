/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticErrorType;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.AnnotationSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.BlockSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.ParameterSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.AnnotationSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.ParameterSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.TypeSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class FunctionDefinitionSemanticAST extends AbstractSemanticAST<FunctionDefinitionSyntaxAST>
{
    
    
    @Nullable
    private List<AnnotationSemanticAST> functionAnnotations;
    
    
    @Nullable
    private List<ParameterSemanticAST> functionArguments;
    
    
    @Nullable
    private BlockSemanticAST functionBlock;
    
    
    @Nullable
    private String functionDescription;
    
    
    public FunctionDefinitionSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final AbstractSemanticAST<?> lastContainerAST, @NonNull final FunctionDefinitionSyntaxAST functionDefinitionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, functionDefinitionSyntaxAST, ASTType.FUNCTION_DEFINITION);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "├── annotations: " + (this.getFunctionAnnotations().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getFunctionAnnotations().size(); index++) {
            final AbstractSemanticAST<?> abstractSemanticAST = this.getFunctionAnnotations().get(index);
            if (index == this.getFunctionAnnotations().size() - 1) {
                printStream.println(indents + "│   └── " + abstractSemanticAST.getClass().getSimpleName());
                abstractSemanticAST.printSemanticAST(printStream, indents + "│       ");
            } else {
                printStream.println(indents + "│   ├── " + abstractSemanticAST.getClass().getSimpleName());
                abstractSemanticAST.printSemanticAST(printStream, indents + "│   │   ");
                printStream.println(indents + "│   │   ");
            }
        }
        printStream.println(indents + "│");
        printStream.println(indents + "├── name: " + this.getFunctionName().getTokenContent());
        printStream.println(indents + "├── type: " + (this.getFunctionReturnType() != null ? this.getFunctionReturnType().getTypeKeywordToken().getKeywordType() + (this.getFunctionReturnType().isArray() ? "[]" : "") : null));
        printStream.println(indents + "│");
        printStream.println(indents + "├── arguments: " + (this.getFunctionArguments().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getFunctionArguments().size(); index++) {
            final AbstractSemanticAST<?> abstractSemanticAST = this.getFunctionArguments().get(index);
            if (index == this.getFunctionArguments().size() - 1) {
                printStream.println(indents + "│   └── " + abstractSemanticAST.getClass().getSimpleName());
                abstractSemanticAST.printSemanticAST(printStream, indents + "│       ");
            } else {
                printStream.println(indents + "│   ├── " + abstractSemanticAST.getClass().getSimpleName());
                abstractSemanticAST.printSemanticAST(printStream, indents + "│   │   ");
                printStream.println(indents + "│   │   ");
            }
        }
        printStream.println(indents + "│");
        printStream.println(indents + "└── block: " + (this.getFunctionBlock() == null ? null : ""));
        if (this.getFunctionBlock() != null)
            this.getFunctionBlock().printSemanticAST(printStream, indents + "     ");
    }
    
    
    public List<AnnotationSemanticAST> getFunctionAnnotations() {
        Objects.requireNonNull(this.getSemanticAnalyzer());
    
        if (this.functionAnnotations == null) {
            this.functionAnnotations = new ArrayList<>();
        
            final HashMap<String, AbstractSemanticAST<?>> names = new HashMap<>();
            for (final AnnotationSyntaxAST annotationSyntaxAST : this.getSyntaxAST().getFunctionAnnotations()) {
                final AnnotationSemanticAST annotationSemanticAST
                        = new AnnotationSemanticAST(this.getSemanticAnalyzer(), this, annotationSyntaxAST);
    
                final IdentifierToken annotationName = annotationSemanticAST.getAnnotationName();
                if (annotationName != null) {
                    if (names.containsKey(annotationName.getTokenContent())) {
                        final AbstractSemanticAST<?> alreadyExistAST = names.get(annotationName.getTokenContent());
                        this.addError(
                                this.getSemanticAnalyzer().getArkoiClass(), new AbstractSemanticAST[] {
                                        this,
                                        alreadyExistAST
                                }, SemanticErrorType.FUNCTION_ANNOTATION_SAME_NAME
                        );
                    } else
                        names.put(annotationName.getTokenContent(), annotationSemanticAST);
                } else this.failed();
    
                if (annotationSemanticAST.isFailed())
                    this.failed();
                this.functionAnnotations.add(annotationSemanticAST);
            }
            return this.functionAnnotations;
        }
        return this.functionAnnotations;
    }
    
    
    @Nullable
    public TypeSyntaxAST getFunctionReturnType() {
        return this.getSyntaxAST().getFunctionReturnType();
    }
    
    
    @NotNull
    public IdentifierToken getFunctionName() {
        return this.getSyntaxAST().getFunctionName();
    }
    
    
    @NotNull
    public List<ParameterSemanticAST> getFunctionArguments() {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        if (this.functionArguments == null) {
            this.functionArguments = new ArrayList<>();
            
            final HashMap<String, ParameterSemanticAST> names = new HashMap<>();
            for (final ParameterSyntaxAST parameterSyntaxAST : this.getSyntaxAST().getFunctionParameters()) {
                final ParameterSemanticAST parameterSemanticAST
                        = new ParameterSemanticAST(this.getSemanticAnalyzer(), this, parameterSyntaxAST);
        
                final IdentifierToken argumentName = parameterSemanticAST.getParameterName();
                if (names.containsKey(argumentName.getTokenContent())) {
                    final AbstractSemanticAST<?> alreadyExistAST = names.get(argumentName.getTokenContent());
                    this.addError(
                            this.getSemanticAnalyzer().getArkoiClass(), new AbstractSemanticAST[] {
                                    alreadyExistAST,
                                    parameterSemanticAST
                            }, SemanticErrorType.FUNCTION_ARGUMENT_SAME_NAME
                    );
                } else
                    names.put(argumentName.getTokenContent(), parameterSemanticAST);
                
                if (parameterSemanticAST.isFailed())
                    this.failed();
                this.functionArguments.add(parameterSemanticAST);
            }
        }
        return this.functionArguments;
    }
    
    
    @NotNull
    public String getFunctionDescription() {
        if (this.functionDescription == null) {
            final StringBuilder descriptionBuilder = new StringBuilder(this.getFunctionName().getTokenContent());
            for (final ParameterSemanticAST parameterSemanticAST : this.getFunctionArguments())
                descriptionBuilder.append((parameterSemanticAST.getParameterType() != null ? parameterSemanticAST.getParameterType().getTypeKind() : ""));
            return (this.functionDescription = descriptionBuilder.toString());
        }
        return this.functionDescription;
    }
    
    
    @Nullable
    public BlockSemanticAST getFunctionBlock() {
        if (this.functionBlock == null) {
            if (this.getSyntaxAST().getFunctionBlock() == null)
                return null;
            this.functionBlock = new BlockSemanticAST(this.getSemanticAnalyzer(), this, this.getSyntaxAST().getFunctionBlock());
    
            final HashMap<String, AbstractSemanticAST<?>> names = new HashMap<>();
            for (final ParameterSemanticAST parameterSemanticAST : this.getFunctionArguments())
                names.put(parameterSemanticAST.getParameterName().getTokenContent(), parameterSemanticAST);
    
            this.functionBlock.getBlockType();
            this.functionBlock.getBlockStorage(names);
    
            if (this.functionBlock.isFailed())
                this.failed();
        }
        return this.functionBlock;
    }
    
    
    @Nullable
    public AbstractSemanticAST<?> findIdentifier(final IdentifierToken identifierToken) {
        if (this.getFunctionBlock() == null)
            return null;
    
        final AbstractSemanticAST<?> abstractSemanticAST = this.getFunctionBlock().findIdentifier(identifierToken);
        if (abstractSemanticAST == null) {
            for (final ParameterSemanticAST parameterSemanticAST : this.getFunctionArguments())
                if (parameterSemanticAST.getParameterName().getTokenContent().equals(identifierToken.getTokenContent()))
                    return parameterSemanticAST;
            return null;
        } else return abstractSemanticAST;
    }
    
}
