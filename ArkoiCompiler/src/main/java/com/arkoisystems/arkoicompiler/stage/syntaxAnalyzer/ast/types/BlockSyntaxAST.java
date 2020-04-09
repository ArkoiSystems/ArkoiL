/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.api.ICompilerSyntaxAST;
import com.arkoisystems.arkoicompiler.api.ISyntaxParser;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.OperatorType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.BlockType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parsers.BlockParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BlockSyntaxAST extends ArkoiSyntaxAST
{
    
    public static BlockParser BLOCK_PARSER = new BlockParser();
    
    
    private static ISyntaxParser[] BLOCK_PARSERS = new ISyntaxParser[] {
            AbstractStatementSyntaxAST.STATEMENT_PARSER,
            BlockSyntaxAST.BLOCK_PARSER,
    };
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private BlockType blockType;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private List<ICompilerSyntaxAST> blockStorage = new ArrayList<>();
    
    
    protected BlockSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.BLOCK);
    }
    
    
    @NotNull
    @Override
    public BlockSyntaxAST parseAST(@NotNull final ICompilerSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (!(parentAST instanceof FunctionSyntaxAST) && !(parentAST instanceof VariableSyntaxAST) && !(parentAST instanceof BlockSyntaxAST)) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.BLOCK_WRONG_START
            );
        }
        
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_BRACE) != null) {
            this.blockType = BlockType.BLOCK;
            this.getSyntaxAnalyzer().nextToken();
            
            main_loop:
            while (this.getSyntaxAnalyzer().getPosition() < this.getSyntaxAnalyzer().getTokens().length) {
                if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACE) != null)
                    break;
    
                for (final ISyntaxParser parser : BLOCK_PARSERS) {
                    if (!parser.canParse(this, this.getSyntaxAnalyzer()))
                        continue;
        
                    final ICompilerSyntaxAST syntaxAST = parser.parse(this, this.getSyntaxAnalyzer());
                    this.getMarkerFactory().addFactory(syntaxAST.getMarkerFactory());
        
                    if (!syntaxAST.isFailed()) {
                        if (syntaxAST instanceof BlockSyntaxAST) {
                            if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACE) == null) {
                                this.addError(
                                        this,
                                        this.getSyntaxAnalyzer().getCompilerClass(),
                                        this.getSyntaxAnalyzer().currentToken(),
                                        SyntaxErrorType.BLOCK_BLOCK_HAS_WRONG_ENDING
                                );
                                this.skipToNextValidToken();
                                continue main_loop;
                            }
                        }
            
                        this.blockStorage.add(syntaxAST);
                        this.getSyntaxAnalyzer().nextToken();
                    } else this.skipToNextValidToken();
                    continue main_loop;
                }
    
                if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACE) != null)
                    break;
    
                this.addError(
                        this,
                        this.getSyntaxAnalyzer().getCompilerClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        SyntaxErrorType.BLOCK_NO_PARSER_FOUND
                );
                this.skipToNextValidToken();
            }
        } else if (this.getSyntaxAnalyzer().matchesCurrentToken(OperatorType.EQUALS) != null) {
            this.blockType = BlockType.INLINE;
            this.getSyntaxAnalyzer().nextToken();
            
            if (!ExpressionSyntaxAST.EXPRESSION_PARSER.canParse(this, this.getSyntaxAnalyzer())) {
                return this.addError(
                        this,
                        this.getSyntaxAnalyzer().getCompilerClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        SyntaxErrorType.BLOCK_NO_VALID_EXPRESSION
                );
            }
            
            final OperableSyntaxAST operableSyntaxAST = ExpressionSyntaxAST.EXPRESSION_PARSER.parse(this, this.getSyntaxAnalyzer());
            this.getMarkerFactory().addFactory(operableSyntaxAST.getMarkerFactory());
            
            if (operableSyntaxAST.isFailed()) {
                this.failed();
                return this;
            } else this.blockStorage.add(operableSyntaxAST);
        } else {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.BLOCK_INVALID_SEPARATOR
            );
        }
        
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getStart());
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getEnd());
    
        printStream.println(indents + "├── factory:");
        printStream.println(indents + "│    ├── next: " + this.getMarkerFactory()
                .getNextMarkerFactories()
                .stream()
                .map(markerFactory -> markerFactory.getCurrentMarker().getAstType().name())
                .collect(Collectors.joining(", "))
        );
        printStream.println(indents + "│    ├── start: " + this.getMarkerFactory().getCurrentMarker().getStart().getStart());
        printStream.println(indents + "│    └── end: " + this.getMarkerFactory().getCurrentMarker().getEnd().getEnd());
        printStream.println(indents + "│");
        printStream.println(indents + "├── type: " + this.getBlockType());
        printStream.println(indents + "└── storage: " + (this.getBlockStorage().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getBlockStorage().size(); index++) {
            final ICompilerSyntaxAST syntaxAST = this.getBlockStorage().get(index);
            if (index == this.getBlockStorage().size() - 1) {
                printStream.println(indents + "    └── " + syntaxAST.getClass().getSimpleName());
                syntaxAST.printSyntaxAST(printStream, indents + "        ");
            } else {
                printStream.println(indents + "    ├── " + syntaxAST.getClass().getSimpleName());
                syntaxAST.printSyntaxAST(printStream, indents + "    │   ");
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
        private List<ICompilerSyntaxAST> blockStorage;
        
        
        @Nullable
        private BlockType blockType;
        
        
        private AbstractToken startToken, endToken;
        
        
        public BlockSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public BlockSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
    
    
        public BlockSyntaxASTBuilder storage(final List<ICompilerSyntaxAST> blockStorage) {
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
            blockSyntaxAST.getMarkerFactory().getCurrentMarker().setStart(blockSyntaxAST.getStartToken());
            blockSyntaxAST.setEndToken(this.endToken);
            blockSyntaxAST.getMarkerFactory().getCurrentMarker().setEnd(blockSyntaxAST.getEndToken());
            return blockSyntaxAST;
        }
        
    }
    
}
