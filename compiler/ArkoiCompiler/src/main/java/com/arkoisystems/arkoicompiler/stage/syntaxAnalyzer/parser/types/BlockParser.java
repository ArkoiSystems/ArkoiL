/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.BlockSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.AbstractParser;

public class BlockParser extends AbstractParser<BlockSyntaxAST>
{
    
    @Override
    public BlockSyntaxAST parse(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return new BlockSyntaxAST().parseAST(parentAST, syntaxAnalyzer);
    }
    
    @Override
    public boolean canParse(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.OPENING_BRACE) != null || syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.EQUAL) != null;
    }
    
}