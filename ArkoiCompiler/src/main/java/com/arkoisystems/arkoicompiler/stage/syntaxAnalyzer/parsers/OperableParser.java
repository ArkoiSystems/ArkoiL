/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parsers;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.ISyntaxParser;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.StatementAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import org.jetbrains.annotations.NotNull;

public class OperableParser implements ISyntaxParser
{

    @Override
    public @NotNull
    IASTNode parse(@NotNull final IASTNode parentAST, @NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new OperableAST(syntaxAnalyzer, ASTType.OPERABLE)
                .parseAST(parentAST);
    }
    
    
    @Override
    public boolean canParse(@NotNull final IASTNode parentAST, @NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        switch (syntaxAnalyzer.currentToken().getTokenType()) {
            case STRING_LITERAL:
            case NUMBER_LITERAL:
                return true;
            case SYMBOL:
                return syntaxAnalyzer.matchesCurrentToken(SymbolType.OPENING_BRACKET) != null;
            case IDENTIFIER:
                return StatementAST.STATEMENT_PARSER.canParse(parentAST, syntaxAnalyzer);
            default:
                return false;
        }
    }
    
}