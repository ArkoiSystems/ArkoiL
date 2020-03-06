/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.NumberToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;

import java.io.PrintStream;

public class NumberOperableSyntaxAST extends AbstractOperableSyntaxAST<NumberToken>
{
    
    public NumberOperableSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.NUMBER_OPERABLE);
    }
    
    
    @Override
    public NumberOperableSyntaxAST parseAST(final AbstractSyntaxAST parentAST) {
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.NUMBER_LITERAL) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.NUMBER_OPERABLE_NO_NUMBER
            );
            return null;
        }
        
        this.setOperableObject((NumberToken) this.getSyntaxAnalyzer().currentToken());
        this.setStart(this.getOperableObject().getStart());
        this.setEnd(this.getOperableObject().getEnd());
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(final PrintStream printStream, final String indents) {
        printStream.println(indents + "└── operable: " + this.getOperableObject().getTokenContent());
    }
    
}
