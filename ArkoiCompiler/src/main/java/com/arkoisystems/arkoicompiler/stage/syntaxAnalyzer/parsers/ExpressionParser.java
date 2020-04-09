/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parsers;

import com.arkoisystems.arkoicompiler.api.ICompilerSyntaxAST;
import com.arkoisystems.arkoicompiler.api.ISyntaxParser;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.KeywordToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.OperatorToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import org.jetbrains.annotations.NotNull;

public class ExpressionParser implements ISyntaxParser
{
    
    @NotNull
    @Override
    public OperableSyntaxAST parse(@NotNull final ICompilerSyntaxAST parentAST, @NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new ExpressionSyntaxAST(syntaxAnalyzer, ASTType.EXPRESSION)
                .parseAST(parentAST);
    }
    
    
    @Override
    public boolean canParse(@NotNull final ICompilerSyntaxAST parentAST, @NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        switch (syntaxAnalyzer.currentToken().getTokenType()) {
            case STRING_LITERAL:
            case NUMBER_LITERAL:
                return true;
            case KEYWORD:
                final KeywordToken keywordToken = (KeywordToken) syntaxAnalyzer.currentToken();
                return keywordToken.getKeywordType() == KeywordType.THIS;
            case OPERATOR:
                final OperatorToken operatorToken = (OperatorToken) syntaxAnalyzer.currentToken();
                switch (operatorToken.getOperatorType()) {
                    case PLUS:
                    case MINUS:
                        return true;
                    default:
                        return false;
                }
            case SYMBOL:
                final SymbolToken symbolToken = (SymbolToken) syntaxAnalyzer.currentToken();
                return symbolToken.getSymbolType() == SymbolType.OPENING_PARENTHESIS;
            case IDENTIFIER:
                return AbstractStatementSyntaxAST.STATEMENT_PARSER.canParse(parentAST, syntaxAnalyzer);
            default:
                return false;
        }
    }
    
}