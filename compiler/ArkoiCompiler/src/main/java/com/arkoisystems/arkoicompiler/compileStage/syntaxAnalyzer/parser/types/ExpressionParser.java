package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.types;

import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.expression.AbstractExpressionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.AbstractOperableAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.Parser;

public class ExpressionParser extends Parser<AbstractExpressionAST>
{
    
    @Override
    public AbstractExpressionAST parse(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return new AbstractExpressionAST(null).parseAST(parentAST, syntaxAnalyzer);
    }
    
    @Override
    public boolean canParse(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return AbstractOperableAST.OPERABLE_PARSER.canParse(parentAST, syntaxAnalyzer);
    }
    
}