/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.types.OperableParser;
import lombok.Getter;
import lombok.Setter;

import java.io.PrintStream;

public class AbstractOperableSyntaxAST<O> extends AbstractSyntaxAST
{
    
    @Getter
    @Setter
    private O operableObject;
    
    
    public AbstractOperableSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer, final ASTType astType) {
        super(syntaxAnalyzer, astType);
    }
    
    
    @Override
    public AbstractOperableSyntaxAST<?> parseAST(final AbstractSyntaxAST parentAST) {
        final AbstractToken currentToken = this.getSyntaxAnalyzer().currentToken();
        switch (currentToken.getTokenType()) {
            case STRING_LITERAL:
                return new StringOperableSyntaxAST(this.getSyntaxAnalyzer()).parseAST(parentAST);
            case NUMBER_LITERAL:
                return new NumberOperableSyntaxAST(this.getSyntaxAnalyzer()).parseAST(parentAST);
            case SYMBOL:
                if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_BRACKET) == null) {
                    this.addError(
                            this.getSyntaxAnalyzer().getArkoiClass(),
                            currentToken,
                            SyntaxErrorType.OPERABLE_UNSUPPORTED_SYMBOL_TYPE
                    );
                    return null;
                }
                return new CollectionOperableSyntaxAST(this.getSyntaxAnalyzer()).parseAST(parentAST);
            case IDENTIFIER:
                if (!AbstractStatementSyntaxAST.STATEMENT_PARSER.canParse(parentAST, this.getSyntaxAnalyzer())) {
                    this.addError(
                            this.getSyntaxAnalyzer().getArkoiClass(),
                            currentToken,
                            SyntaxErrorType.OPERABLE_IDENTIFIER_NOT_PARSEABLE
                    );
                    return null;
                }
                
                final AbstractSyntaxAST abstractSyntaxAST = AbstractStatementSyntaxAST.STATEMENT_PARSER.parse(parentAST, this.getSyntaxAnalyzer());
                if (abstractSyntaxAST instanceof IdentifierCallOperableSyntaxAST)
                    return (IdentifierCallOperableSyntaxAST) abstractSyntaxAST;
                else if (abstractSyntaxAST instanceof FunctionInvokeOperableSyntaxAST)
                    return (FunctionInvokeOperableSyntaxAST) abstractSyntaxAST;
                else if (abstractSyntaxAST instanceof IdentifierInvokeOperableSyntaxAST)
                    return (IdentifierInvokeOperableSyntaxAST) abstractSyntaxAST;
                else if (abstractSyntaxAST != null) {
                    this.addError(
                            this.getSyntaxAnalyzer().getArkoiClass(),
                            abstractSyntaxAST,
                            SyntaxErrorType.OPERABLE_UNSUPPORTED_STATEMENT
                    );
                    return null;
                }
        }
        return null;
    }
    
    
    @Override
    public void printSyntaxAST(final PrintStream printStream, final String indents) { }
    
}
