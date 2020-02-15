/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.doubles.DoubleSyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.FunctionInvokeOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types.ExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.ReturnStatementSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.VariableDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.BlockSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.FunctionInvokeOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ReturnStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableDefinitionSyntaxAST;
import com.google.gson.annotations.Expose;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Setter
public class BlockSemanticAST extends AbstractSemanticAST<BlockSyntaxAST>
{
    
    @Expose
    private List<AbstractSemanticAST<?>> blockStorage;
    
    public BlockSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final BlockSyntaxAST blockSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, blockSyntaxAST, ASTType.BLOCK);
    }
    
    public BlockSyntaxAST.BlockType getBlockType() {
        return this.getSyntaxAST().getBlockType();
    }
    
    public List<AbstractSemanticAST<?>> getBlockStorage() {
        if (this.blockStorage == null) {
            this.blockStorage = new ArrayList<>();
            
            for (final AbstractSyntaxAST abstractSyntaxAST : this.getSyntaxAST().getBlockStorage()) {
                if (this.getBlockType() == null)
                    return null;
                
                if (this.getBlockType() == BlockSyntaxAST.BlockType.INLINE) {
                    if (!(abstractSyntaxAST instanceof ExpressionSyntaxAST)) {
                        this.getSemanticAnalyzer().errorHandler().addError(new SyntaxASTError<>(abstractSyntaxAST, "Couldn't analyze this inlined-block because a non expression is inside the block."));
                        return null;
                    }
                    
                    final ExpressionSyntaxAST expressionSyntaxAST = (ExpressionSyntaxAST) abstractSyntaxAST;
                    final ExpressionSemanticAST expressionSemanticAST
                            = new ExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), expressionSyntaxAST);
                    
                    if (expressionSemanticAST.getExpressionType() == null)
                        return null;
                    this.blockStorage.add(expressionSemanticAST);
                } else {
                    if (abstractSyntaxAST instanceof BlockSyntaxAST) {
                        final BlockSyntaxAST blockSyntaxAST = (BlockSyntaxAST) abstractSyntaxAST;
                        final BlockSemanticAST blockSemanticAST
                                = new BlockSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), blockSyntaxAST);
                        this.blockStorage.add(blockSemanticAST);
                        
                        if (blockSemanticAST.getBlockType() == null)
                            return null;
                        if (blockSemanticAST.getBlockStorage() == null)
                            return null;
                    } else if (abstractSyntaxAST instanceof VariableDefinitionSyntaxAST) {
                        final VariableDefinitionSyntaxAST variableDefinitionSyntaxAST = (VariableDefinitionSyntaxAST) abstractSyntaxAST;
                        final VariableDefinitionSemanticAST variableDefinitionSemanticAST
                                = new VariableDefinitionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), variableDefinitionSyntaxAST);
                        
                        if (variableDefinitionSemanticAST.getVariableAnnotations() == null)
                            return null;
                        if (variableDefinitionSemanticAST.getVariableName() == null)
                            return null;
                        if (variableDefinitionSemanticAST.getVariableExpression() == null)
                            return null;
                        this.blockStorage.add(variableDefinitionSemanticAST);
                    } else if (abstractSyntaxAST instanceof FunctionInvokeOperableSyntaxAST) {
                        final FunctionInvokeOperableSyntaxAST functionInvokeOperableSyntaxAST = (FunctionInvokeOperableSyntaxAST) abstractSyntaxAST;
                        final FunctionInvokeOperableSemanticAST functionInvokeOperableSemanticAST
                                = new FunctionInvokeOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), functionInvokeOperableSyntaxAST);
    
                        if (functionInvokeOperableSemanticAST.getInvokedFunction() == null)
                            return null;
                        if (functionInvokeOperableSemanticAST.getInvokedExpressions() == null)
                            return null;
                        if (functionInvokeOperableSemanticAST.getExpressionType() == null)
                            return null;
                        this.blockStorage.add(functionInvokeOperableSemanticAST);
                    } else if (abstractSyntaxAST instanceof ReturnStatementSyntaxAST) {
                        final ReturnStatementSyntaxAST returnStatementSyntaxAST = (ReturnStatementSyntaxAST) abstractSyntaxAST;
                        final ReturnStatementSemanticAST returnStatementSemanticAST
                                = new ReturnStatementSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), returnStatementSyntaxAST);
    
                        // TODO: Check if the function has a return type of void and if doesn't has a expression.
                        
                        if (returnStatementSemanticAST.getReturnExpression() == null)
                            return null;
                        this.blockStorage.add(returnStatementSemanticAST);
                    } else {
                        this.getSemanticAnalyzer().errorHandler().addError(new SyntaxASTError<>(abstractSyntaxAST, "Couldn't analyze this AST because it isn't supported by the block."));
                        return null;
                    }
                }
            }
            
            if (this.foundDuplicatedNames(new HashMap<>()))
                return null;
        }
        return this.blockStorage;
    }
    
    private boolean foundDuplicatedNames(HashMap<String, AbstractSemanticAST<?>> names) {
        if (this.getBlockStorage() == null)
            return true;
        
        for (final AbstractSemanticAST<?> abstractSemanticAST : this.getBlockStorage()) {
            if (abstractSemanticAST instanceof BlockSemanticAST) {
                final BlockSemanticAST blockSemanticAST = (BlockSemanticAST) abstractSemanticAST;
                if (blockSemanticAST.foundDuplicatedNames(names))
                    return true;
            } else if (abstractSemanticAST instanceof VariableDefinitionSemanticAST) {
                final VariableDefinitionSemanticAST variableDefinitionSemanticAST = (VariableDefinitionSemanticAST) abstractSemanticAST;
                final IdentifierToken variableName = variableDefinitionSemanticAST.getVariableName();
                if (variableName == null)
                    return true;
                
                if (names.containsKey(variableName.getTokenContent())) {
                    final AbstractSemanticAST<?> alreadyExistAST = names.get(variableName.getTokenContent());
                    this.getSemanticAnalyzer().errorHandler().addError(new DoubleSyntaxASTError<>(alreadyExistAST.getSyntaxAST(), abstractSemanticAST.getSyntaxAST(), "Couldn't analyze this variable because there already exists another one with the same name."));
                    return true;
                } else
                    names.put(variableName.getTokenContent(), variableDefinitionSemanticAST);
            }
        }
        return false;
    }
    
    public AbstractSemanticAST<?> findIdentifier(final IdentifierToken identifierToken) {
        if(this.getBlockStorage() == null)
            return null;
        
        for(final AbstractSemanticAST<?> abstractSemanticAST : this.getBlockStorage()) {
            if (abstractSemanticAST instanceof BlockSemanticAST) {
                final BlockSemanticAST blockSemanticAST = (BlockSemanticAST) abstractSemanticAST;
                final AbstractSemanticAST<?> foundIdentifier = blockSemanticAST.findIdentifier(identifierToken);
                if(foundIdentifier != null)
                    return foundIdentifier;
            } else if (abstractSemanticAST instanceof VariableDefinitionSemanticAST) {
                final VariableDefinitionSemanticAST variableDefinitionSemanticAST = (VariableDefinitionSemanticAST) abstractSemanticAST;
                final IdentifierToken variableName = variableDefinitionSemanticAST.getVariableName();
                if (variableName == null)
                    return null;
                if(variableName.getTokenContent().equals(identifierToken.getTokenContent()))
                    return variableDefinitionSemanticAST;
            }
        }
        return null;
    }
    
}
