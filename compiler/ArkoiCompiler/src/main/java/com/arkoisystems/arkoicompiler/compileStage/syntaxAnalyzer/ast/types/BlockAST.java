package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ASTError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.EndOfFileToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.AbstractSemantic;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.expression.AbstractExpressionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.AbstractStatementAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.function.FunctionDefinitionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.variable.VariableDefinitionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.Parser;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.types.BlockParser;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
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
public class BlockAST extends AbstractAST<AbstractSemantic<?>>
{
    
    public static BlockParser BLOCK_PARSER = new BlockParser();
    
    private static Parser<?>[] BLOCK_PARSERS = new Parser<?>[] {
            AbstractStatementAST.STATEMENT_PARSER,
            BlockAST.BLOCK_PARSER,
    };
    
    
    @Expose
    private BlockType blockType;
    
    @Expose
    private final List<AbstractAST<?>> blockStorage;
    
    /**
     * This constructor will initialize the statement with the AST-Type "BLOCK". This will
     * help to debug problems or check the AST for correct syntax. Also it will create the
     * list for the block storage.
     */
    public BlockAST() {
        super(ASTType.BLOCK);
        
        this.blockStorage = new ArrayList<>();
    }
    
    /**
     * This method will parse the BlockAST and checks it for the correct syntax. This AST
     * can just be used by a FunctionDefinitionAST or ASTs which use an expression as body
     * e.g. VariableDefinitionAST. A BlockAST needs to end with a closing brace if it's a
     * block or with a semicolon if it's an inlined block.
     * <p>
     * An example for this AST:
     * <p>
     * fun greeting<string>() = "Hello World";
     * <p>
     * or
     * <p>
     * fun main<int>(args: string[]) { return 0; }
     *
     * @param parentAST
     *         The parent of the AST. With it you can check for correct usage of the
     *         statement.
     * @param syntaxAnalyzer
     *         The given SyntaxAnalyzer is used for checking the syntax of the current
     *         Token list.
     *
     * @return It will just return null if an error occurred or a BlockAST if it parsed
     *         until to the end.
     */
    @Override
    public BlockAST parseAST(final AbstractAST<?> parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (!(parentAST instanceof FunctionDefinitionAST) && !(parentAST instanceof VariableDefinitionAST) && !(parentAST instanceof BlockAST)) {
            syntaxAnalyzer.errorHandler().addError(new ASTError<>(parentAST, "Couldn't parse the BlockAST because it isn't declared inside a function/variable definition or block."));
            return null;
        } else this.setStart(syntaxAnalyzer.currentToken().getStart());
    
        if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.OPENING_BRACE) != null) {
            this.blockType = BlockType.BLOCK;
            syntaxAnalyzer.nextToken(); // Because it would parse a second block if we wouldn't do this.
        
            main_loop:
            while (syntaxAnalyzer.getPosition() < syntaxAnalyzer.getTokens().length) {
                if (syntaxAnalyzer.currentToken() instanceof EndOfFileToken)
                    break;
            
                for (final Parser<?> parser : BLOCK_PARSERS) {
                    if (!parser.canParse(this, syntaxAnalyzer))
                        continue;
    
                    final AbstractAST<?> abstractAST = parser.parse(this, syntaxAnalyzer);
                    if (abstractAST == null) {
                        syntaxAnalyzer.errorHandler().addError(new ParserError<>(parser, syntaxAnalyzer.currentToken()));
                        return null;
                    } else {
                        if (abstractAST instanceof BlockAST) {
                            if(syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.CLOSING_BRACE) == null) {
                                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"BlockAST\" because it doesn't end with a closing brace."));
                                return null;
                            }
                        } else if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.SEMICOLON) == null) {
                            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"%s\" because it doesn't end with a semicolon.", abstractAST.getClass().getSimpleName()));
                            return null;
                        }
    
                        syntaxAnalyzer.nextToken();
                        continue main_loop;
                    }
                }
            
                if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.CLOSING_BRACE) != null)
                    break;
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the AST because no parser could parse this token. Check for misspelling or something else."));
                return null;
            }
        } else if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.EQUAL) != null) {
            this.blockType = BlockType.INLINE;
            syntaxAnalyzer.nextToken(); // Because it would try to parse the equal sign as expression.
        
            if (!AbstractExpressionAST.EXPRESSION_PARSER.canParse(this, syntaxAnalyzer)) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the BlockAST because the equal sign isn't followed by an valid expression."));
                return null;
            }
        
            final AbstractExpressionAST<?> abstractExpressionAST = AbstractExpressionAST.EXPRESSION_PARSER.parse(this, syntaxAnalyzer);
            if (abstractExpressionAST == null) {
                syntaxAnalyzer.errorHandler().addError(new ParserError<>(AbstractExpressionAST.EXPRESSION_PARSER, this.getStart(), syntaxAnalyzer.currentToken().getEnd(), "Couldn't parse the BlockAST because an error occurred during the parsing of the expression."));
                return null;
            }
        
            if (syntaxAnalyzer.matchesNextToken(SymbolToken.SymbolType.SEMICOLON) == null) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the BlockAST because the inlined block doesn't end with a semicolon."));
                return null;
            }
        } else {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the BlockAST because the parsing doesn't start with an opening brace or equal sign to identify the block type."));
            return null;
        }
        
        this.setEnd(syntaxAnalyzer.currentToken().getEnd());
        return parentAST.addAST(this, syntaxAnalyzer);
    }
    
    /**
     * This method is just overwritten because this class extends the AbstractAST class.
     * It will just return the input and doesn't check anything.
     *
     * @param toAddAST
     *         The AST which should get add to this class.
     * @param syntaxAnalyzer
     *         The given SyntaxAnalyzer is needed for checking and modification of the
     *         current Token list/order.
     * @param <T>
     *         The Type of the AST which should be added to the "BlockAST".
     *
     * @return It will just return the input "toAddAST" because you can't add ASTs to a
     *         BlockAST.
     */
    @Override
    public <T extends AbstractAST<?>> T addAST(final T toAddAST, final SyntaxAnalyzer syntaxAnalyzer) {
        this.blockStorage.add(toAddAST);
        return toAddAST;
    }
    
    public enum BlockType
    {
        
        BLOCK, INLINE
        
    }
    
}
