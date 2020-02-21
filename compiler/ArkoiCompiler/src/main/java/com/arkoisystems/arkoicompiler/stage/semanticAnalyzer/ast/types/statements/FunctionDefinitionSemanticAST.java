/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SemanticASTError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.AnnotationSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.ArgumentDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.BlockSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.AnnotationSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.ArgumentDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.BlockSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.TypeSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FunctionDefinitionSemanticAST extends AbstractSemanticAST<FunctionDefinitionSyntaxAST>
{
    
    
    private List<AnnotationSemanticAST> functionAnnotations;
    
    
    private List<ArgumentDefinitionSemanticAST> functionArguments;
    
    
    private BlockSemanticAST functionBlock;
    
    
    private String functionDescription;
    
    
    public FunctionDefinitionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final FunctionDefinitionSyntaxAST functionDefinitionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, functionDefinitionSyntaxAST, ASTType.FUNCTION_DEFINITION);
    }
    
    
    public List<AnnotationSemanticAST> getFunctionAnnotations() {
        if(this.functionAnnotations == null) {
            this.functionAnnotations = new ArrayList<>();
            
            final HashMap<String, AbstractSemanticAST<?>> names = new HashMap<>();
            for(final AnnotationSyntaxAST annotationSyntaxAST : this.getSyntaxAST().getFunctionAnnotations()) {
                final AnnotationSemanticAST annotationSemanticAST
                        = new AnnotationSemanticAST(this.getSemanticAnalyzer(), this, annotationSyntaxAST);
        
                final IdentifierToken annotationName = annotationSemanticAST.getAnnotationName();
                if(annotationName == null)
                    return null;
        
                if (names.containsKey(annotationName.getTokenContent())) {
                    final AbstractSemanticAST<?> alreadyExistAST = names.get(annotationName.getTokenContent());
                    this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                            this.getSemanticAnalyzer().getArkoiClass(),
                            new AbstractSemanticAST[]{alreadyExistAST},
                            alreadyExistAST.getStart(),
                            annotationSemanticAST.getEnd(),
                            "Couldn't analyze this annotation because there already exists another one with the same name."
                    ));
                    return null;
                }
                
                names.put(annotationName.getTokenContent(), annotationSemanticAST);
                this.functionAnnotations.add(annotationSemanticAST);
            }
        }
        return this.functionAnnotations;
    }
    
    
    public TypeSyntaxAST getFunctionReturnType() {
        return this.getSyntaxAST().getFunctionReturnType();
    }
    
    
    public IdentifierToken getFunctionName() {
        return this.getSyntaxAST().getFunctionName();
    }
    
    
    public List<ArgumentDefinitionSemanticAST> getFunctionArguments() {
        if (this.functionArguments == null) {
            this.functionArguments = new ArrayList<>();
            
            final HashMap<String, ArgumentDefinitionSemanticAST> names = new HashMap<>();
            for (final ArgumentDefinitionSyntaxAST argumentDefinitionSyntaxAST : this.getSyntaxAST().getFunctionArguments()) {
                final ArgumentDefinitionSemanticAST argumentDefinitionSemanticAST
                        = new ArgumentDefinitionSemanticAST(this.getSemanticAnalyzer(), this, argumentDefinitionSyntaxAST);
                
                final IdentifierToken argumentName = argumentDefinitionSemanticAST.getArgumentName();
                if (argumentName == null)
                    return null;
                
                if (names.containsKey(argumentName.getTokenContent())) {
                    final AbstractSemanticAST<?> alreadyExistAST = names.get(argumentName.getTokenContent());
                    this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                            this.getSemanticAnalyzer().getArkoiClass(),
                            new AbstractSemanticAST[]{alreadyExistAST},
                            alreadyExistAST.getStart(),
                            argumentDefinitionSemanticAST.getEnd(),
                            "Couldn't analyze this argument because there already exists another AST with the same name."
                    ));
                    return null;
                } else names.put(argumentName.getTokenContent(), argumentDefinitionSemanticAST);
                
                this.functionArguments.add(argumentDefinitionSemanticAST);
            }
        }
        return this.functionArguments;
    }
    
    
    public String getFunctionDescription() {
        if (this.functionDescription == null) {
            final StringBuilder descriptionBuilder = new StringBuilder(this.getFunctionName().getTokenContent());
            for (final ArgumentDefinitionSemanticAST argumentDefinitionSemanticAST : this.getFunctionArguments())
                descriptionBuilder.append(argumentDefinitionSemanticAST.getArgumentType().getTypeKind());
            this.functionDescription = descriptionBuilder.toString();
        }
        return this.functionDescription;
    }
    
    
    public BlockSemanticAST getFunctionBlock() {
        if (this.functionBlock == null) {
            final BlockSyntaxAST blockSyntaxAST = this.getSyntaxAST().getFunctionBlock();
            this.functionBlock
                    = new BlockSemanticAST(this.getSemanticAnalyzer(), this, blockSyntaxAST);
    
            final HashMap<String, AbstractSemanticAST<?>> names = new HashMap<>();
            for(final ArgumentDefinitionSemanticAST argumentDefinitionSemanticAST : this.getFunctionArguments())
                names.put(argumentDefinitionSemanticAST.getArgumentName().getTokenContent(), argumentDefinitionSemanticAST);
            
            if (this.functionBlock.getBlockType() == null)
                return null;
            if (this.functionBlock.getBlockStorage(names) == null)
                return null;
        }
        return this.functionBlock;
    }
    
    
    public AbstractSemanticAST<?> findIdentifier(final IdentifierToken identifierToken) {
        if (this.getFunctionBlock() == null)
            return null;
        AbstractSemanticAST<?> abstractSemanticAST = this.getFunctionBlock().findIdentifier(identifierToken);
        if (abstractSemanticAST == null) {
            for (final ArgumentDefinitionSemanticAST argumentDefinitionSemanticAST : this.getFunctionArguments())
                if (argumentDefinitionSemanticAST.getArgumentName().getTokenContent().equals(identifierToken.getTokenContent()))
                    return argumentDefinitionSemanticAST;
            return null;
        } else return abstractSemanticAST;
    }
    
}
