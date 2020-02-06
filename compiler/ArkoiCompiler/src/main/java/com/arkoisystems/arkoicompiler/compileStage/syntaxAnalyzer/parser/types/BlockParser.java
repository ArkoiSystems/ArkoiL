package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.types;

import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.BlockAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.Parser;

public class BlockParser extends Parser<BlockAST>
{
    
    @Override
    public BlockAST parse(final AbstractAST<?> parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return new BlockAST().parseAST(parentAST, syntaxAnalyzer);
    }
    
    @Override
    public boolean canParse(final AbstractAST<?> parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.OPENING_BRACE) != null || syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.EQUAL) != null;
    }
    
}