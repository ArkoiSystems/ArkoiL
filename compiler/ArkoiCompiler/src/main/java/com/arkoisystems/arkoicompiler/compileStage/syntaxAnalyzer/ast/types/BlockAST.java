package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ASTError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SeparatorToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.operators.types.AssignmentOperatorToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.IInitializeable;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.expressions.AbstractExpressionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.FunctionStatementAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.ReturnStatementAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.VariableStatementAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.functionStatements.FunctionDefinitionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.variableStatements.VariableDefinitionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.variableStatements.VariableIncrementByAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.Parser;
import com.arkoisystems.arkoicompiler.utils.ICallback;
import com.google.gson.annotations.Expose;
import jdk.nashorn.internal.ir.Block;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Copyright © 2019 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on the Sat Nov 09 2019 Author єхcsє#5543 aka Timo
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
public class BlockAST extends AbstractAST implements IInitializeable
{
    
    @Expose
    private BlockType blockType;
    
    @Expose
    private final List<AbstractAST> blockStorage;
    
    private final Parser<?>[] parsers;
    
    private ICallback blockCallback;
    
    
    public BlockAST(final Parser<?>... parsers) {
        super(ASTType.BLOCK);
        
        this.blockStorage = new ArrayList<>();
        this.parsers = parsers;
    }
    
    @Override
    public BlockAST parseAST(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (!(parentAST instanceof FunctionDefinitionAST) &&
                !(parentAST instanceof BlockAST) &&
                !(parentAST instanceof VariableDefinitionAST) &&
                !(parentAST instanceof VariableIncrementByAST)) {
            syntaxAnalyzer.errorHandler().addError(new ASTError(parentAST, "Couldn't parse the BlockAST because the ParentAST isn't valid."));
            return null;
        }
        
        if (syntaxAnalyzer.matchesCurrentToken(SeparatorToken.SeparatorType.OPENING_BRACE) != null) {
            this.setStart(syntaxAnalyzer.currentToken().getStart());
            syntaxAnalyzer.nextToken();
            
            this.blockCallback = new BlockParsingCallback(this, syntaxAnalyzer);
            this.blockType = BlockType.BLOCK;
        } else if (syntaxAnalyzer.matchesCurrentToken(AssignmentOperatorToken.AssignmentOperatorType.ASSIGNMENT) != null) {
            this.setStart(syntaxAnalyzer.currentToken().getStart());
            syntaxAnalyzer.nextToken();
            
            this.blockCallback = new InlineParsingCallback(this, syntaxAnalyzer);
            this.blockType = BlockType.INLINE;
        }
        
        return parentAST.addAST(this, syntaxAnalyzer);
    }
    
    @Override
    public <T extends AbstractAST> T addAST(final T toAddAST, final SyntaxAnalyzer syntaxAnalyzer) {
//        System.out.println(toAddAST.getClass().getSimpleName());
        this.blockStorage.add(toAddAST);
        return toAddAST;
    }
    
    @Override
    public boolean initialize(final SyntaxAnalyzer syntaxAnalyzer) {
        if (this.blockCallback == null || !this.blockCallback.execute())
            return false;
        
        for (final AbstractAST abstractAST : this.blockStorage) {
            if (abstractAST instanceof VariableStatementAST) {
                if (!((VariableStatementAST) abstractAST).initialize(syntaxAnalyzer))
                    return false;
            } else if (abstractAST instanceof FunctionStatementAST) {
                if (!((FunctionStatementAST) abstractAST).initialize(syntaxAnalyzer))
                    return false;
            } else if (abstractAST instanceof ReturnStatementAST) {
                if (!((ReturnStatementAST) abstractAST).initialize(syntaxAnalyzer))
                    return false;
            }
        }
        
        for (final BlockAST innerBlockAST : this.getInnerBlocks())
            if (!innerBlockAST.initialize(syntaxAnalyzer))
                return false;
        
        return !(this.blockStorage.isEmpty() && this.blockType == BlockType.INLINE);
    }
    
    public VariableDefinitionAST getVariableByName(final AbstractToken identifierToken) {
        for (final AbstractAST abstractAST : this.blockStorage) {
            if (!(abstractAST instanceof VariableDefinitionAST))
                continue;
            
            final VariableDefinitionAST variableDefinitionAST = (VariableDefinitionAST) abstractAST;
            if (variableDefinitionAST.getNameIdentifierToken().getTokenContent().equals(identifierToken.getTokenContent()))
                return variableDefinitionAST;
        }
        return null;
    }
    
    public BlockAST[] getInnerBlocks() {
        final ArrayList<BlockAST> blockASTs = new ArrayList<>();
        for (final AbstractAST abstractAST : this.blockStorage) {
            if (!(abstractAST instanceof BlockAST))
                continue;
            final BlockAST blockAST = (BlockAST) abstractAST;
            
            blockASTs.add(blockAST);
            blockASTs.addAll(Arrays.asList(blockAST.getInnerBlocks()));
        }
        return blockASTs.toArray(new BlockAST[] { });
    }
    
    @Setter
    @Getter
    private static class BlockParsingCallback implements ICallback
    {
        
        private final int startPosition, endPosition;
        
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        private final Parser<?>[] parsers;
        
        private final BlockAST blockAST;
        
        public BlockParsingCallback(final BlockAST blockAST, final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
            this.blockAST = blockAST;
            
            this.startPosition = syntaxAnalyzer.getPosition();
            
            this.parsers = blockAST.getParsers();
            syntaxAnalyzer.findMatchingSeparator(blockAST, SeparatorToken.SeparatorType.OPENING_BRACE);
            this.endPosition = syntaxAnalyzer.getPosition();
        }
        
        @Override
        public boolean execute() {
            final int lastPosition = this.syntaxAnalyzer.getPosition();
            this.syntaxAnalyzer.setPosition(this.startPosition);
            
            while (this.syntaxAnalyzer.getPosition() < this.endPosition) {
                final AbstractToken parserStartToken = this.syntaxAnalyzer.currentToken();
                for (final Parser<?> parser : this.parsers) {
                    if (!parser.canParse(this.blockAST, this.syntaxAnalyzer))
                        continue;
                    
                    final AbstractAST abstractAST = parser.parse(this.blockAST, this.syntaxAnalyzer);
                    if (abstractAST == null) {
                        this.syntaxAnalyzer.errorHandler().addError(new ParserError(parser, parserStartToken.getStart(), this.syntaxAnalyzer.currentToken().getEnd()));
                        return false;
                    } else break;
                }
                
                if (syntaxAnalyzer.matchesCurrentToken(SeparatorToken.SeparatorType.SEMICOLON) == null) {
                    this.syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the statement because there is no ending separator for the parser (for statements \";\")."));
                    return false;
                } else this.syntaxAnalyzer.nextToken();
            }
            
            if (this.syntaxAnalyzer.getPosition() != this.endPosition) {
                this.blockAST.setEnd(this.syntaxAnalyzer.currentToken().getEnd());
                this.syntaxAnalyzer.errorHandler().addError(new ASTError(this.blockAST, "Couldn't parse the BlockAST because the end position doesn't match the calculated one."));
                return false;
            } else this.syntaxAnalyzer.setPosition(lastPosition);
            return true;
        }
        
    }
    
    @Setter
    @Getter
    private static class InlineParsingCallback implements ICallback
    {
        
        private final int startPosition, endPosition;
        
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        private final BlockAST blockAST;
        
        public InlineParsingCallback(final BlockAST blockAST, final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
            this.blockAST = blockAST;
            
            this.startPosition = syntaxAnalyzer.getPosition();
            syntaxAnalyzer.findSeparator(SeparatorToken.SeparatorType.SEMICOLON);
            this.endPosition = syntaxAnalyzer.getPosition();
        }
        
        @Override
        public boolean execute() {
            final int lastPosition = this.syntaxAnalyzer.getPosition();
            this.syntaxAnalyzer.setPosition(this.startPosition);
            
            final AbstractExpressionAST abstractExpressionAST = AbstractExpressionAST.EXPRESSION_PARSER.parse(this.blockAST, this.syntaxAnalyzer);
            if (abstractExpressionAST == null) {
                this.blockAST.setEnd(this.syntaxAnalyzer.currentToken().getEnd());
                this.syntaxAnalyzer.errorHandler().addError(new ASTError(this.blockAST, "Couldn't parse the BlockAST because the parser couldn't parse the expression for it."));
                return false;
            }
            
            if (this.syntaxAnalyzer.matchesNextToken(SeparatorToken.SeparatorType.SEMICOLON) == null) {
                this.blockAST.setEnd(this.syntaxAnalyzer.currentToken().getEnd());
                this.syntaxAnalyzer.errorHandler().addError(new ASTError(this.blockAST, "Couldn't parse the inlined BlockAST because it doesn't end with a semicolon."));
                return false;
            }
            
            if (this.syntaxAnalyzer.getPosition() != this.endPosition) {
                this.blockAST.setEnd(this.syntaxAnalyzer.currentToken().getEnd());
                this.syntaxAnalyzer.errorHandler().addError(new ASTError(this.blockAST, "Couldn't parse the BlockAST because the end position doesn't match the calculated one."));
                return false;
            }
            
            this.syntaxAnalyzer.setPosition(lastPosition);
            return true;
        }
        
    }
    
    public enum BlockType
    {
        
        BLOCK, INLINE
        
    }
    
}
