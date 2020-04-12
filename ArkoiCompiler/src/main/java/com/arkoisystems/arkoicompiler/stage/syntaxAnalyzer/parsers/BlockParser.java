/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parsers;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.ISyntaxParser;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.OperatorType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.BlockAST;
import org.jetbrains.annotations.NotNull;

public class BlockParser implements ISyntaxParser
{
    
    @NotNull
    @Override
    public BlockAST parse(@NotNull final IASTNode parentAST, @NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return BlockAST
                .builder(syntaxAnalyzer)
                .build()
                .parseAST(parentAST);
    }
    
    
    @Override
    public boolean canParse(@NotNull final IASTNode parentAST, @NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return syntaxAnalyzer.matchesCurrentToken(SymbolType.OPENING_BRACE) != null || syntaxAnalyzer.matchesCurrentToken(OperatorType.EQUALS) != null;
    }
    
}