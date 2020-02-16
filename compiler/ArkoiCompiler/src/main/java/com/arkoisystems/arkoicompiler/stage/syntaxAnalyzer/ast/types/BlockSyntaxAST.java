/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.EndOfFileToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.numbers.AbstractNumberToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.BlockType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.AbstractParser;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.types.BlockParser;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Used if you want to create a new {@link BlockSyntaxAST}. But it is recommend to use the
 * {@link BlockSyntaxAST#BLOCK_PARSER} to parse a new {@link BlockSyntaxAST} because with
 * it you can check if the current {@link AbstractToken} is capable to parse this AST.
 */
@Getter
@Setter
public class BlockSyntaxAST extends AbstractSyntaxAST
{
    
    /**
     * This variable is static because we just want a single instance of the {@link
     * BlockParser}.
     */
    public static BlockParser BLOCK_PARSER = new BlockParser();
    
    
    /**
     * This variable is used to get all {@link AbstractParser}'s which are supported by
     * the {@link BlockParser}.
     */
    private static AbstractParser<?>[] BLOCK_PARSERS = new AbstractParser<?>[] {
            AbstractStatementSyntaxAST.STATEMENT_PARSER,
            BlockSyntaxAST.BLOCK_PARSER,
    };
    
    
    /**
     * Defines the type of the {@link BlockSyntaxAST}. It can be an inlined block or just
     * a block. An example for this is:
     *
     * <p>Inlined-Block:</p>
     * <pre>
     *     fun greeting<string>() = "Hello World";
     * </pre>
     * <p>or</p>
     * <p>Block:</p>
     * <pre>
     *      fun main<int>(args: string[]) {
     *          return 0;
     *      }
     * </pre>
     */
    @Expose
    private BlockType blockType;
    
    
    /**
     * Declares the {@link List} for every {@link AbstractSyntaxAST} which got parsed
     * inside the block.
     */
    @Expose
    private final List<AbstractSyntaxAST> blockStorage;
    
    
    /**
     * Constructs a new {@link BlockSyntaxAST} and initializes the {@link
     * BlockSyntaxAST#blockStorage} so it isn't null.
     */
    public BlockSyntaxAST() {
        super(ASTType.BLOCK);
        
        this.blockStorage = new ArrayList<>();
    }
    
    /**
     * Parses a new {@link BlockSyntaxAST} with the given parameters, where the {@link
     * SyntaxAnalyzer} is used to check the syntax and the parent {@link
     * AbstractSyntaxAST} is used to see if this AST can be created inside the parent. It
     * will parse every {@link AbstractSyntaxAST} which is included in the {@link
     * BlockSyntaxAST#BLOCK_PARSERS} array. Also the method will set if the block is
     * inlined or not.
     *
     * <p>Inlined-Block:</p>
     * <pre>
     *     fun greeting<string>() = "Hello World";
     * </pre>
     * <p>or</p>
     * <p>Block:</p>
     * <pre>
     *      fun main<int>(args: string[]) {
     *          return 0;
     *      }
     * </pre>
     *
     * @param parentAST
     *         the parent {@link AbstractSyntaxAST} which should get used to check if the
     *         {@link AbstractSyntaxAST} can be created inside it.
     * @param syntaxAnalyzer
     *         the {@link SyntaxAnalyzer} which is used for checking the syntax with
     *         methods like {@link SyntaxAnalyzer#matchesCurrentToken(TokenType)} )} or
     *         {@link SyntaxAnalyzer#matchesNextToken(AbstractNumberToken.NumberType)}.
     *
     * @return {@code null} if an error occurred or the {@link BlockSyntaxAST} if
     *         everything worked correctly.
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
    
}
