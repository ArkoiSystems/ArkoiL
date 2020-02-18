/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.ArkoiCompiler;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.EndOfFileToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ImportDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.BlockType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.AbstractParser;
import com.google.gson.annotations.Expose;
import lombok.Getter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Used if you want to create a new {@link RootSyntaxAST}. It doesn't has an {@link
 * AbstractParser} because you shouldn't treat this class like that. There should only be
 * one instance for one {@link ArkoiClass}.
 */
@Getter
public class RootSyntaxAST extends AbstractSyntaxAST
{
    
    /**
     * This variable is used to get all {@link AbstractParser}'s which are supported by
     * the {@link RootSyntaxAST}.
     */
    public static AbstractParser<?>[] ROOT_PARSERS = new AbstractParser<?>[] {
            AnnotationSyntaxAST.ANNOTATION_PARSER,
            AbstractStatementSyntaxAST.STATEMENT_PARSER,
    };
    
    
    /**
     * Declares the {@link List} for all {@link ImportDefinitionSyntaxAST}'s which got
     * parsed. This variable is used frequently within the {@link SemanticAnalyzer}.
     */
    @Expose
    private final List<ImportDefinitionSyntaxAST> importStorage;
    
    
    /**
     * Declares the {@link List} for all {@link VariableDefinitionSyntaxAST}'s which got
     * parsed. This variable is used frequently within the {@link SemanticAnalyzer}.
     */
    @Expose
    private final List<VariableDefinitionSyntaxAST> variableStorage;
    
    
    /**
     * Declares the {@link List} for all {@link FunctionDefinitionSyntaxAST}'s which got
     * parsed. This variable is used frequently within the {@link SemanticAnalyzer}.
     */
    @Expose
    private final List<FunctionDefinitionSyntaxAST> functionStorage;
    
    
    /**
     * Constructs a new {@link RootSyntaxAST} with the given parameters. The {@link
     * SyntaxAnalyzer} is used to check syntax and also to get the {@link ArkoiClass} or
     * {@link ArkoiCompiler}.
     *
     * @param syntaxAnalyzer
     *         the {@link SyntaxAnalyzer} is part for the most code used by {@link
     *         AbstractSyntaxAST}'s.
     */
    public RootSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer) {
        super(ASTType.ROOT);
        
        this.setEnd(syntaxAnalyzer.getArkoiClass().getContent().length());
        this.setStart(0);
        
        this.variableStorage = new ArrayList<>();
        this.functionStorage = new ArrayList<>();
        this.importStorage = new ArrayList<>();
    }
    
    
    /**
     * Parses a new {@link TypeSyntaxAST} with the given parameters, where the {@link
     * SyntaxAnalyzer} is used to check the syntax and the parent {@link
     * AbstractSyntaxAST} is used to see if this AST can be created inside the parent. It
     * will parse every {@link ImportDefinitionSyntaxAST}, {@link
     * VariableDefinitionSyntaxAST} and {@link FunctionDefinitionSyntaxAST} which are
     * present at the root-level (the first layer of the AST). Besides that it will parse
     * also {@link AnnotationSyntaxAST}'s but they return a followed {@link
     * FunctionDefinitionSyntaxAST} or {@link VariableDefinitionSyntaxAST}. So basically
     * just these three types of ASTs are getting parsed.
     *
     * @param parentAST
     *         the parent {@link AbstractSyntaxAST} which should get used to check if the
     *         {@link AbstractSyntaxAST} can be created inside it.
     * @param syntaxAnalyzer
     *         the {@link SyntaxAnalyzer} which is used for checking the syntax with
     *         methods like {@link SyntaxAnalyzer#matchesCurrentToken(TokenType)} )} or
     *         {@link SyntaxAnalyzer#matchesNextToken(SymbolToken.SymbolType)}.
     *
     * @return {@code null} if an error occurred or the {@link RootSyntaxAST} if
     *         everything worked correctly.
     */
    @Override
    public RootSyntaxAST parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        main_loop:
        while (syntaxAnalyzer.getPosition() < syntaxAnalyzer.getTokens().length) {
            if (syntaxAnalyzer.currentToken() instanceof EndOfFileToken)
                break;
            
            for (final AbstractParser<?> abstractParser : ROOT_PARSERS) {
                if (!abstractParser.canParse(this, syntaxAnalyzer))
                    continue;
                
                final AbstractSyntaxAST abstractSyntaxAST = abstractParser.parse(this, syntaxAnalyzer);
                if (abstractSyntaxAST != null) {
                    if (abstractSyntaxAST instanceof FunctionDefinitionSyntaxAST) {
                        final FunctionDefinitionSyntaxAST functionDefinitionAST = (FunctionDefinitionSyntaxAST) abstractSyntaxAST;
                        if (functionDefinitionAST.getFunctionBlock().getBlockType() == BlockType.INLINE && syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.SEMICOLON) == null) {
                            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function definition\" statement because the inlined function doesn't end with a semicolon."));
                            return null;
                        } else if (functionDefinitionAST.getFunctionBlock().getBlockType() == BlockType.BLOCK && syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.CLOSING_BRACE) == null) {
                            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function definition\" statement because it doesn't end with a closing brace."));
                            return null;
                        } else this.functionStorage.add(functionDefinitionAST);
                    } else {
                        if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.SEMICOLON) == null) {
                            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"%s\" because it doesn't end with a semicolon.", abstractSyntaxAST.getClass().getSimpleName()));
                            return null;
                        }
                        
                        if(abstractSyntaxAST instanceof VariableDefinitionSyntaxAST)
                            this.variableStorage.add((VariableDefinitionSyntaxAST) abstractSyntaxAST);
                        else if(abstractSyntaxAST instanceof ImportDefinitionSyntaxAST)
                            this.importStorage.add((ImportDefinitionSyntaxAST) abstractSyntaxAST);
                    }
                    syntaxAnalyzer.nextToken();
                    continue main_loop;
                } else return null;
            }
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the AST because no parser could parse this token. Check for misspelling or something else."));
            return null;
        }
        return this;
    }
    
    
    @Override
    public void printAST(final PrintStream printStream, final String indents) {
        final List<AbstractSyntaxAST> abstractSyntaxASTs = new ArrayList<>();
        abstractSyntaxASTs.addAll(this.getImportStorage());
        abstractSyntaxASTs.addAll(this.getVariableStorage());
        abstractSyntaxASTs.addAll(this.getFunctionStorage());
        abstractSyntaxASTs.sort(Comparator.comparingInt(AbstractSyntaxAST::getStart));
        
        for (int index = 0; index < abstractSyntaxASTs.size(); index++) {
            final AbstractSyntaxAST abstractSyntaxAST = abstractSyntaxASTs.get(index);
            if (index == abstractSyntaxASTs.size() - 1) {
                printStream.println(indents + "│");
                printStream.println(indents + "└── " + abstractSyntaxAST.getClass().getSimpleName());
                abstractSyntaxAST.printAST(printStream, indents + "    ");
            } else {
                printStream.println(indents + "│");
                printStream.println(indents + "├── " + abstractSyntaxAST.getClass().getSimpleName());
                abstractSyntaxAST.printAST(printStream, indents + "│   ");
            }
        }
    }
    
}
