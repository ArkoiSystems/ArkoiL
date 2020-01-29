package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.types;

import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SeparatorToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.AnnotationAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.TypeAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.Parser;

public class AnnotationParser extends Parser<AnnotationAST>
{
    
    @Override
    public AnnotationAST parse(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final AnnotationAST annotationAST = new AnnotationAST().parseAST(parentAST, syntaxAnalyzer);
        if (annotationAST != null)
            return annotationAST;
        
        // TODO: 1/5/2020 Throw error
        return null;
    }
    
    @Override
    public boolean canParse(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return syntaxAnalyzer.matchesCurrentToken(SeparatorToken.SeparatorType.AT_SIGN) != null;
    }
    
    @Override
    public String childName() {
        return AnnotationAST.class.getSimpleName();
    }
    
}