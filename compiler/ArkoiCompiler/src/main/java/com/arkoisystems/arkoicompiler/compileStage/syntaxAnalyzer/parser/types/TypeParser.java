package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.types;

import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.TypeAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.Parser;

public class TypeParser extends Parser<TypeAST>
{
    
    @Override
    public TypeAST parse(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final TypeAST typeAST = new TypeAST().parseAST(parentAST, syntaxAnalyzer);
        if (typeAST != null)
            return typeAST;
        
        // TODO: 1/5/2020 Throw error
        return null;
    }
    
    @Override
    public boolean canParse(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return syntaxAnalyzer.currentToken().getTokenType() == TokenType.IDENTIFIER;
    }
    
    @Override
    public String childName() {
        return TypeAST.class.getSimpleName();
    }
    
}