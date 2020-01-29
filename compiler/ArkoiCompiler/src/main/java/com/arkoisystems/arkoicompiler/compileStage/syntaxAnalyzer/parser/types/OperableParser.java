package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.types;

import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operables.AbstractOperableAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.Parser;

public class OperableParser extends Parser<AbstractOperableAST<?>>
{
    
    @Override
    public AbstractOperableAST<?> parse(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final AbstractOperableAST<?> abstractOperableAST = new AbstractOperableAST<>(null).parseAST(parentAST, syntaxAnalyzer);
        if (abstractOperableAST != null)
            return abstractOperableAST;
        
        // TODO: 1/5/2020 Throw error
        return null;
    }
    
    @Override
    public boolean canParse(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final AbstractToken currentToken = syntaxAnalyzer.currentToken();
        switch (currentToken.getTokenType()) {
            case STRING:
            case NUMBER:
            case IDENTIFIER:
                return true;
            default:
                return false;
        }
    }
    
    @Override
    public String childName() {
        return AbstractOperableAST.class.getSimpleName();
    }
    
}