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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.CollectionOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.NumberOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.StringOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.Optional;

public class AbstractOperableSyntaxAST<O> extends AbstractSyntaxAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private O operableObject;
    
    
    public AbstractOperableSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer, @NotNull final ASTType astType) {
        super(syntaxAnalyzer, astType);
    }
    
    
    @Override
    public Optional<? extends AbstractOperableSyntaxAST<?>> parseAST(@NotNull final AbstractSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        final AbstractToken currentToken = this.getSyntaxAnalyzer().currentToken();
        switch (currentToken.getTokenType()) {
            case STRING_LITERAL:
                return StringOperableSyntaxAST
                        .builder(this.getSyntaxAnalyzer())
                        .build()
                        .parseAST(parentAST);
            case NUMBER_LITERAL:
                return NumberOperableSyntaxAST
                        .builder(this.getSyntaxAnalyzer())
                        .build()
                        .parseAST(parentAST);
            case SYMBOL:
                if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_BRACKET) == null) {
                    this.addError(
                            this.getSyntaxAnalyzer().getArkoiClass(),
                            currentToken,
                            SyntaxErrorType.OPERABLE_UNSUPPORTED_SYMBOL_TYPE
                    );
                    return Optional.empty();
                }
                return CollectionOperableSyntaxAST
                        .builder(this.getSyntaxAnalyzer())
                        .build()
                        .parseAST(parentAST);
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
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) { }
    
}
