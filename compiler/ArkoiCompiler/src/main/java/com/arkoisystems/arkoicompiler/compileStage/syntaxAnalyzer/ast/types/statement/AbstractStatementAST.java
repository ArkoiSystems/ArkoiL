package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.expression.AbstractExpressionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.*;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.functionStatements.FunctionDefinitionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.functionStatements.FunctionInvokeAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.variableStatements.VariableDefinitionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.types.StatementParser;
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
public class AbstractStatementAST extends AbstractAST
{
    
    public static StatementParser STATEMENT_PARSER = new StatementParser();
    
    /**
     * This constructor is used to get the AST-Type of the classes which extends this
     * class. This will help to debug problems or check the AST for correct syntax.
     *
     * @param astType
     *         The AST-Type which should get used by the class.
     */
    public AbstractStatementAST(final ASTType astType) {
        super(astType);
    }
    
    /**
     * This method will parse the AbstractStatementAST and checks it for the correct
     * syntax. This AST can be used by everything but some ASTs are different then others.
     * So the ThisStatementAST will not accept a "this" keyword etc. Every statement needs
     * to start with an IdentifierToken so they can be separated.
     *
     * @param parentAST
     *         The parent of the AST. With it you can check for correct usage of the
     *         statement.
     * @param syntaxAnalyzer
     *         The given SyntaxAnalyzer is used for checking the syntax of the current
     *         Token list.
     *
     * @return It will return null if an error occurred or an AbstractStatementAST if it
     *         parsed until to the end.
     */
    @Override
    public AbstractStatementAST parseAST(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final AbstractToken currentToken = syntaxAnalyzer.currentToken();
        if (syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(currentToken, "Couldn't parse the statement because it doesn't start with an IdentifierToken."));
            return null;
        }
        
        if (parentAST instanceof ThisStatementAST) {
            switch (currentToken.getTokenContent()) {
                case "var":
                case "fun":
                case "import":
                case "this":
                case "return":
                    syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the statement because you can't use it with the \"this\" keyword. The \"this\" keyword can just be followed by a function or variable."));
                    return null;
                default:
                    if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.OPENING_PARENTHESIS) != null)
                        return new FunctionInvokeAST((IdentifierToken) currentToken).parseAST(parentAST, syntaxAnalyzer);
                    return new IdentifierCallAST().parseAST(parentAST, syntaxAnalyzer);
            }
        } else if (parentAST instanceof AbstractExpressionAST) {
            switch (currentToken.getTokenContent()) {
                case "var":
                case "fun":
                case "import":
                case "return":
                    return null;
                case "this":
                    return new ThisStatementAST().parseAST(parentAST, syntaxAnalyzer);
                default:
                    if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.OPENING_PARENTHESIS) != null)
                        return new FunctionInvokeAST((IdentifierToken) currentToken, FunctionInvokeAST.FunctionInvocationAccess.EXPRESSION_INVOCATION).parseAST(parentAST, syntaxAnalyzer);
                    if(syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.PERIOD) != null)
                        return new IdentifierInvokeAST().parseAST(parentAST, syntaxAnalyzer);
                    return new IdentifierCallAST().parseAST(parentAST, syntaxAnalyzer);
            }
        } else if (parentAST instanceof IdentifierInvokeAST) {
            switch (currentToken.getTokenContent()) {
                case "var":
                case "fun":
                case "import":
                case "return":
                case "this":
                    return null;
                default:
                    if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.OPENING_PARENTHESIS) != null)
                        return new FunctionInvokeAST((IdentifierToken) currentToken, FunctionInvokeAST.FunctionInvocationAccess.EXPRESSION_INVOCATION).parseAST(parentAST, syntaxAnalyzer);
                    if(syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.PERIOD) != null)
                        return new IdentifierInvokeAST().parseAST(parentAST, syntaxAnalyzer);
                    return new IdentifierCallAST().parseAST(parentAST, syntaxAnalyzer);
            }
        } else {
            switch (currentToken.getTokenContent()) {
                case "var":
                    return new VariableDefinitionAST().parseAST(parentAST, syntaxAnalyzer);
                case "this":
                    return new ThisStatementAST().parseAST(parentAST, syntaxAnalyzer);
                case "import":
                    return new ImportDefinitionAST().parseAST(parentAST, syntaxAnalyzer);
                case "fun":
                    return new FunctionDefinitionAST().parseAST(parentAST, syntaxAnalyzer);
                case "return":
                    return new ReturnStatementAST().parseAST(parentAST, syntaxAnalyzer);
                default:
                    if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.OPENING_PARENTHESIS) != null)
                        return new FunctionInvokeAST((IdentifierToken) currentToken).parseAST(parentAST, syntaxAnalyzer);
                    if(syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.PERIOD) != null)
                        return new IdentifierInvokeAST().parseAST(parentAST, syntaxAnalyzer);
                    return new IdentifierCallAST().parseAST(parentAST, syntaxAnalyzer);
            }
        }
    }
    
    /**
     * This method is just overwritten because this class extends the AbstractAST class.
     * It will just return the input and doesn't check anything.
     *
     * @param toAddAST
     *         The AST which should get add to this class.
     * @param syntaxAnalyzer
     *         The given SyntaxAnalyzer is needed for checking and modification of the
     *         current Token list/order.
     * @param <T>
     *         The Type of the AST which should be added to the AbstractStatementAST.
     *
     * @return It will just return the input "toAddAST" because you can't add ASTs to an
     *         AbstractStatementAST.
     */
    @Override
    public <T extends AbstractAST> T addAST(final T toAddAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return toAddAST;
    }
    
}
