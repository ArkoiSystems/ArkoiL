/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parsers;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.ISyntaxParser;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.ExpressionAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.StatementAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import org.jetbrains.annotations.NotNull;

public class StatementParser implements ISyntaxParser
{

    @Override
    public @NotNull
    IASTNode parse(@NotNull final IASTNode parentAST, @NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new StatementAST(syntaxAnalyzer, ASTType.STATEMENT)
                .parseAST(parentAST);
    }
    
    
    @Override
    public boolean canParse(@NotNull final IASTNode parentAST, @NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        final AbstractToken currentToken = syntaxAnalyzer.currentToken();
        if (parentAST instanceof ExpressionAST) {
            switch (currentToken.getTokenContent()) {
                case "var":
                case "fun":
                case "import":
                case "return":
                    return false;
                default:
                    return true;
            }
        } else {
            switch (currentToken.getTokenContent()) {
                case "var":
                case "this":
                case "import":
                case "fun":
                case "return":
                default:
                    return true;
            }
        }
    }
    
}