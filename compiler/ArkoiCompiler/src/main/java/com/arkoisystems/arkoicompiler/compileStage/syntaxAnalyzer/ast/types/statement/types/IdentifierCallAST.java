package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.types.statements.IdentifierCallSemantic;
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
public class IdentifierCallAST extends AbstractStatementAST<IdentifierCallSemantic>
{
    
    @Expose
    private IdentifierAccess identifierAccess;
    
    @Expose
    private IdentifierToken calledIdentifierToken;
    
    public IdentifierCallAST() {
        super(ASTType.IDENTIFIER_CALL);
        
        this.identifierAccess = IdentifierAccess.GLOBAL_ACCESS;
    }
    
    /**
     * This method will parse the "identifier call" statement and checks it for correct
     * syntax. This statement can be used everywhere it is needed but especially in
     * AbstractExpressionAST.
     *
     * @param parentAST
     *         The parent of the AST. With it you can check for correct usage of the
     *         statement.
     * @param syntaxAnalyzer
     *         The given SyntaxAnalyzer is used for checking the syntax of the current
     *         Token list.
     *
     * @return It will return null if an error occurred or an IdentifierCallStatementAST
     *         if it parsed until to the end.
     */
    @Override
    public IdentifierCallAST parseAST(final AbstractAST<?> parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"identifier call\" statement because the parsing doesn't start with an Identifier."));
            return null;
        } else {
            this.calledIdentifierToken = (IdentifierToken) syntaxAnalyzer.currentToken();
            this.setStart(this.calledIdentifierToken.getStart());
            this.setEnd(this.calledIdentifierToken.getEnd());
        }
        return parentAST.addAST(this, syntaxAnalyzer);
    }
    
    /**
     * This method is just overwritten to prevent default code execution. So it will just
     * return the input and doesn't check anything.
     *
     * @param toAddAST
     *         The AST which should get added to the "IdentifierCallStatementAST".
     * @param syntaxAnalyzer
     *         The SyntaxAnalyzer which should get used if you want to compare Tokens.
     * @param <T>
     *         The Type of the AST which should be added to the "IdentifierCallStatementAST".
     *
     * @return It will just return the input "toAddAST" because you can't add ASTs to an
     *         IdentifierCallStatementAST.
     */
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
