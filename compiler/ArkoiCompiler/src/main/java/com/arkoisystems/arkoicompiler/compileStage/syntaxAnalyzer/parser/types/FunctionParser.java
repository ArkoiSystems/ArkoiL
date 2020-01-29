package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.types;

import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.functionStatements.FunctionDefinitionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.Parser;

public class FunctionParser extends Parser<FunctionDefinitionAST>
{
    
    @Override
    public FunctionDefinitionAST parse(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final FunctionDefinitionAST functionDefinitionAST = new FunctionDefinitionAST().parseAST(parentAST, syntaxAnalyzer);
        if (functionDefinitionAST != null)
            return functionDefinitionAST;
        
        // TODO: 1/5/2020 Throw error
        return null;
    }
    
    @Override
    public boolean canParse(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return syntaxAnalyzer.currentToken().getTokenType() == TokenType.IDENTIFIER && syntaxAnalyzer.currentToken().getTokenContent().equals("fun");
    }
    
    @Override
    public String childName() {
        return FunctionDefinitionAST.class.getSimpleName();
    }
    
}