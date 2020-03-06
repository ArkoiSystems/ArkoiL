/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.BlockSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.FunctionInvokeOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierInvokeOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTAccess;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;

import java.io.PrintStream;

public class ThisStatementSyntaxAST extends AbstractStatementSyntaxAST
{
    
    public ThisStatementSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.THIS_STATEMENT);
    }
    
    
    @Override
    public AbstractSyntaxAST parseAST(final AbstractSyntaxAST parentAST) {
        if (!(parentAST instanceof BlockSyntaxAST) && !(parentAST instanceof AbstractExpressionSyntaxAST)) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.THIS_STATEMENT_WRONG_PARENT
            );
            return null;
        }
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.IDENTIFIER) == null || !this.getSyntaxAnalyzer().currentToken().getTokenContent().equals("this")) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.THIS_STATEMENT_WRONG_START
            );
            return null;
        }
        this.setStart(this.getSyntaxAnalyzer().currentToken().getStart());
        
        if (this.getSyntaxAnalyzer().matchesNextToken(SymbolType.PERIOD) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.THIS_STATEMENT_NO_SEPARATOR
            );
            return null;
        }
        
        this.setEnd(this.getSyntaxAnalyzer().currentToken().getEnd());
        this.getSyntaxAnalyzer().nextToken(); // This will skip to the followed token after the period. So we can check if the next token is a statement.
        
        // For the "parentAST" we don't use "this" because we don't want that other AST tries to add theirselves to this class.
        if (!AbstractStatementSyntaxAST.STATEMENT_PARSER.canParse(parentAST, this.getSyntaxAnalyzer())) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.THIS_STATEMENT_NO_VALID_STATEMENT
            );
            return null;
        }
        
        final AbstractSyntaxAST abstractSyntaxAST = AbstractStatementSyntaxAST.STATEMENT_PARSER.parse(parentAST, this.getSyntaxAnalyzer());
        if (abstractSyntaxAST == null)
            return null;
        
        if (abstractSyntaxAST instanceof IdentifierInvokeOperableSyntaxAST) {
            final IdentifierInvokeOperableSyntaxAST identifierInvokeOperableAST = (IdentifierInvokeOperableSyntaxAST) abstractSyntaxAST;
            identifierInvokeOperableAST.setIdentifierAccess(ASTAccess.THIS_ACCESS);
        } else if (abstractSyntaxAST instanceof FunctionInvokeOperableSyntaxAST) {
            final FunctionInvokeOperableSyntaxAST functionInvokeOperableSyntaxAST = (FunctionInvokeOperableSyntaxAST) abstractSyntaxAST;
            functionInvokeOperableSyntaxAST.setFunctionAccess(ASTAccess.THIS_ACCESS);
        } else if (abstractSyntaxAST instanceof IdentifierCallOperableSyntaxAST) {
            final IdentifierCallOperableSyntaxAST identifierCallOperableAST = (IdentifierCallOperableSyntaxAST) abstractSyntaxAST;
            identifierCallOperableAST.setIdentifierAccess(ASTAccess.THIS_ACCESS);
        }
        return abstractSyntaxAST;
    }
    
    
    @Override
    public void printSyntaxAST(final PrintStream printStream, final String indents) { }
    
}
