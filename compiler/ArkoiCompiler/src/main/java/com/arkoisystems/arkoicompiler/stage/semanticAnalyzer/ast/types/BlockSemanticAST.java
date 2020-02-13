package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.doubles.DoubleSemanticASTError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types.ExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.VariableDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.BlockSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableDefinitionSyntaxAST;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Copyright © 2019 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on the Sat Nov 09 2019 Author єхcsє#5543 aka timo
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * <p>
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
@Getter
@Setter
public class BlockSemanticAST extends AbstractSemanticAST<BlockSyntaxAST>
{
    
    @Expose
    private final List<AbstractSemanticAST<?>> blockStorage;
    
    @Expose
    private final List<BlockSemanticAST> innerBlocks;
    
    public BlockSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final BlockSyntaxAST blockSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, blockSyntaxAST, ASTType.BLOCK);
        
        this.blockStorage = new ArrayList<>();
        this.innerBlocks = new ArrayList<>();
    }
    
//    @Override
//    public BlockSemanticAST analyseAST(final SemanticAnalyzer semanticAnalyzer) {
//        for (final AbstractSyntaxAST abstractSyntaxAST : this.getSyntaxAST().getBlockStorage()) {
//            if (this.getSyntaxAST().getBlockType() == BlockSyntaxAST.BlockType.INLINE) {
//                if (!(abstractSyntaxAST instanceof ExpressionSyntaxAST)) {
//                    semanticAnalyzer.errorHandler().addError(new SyntaxASTError<>(abstractSyntaxAST, "Couldn't analyze the inlined-block because the AST isn't supported by it."));
//                    return null;
//                }
//                final ExpressionSyntaxAST expressionSyntaxAST = (ExpressionSyntaxAST) abstractSyntaxAST;
//                final ExpressionSemanticAST expressionSemanticAST
//                        = new ExpressionSemanticAST(this.getLastContainerAST(), expressionSyntaxAST).analyseAST(semanticAnalyzer);
//                if (expressionSemanticAST == null)
//                    return null;
//
//                this.blockStorage.add(expressionSemanticAST);
//            } else {
//                if (abstractSyntaxAST instanceof BlockSyntaxAST) {
//                    final BlockSyntaxAST blockSyntaxAST = (BlockSyntaxAST) abstractSyntaxAST;
//                    final BlockSemanticAST blockSemanticAST
//                            = new BlockSemanticAST(this.getLastContainerAST(), blockSyntaxAST).analyseAST(semanticAnalyzer);
//                    if (blockSemanticAST == null)
//                        return null;
//                    System.out.println(blockSemanticAST);
//
//                    this.blockStorage.add(blockSemanticAST);
//                    this.innerBlocks.add(blockSemanticAST);
//                }else if (abstractSyntaxAST instanceof VariableDefinitionSyntaxAST) {
//                    final VariableDefinitionSyntaxAST variableDefinitionSyntaxAST = (VariableDefinitionSyntaxAST) abstractSyntaxAST;
//                    final VariableDefinitionSemanticAST variableDefinitionSemanticAST
//                            = new VariableDefinitionSemanticAST(this.getLastContainerAST(), variableDefinitionSyntaxAST).analyseAST(semanticAnalyzer);
//                    if (variableDefinitionSemanticAST == null)
//                        return null;
//
//                    this.blockStorage.add(variableDefinitionSemanticAST);
//                } else {
//                    semanticAnalyzer.errorHandler().addError(new SyntaxASTError<>(abstractSyntaxAST, "Couldn't analyze the AST because it isn't supported by the block."));
//                    return null;
//                }
//            }
//        }
//        return this;
//    }
    
//    public boolean containDuplicatedNames(final HashMap<String, AbstractSemanticAST<?>> names, final SemanticAnalyzer semanticAnalyzer) {
//        for (final AbstractSemanticAST<?> abstractSemanticAST : this.getBlockStorage()) {
//            if (abstractSemanticAST instanceof BlockSemanticAST) {
//                final BlockSemanticAST blockSemanticAST = (BlockSemanticAST) abstractSemanticAST;
//                if (blockSemanticAST.containDuplicatedNames(names, semanticAnalyzer))
//                    return true;
//            } else if (abstractSemanticAST instanceof VariableDefinitionSemanticAST) {
//                final VariableDefinitionSemanticAST variableDefinitionSemanticAST = (VariableDefinitionSemanticAST) abstractSemanticAST;
//                final String variableName = variableDefinitionSemanticAST.getVariableName().getTokenContent();
//
//                if (names.containsKey(variableName)) {
//                    semanticAnalyzer.errorHandler().addError(new DoubleSemanticASTError<>(names.get(variableName), variableDefinitionSemanticAST, "Couldn't analyze the block because there already exists a variable with the same name."));
//                    return true;
//                } else names.put(variableName, abstractSemanticAST);
//            }
//        }
//        return false;
//    }
//
//    public AbstractSemanticAST<?> findSemanticAST(final IdentifierToken nameToken) {
//        for (final AbstractSemanticAST<?> abstractSemanticAST : this.getBlockStorage()) {
//            if (abstractSemanticAST instanceof BlockSemanticAST) {
//                final BlockSemanticAST blockSemanticAST = (BlockSemanticAST) abstractSemanticAST;
//                final AbstractSemanticAST<?> foundAST = blockSemanticAST.findSemanticAST(nameToken);
//                if (foundAST != null)
//                    return foundAST;
//            } else if (abstractSemanticAST instanceof VariableDefinitionSemanticAST) {
//                final VariableDefinitionSemanticAST variableDefinitionSemanticAST = (VariableDefinitionSemanticAST) abstractSemanticAST;
//                final String variableName = variableDefinitionSemanticAST.getVariableName().getTokenContent();
//
//                if (variableName.equals(nameToken.getTokenContent()))
//                    return variableDefinitionSemanticAST;
//            }
//        }
//        return null;
//    }
    
}
