/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.doubles.DoubleSyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.FunctionDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.ImportDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.VariableDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.RootSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ImportDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Used if you want to create a new {@link RootSemanticAST}. Usually you don't create an
 * {@link AbstractSemanticAST} manually because it is just a representation of an {@link
 * AbstractSyntaxAST} which got checked for semantic errors.
 */
public class RootSemanticAST extends AbstractSemanticAST<RootSyntaxAST>
{
    
    
    /**
     * Declares a {@link List} of {@link ImportDefinitionSemanticAST}'s that were added
     * when the {@link #initialize()} method was executed.
     */
    @Getter
    private final List<ImportDefinitionSemanticAST> importStorage;
    
    
    /**
     * Declares a {@link List} of {@link VariableDefinitionSemanticAST}'s that were added
     * when the {@link #initialize()} method was executed.
     */
    @Getter
    private final List<VariableDefinitionSemanticAST> variableStorage;
    
    
    /**
     * Declares a {@link List} of {@link FunctionDefinitionSemanticAST}'s that were added
     * when the {@link RootSemanticAST} was constructed.
     */
    @Getter
    private final List<FunctionDefinitionSemanticAST> functionStorage;
    
    
    /**
     * Constructs a new {@link RootSemanticAST} with the given parameters. The {@code
     * semanticAnalyzer} is used to set the internal {@link SemanticAnalyzer} for later
     * use when checking the semantic or creating new {@link AbstractSemanticAST}'s.
     *
     * @param semanticAnalyzer
     *         the {@link SemanticAnalyzer} which should get used when creating new {@link
     *         AbstractSemanticAST}'s or when checking the semantic of them.
     * @param rootSyntaxAST
     *         the {@link RootSyntaxAST} which should get checked for semantic errors.
     */
    public RootSemanticAST(final SemanticAnalyzer semanticAnalyzer, final RootSyntaxAST rootSyntaxAST) {
        super(semanticAnalyzer, null, rootSyntaxAST, ASTType.ROOT);
        
        this.functionStorage = new ArrayList<>();
        this.variableStorage = new ArrayList<>();
        this.importStorage = new ArrayList<>();
        
        for (final FunctionDefinitionSyntaxAST functionDefinitionSyntaxAST : rootSyntaxAST.getFunctionStorage()) {
            final FunctionDefinitionSemanticAST functionDefinitionSemanticAST
                    = new FunctionDefinitionSemanticAST(semanticAnalyzer, this, functionDefinitionSyntaxAST);
            this.functionStorage.add(functionDefinitionSemanticAST);
        }
    }
    
    
    /**
     * Initializes the {@link RootSemanticAST} and adds every {@link
     * ImportDefinitionSemanticAST} or {@link VariableDefinitionSemanticAST} if there
     * occurred no error. Depending on that it will return null (when an error happened)
     * or itself if everything worked correctly.
     *
     * @return {@code null} if an error occurred or itself if everything worked
     *         correctly.
     */
    @Override
    public RootSemanticAST initialize() {
        final HashMap<String, AbstractSemanticAST<?>> names = new HashMap<>();
        for (final AbstractSyntaxAST abstractSyntaxAST : this.getSyntaxAST().getSortedStorage()) {
            if (abstractSyntaxAST instanceof FunctionDefinitionSyntaxAST)
                continue;
            
            if (abstractSyntaxAST instanceof ImportDefinitionSyntaxAST) {
                final ImportDefinitionSyntaxAST importDefinitionSyntaxAST = (ImportDefinitionSyntaxAST) abstractSyntaxAST;
                final ImportDefinitionSemanticAST importDefinitionSemanticAST
                        = new ImportDefinitionSemanticAST(this.getSemanticAnalyzer(), this, importDefinitionSyntaxAST);
                
                final IdentifierToken importName = importDefinitionSemanticAST.getImportName();
                if (importName == null)
                    return null;
                
                if (names.containsKey(importName.getTokenContent())) {
                    final AbstractSemanticAST<?> alreadyExistAST = names.get(importName.getTokenContent());
                    this.getSemanticAnalyzer().errorHandler().addError(new DoubleSyntaxASTError<>(
                            importDefinitionSemanticAST.getSyntaxAST(),
                            alreadyExistAST.getSyntaxAST(),
                            "There already exists %s with the same name.",
                            (alreadyExistAST instanceof ImportDefinitionSemanticAST) ? "an import" : "a variable"
                    ));
                    return null;
                } else
                    names.put(importName.getTokenContent(), importDefinitionSemanticAST);
                
                if (importDefinitionSemanticAST.getImportTargetClass() == null)
                    return null;
                this.importStorage.add(importDefinitionSemanticAST);
            } else if (abstractSyntaxAST instanceof VariableDefinitionSyntaxAST) {
                final VariableDefinitionSyntaxAST variableDefinitionSyntaxAST = (VariableDefinitionSyntaxAST) abstractSyntaxAST;
                final VariableDefinitionSemanticAST variableDefinitionSemanticAST
                        = new VariableDefinitionSemanticAST(this.getSemanticAnalyzer(), this, variableDefinitionSyntaxAST);
                
                final IdentifierToken variableName = variableDefinitionSemanticAST.getVariableName();
                if (variableName == null)
                    return null;
                
                if (names.containsKey(variableName.getTokenContent())) {
                    final AbstractSemanticAST<?> alreadyExistAST = names.get(variableName.getTokenContent());
                    this.getSemanticAnalyzer().errorHandler().addError(new DoubleSyntaxASTError<>(
                            variableDefinitionSemanticAST.getSyntaxAST(),
                            alreadyExistAST.getSyntaxAST(),
                            "There already exists %s with the same name.",
                            (alreadyExistAST instanceof ImportDefinitionSemanticAST) ? "an import" : "a variable"
                    ));
                    return null;
                } else
                    names.put(variableName.getTokenContent(), variableDefinitionSemanticAST);
                
                if (variableDefinitionSemanticAST.getVariableExpression() == null)
                    return null;
                this.variableStorage.add(variableDefinitionSemanticAST);
            }
        }
        
        for (final FunctionDefinitionSemanticAST functionDefinitionSemanticAST : this.getFunctionStorage()) {
            if (functionDefinitionSemanticAST.getFunctionAnnotations() == null)
                return null;
            
            final String functionDescription = functionDefinitionSemanticAST.getFunctionDescription();
            if (functionDescription == null)
                return null;
            
            if (names.containsKey(functionDescription)) {
                final AbstractSemanticAST<?> alreadyExistAST = names.get(functionDescription);
                this.getSemanticAnalyzer().errorHandler().addError(new DoubleSyntaxASTError<>(
                        functionDefinitionSemanticAST.getSyntaxAST(),
                        alreadyExistAST.getSyntaxAST(),
                        "There already exists another function with the same name and arguments."
                ));
                return null;
            } else names.put(functionDescription, functionDefinitionSemanticAST);
            
            if (functionDefinitionSemanticAST.getFunctionBlock() == null)
                return null;
        }
        return this;
    }
    
    
    /**
     * Finds an {@link AbstractSemanticAST} with help of an {@link IdentifierToken}. It
     * will go through all {@link ImportDefinitionSemanticAST}'s and {@link
     * VariableDefinitionSemanticAST}'s and searched for a match with the given {@link
     * IdentifierToken}.
     *
     * @param identifierToken
     *         the {@link IdentifierToken} which should get used to search the {@link
     *         AbstractSemanticAST}.
     *
     * @return {@code null} if it found nothing, or the found {@link
     *         AbstractSemanticAST}.
     */
    public AbstractSemanticAST<?> findIdentifier(final IdentifierToken identifierToken) {
        for (final ImportDefinitionSemanticAST importDefinitionSemanticAST : this.getImportStorage()) {
            if (importDefinitionSemanticAST.getImportName().getTokenContent().equals(identifierToken.getTokenContent()))
                return importDefinitionSemanticAST;
        }
        
        for (final VariableDefinitionSemanticAST variableDefinitionSemanticAST : this.getVariableStorage()) {
            if (variableDefinitionSemanticAST.getVariableName().getTokenContent().equals(identifierToken.getTokenContent()))
                return variableDefinitionSemanticAST;
        }
        return null;
    }
    
    
    /**
     * Finds an {@link FunctionDefinitionSemanticAST} using the given {@code
     * functionDescription}. It goes through all {@link FunctionDefinitionSemanticAST}'s
     * and checks them for a match. Depending on this {@code null} or the found {@link
     * FunctionDefinitionSemanticAST} is returned.
     *
     * @param functionDescription
     *         the {@code functionDescription} which should get used when searching the
     *         {@link FunctionDefinitionSemanticAST}
     *
     * @return {@code null} if it found nothing, or the found {@link
     *         FunctionDefinitionSemanticAST}.
     */
    public FunctionDefinitionSemanticAST findFunction(final String functionDescription) {
        for (final FunctionDefinitionSemanticAST functionDefinitionSemanticAST : this.getFunctionStorage()) {
            if (functionDefinitionSemanticAST.getFunctionDescription().equals(functionDescription))
                return functionDefinitionSemanticAST;
        }
        return null;
    }
    
}
