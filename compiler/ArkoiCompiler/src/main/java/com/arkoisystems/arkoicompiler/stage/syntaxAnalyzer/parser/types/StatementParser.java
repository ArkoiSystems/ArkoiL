package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierInvokeOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ThisStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.AbstractParser;
import lombok.Getter;

@Getter
public class StatementParser extends AbstractParser<AbstractSyntaxAST>
{
    
    @Override
    public AbstractSyntaxAST parse(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return new AbstractStatementSyntaxAST(null).parseAST(parentAST, syntaxAnalyzer);
    }
    
    @Override
    public boolean canParse(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final AbstractToken currentToken = syntaxAnalyzer.currentToken();
        if (syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER) == null)
            return false;
        
        if (parentAST instanceof ThisStatementSyntaxAST) {
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
        } else if (parentAST instanceof AbstractExpressionSyntaxAST) {
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
        } else if (parentAST instanceof IdentifierInvokeOperableSyntaxAST) {
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