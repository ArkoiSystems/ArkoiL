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
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.PrintStream;
import java.util.Optional;

public class AbstractOperableSyntaxAST<O> extends AbstractSyntaxAST
{
    
    @Getter
    @Setter
    private O operableObject;
    
    
    public AbstractOperableSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer, final ASTType astType) {
        super(syntaxAnalyzer, astType);
    }
    
    
    @Override
    public Optional<? extends AbstractOperableSyntaxAST<?>> parseAST(@NonNull final AbstractSyntaxAST parentAST) {
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
                    return Optional.empty();
                }
                return new CollectionOperableSyntaxAST(this.getSyntaxAnalyzer()).parseAST(parentAST);
            case IDENTIFIER:
                if (!AbstractStatementSyntaxAST.STATEMENT_PARSER.canParse(parentAST, this.getSyntaxAnalyzer())) {
                    this.addError(
                            this.getSyntaxAnalyzer().getArkoiClass(),
                            currentToken,
                            SyntaxErrorType.OPERABLE_IDENTIFIER_NOT_PARSEABLE
                    );
                    return Optional.empty();
                }
                
                final Optional<? extends AbstractSyntaxAST> optionalAbstractSyntaxAST = AbstractStatementSyntaxAST.STATEMENT_PARSER.parse(parentAST, this.getSyntaxAnalyzer());
                if (optionalAbstractSyntaxAST.isEmpty())
                    return Optional.empty();
                
                final AbstractSyntaxAST abstractSyntaxAST = optionalAbstractSyntaxAST.get();
                if (abstractSyntaxAST instanceof IdentifierCallOperableSyntaxAST)
                    return Optional.of((IdentifierCallOperableSyntaxAST) abstractSyntaxAST);
                else if (abstractSyntaxAST instanceof FunctionInvokeOperableSyntaxAST)
                    return Optional.of((FunctionInvokeOperableSyntaxAST) abstractSyntaxAST);
                else if (abstractSyntaxAST instanceof IdentifierInvokeOperableSyntaxAST)
                    return Optional.of((IdentifierInvokeOperableSyntaxAST) abstractSyntaxAST);
                else {
                    this.addError(
                            this.getSyntaxAnalyzer().getArkoiClass(),
                            optionalAbstractSyntaxAST.get(),
                            SyntaxErrorType.OPERABLE_UNSUPPORTED_STATEMENT
                    );
                    return Optional.empty();
                }
        }
        return Optional.empty();
    }
    
    
    @Override
    public void printSyntaxAST(@NonNull final PrintStream printStream, @NonNull final String indents) { }
    
}
