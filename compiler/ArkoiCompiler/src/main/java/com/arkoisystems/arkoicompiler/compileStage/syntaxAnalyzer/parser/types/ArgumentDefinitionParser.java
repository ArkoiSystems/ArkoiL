package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.types;

import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.ArgumentDefinitionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.Parser;

public class ArgumentDefinitionParser extends Parser<ArgumentDefinitionAST>
{
    
    @Override
    public ArgumentDefinitionAST parse(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final ArgumentDefinitionAST VariableStatementAST = new ArgumentDefinitionAST().parseAST(parentAST, syntaxAnalyzer);
        if(VariableStatementAST != null)
            return VariableStatementAST;
    
        // TODO: 1/5/2020 Throw error
        return null;
    }
    
    @Override
    public boolean canParse(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return syntaxAnalyzer.currentToken().getTokenType() == TokenType.IDENTIFIER && syntaxAnalyzer.peekToken(1).getTokenType() == TokenType.SEPARATOR;
    }
    
    @Override
    public String childName() {
        return ArgumentDefinitionAST.class.getSimpleName();
    }
    
}