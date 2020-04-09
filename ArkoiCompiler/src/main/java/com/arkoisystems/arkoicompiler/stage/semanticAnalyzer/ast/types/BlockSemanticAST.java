/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.api.ICompilerSemanticAST;
import com.arkoisystems.arkoicompiler.api.ICompilerSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticErrorType;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.ArkoiSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.IdentifierCallSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.ExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.ImportSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.ReturnSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.VariableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.BlockSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ReturnSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.BlockType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class BlockSemanticAST extends ArkoiSemanticAST<BlockSyntaxAST>
{
    
    @Nullable
    private List<ICompilerSemanticAST<?>> blockStorage;
    
    
    public BlockSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final ICompilerSemanticAST<?> lastContainerAST, @NotNull final BlockSyntaxAST blockSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, blockSyntaxAST, ASTType.BLOCK);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        final List<ICompilerSemanticAST<?>> blockStorage = this.getBlockStorage(new HashMap<>());
        printStream.println(indents + "├── type: " + this.getBlockType());
        printStream.println(indents + "└── storage: " + (blockStorage.isEmpty() ? "N/A" : ""));
        for (int index = 0; index < blockStorage.size(); index++) {
            final ICompilerSemanticAST<?> arkoiSemanticAST = blockStorage.get(index);
            if (index == blockStorage.size() - 1) {
                printStream.println(indents + "    └── " + arkoiSemanticAST.getClass().getSimpleName());
                arkoiSemanticAST.printSemanticAST(printStream, indents + "        ");
            } else {
                printStream.println(indents + "    ├── " + arkoiSemanticAST.getClass().getSimpleName());
                arkoiSemanticAST.printSemanticAST(printStream, indents + "    │   ");
                printStream.println(indents + "    │   ");
            }
        }
    }
    
    
    @Nullable
    public BlockType getBlockType() {
        return this.getSyntaxAST().getBlockType();
    }
    
    
    @NotNull
    public List<ICompilerSemanticAST<?>> getBlockStorage(final HashMap<String, ArkoiSemanticAST<?>> names) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        if (this.blockStorage == null) {
            this.blockStorage = new ArrayList<>();
            
            if (this.getBlockType() == null)
                this.failed();
            
            for (final ICompilerSyntaxAST syntaxAST : this.getSyntaxAST().getBlockStorage()) {
                if (this.getBlockType() == BlockType.INLINE) {
                    if (!(syntaxAST instanceof OperableSyntaxAST)) {
                        this.addError(
                                null,
                                this.getSemanticAnalyzer().getArkoiClass(),
                                syntaxAST,
                                SemanticErrorType.BLOCK_INLINE_EXPRESSION
                        );
                        continue;
                    }
                    
                    final OperableSyntaxAST expressionSyntaxAST = (OperableSyntaxAST) syntaxAST;
                    final ExpressionSemanticAST<?> expressionSemanticAST = new ExpressionSemanticAST<>(
                            this.getSemanticAnalyzer(),
                            this.getLastContainerAST(),
                            expressionSyntaxAST
                    );
                    
                    expressionSemanticAST.getTypeKind();
                    
                    if (expressionSemanticAST.isFailed())
                        this.failed();
                    this.blockStorage.add(expressionSemanticAST);
                } else {
                    if (syntaxAST instanceof BlockSyntaxAST) {
                        final BlockSyntaxAST blockSyntaxAST = (BlockSyntaxAST) syntaxAST;
                        final BlockSemanticAST blockSemanticAST
                                = new BlockSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), blockSyntaxAST);
                        this.blockStorage.add(blockSemanticAST);
                        
                        blockSemanticAST.getBlockType();
                        blockSemanticAST.getBlockStorage(names);
                        
                        if (blockSemanticAST.isFailed())
                            this.failed();
                    } else if (syntaxAST instanceof VariableSyntaxAST) {
                        final VariableSyntaxAST variableSyntaxAST = (VariableSyntaxAST) syntaxAST;
                        final VariableSemanticAST variableSemanticAST
                                = new VariableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), variableSyntaxAST);
                        
                        variableSemanticAST.getVariableAnnotations();
                        
                        final IdentifierToken variableName = variableSemanticAST.getVariableName();
                        if (names.containsKey(variableName.getTokenContent())) {
                            final ArkoiSemanticAST<?> alreadyExistAST = names.get(variableName.getTokenContent());
                            this.addError(
                                    null,
                                    this.getSemanticAnalyzer().getArkoiClass(),
                                    new ArkoiSemanticAST[] {
                                            alreadyExistAST,
                                            variableSemanticAST
                                    },
                                    SemanticErrorType.VARIABLE_NAME_ALREADY_TAKEN,
                                    (alreadyExistAST instanceof ImportSemanticAST) ? "an import" : "a variable"
                            );
                            this.failed();
                        } else
                            names.put(variableName.getTokenContent(), variableSemanticAST);
                        
                        variableSemanticAST.getVariableExpression();
                        
                        if (variableSemanticAST.isFailed())
                            this.failed();
                        this.blockStorage.add(variableSemanticAST);
                    } else if (syntaxAST instanceof ReturnSyntaxAST) {
                        final ReturnSyntaxAST returnSyntaxAST = (ReturnSyntaxAST) syntaxAST;
                        final ReturnSemanticAST returnSemanticAST
                                = new ReturnSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), returnSyntaxAST);
                        
                        // TODO: Check if the function has a return type of void and if doesn't has a expression.
                        returnSemanticAST.getReturnExpression();
                        
                        if (returnSemanticAST.isFailed())
                            this.failed();
                        this.blockStorage.add(returnSemanticAST);
                    } else if (syntaxAST instanceof IdentifierCallSyntaxAST) {
                        final IdentifierCallSyntaxAST identifierCallSyntaxAST = (IdentifierCallSyntaxAST) syntaxAST;
                        final IdentifierCallSemanticAST identifierCallSemanticAST
                                = new IdentifierCallSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), identifierCallSyntaxAST);
                        
                        if (identifierCallSemanticAST.isFailed())
                            this.failed();
                        this.blockStorage.add(identifierCallSemanticAST);
                    } else {
                        this.addError(
                                null,
                                this.getSemanticAnalyzer().getArkoiClass(),
                                syntaxAST,
                                SemanticErrorType.BLOCK_AST_NOT_SUPPORTED
                        );
                    }
                }
            }
        }
        return this.blockStorage;
    }
    
    
    @Nullable
    public ArkoiSemanticAST<?> findIdentifier(final IdentifierToken identifierToken) {
        for (final ICompilerSemanticAST<?> semanticAST : this.getBlockStorage(new HashMap<>())) {
            if (semanticAST instanceof BlockSemanticAST) {
                final BlockSemanticAST blockSemanticAST = (BlockSemanticAST) semanticAST;
                final ArkoiSemanticAST<?> foundIdentifier = blockSemanticAST.findIdentifier(identifierToken);
                if (foundIdentifier != null)
                    return foundIdentifier;
            } else if (semanticAST instanceof VariableSemanticAST) {
                final VariableSemanticAST variableSemanticAST = (VariableSemanticAST) semanticAST;
                final IdentifierToken variableName = variableSemanticAST.getVariableName();
                if (variableName.getTokenContent().equals(identifierToken.getTokenContent()))
                    return variableSemanticAST;
            }
        }
        return null;
    }
    
}
