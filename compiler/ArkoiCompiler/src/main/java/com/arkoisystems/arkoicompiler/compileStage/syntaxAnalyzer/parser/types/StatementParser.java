package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.types;

import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.expression.AbstractExpressionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.AbstractStatementAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.IdentifierInvokeAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.ThisStatementAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.Parser;
import lombok.Getter;

@Getter
public class StatementParser extends Parser<AbstractStatementAST>
{
    
    @Override
    public AbstractStatementAST parse(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return new AbstractStatementAST(null).parseAST(parentAST, syntaxAnalyzer);
    }
    
    @Override
    public boolean canParse(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final AbstractToken currentToken = syntaxAnalyzer.currentToken();
        if (syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER) == null)
            return false;
        
        if (parentAST instanceof ThisStatementAST) {
            switch (currentToken.getTokenContent()) {
                case "var":
                case "fun":
                case "import":
                case "this":
                case "return":
                    return false;
                default:
                    return true;
            }
        } else if (parentAST instanceof AbstractExpressionAST) {
            switch (currentToken.getTokenContent()) {
                case "var":
                case "fun":
                case "import":
                case "return":
                    return false;
                case "this":
                default:
                    return true;
            }
        } else if (parentAST instanceof IdentifierInvokeAST) {
            switch (currentToken.getTokenContent()) {
                case "var":
                case "fun":
                case "import":
                case "return":
                case "this":
                    return false;
                default:
                    return true;
            }
        } else {
            switch (currentToken.getTokenContent()) {
                case "var":
                case "this":
                case "import":
                case "fun":
                case "return":
                default:
                    return true;
            }
        }
    }
    
}