/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.EndOfFileToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ImportDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.AbstractParser;
import com.google.gson.annotations.Expose;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RootSyntaxAST extends AbstractSyntaxAST
{
    
    private static AbstractParser<?>[] ROOT_PARSERS = new AbstractParser<?>[] {
            AnnotationSyntaxAST.ANNOTATION_PARSER,
            AbstractStatementSyntaxAST.STATEMENT_PARSER,
    };
    
    
    @Expose
    private final List<ImportDefinitionSyntaxAST> importStorage;
    
    @Expose
    private final List<VariableDefinitionSyntaxAST> variableStorage;
    
    @Expose
    private final List<FunctionDefinitionSyntaxAST> functionStorage;
    
    /**
     * This constructor will initialize the RootAST with the AST-Type "ROOT". This will
     * help to debug problems or check the AST for correct syntax. Also it will pass the
     * SyntaxAnalyzer for setting the end of this AST (input file length).
     *
     * @param syntaxAnalyzer
     *         The SyntaxAnalyzer which get used to parse the tokens.
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
     * This method will parse the "RootAST" and checks it for correct syntax. The RootAST
     * is used to store all variables, functions etc. Also it is called the "root file"
     * because it is the outers layer of the whole AST. It will just parse annotations and
     * statements and checks if they got parsed right.
     *
     * @param parentAST
     *         The parent of the AST. With it you can check for correct usage of the
     *         statement.
     * @param syntaxAnalyzer
     *         The given SyntaxAnalyzer is used for checking the syntax of the current
     *         Token list.
     *
     * @return It will return null if an error occurred or a RootAST if it parsed until to
     *         the end.
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
                        if (functionDefinitionAST.getFunctionBlock().getBlockType() == BlockSyntaxAST.BlockType.INLINE && syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.SEMICOLON) == null) {
                            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function definition\" statement because the inlined function doesn't end with a semicolon."));
                            return null;
                        } else if (functionDefinitionAST.getFunctionBlock().getBlockType() == BlockSyntaxAST.BlockType.BLOCK && syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.CLOSING_BRACE) == null) {
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
    
}
