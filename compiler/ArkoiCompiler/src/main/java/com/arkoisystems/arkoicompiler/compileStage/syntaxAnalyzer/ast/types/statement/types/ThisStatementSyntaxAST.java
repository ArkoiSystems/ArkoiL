package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTAccess;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.BlockSyntaxAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.types.FunctionInvokeOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.types.IdentifierInvokeOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
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
public class ThisStatementSyntaxAST extends AbstractStatementSyntaxAST
{
    
    private AbstractSyntaxAST parentAST;
    
    /**
     * This constructor will initialize the statement with the AST-Type
     * "THIS_STATEMENT_AST". This will help to debug problems or check the AST for correct
     * syntax.
     */
    public ThisStatementSyntaxAST() {
        super(ASTType.THIS_STATEMENT);
    }
    
    /**
     * This method will parse the "this" statement and checks it for the correct syntax.
     * This statement can just be used inside a BlockAST or inside an
     * AbstractExpressionAST. You also can't use the "this" keyword a second time. You
     * need to specify a period after the keyword which is followed by another statement
     * which will call a function or variable.
     * <p>
     * An example for this statement:
     * <p>
     * var test_string = this.test();
     * <p>
     * fun main<int>(args: string[]) { println(this.test()); return 0; }
     * <p>
     * fun test<string>() = "Hello World";
     *
     * @param parentAST
     *         The parent of this AST. It can be a BlockAST or an AbstractExpressionAST.
     * @param syntaxAnalyzer
     *         The given SyntaxAnalyzer is used for checking the syntax of the current
     *         Token list.
     *
     * @return It will return null if an error occurred or an AbstractStatementAST if it
     *         parsed until to the end.
     */
    @Override
    public AbstractSyntaxAST parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (!(parentAST instanceof BlockSyntaxAST) && !(parentAST instanceof AbstractExpressionSyntaxAST)) {
            syntaxAnalyzer.errorHandler().addError(new SyntaxASTError<>(parentAST, "Couldn't parse the \"this\" statement because it isn't declared inside a block or an expression."));
            return null;
        } else this.parentAST = parentAST;
    
        if (syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER) == null || !syntaxAnalyzer.currentToken().getTokenContent().equals("this")) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"this\" statement because the parsing doesn't start with the \"this\" keyword."));
            return null;
        } else this.setStart(syntaxAnalyzer.currentToken().getStart());
    
        if (syntaxAnalyzer.matchesNextToken(SymbolToken.SymbolType.PERIOD) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"this\" statement because the \"this\" keyword isn't followed by an period."));
            return null;
        } else {
            this.setEnd(syntaxAnalyzer.currentToken().getEnd());
            syntaxAnalyzer.nextToken(); // This will skip to the followed token after the period. So we can check if the next token is a statement.
        }
    
        // For the "parentAST" we don't use "this" because we don't want that other AST tries to add theirselves to this class.
        if (!AbstractStatementSyntaxAST.STATEMENT_PARSER.canParse(parentAST, syntaxAnalyzer)) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"this\" statement because the period isn't followed by a valid statement."));
            return null;
        }
    
        final AbstractSyntaxAST abstractSyntaxAST = AbstractStatementSyntaxAST.STATEMENT_PARSER.parse(parentAST, syntaxAnalyzer);
        if (abstractSyntaxAST == null) {
            syntaxAnalyzer.errorHandler().addError(new ParserError<>(AbstractStatementSyntaxAST.STATEMENT_PARSER, this, "Couldn't parse the \"this\" statement because an error occurred during the parsing of the statement."));
            return null;
        }
    
        if (abstractSyntaxAST instanceof FunctionInvokeOperableSyntaxAST) {
            final FunctionInvokeOperableSyntaxAST functionInvokeOperableAST = (FunctionInvokeOperableSyntaxAST) abstractSyntaxAST;
            functionInvokeOperableAST.setFunctionAccess(ASTAccess.THIS_ACCESS);
        } else if (abstractSyntaxAST instanceof IdentifierInvokeOperableSyntaxAST) {
            final IdentifierInvokeOperableSyntaxAST identifierInvokeOperableAST = (IdentifierInvokeOperableSyntaxAST) abstractSyntaxAST;
            identifierInvokeOperableAST.setIdentifierAccess(ASTAccess.THIS_ACCESS);
        } else if (abstractSyntaxAST instanceof IdentifierCallOperableSyntaxAST) {
            final IdentifierCallOperableSyntaxAST identifierCallOperableAST = (IdentifierCallOperableSyntaxAST) abstractSyntaxAST;
            identifierCallOperableAST.setIdentifierAccess(ASTAccess.THIS_ACCESS);
        }
    
        return abstractSyntaxAST;
    }
    
}
