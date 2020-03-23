/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.EndOfFileToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.BlockType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.AbstractParser;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.types.BlockParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
     * This variable is used to get all {@link AbstractParser}s which are supported by the
     * {@link BlockParser}.
     */
    private static AbstractParser[] BLOCK_PARSERS = new AbstractParser[] {
            AbstractStatementSyntaxAST.STATEMENT_PARSER,
            BlockSyntaxAST.BLOCK_PARSER,
    };
    
    
    /**
     * Defines the type of the {@link BlockSyntaxAST}. It can be an inlined block or just
     * a block. An example for this is:
     */
    @Setter(AccessLevel.PROTECTED)
    private BlockType blockType;
    
    
    /**
     * Declares the {@link List} for every {@link AbstractSyntaxAST} which got parsed
     * inside the block.
     */
    @Setter(AccessLevel.PROTECTED)
    private List<AbstractSyntaxAST> blockStorage;
    
    
    /**
     * Constructs a new {@link BlockSyntaxAST} and initializes the {@link
     * BlockSyntaxAST#blockStorage} so it isn't null.
     *
     * @param syntaxAnalyzer
     *         the {@link SyntaxAnalyzer} which is used to check for correct syntax with
     *         methods like {@link SyntaxAnalyzer#matchesNextToken(SymbolType)} or {@link
     *         SyntaxAnalyzer#nextToken()}.
     */
    protected BlockSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.BLOCK);
    }
    
    /**
     * Parses a new {@link BlockSyntaxAST} with the given parameters, where the {@link
     * SyntaxAnalyzer} is used to check the syntax and the parent {@link
     * AbstractSyntaxAST} is used to see if this AST can be created inside the parent. It
     * will parse every {@link AbstractSyntaxAST} which is included in the {@link
     * BlockSyntaxAST#BLOCK_PARSERS} array. Also the method will set if the block is
     * inlined or not.
     *
     * @param parentAST
     *         the parent {@link AbstractSyntaxAST} which is used used to check if the
     *         {@link AbstractSyntaxAST} can be created inside it.
     *
     * @return {@code null} if an error occurred or the {@link BlockSyntaxAST} if
     *         everything worked correctly.
     */
    @Override
    public Optional<BlockSyntaxAST> parseAST(@NonNull final AbstractSyntaxAST parentAST) {
        if (!(parentAST instanceof FunctionDefinitionSyntaxAST) && !(parentAST instanceof VariableDefinitionSyntaxAST) && !(parentAST instanceof BlockSyntaxAST)) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.BLOCK_WRONG_START
            );
            return Optional.empty();
        }
        this.setStart(this.getSyntaxAnalyzer().currentToken().getStart());
    
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_BRACE) != null) {
            this.blockType = BlockType.BLOCK;
            this.getSyntaxAnalyzer().nextToken(); // Because it would parse a second block if we wouldn't do this.
        
            main_loop:
            while (this.getSyntaxAnalyzer().getPosition() < this.getSyntaxAnalyzer().getTokens().length) {
                if (this.getSyntaxAnalyzer().currentToken() instanceof EndOfFileToken)
                    break;
                if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACE) != null)
                    break;
    
                for (final AbstractParser abstractParser : BLOCK_PARSERS) {
                    if (!abstractParser.canParse(this, this.getSyntaxAnalyzer()))
                        continue;
        
                    final Optional<? extends AbstractSyntaxAST> optionalAbstractSyntaxAST = abstractParser.parse(this, this.getSyntaxAnalyzer());
                    if (optionalAbstractSyntaxAST.isPresent()) {
                        final AbstractSyntaxAST abstractSyntaxAST = optionalAbstractSyntaxAST.get();
                        if (abstractSyntaxAST.isFailed())
                            this.failed();
            
                        if (abstractSyntaxAST instanceof BlockSyntaxAST) {
                            if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACE) == null) {
                                this.addError(
                                        this.getSyntaxAnalyzer().getArkoiClass(),
                                        this.getSyntaxAnalyzer().currentToken(),
                                        SyntaxErrorType.BLOCK_BLOCK_HAS_WRONG_ENDING
                                );
                                this.skipToNextValidToken();
                                continue main_loop;
                            }
                        } else if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.SEMICOLON) == null) {
                            this.addError(
                                    this.getSyntaxAnalyzer().getArkoiClass(),
                                    this.getSyntaxAnalyzer().currentToken(),
                                    SyntaxErrorType.BLOCK_STATEMENT_HAS_WRONG_ENDING,
                                    abstractSyntaxAST.getClass().getSimpleName()
                            );
                            this.skipToNextValidToken();
                            continue main_loop;
                        }
                        
                        this.blockStorage.add(abstractSyntaxAST);
                        this.getSyntaxAnalyzer().nextToken();
                    } else {
                        this.skipToNextValidToken();
                        this.failed();
                    }
                    continue main_loop;
                }
    
                if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACE) != null)
                    break;
            
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        SyntaxErrorType.BLOCK_NO_PARSER_FOUND
                );
                this.skipToNextValidToken();
            }
        } else if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.EQUAL) != null) {
            this.blockType = BlockType.INLINE;
            this.getSyntaxAnalyzer().nextToken(); // Because it would try to parse the equal sign as expression.
        
            if (!AbstractExpressionSyntaxAST.EXPRESSION_PARSER.canParse(this, this.getSyntaxAnalyzer())) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        SyntaxErrorType.BLOCK_NO_VALID_EXPRESSION
                );
                return Optional.empty();
            }
        
            final Optional<ExpressionSyntaxAST> optionalExpressionSyntaxAST = AbstractExpressionSyntaxAST.EXPRESSION_PARSER.parse(this, this.getSyntaxAnalyzer());
            if (optionalExpressionSyntaxAST.isEmpty())
                return Optional.empty();
            this.blockStorage.add(optionalExpressionSyntaxAST.get());
        
            if (this.getSyntaxAnalyzer().matchesNextToken(SymbolType.SEMICOLON) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        SyntaxErrorType.BLOCK_INLINED_BLOCK_WRONG_ENDING
                );
                return Optional.empty();
            }
        } else {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.BLOCK_INVALID_SEPARATOR
            );
            return Optional.empty();
        }
    
        this.setEnd(this.getSyntaxAnalyzer().currentToken().getEnd());
        return this.isFailed() ? Optional.empty() : Optional.of(this);
    }
    
    
    @Override
    public void printSyntaxAST(@NonNull final PrintStream printStream, @NonNull final String indents) {
        printStream.println(indents + "├── type: " + this.getBlockType());
        printStream.println(indents + "└── storage: " + (this.getBlockStorage().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getBlockStorage().size(); index++) {
            final AbstractSyntaxAST abstractSyntaxAST = this.getBlockStorage().get(index);
            if (index == this.getBlockStorage().size() - 1) {
                printStream.println(indents + "    └── " + abstractSyntaxAST.getClass().getSimpleName());
                abstractSyntaxAST.printSyntaxAST(printStream, indents + "        ");
            } else {
                printStream.println(indents + "    ├── " + abstractSyntaxAST.getClass().getSimpleName());
                abstractSyntaxAST.printSyntaxAST(printStream, indents + "    │   ");
                printStream.println(indents + "    │   ");
            }
        }
    }
    
    
    public static BlockSyntaxASTBuilder builder(final SyntaxAnalyzer syntaxAnalyzer) {
        return new BlockSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static BlockSyntaxASTBuilder builder() {
        return new BlockSyntaxASTBuilder();
    }
    
    
    public static class BlockSyntaxASTBuilder
    {
        
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        private List<AbstractSyntaxAST> blockStorage;
        
        
        private BlockType blockType;
        
        
        private int start, end;
        
        
        public BlockSyntaxASTBuilder(final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
            
            this.blockStorage = new ArrayList<>();
        }
        
        
        public BlockSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public BlockSyntaxASTBuilder type(final BlockType blockType) {
            this.blockType = blockType;
            return this;
        }
        
        
        public BlockSyntaxASTBuilder storage(final List<AbstractSyntaxAST> blockStorage) {
            this.blockStorage = blockStorage;
            return this;
        }
        
        
        public BlockSyntaxASTBuilder start(final int start) {
            this.start = start;
            return this;
        }
        
        
        public BlockSyntaxASTBuilder end(final int end) {
            this.end = end;
            return this;
        }
        
        
        public BlockSyntaxAST build() {
            final BlockSyntaxAST blockSyntaxAST = new BlockSyntaxAST(this.syntaxAnalyzer);
            blockSyntaxAST.setBlockStorage(this.blockStorage);
            blockSyntaxAST.setBlockType(this.blockType);
            blockSyntaxAST.setStart(this.start);
            blockSyntaxAST.setEnd(this.end);
            return blockSyntaxAST;
        }
        
    }
    
}
