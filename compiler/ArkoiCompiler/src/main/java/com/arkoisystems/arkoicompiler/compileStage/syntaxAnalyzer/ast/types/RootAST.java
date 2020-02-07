package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.EndOfFileToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.types.RootSemantic;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.AbstractStatementAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.ImportDefinitionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.function.FunctionDefinitionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.variable.VariableDefinitionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.Parser;
import com.google.gson.annotations.Expose;
import lombok.Getter;

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
public class RootAST extends AbstractAST<RootSemantic>
{
    
    private static Parser<?>[] ROOT_PARSERS = new Parser<?>[] {
            AnnotationAST.ANNOTATION_PARSER,
            AbstractStatementAST.STATEMENT_PARSER,
    };
    
    
    @Expose
    private final List<ImportDefinitionAST> importStorage;
    
    @Expose
    private final List<VariableDefinitionAST> variableStorage;
    
    @Expose
    private final List<FunctionDefinitionAST> functionStorage;
    
    /**
     * This constructor will initialize the RootAST with the AST-Type "ROOT". This will
     * help to debug problems or check the AST for correct syntax. Also it will pass the
     * SyntaxAnalyzer for setting the end of this AST (input file length).
     *
     * @param syntaxAnalyzer
     *         The SyntaxAnalyzer which get used to parse the tokens.
     */
    public RootAST(final SyntaxAnalyzer syntaxAnalyzer) {
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
    public RootAST parseAST(final AbstractAST<?> parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        main_loop:
        while (syntaxAnalyzer.getPosition() < syntaxAnalyzer.getTokens().length) {
            if (syntaxAnalyzer.currentToken() instanceof EndOfFileToken)
                break;
        
            for (final Parser<?> parser : ROOT_PARSERS) {
                if (!parser.canParse(this, syntaxAnalyzer))
                    continue;
            
                final AbstractAST<?> abstractAST = parser.parse(this, syntaxAnalyzer);
                if (abstractAST == null) {
                    syntaxAnalyzer.errorHandler().addError(new ParserError<>(parser, syntaxAnalyzer.currentToken()));
                    return null;
                } else {
                    if (abstractAST instanceof FunctionDefinitionAST) {
                        final FunctionDefinitionAST functionDefinitionAST = (FunctionDefinitionAST) abstractAST;
                        if (functionDefinitionAST.getBlockAST().getBlockType() == BlockAST.BlockType.INLINE && syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.SEMICOLON) == null) {
                            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function definition\" statement because the inlined function doesn't end with a semicolon."));
                            return null;
                        } else if (functionDefinitionAST.getBlockAST().getBlockType() == BlockAST.BlockType.BLOCK && syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.CLOSING_BRACE) == null) {
                            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function definition\" statement because it doesn't end with a closing brace."));
                            return null;
                        }
                    } else {
                        if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.SEMICOLON) == null) {
                            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"%s\" because it doesn't end with a semicolon.", abstractAST.getClass().getSimpleName()));
                            return null;
                        }
                    }
                    syntaxAnalyzer.nextToken();
                    continue main_loop;
                }
            }
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the AST because no parser could parse this token. Check for misspelling or something else."));
            return null;
        }
        return this;
    }
    
    /**
     * This method is used to add ASTs to the RootAST with just one method. It will just
     * handle the supported ASTs and will add them to their specified list.
     *
     * @param toAddAST
     *         The AST which should get add to this class.
     * @param syntaxAnalyzer
     *         The given SyntaxAnalyzer is needed for checking and modification of the
     *         current Token list/order.
     * @param <T>
     *         The Type of the AST which should be added to the "RootAST".
     *
     * @return It will just return the input "toAddAST" if the AST isn't supported.
     *         Otherwise the ASTs get added to their specified list.
     */
    @Override
    public <T extends AbstractAST<?>> T addAST(final T toAddAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (toAddAST instanceof VariableDefinitionAST)
            this.variableStorage.add((VariableDefinitionAST) toAddAST);
        else if (toAddAST instanceof FunctionDefinitionAST)
            this.functionStorage.add((FunctionDefinitionAST) toAddAST);
        else if (toAddAST instanceof ImportDefinitionAST)
            this.importStorage.add((ImportDefinitionAST) toAddAST);
        return toAddAST;
    }
    
}
