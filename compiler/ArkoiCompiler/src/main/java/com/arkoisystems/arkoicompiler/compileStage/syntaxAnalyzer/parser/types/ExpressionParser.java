package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.types;

import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.expressions.AbstractExpressionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operables.AbstractOperableAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.functionStatements.FunctionDefinitionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.Parser;

public class ExpressionParser extends Parser<AbstractExpressionAST>
{
    
    @Override
    public AbstractExpressionAST parse(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final AbstractExpressionAST abstractExpressionAST = new AbstractExpressionAST().parseAST(parentAST, syntaxAnalyzer);
        if (abstractExpressionAST != null)
            return abstractExpressionAST;
        
        // TODO: 1/5/2020 Throw error
        return null;
    }
    
    @Override
    public boolean canParse(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return AbstractOperableAST.OPERABLE_PARSER.canParse(parentAST, syntaxAnalyzer);
    }
    
    @Override
    public String childName() {
        return FunctionDefinitionAST.class.getSimpleName();
    }
    
}