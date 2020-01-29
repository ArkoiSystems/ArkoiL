package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ASTError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SeparatorToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.BlockAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.expressions.AbstractExpressionAST;
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
public class ThisStatementAST extends AbstractStatementAST
{
    
    public ThisStatementAST() {
        this.setAstType(ASTType.THIS_STATEMENT_AST);
    }
    
    @Override
    public AbstractStatementAST parseAST(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (!(parentAST instanceof BlockAST) && !(parentAST instanceof AbstractExpressionAST)) {
            syntaxAnalyzer.errorHandler().addError(new ASTError(parentAST, "Couldn't parse the this statement because it wasn't declared inside a BlockAST."));
            return null;
        }
//        System.out.println(parentAST.getClass().getSimpleName() + ", " + syntaxAnalyzer.currentToken());
        
        final AbstractToken thisIdentifierToken = syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER);
        if (thisIdentifierToken == null || !thisIdentifierToken.getTokenContent().equals("this")) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the this statement because the parsing doesn't start with an IdentifierToken with the name \"this\"."));
            return null;
        } else this.setStart(thisIdentifierToken.getStart());
        
        final AbstractToken periodToken = syntaxAnalyzer.matchesNextToken(SeparatorToken.SeparatorType.PERIOD);
        if (periodToken == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't üarse the this statement because the keyword isn't followed by an period."));
            return null;
        } else this.setEnd(periodToken.getEnd());
        
        syntaxAnalyzer.nextToken();
        return new AbstractStatementAST().parseAST(this, syntaxAnalyzer);
    }
    
}
