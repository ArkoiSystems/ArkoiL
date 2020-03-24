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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.BlockSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.ParameterSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.TypeSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FunctionDefinitionSemanticAST extends AbstractSemanticAST<FunctionDefinitionSyntaxAST>
{
    
    
    private List<AnnotationSemanticAST> functionAnnotations;
    
    
    private List<ParameterSemanticAST> functionArguments;
    
    
    private BlockSemanticAST functionBlock;
    
    
    private String functionDescription;
    
    
    public FunctionDefinitionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final FunctionDefinitionSyntaxAST functionDefinitionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, functionDefinitionSyntaxAST, ASTType.FUNCTION_DEFINITION);
    }
    
    
    // TODO: Check for null safety.
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
        printStream.println(indents + "├── type: " + this.getFunctionReturnType().getTypeKind().getName() + (this.getFunctionReturnType().isArray() ? "[]" : ""));
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
        printStream.println(indents + "└── block: ");
        this.getFunctionBlock().printSemanticAST(printStream, indents + "     ");
    }
    
    
    public List<AnnotationSemanticAST> getFunctionAnnotations() {
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
    
    
    public TypeSyntaxAST getFunctionReturnType() {
        return this.getSyntaxAST().getFunctionReturnType();
    }
    
    
    public IdentifierToken getFunctionName() {
        return this.getSyntaxAST().getFunctionName();
    }
    
    
    public List<ParameterSemanticAST> getFunctionArguments() {
        if (this.functionArguments == null) {
            this.functionArguments = new ArrayList<>();
            
            final HashMap<String, ParameterSemanticAST> names = new HashMap<>();
            for (final ParameterSyntaxAST parameterSyntaxAST : this.getSyntaxAST().getFunctionArguments()) {
                final ParameterSemanticAST parameterSemanticAST
                        = new ParameterSemanticAST(this.getSemanticAnalyzer(), this, parameterSyntaxAST);
                
                final IdentifierToken argumentName = parameterSemanticAST.getParameterName();
                if (argumentName != null) {
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
                } else this.failed();
                
                if (parameterSemanticAST.isFailed())
                    this.failed();
                this.functionArguments.add(parameterSemanticAST);
            }
        }
        return this.functionArguments;
    }
    
    
    public String getFunctionDescription() {
        if (this.functionDescription == null) {
            final StringBuilder descriptionBuilder = new StringBuilder(this.getFunctionName().getTokenContent());
            if (this.getFunctionArguments() == null)
                return null;
            
            for (final ParameterSemanticAST parameterSemanticAST : this.getFunctionArguments())
                descriptionBuilder.append(parameterSemanticAST.getParameterType().getTypeKind());
            return (this.functionDescription = descriptionBuilder.toString());
        }
        return this.functionDescription;
    }
    
    
    public BlockSemanticAST getFunctionBlock() {
        if (this.functionBlock == null) {
            final BlockSyntaxAST blockSyntaxAST = this.getSyntaxAST().getFunctionBlock();
            this.functionBlock
                    = new BlockSemanticAST(this.getSemanticAnalyzer(), this, blockSyntaxAST);
            
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
