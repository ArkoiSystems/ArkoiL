/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticErrorType;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.FunctionInvokeOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types.ExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.ImportDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.ReturnStatementSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.VariableDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.BlockSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.FunctionInvokeOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ReturnStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.BlockType;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockSemanticAST extends AbstractSemanticAST<BlockSyntaxAST>
{
    
    private List<AbstractSemanticAST<?>> blockStorage;
    
    
    public BlockSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final BlockSyntaxAST blockSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, blockSyntaxAST, ASTType.BLOCK);
    }
    
    
    // TODO: Check for null safety.
    @Override
    public void printSemanticAST(final PrintStream printStream, final String indents) {
        final List<AbstractSemanticAST<?>> blockStorage = this.getBlockStorage(new HashMap<>());
        printStream.println(indents + "├── type: " + this.getBlockType());
        printStream.println(indents + "└── storage: " + (blockStorage.isEmpty() ? "N/A" : ""));
        for (int index = 0; index < blockStorage.size(); index++) {
            final AbstractSemanticAST<?> abstractSemanticAST = blockStorage.get(index);
            if (index == blockStorage.size() - 1) {
                printStream.println(indents + "    └── " + abstractSemanticAST.getClass().getSimpleName());
                abstractSemanticAST.printSemanticAST(printStream, indents + "        ");
            } else {
                printStream.println(indents + "    ├── " + abstractSemanticAST.getClass().getSimpleName());
                abstractSemanticAST.printSemanticAST(printStream, indents + "    │   ");
                printStream.println(indents + "    │   ");
            }
        }
    }
    
    
    public BlockType getBlockType() {
        return this.getSyntaxAST().getBlockType();
    }
    
    
    public List<AbstractSemanticAST<?>> getBlockStorage(final HashMap<String, AbstractSemanticAST<?>> names) {
        if (this.blockStorage == null) {
            this.blockStorage = new ArrayList<>();
    
            if (this.getBlockType() == null)
                this.failed();
    
            for (final AbstractSyntaxAST abstractSyntaxAST : this.getSyntaxAST().getBlockStorage()) {
                if (this.getBlockType() == BlockType.INLINE) {
                    if (!(abstractSyntaxAST instanceof ExpressionSyntaxAST)) {
                        this.addError(
                                this.getSemanticAnalyzer().getArkoiClass(),
                                abstractSyntaxAST,
                                SemanticErrorType.BLOCK_INLINE_EXPRESSION
                        );
                        continue;
                    }
            
                    final ExpressionSyntaxAST expressionSyntaxAST = (ExpressionSyntaxAST) abstractSyntaxAST;
                    final ExpressionSemanticAST expressionSemanticAST
                            = new ExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), expressionSyntaxAST);
            
                    expressionSemanticAST.getOperableObject();
            
                    if (expressionSemanticAST.isFailed())
                        this.failed();
                    this.blockStorage.add(expressionSemanticAST);
                } else {
                    if (abstractSyntaxAST instanceof BlockSyntaxAST) {
                        final BlockSyntaxAST blockSyntaxAST = (BlockSyntaxAST) abstractSyntaxAST;
                        final BlockSemanticAST blockSemanticAST
                                = new BlockSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), blockSyntaxAST);
                        this.blockStorage.add(blockSemanticAST);
    
                        blockSemanticAST.getBlockType();
                        blockSemanticAST.getBlockStorage(names);
    
                        if (blockSemanticAST.isFailed())
                            this.failed();
                    } else if (abstractSyntaxAST instanceof VariableDefinitionSyntaxAST) {
                        final VariableDefinitionSyntaxAST variableDefinitionSyntaxAST = (VariableDefinitionSyntaxAST) abstractSyntaxAST;
                        final VariableDefinitionSemanticAST variableDefinitionSemanticAST
                                = new VariableDefinitionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), variableDefinitionSyntaxAST);
    
                        variableDefinitionSemanticAST.getVariableAnnotations();
    
                        final IdentifierToken variableName = variableDefinitionSemanticAST.getVariableName();
                        if (variableName != null) {
                            if (names.containsKey(variableName.getTokenContent())) {
                                final AbstractSemanticAST<?> alreadyExistAST = names.get(variableName.getTokenContent());
                                this.addError(
                                        this.getSemanticAnalyzer().getArkoiClass(),
                                        new AbstractSemanticAST[] {
                                                alreadyExistAST,
                                                variableDefinitionSemanticAST
                                        },
                                        SemanticErrorType.VARIABLE_NAME_ALREADY_TAKEN,
                                        (alreadyExistAST instanceof ImportDefinitionSemanticAST) ? "an import" : "a variable"
                                );
                                this.failed();
                            } else
                                names.put(variableName.getTokenContent(), variableDefinitionSemanticAST);
                        } else this.failed();
    
                        variableDefinitionSemanticAST.getVariableExpression();
    
                        if (variableDefinitionSemanticAST.isFailed())
                            this.failed();
                        this.blockStorage.add(variableDefinitionSemanticAST);
                    } else if (abstractSyntaxAST instanceof FunctionInvokeOperableSyntaxAST) {
                        final FunctionInvokeOperableSyntaxAST functionInvokeOperableSyntaxAST = (FunctionInvokeOperableSyntaxAST) abstractSyntaxAST;
                        final FunctionInvokeOperableSemanticAST functionInvokeOperableSemanticAST
                                = new FunctionInvokeOperableSemanticAST(this.getSemanticAnalyzer(), this, this.getLastContainerAST(), functionInvokeOperableSyntaxAST);
    
                        functionInvokeOperableSemanticAST.getInvokedFunction();
                        functionInvokeOperableSemanticAST.getInvokedExpressions();
                        functionInvokeOperableSemanticAST.getOperableObject();
    
                        if (functionInvokeOperableSemanticAST.isFailed())
                            this.failed();
                        this.blockStorage.add(functionInvokeOperableSemanticAST);
                    } else if (abstractSyntaxAST instanceof ReturnStatementSyntaxAST) {
                        final ReturnStatementSyntaxAST returnStatementSyntaxAST = (ReturnStatementSyntaxAST) abstractSyntaxAST;
                        final ReturnStatementSemanticAST returnStatementSemanticAST
                                = new ReturnStatementSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), returnStatementSyntaxAST);
    
                        // TODO: Check if the function has a return type of void and if doesn't has a expression.
                        returnStatementSemanticAST.getReturnExpression();
    
                        if (returnStatementSemanticAST.isFailed())
                            this.failed();
                        this.blockStorage.add(returnStatementSemanticAST);
                    } else {
                        this.addError(
                                this.getSemanticAnalyzer().getArkoiClass(),
                                abstractSyntaxAST,
                                SemanticErrorType.BLOCK_AST_NOT_SUPPORTED
                        );
                    }
                }
            }
        }
        return this.blockStorage;
    }
    
    
    public AbstractSemanticAST<?> findIdentifier(final IdentifierToken identifierToken) {
        if (this.getBlockStorage(new HashMap<>()) == null)
            return null;
    
        for (final AbstractSemanticAST<?> abstractSemanticAST : this.getBlockStorage(new HashMap<>())) {
            if (abstractSemanticAST instanceof BlockSemanticAST) {
                final BlockSemanticAST blockSemanticAST = (BlockSemanticAST) abstractSemanticAST;
                final AbstractSemanticAST<?> foundIdentifier = blockSemanticAST.findIdentifier(identifierToken);
                if (foundIdentifier != null)
                    return foundIdentifier;
            } else if (abstractSemanticAST instanceof VariableDefinitionSemanticAST) {
                final VariableDefinitionSemanticAST variableDefinitionSemanticAST = (VariableDefinitionSemanticAST) abstractSemanticAST;
                final IdentifierToken variableName = variableDefinitionSemanticAST.getVariableName();
                if (variableName == null)
                    continue;
                if(variableName.getTokenContent().equals(identifierToken.getTokenContent()))
                    return variableDefinitionSemanticAST;
            }
        }
        return null;
    }
    
}
