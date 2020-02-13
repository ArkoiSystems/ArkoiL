package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.EndOfFileToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.AbstractParser;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.types.BlockParser;
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
public class BlockSyntaxAST extends AbstractSyntaxAST
{
    
    public static BlockParser BLOCK_PARSER = new BlockParser();
    
    private static AbstractParser<?>[] BLOCK_PARSERS = new AbstractParser<?>[] {
            AbstractStatementSyntaxAST.STATEMENT_PARSER,
            BlockSyntaxAST.BLOCK_PARSER,
    };
    
    
    @Expose
    private BlockType blockType;
    
    @Expose
    private final List<AbstractSyntaxAST> blockStorage;
    
    /**
     * This constructor will initialize the statement with the AST-Type "BLOCK". This will
     * help to debug problems or check the AST for correct syntax. Also it will create the
     * list for the block storage.
     */
    public BlockSyntaxAST() {
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
    public BlockSyntaxAST parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (!(parentAST instanceof FunctionDefinitionSyntaxAST) && !(parentAST instanceof VariableDefinitionSyntaxAST) && !(parentAST instanceof BlockSyntaxAST)) {
            syntaxAnalyzer.errorHandler().addError(new SyntaxASTError<>(parentAST, "Couldn't parse the BlockAST because it isn't declared inside a function/variable definition or block."));
            return null;
        } else this.setStart(syntaxAnalyzer.currentToken().getStart());
    
        if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.OPENING_BRACE) != null) {
            this.blockType = BlockType.BLOCK;
            syntaxAnalyzer.nextToken(); // Because it would parse a second block if we wouldn't do this.
        
            main_loop:
            while (syntaxAnalyzer.getPosition() < syntaxAnalyzer.getTokens().length) {
                if (syntaxAnalyzer.currentToken() instanceof EndOfFileToken)
                    break;
                
                for (final AbstractParser<?> abstractParser : BLOCK_PARSERS) {
                    if (!abstractParser.canParse(this, syntaxAnalyzer))
                        continue;
    
                    final AbstractSyntaxAST abstractSyntaxAST = abstractParser.parse(this, syntaxAnalyzer);
                    if (abstractSyntaxAST == null) {
                        syntaxAnalyzer.errorHandler().addError(new ParserError<>(abstractParser, syntaxAnalyzer.currentToken()));
                        return null;
                    } else {
                        if (abstractSyntaxAST instanceof BlockSyntaxAST) {
                            if(syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.CLOSING_BRACE) == null) {
                                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"BlockAST\" because it doesn't end with a closing brace."));
                                return null;
                            }
                        } else if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.SEMICOLON) == null) {
                            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"%s\" because it doesn't end with a semicolon.", abstractSyntaxAST.getClass().getSimpleName()));
                            return null;
                        }
                        
                        this.blockStorage.add(abstractSyntaxAST);
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
        
            if (!AbstractExpressionSyntaxAST.EXPRESSION_PARSER.canParse(this, syntaxAnalyzer)) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the BlockAST because the equal sign isn't followed by an valid expression."));
                return null;
            }
        
            final AbstractExpressionSyntaxAST abstractExpressionAST = AbstractExpressionSyntaxAST.EXPRESSION_PARSER.parse(this, syntaxAnalyzer);
            if (abstractExpressionAST == null) {
                syntaxAnalyzer.errorHandler().addError(new ParserError<>(AbstractExpressionSyntaxAST.EXPRESSION_PARSER, this.getStart(), syntaxAnalyzer.currentToken().getEnd(), "Couldn't parse the BlockAST because an error occurred during the parsing of the expression."));
                return null;
            } else this.blockStorage.add(abstractExpressionAST);
        
            if (syntaxAnalyzer.matchesNextToken(SymbolToken.SymbolType.SEMICOLON) == null) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the BlockAST because the inlined block doesn't end with a semicolon."));
                return null;
            }
        } else {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the BlockAST because the parsing doesn't start with an opening brace or equal sign to identify the block type."));
            return null;
        }
        
        this.setEnd(syntaxAnalyzer.currentToken().getEnd());
        return this;
    }
    
    public enum BlockType
    {
        
        BLOCK, INLINE
        
    }
    
}
