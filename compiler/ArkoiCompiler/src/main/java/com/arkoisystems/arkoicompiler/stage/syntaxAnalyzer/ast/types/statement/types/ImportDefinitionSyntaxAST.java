package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.StringToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.RootSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
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
public class ImportDefinitionSyntaxAST extends AbstractStatementSyntaxAST
{
    
    @Expose
    private StringToken importFilePath;
    
    @Expose
    private IdentifierToken importName;
    
    /**
     * This constructor will initialize the statement with the AST-Type
     * "IMPORT_STATEMENT_AST". This will help to debug problems or check the AST for
     * correct syntax.
     */
    public ImportDefinitionSyntaxAST() {
        super(ASTType.IMPORT_DEFINITION);
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
    public ImportDefinitionSyntaxAST parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (!(parentAST instanceof RootSyntaxAST)) {
            syntaxAnalyzer.errorHandler().addError(new SyntaxASTError<>(parentAST, "Couldn't parse the \"import\" statement because it isn't declared inside the root file."));
            return null;
        }
    
        if (syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER) == null || !syntaxAnalyzer.currentToken().getTokenContent().equals("import")) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"import\" statement because the parsing doesn't start with the \"import\" keyword."));
            return null;
        } else this.setStart(syntaxAnalyzer.currentToken().getStart());
    
        if (syntaxAnalyzer.matchesNextToken(TokenType.STRING_LITERAL) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"import\" statement because the \"import\" keyword isn't followed by an file path."));
            return null;
        } else {
            this.importFilePath = (StringToken) syntaxAnalyzer.currentToken();
        
            if (this.importFilePath.getTokenContent().endsWith(".ark"))
                this.importFilePath.setTokenContent(this.importFilePath.getTokenContent().substring(0, this.importFilePath.getTokenContent().length() - 4));
        }
    
        if (syntaxAnalyzer.matchesPeekToken(1, TokenType.IDENTIFIER) != null && syntaxAnalyzer.peekToken(1).getTokenContent().equals("as")) {
            syntaxAnalyzer.nextToken();
        
            if (syntaxAnalyzer.matchesNextToken(TokenType.IDENTIFIER) == null) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"import\" statement because the \"named\" keyword isn't followed by an name identifier."));
                return null;
            } else this.importName = (IdentifierToken) syntaxAnalyzer.currentToken();
        } else {
            final String[] splittedPath = this.importFilePath.getTokenContent().split("/");
            this.importName = new IdentifierToken(splittedPath[splittedPath.length - 1].replace(".ark", ""), -1, -1);
        }
        
        if (syntaxAnalyzer.matchesNextToken(SymbolToken.SymbolType.SEMICOLON) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"import\" statement because it doesn't end with a semicolon."));
            return null;
        } else this.setEnd(syntaxAnalyzer.currentToken().getEnd());
        return this;
    }
    
}
