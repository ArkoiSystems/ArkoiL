package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.types;

import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.ArgumentDefinitionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.Parser;

public class ArgumentDefinitionParser extends Parser<ArgumentDefinitionAST>
{
    
    @Override
    public ArgumentDefinitionAST parse(final AbstractAST<?> parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return new ArgumentDefinitionAST().parseAST(parentAST, syntaxAnalyzer);
    }
    
    @Override
    public boolean canParse(final AbstractAST<?> parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return syntaxAnalyzer.currentToken().getTokenType() == TokenType.IDENTIFIER && syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.COLON) != null;
    }
    
}