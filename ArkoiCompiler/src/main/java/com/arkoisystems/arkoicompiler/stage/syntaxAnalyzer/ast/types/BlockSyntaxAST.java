/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.EndOfFileToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.OperatorType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.BlockType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.AbstractParser;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.types.BlockParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Used if you want to create a new {@link BlockSyntaxAST}. But it is recommend to use the
 * {@link BlockSyntaxAST#BLOCK_PARSER} to parse a new {@link BlockSyntaxAST} because with
 * it you can check if the current {@link AbstractToken} is capable to parse this AST.
 */
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
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private BlockType blockType;
    
    
    /**
     * Declares the {@link List} for every {@link AbstractSyntaxAST} which got parsed
     * inside the block.
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private List<AbstractSyntaxAST> blockStorage = new ArrayList<>();
    
    
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
    public @NotNull BlockSyntaxAST parseAST(@NotNull final AbstractSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
    
        if (!(parentAST instanceof FunctionDefinitionSyntaxAST) && !(parentAST instanceof VariableDefinitionSyntaxAST) && !(parentAST instanceof BlockSyntaxAST)) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.BLOCK_WRONG_START
            );
            return this;
        }
    
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_BRACE) != null) {
            this.blockType = BlockType.BLOCK;
            this.getSyntaxAnalyzer().nextToken();
            
            main_loop:
            while (this.getSyntaxAnalyzer().getPosition() < this.getSyntaxAnalyzer().getTokens().length) {
                if (this.getSyntaxAnalyzer().currentToken() instanceof EndOfFileToken)
                    break;
                if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACE) != null)
                    break;
                
                for (final AbstractParser abstractParser : BLOCK_PARSERS) {
                    if (!abstractParser.canParse(this, this.getSyntaxAnalyzer()))
                        continue;
    
                    final AbstractSyntaxAST abstractSyntaxAST = abstractParser.parse(this, this.getSyntaxAnalyzer());
                    this.getMarkerFactory().addFactory(abstractSyntaxAST.getMarkerFactory());
    
                    if (!abstractSyntaxAST.isFailed()) {
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
                        }
        
                        this.blockStorage.add(abstractSyntaxAST);
                        this.getSyntaxAnalyzer().nextToken();
                    } else this.skipToNextValidToken();
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
        } else if (this.getSyntaxAnalyzer().matchesCurrentToken(OperatorType.EQUALS) != null) {
            this.blockType = BlockType.INLINE;
            this.getSyntaxAnalyzer().nextToken();
    
            if (!AbstractExpressionSyntaxAST.EXPRESSION_PARSER.canParse(this, this.getSyntaxAnalyzer())) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        SyntaxErrorType.BLOCK_NO_VALID_EXPRESSION
                );
                return this;
            }
    
            final AbstractOperableSyntaxAST<?> abstractOperableSyntaxAST = AbstractExpressionSyntaxAST.EXPRESSION_PARSER.parse(this, this.getSyntaxAnalyzer());
            this.getMarkerFactory().addFactory(abstractOperableSyntaxAST.getMarkerFactory());
            
            if (abstractOperableSyntaxAST.isFailed()) {
                this.failed();
                return this;
            } else this.blockStorage.add(abstractOperableSyntaxAST);
        } else {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.BLOCK_INVALID_SEPARATOR
            );
            return this;
        }
        
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
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
    
    
    public static BlockSyntaxASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new BlockSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static BlockSyntaxASTBuilder builder() {
        return new BlockSyntaxASTBuilder();
    }
    
    
    public static class BlockSyntaxASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private List<AbstractSyntaxAST> blockStorage;
        
        
        @Nullable
        private BlockType blockType;
        
        
        private AbstractToken startToken, endToken;
        
        
        public BlockSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public BlockSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public BlockSyntaxASTBuilder storage(final List<AbstractSyntaxAST> blockStorage) {
            this.blockStorage = blockStorage;
            return this;
        }
        
        
        public BlockSyntaxASTBuilder type(final BlockType blockType) {
            this.blockType = blockType;
            return this;
        }
        
        
        public BlockSyntaxASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public BlockSyntaxASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public BlockSyntaxAST build() {
            final BlockSyntaxAST blockSyntaxAST = new BlockSyntaxAST(this.syntaxAnalyzer);
            blockSyntaxAST.setBlockType(this.blockType);
            if (this.blockStorage != null)
                blockSyntaxAST.setBlockStorage(this.blockStorage);
            blockSyntaxAST.setStartToken(this.startToken);
            blockSyntaxAST.setEndToken(this.endToken);
            return blockSyntaxAST;
        }
        
    }
    
}
