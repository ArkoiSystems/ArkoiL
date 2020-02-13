package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.AbstractParser;

public class ExpressionParser extends AbstractParser<ExpressionSyntaxAST>
{
    
    @Override
    public ExpressionSyntaxAST parse(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final AbstractExpressionSyntaxAST abstractExpressionAST = new AbstractExpressionSyntaxAST(null).parseAST(parentAST, syntaxAnalyzer);
        if (abstractExpressionAST == null)
            return null;
        if (!(abstractExpressionAST instanceof ExpressionSyntaxAST)) {
            syntaxAnalyzer.errorHandler().addError(new SyntaxASTError<>(abstractExpressionAST, "Couldn't parse the expression because the result isn't an ExpressionAST."));
            return null;
        }
        return (ExpressionSyntaxAST) abstractExpressionAST;
    }
    
    @Override
    public boolean canParse(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        switch (syntaxAnalyzer.currentToken().getTokenType()) {
            case STRING_LITERAL:
            case NUMBER_LITERAL:
                return true;
            case SYMBOL:
                final SymbolToken symbolToken = (SymbolToken) syntaxAnalyzer.currentToken();
                switch (symbolToken.getSymbolType()) {
                    case OPENING_PARENTHESIS:
                    case PLUS:
                    case MINUS:
                    case EXCLAMATION_MARK:
                        return true;
                    default:
                        return false;
                }
            case IDENTIFIER:
                return AbstractStatementSyntaxAST.STATEMENT_PARSER.canParse(parentAST, syntaxAnalyzer);
            default:
                return false;
        }
    }
    
}