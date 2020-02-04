package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.types;

import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.AnnotationAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.Parser;

public class AnnotationParser extends Parser<AnnotationAST>
{
    
    @Override
    public AnnotationAST parse(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return new AnnotationAST().parseAST(parentAST, syntaxAnalyzer);
    }
    
    @Override
    public boolean canParse(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.AT_SIGN) != null;
    }
    
}