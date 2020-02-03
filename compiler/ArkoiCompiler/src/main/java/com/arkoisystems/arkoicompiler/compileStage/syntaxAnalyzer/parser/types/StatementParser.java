package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.types;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SeparatorToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.expression.AbstractExpressionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.AbstractStatementAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.ThisStatementAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.Parser;
import lombok.Getter;

@Getter
public class StatementParser extends Parser<AbstractStatementAST>
{
    
    @Override
    public AbstractStatementAST parse(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return new AbstractStatementAST().parseAST(parentAST, syntaxAnalyzer);
    }
    
    @Override
    public boolean canParse(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final AbstractToken currentToken = syntaxAnalyzer.currentToken();
        if (currentToken == null || currentToken.getTokenType() != TokenType.IDENTIFIER)
            return false;
        
        if (parentAST instanceof ThisStatementAST) {
            switch (currentToken.getTokenContent()) {
                case "val":
                case "fun":
                case "import":
                case "this":
                case "return":
                    syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the statement because you can't use it with the \"this\" keyword. The \"this\" keyword can just be followed by a function or variable."));
                    break;
                default:
                    if (syntaxAnalyzer.matchesPeekToken(1, SeparatorToken.SeparatorType.OPENING_PARENTHESIS) != null)
                        return true;
                    break;
            }
        } else if (parentAST instanceof AbstractExpressionAST) {
            if (currentToken.getTokenContent().equals("this"))
                return true;
            else
                return syntaxAnalyzer.matchesPeekToken(1, SeparatorToken.SeparatorType.OPENING_PARENTHESIS) != null;
        } else {
            switch (currentToken.getTokenContent()) {
                case "val":
                case "this":
                case "import":
                case "fun":
                case "return":
                    return true;
            }
    
            return syntaxAnalyzer.matchesPeekToken(1, SeparatorToken.SeparatorType.OPENING_PARENTHESIS) != null;
        }
        return false;
    }
    
}