/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.ArkoiCompiler;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.CommentToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.EndOfFileToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ImportDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.BlockType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.AbstractParser;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.*;

/**
 * Used if you want to create a new {@link RootSyntaxAST}. It doesn't has an {@link
 * AbstractParser} because you shouldn't treat this class like that. There should only be
 * one instance for one {@link ArkoiClass}.
 */
public class RootSyntaxAST extends AbstractSyntaxAST
{
    
    /**
     * This variable is used to get all {@link AbstractParser}s which are supported by the
     * {@link RootSyntaxAST}.
     */
    public static AbstractParser[] ROOT_PARSERS = new AbstractParser[] {
            AnnotationSyntaxAST.ANNOTATION_PARSER,
            AbstractStatementSyntaxAST.STATEMENT_PARSER,
    };
    
    
    /**
     * Declares the {@link List} for all {@link ImportDefinitionSyntaxAST}s which got
     * parsed. This variable is used frequently within the {@link SemanticAnalyzer}.
     */
    @Getter
    @NotNull
    private final List<ImportDefinitionSyntaxAST> importStorage = new ArrayList<>();
    
    
    /**
     * Declares the {@link List} for all {@link VariableDefinitionSyntaxAST}s which got
     * parsed. This variable is used frequently within the {@link SemanticAnalyzer}.
     */
    @Getter
    @NotNull
    private final List<VariableDefinitionSyntaxAST> variableStorage = new ArrayList<>();
    
    
    /**
     * Declares the {@link List} for all {@link FunctionDefinitionSyntaxAST}s which got
     * parsed. This variable is used frequently within the {@link SemanticAnalyzer}.
     */
    @Getter
    @NotNull
    private final List<FunctionDefinitionSyntaxAST> functionStorage = new ArrayList<>();
    
    
    /**
     * Declares the {@link List} for all {@link AbstractSyntaxAST}s which got parsed
     * inside here. Also they are sorted, so you can check the order of variables or
     * imports.
     */
    @NotNull
    private final List<AbstractSyntaxAST> sortedStorage = new ArrayList<>();
    
    
    /**
     * Constructs a new {@link RootSyntaxAST} with the given parameters. The {@link
     * SyntaxAnalyzer} is used to check syntax and also to get the {@link ArkoiClass} or
     * {@link ArkoiCompiler}.
     *
     * @param syntaxAnalyzer
     *         the {@link SyntaxAnalyzer} which is used to check for correct syntax with
     *         methods like {@link SyntaxAnalyzer#matchesNextToken(SymbolType)} or {@link
     *         SyntaxAnalyzer#nextToken()}.
     */
    public RootSyntaxAST(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.ROOT);
    }
    
    
    /**
     * Parses a new {@link TypeSyntaxAST} with the given parameters, where the {@link
     * SyntaxAnalyzer} is used to check the syntax and the parent {@link
     * AbstractSyntaxAST} is used to see if this AST can be created inside the parent. It
     * will parse every {@link ImportDefinitionSyntaxAST}, {@link
     * VariableDefinitionSyntaxAST} and {@link FunctionDefinitionSyntaxAST} which are
     * present at the root-level (the first layer of the AST). Besides that it will parse
     * also {@link AnnotationSyntaxAST}s but they return a followed {@link
     * FunctionDefinitionSyntaxAST} or {@link VariableDefinitionSyntaxAST}. So basically
     * just these three types of ASTs are getting parsed.
     *
     * @param parentAST
     *         the parent {@link AbstractSyntaxAST} which is used used to check if the
     *         {@link AbstractSyntaxAST} can be created inside it.
     *
     * @return {@code null} if an error occurred or the {@link RootSyntaxAST} if
     *         everything worked correctly.
     */
    @Override
    public Optional<RootSyntaxAST> parseAST(@Nullable final AbstractSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        this.setEnd(this.getSyntaxAnalyzer().getArkoiClass().getContent().length);
        this.setStart(0);
        
        main_loop:
        while (this.getSyntaxAnalyzer().getPosition() < this.getSyntaxAnalyzer().getTokens().length) {
            if (this.getSyntaxAnalyzer().currentToken() instanceof EndOfFileToken)
                break;
    
            for (final AbstractParser abstractParser : ROOT_PARSERS) {
                if (!abstractParser.canParse(this, this.getSyntaxAnalyzer()))
                    continue;
        
                final Optional<? extends AbstractSyntaxAST> optionalAbstractSyntaxAST = abstractParser.parse(this, this.getSyntaxAnalyzer());
                if (optionalAbstractSyntaxAST.isPresent()) {
                    final AbstractSyntaxAST abstractSyntaxAST = optionalAbstractSyntaxAST.get();
                    if (abstractSyntaxAST.isFailed())
                        this.failed();
                    
                    if (abstractSyntaxAST instanceof FunctionDefinitionSyntaxAST) {
                        final FunctionDefinitionSyntaxAST functionDefinitionAST = (FunctionDefinitionSyntaxAST) abstractSyntaxAST;
                        if (functionDefinitionAST.getFunctionBlock().getBlockType() == BlockType.BLOCK && this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACE) == null) {
                            this.addError(
                                    this.getSyntaxAnalyzer().getArkoiClass(),
                                    this.getSyntaxAnalyzer().currentToken(),
                                    SyntaxErrorType.ROOT_BLOCK_FUNCTION_HAS_WRONG_ENDING
                            );
                            this.skipToNextValidToken();
                            continue main_loop;
                        } else
                            this.functionStorage.add(functionDefinitionAST);
                    } else {
                        if (abstractSyntaxAST instanceof VariableDefinitionSyntaxAST)
                            this.variableStorage.add((VariableDefinitionSyntaxAST) abstractSyntaxAST);
                        else if (abstractSyntaxAST instanceof ImportDefinitionSyntaxAST)
                            this.importStorage.add((ImportDefinitionSyntaxAST) abstractSyntaxAST);
                    }
                    
                    this.sortedStorage.add(abstractSyntaxAST);
                    this.getSyntaxAnalyzer().nextToken();
                } else {
                    this.skipToNextValidToken();
                    this.failed();
                }
                continue main_loop;
            }
            
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ROOT_NO_PARSER_FOUND
            );
            this.skipToNextValidToken();
        }
        
        return this.isFailed() ? Optional.empty() : Optional.of(this);
    }
    
    
    /**
     * Prints out a Tree for the  {@link RootSyntaxAST} class. It will show every {@link
     * ImportDefinitionSyntaxAST}, {@link VariableDefinitionSyntaxAST} and {@link
     * FunctionDefinitionSyntaxAST}. This method should just been called by {@link
     * ArkoiCompiler#printSyntaxTree(PrintStream)}.
     *
     * @param printStream
     *         the {@link PrintStream} which is used used for the output.
     * @param indents
     *         the indents which the Tree should add before printing a new line.
     */
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream,
            @NotNull final String indents) {
        printStream.println(indents + "├── imports: " + (this.getImportStorage().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getImportStorage().size(); index++) {
            final AbstractSyntaxAST abstractSyntaxAST = this.getImportStorage().get(index);
            if (index == this.getImportStorage().size() - 1) {
                printStream.println(indents + "│   └── " + abstractSyntaxAST.getClass().getSimpleName());
                abstractSyntaxAST.printSyntaxAST(printStream, indents + "│       ");
            } else {
                printStream.println(indents + "│   ├── " + abstractSyntaxAST.getClass().getSimpleName());
                abstractSyntaxAST.printSyntaxAST(printStream, indents + "│   │   ");
                printStream.println(indents + "│   │   ");
            }
        }
        
        printStream.println(indents + "│");
        printStream.println(indents + "├── variables: " + (this.getVariableStorage().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getVariableStorage().size(); index++) {
            final AbstractSyntaxAST abstractSyntaxAST = this.getVariableStorage().get(index);
            if (index == this.getVariableStorage().size() - 1) {
                printStream.println(indents + "│   └── " + abstractSyntaxAST.getClass().getSimpleName());
                abstractSyntaxAST.printSyntaxAST(printStream, indents + "│       ");
            } else {
                printStream.println(indents + "│   ├── " + abstractSyntaxAST.getClass().getSimpleName());
                abstractSyntaxAST.printSyntaxAST(printStream, indents + "│   │   ");
                printStream.println(indents + "│   │   ");
            }
        }
        
        printStream.println(indents + "│");
        printStream.println(indents + "└── functions: " + (this.getFunctionStorage().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getFunctionStorage().size(); index++) {
            final AbstractSyntaxAST abstractSyntaxAST = this.getFunctionStorage().get(index);
            if (index == this.getFunctionStorage().size() - 1) {
                printStream.println(indents + "    └── " + abstractSyntaxAST.getClass().getSimpleName());
                abstractSyntaxAST.printSyntaxAST(printStream, indents + "        ");
            } else {
                printStream.println(indents + "    ├── " + abstractSyntaxAST.getClass().getSimpleName());
                abstractSyntaxAST.printSyntaxAST(printStream, indents + "    │   ");
                printStream.println(indents + "    │   ");
            }
        }
    }
    
    
    /**
     * Just returns the {@link #sortedStorage} {@link List} which got sorted by the
     * integrated Java method. (Not the smartest solution)
     *
     * @return the {@link #sortedStorage} {@link List} which got sorted before.
     */
    public List<AbstractSyntaxAST> getSortedStorage() {
        this.sortedStorage.sort(Comparator.comparingInt(AbstractSyntaxAST::getStart));
        return this.sortedStorage;
    }
    
}
