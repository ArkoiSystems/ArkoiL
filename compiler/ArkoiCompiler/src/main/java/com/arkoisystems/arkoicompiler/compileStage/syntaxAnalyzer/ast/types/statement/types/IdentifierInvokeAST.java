package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.types.statements.IdentifierInvokeSemantic;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.AbstractStatementAST;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

/**
 * Copyright © 2019 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on the Sat Nov 09 2019 Author єхcsє#5543 aka timo
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
@Setter
@Getter
public class IdentifierInvokeAST extends AbstractStatementAST<IdentifierInvokeSemantic>
{
    
    @Expose
    private IdentifierAccess identifierAccess;
    
    @Expose
    private IdentifierToken invokedIdentifierNameToken;
    
    @Expose
    private AbstractStatementAST<?> invokedIdentifierStatement;
    
    public IdentifierInvokeAST() {
        super(ASTType.IDENTIFIER_INVOKE);
        
        this.identifierAccess = IdentifierAccess.GLOBAL_ACCESS;
    }
    
    @Override
    public IdentifierInvokeAST parseAST(final AbstractAST<?> parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"identifier invoke\" statement because the parsing doesn't start with an identifier."));
            return null;
        } else {
            this.invokedIdentifierNameToken = (IdentifierToken) syntaxAnalyzer.currentToken();
            this.setStart(this.invokedIdentifierNameToken.getStart());
        }
        
        if (syntaxAnalyzer.matchesNextToken(SymbolToken.SymbolType.PERIOD) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"identifier invoke\" statement because the name isn't followed by an period."));
            return null;
        } else {
            this.setEnd(syntaxAnalyzer.currentToken().getEnd());
            syntaxAnalyzer.nextToken();
        }
        
        if(!AbstractStatementAST.STATEMENT_PARSER.canParse(this, syntaxAnalyzer)) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"identifier invoke\" statement because the period isn't followed by an valid statement."));
            return null;
        }
        
        final AbstractStatementAST<?> abstractStatementAST = AbstractStatementAST.STATEMENT_PARSER.parse(this, syntaxAnalyzer);
        if(abstractStatementAST == null) {
            syntaxAnalyzer.errorHandler().addError(new ParserError<>(AbstractStatementAST.STATEMENT_PARSER, this, "Couldn't parse the \"identifier invoke\" statement because an error occurred during the parsing of the statement."));
            return null;
        } else this.invokedIdentifierStatement = abstractStatementAST;
        return parentAST.addAST(this, syntaxAnalyzer);
    }
    
    @Override
    public <T extends AbstractAST<?>> T addAST(final T toAddAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return toAddAST;
    }
    
    public enum IdentifierAccess
    {
        
        THIS_ACCESS,
        GLOBAL_ACCESS,
        
    }
    
}
