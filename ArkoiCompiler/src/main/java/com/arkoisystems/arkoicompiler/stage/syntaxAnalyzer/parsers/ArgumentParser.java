/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parsers;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.ISyntaxParser;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.OperatorType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.ArgumentAST;
import org.jetbrains.annotations.NotNull;

public class ArgumentParser implements ISyntaxParser
{
    
    @NotNull
    @Override
    public ArgumentAST parse(@NotNull final IASTNode parentAST, @NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return ArgumentAST
                .builder(syntaxAnalyzer)
                .build()
                .parseAST(parentAST);
    }
    
    
    @Override
    public boolean canParse(@NotNull final IASTNode parentAST, @NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return syntaxAnalyzer.currentToken().getTokenType() == TokenType.IDENTIFIER && syntaxAnalyzer.matchesPeekToken(1, OperatorType.EQUALS) != null;
    }
    
}