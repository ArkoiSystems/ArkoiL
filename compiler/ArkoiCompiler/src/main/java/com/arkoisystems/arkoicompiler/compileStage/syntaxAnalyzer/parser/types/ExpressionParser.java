package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.types;

import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.expression.AbstractExpressionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.AbstractStatementAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.Parser;

/*
    Operator Precedence:
    1. parenthesis ( (expr) )
    2. postfix (expr++ expr--)
    3. prefix (++expr --expr +expr -expr !expr)
    4. multiplicative (* / %)
    5. additive (+ -)
        wip - 5. shift (<< >> >>>)
    6. relational (< > <= >= is)
    7. equality (== !=)
        wip 7. bitwise AND (&)
        wip 8. bitwise inclusive OR (|)
    8. logical AND (&&)
    9. logical OR (||)
        wip 9. ternary (? :)
    10. assignment (= += -= *= /= %=)
*/
public class ExpressionParser extends Parser<AbstractExpressionAST>
{
    
    @Override
    public AbstractExpressionAST parse(final AbstractAST<?> parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return new AbstractExpressionAST(null).parseAST(parentAST, syntaxAnalyzer);
    }
    
    @Override
    public boolean canParse(final AbstractAST<?> parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
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
                return AbstractStatementAST.STATEMENT_PARSER.canParse(parentAST, syntaxAnalyzer);
            default:
                return false;
        }
    }
    
}