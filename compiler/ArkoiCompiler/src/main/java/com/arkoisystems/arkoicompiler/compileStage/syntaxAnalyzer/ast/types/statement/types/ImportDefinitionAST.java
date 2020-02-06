package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ASTError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.StringToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.types.statements.ImportDefinitionSemantic;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.RootAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.AbstractStatementAST;
import com.google.gson.annotations.Expose;
import lombok.Getter;

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
public class ImportDefinitionAST extends AbstractStatementAST<ImportDefinitionSemantic>
{
    
    @Expose
    private StringToken importFilePathToken;
    
    @Expose
    private IdentifierToken importNameToken;
    
    /**
     * This constructor will initialize the statement with the AST-Type
     * "IMPORT_STATEMENT_AST". This will help to debug problems or check the AST for
     * correct syntax.
     */
    public ImportDefinitionAST() {
        super(ASTType.IMPORT_STATEMENT);
    }
    
    /**
     * This method will parse the "import" statement and checks it for the correct syntax.
     * This statement can just be used inside a RootAST. This AST will import files into
     * the next stages (Semantic Analysis etc.).
     *
     * @param parentAST
     *         The parent of the ImportAST which only can be a RootAST.
     * @param syntaxAnalyzer
     *         The given SyntaxAnalyzer is used for checking the syntax of the current
     *         Token list.
     *
     * @return It will return null if an error occurred or an AbstractStatementAST if it
     *         parsed until to the end.
     */
    @Override
    public ImportDefinitionAST parseAST(final AbstractAST<?> parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (!(parentAST instanceof RootAST)) {
            syntaxAnalyzer.errorHandler().addError(new ASTError(parentAST, "Couldn't parse the \"import\" statement because it isn't declared inside the root file."));
            return null;
        }
    
        if (syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER) == null || !syntaxAnalyzer.currentToken().getTokenContent().equals("import")) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"import\" statement because the parsing doesn't start with the \"import\" keyword."));
            return null;
        } else this.setStart(syntaxAnalyzer.currentToken().getStart());
    
        if (syntaxAnalyzer.matchesNextToken(TokenType.STRING_LITERAL) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"import\" statement because the \"import\" keyword isn't followed by an file path."));
            return null;
        } else this.importFilePathToken = (StringToken) syntaxAnalyzer.currentToken();
        
        if (syntaxAnalyzer.matchesPeekToken(1, TokenType.IDENTIFIER) != null && syntaxAnalyzer.peekToken(1).getTokenContent().equals("named")) {
            syntaxAnalyzer.nextToken();
            
            if (syntaxAnalyzer.matchesNextToken(TokenType.IDENTIFIER) == null) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"import\" statement because the \"named\" keyword isn't followed by an name identifier."));
                return null;
            } else this.importNameToken = (IdentifierToken) syntaxAnalyzer.currentToken();
        } else {
            final String[] splittedPath = this.importFilePathToken.getTokenContent().split("/");
            this.importNameToken = new IdentifierToken(splittedPath[splittedPath.length - 1].replace(".ark", ""), -1, -1);
        }
        
        if (syntaxAnalyzer.matchesNextToken(SymbolToken.SymbolType.SEMICOLON) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"import\" statement because it doesn't end with a semicolon."));
            return null;
        }
        return parentAST.addAST(this, syntaxAnalyzer);
    }
    
    /**
     * This method is just overwritten to prevent default code execution. So it will just
     * return the input and doesn't check anything.
     *
     * @param toAddAST
     *         The AST which should get added to the "ImportDefinitionAST".
     * @param syntaxAnalyzer
     *         The SyntaxAnalyzer which should get used if you want to compare Tokens.
     * @param <T>
     *         The Type of the AST which should be added to the "ImportDefinitionAST".
     *
     * @return It will just return the input "toAddAST" because you can't add ASTs to an
     *         ImportDefinitionAST.
     */
    @Override
    public <T extends AbstractAST<?>> T addAST(final T toAddAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return toAddAST;
    }
    
}
