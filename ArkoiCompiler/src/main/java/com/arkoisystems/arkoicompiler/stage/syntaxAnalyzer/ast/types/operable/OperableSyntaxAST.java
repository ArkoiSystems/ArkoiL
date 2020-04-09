/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable;

import com.arkoisystems.arkoicompiler.api.ICompilerSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.CollectionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.NumberSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.StringSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;

public class OperableSyntaxAST extends ArkoiSyntaxAST
{
    
    
    public OperableSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer, @NotNull final ASTType astType) {
        super(syntaxAnalyzer, astType);
    }
    
    
    @NotNull
    @Override
    public OperableSyntaxAST parseAST(@NotNull final ICompilerSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        final AbstractToken currentToken = this.getSyntaxAnalyzer().currentToken();
        switch (currentToken.getTokenType()) {
            case STRING_LITERAL:
                return StringSyntaxAST
                        .builder(this.getSyntaxAnalyzer())
                        .build()
                        .parseAST(parentAST);
            case NUMBER_LITERAL:
                return NumberSyntaxAST
                        .builder(this.getSyntaxAnalyzer())
                        .build()
                        .parseAST(parentAST);
            case SYMBOL:
                if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_BRACKET) == null) {
                    return this.addError(
                            this,
                            this.getSyntaxAnalyzer().getCompilerClass(),
                            currentToken,
                            SyntaxErrorType.OPERABLE_UNSUPPORTED_SYMBOL_TYPE
                    );
                }
                return CollectionSyntaxAST
                        .builder(this.getSyntaxAnalyzer())
                        .build()
                        .parseAST(parentAST);
            case IDENTIFIER:
                if (!AbstractStatementSyntaxAST.STATEMENT_PARSER.canParse(parentAST, this.getSyntaxAnalyzer())) {
                    return this.addError(
                            this,
                            this.getSyntaxAnalyzer().getCompilerClass(),
                            currentToken,
                            SyntaxErrorType.OPERABLE_IDENTIFIER_NOT_PARSEABLE
                    );
                }
                
                final ICompilerSyntaxAST syntaxAST = AbstractStatementSyntaxAST.STATEMENT_PARSER.parse(parentAST, this.getSyntaxAnalyzer());
                this.getMarkerFactory().addFactory(syntaxAST.getMarkerFactory());
                
                if (!(syntaxAST instanceof IdentifierCallSyntaxAST)) {
                    return this.addError(
                            this,
                            this.getSyntaxAnalyzer().getCompilerClass(),
                            syntaxAST,
                            SyntaxErrorType.OPERABLE_UNSUPPORTED_STATEMENT
                    );
                } else {
                    if (syntaxAST.isFailed())
                        this.failed();
                    return (IdentifierCallSyntaxAST) syntaxAST;
                }
            case KEYWORD:
                if (this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.THIS) == null) {
                    return this.addError(
                            this,
                            this.getSyntaxAnalyzer().getCompilerClass(),
                            currentToken,
                            "Couldn't parse the operable because the keyword is not supported."
                    );
                }
                
                return IdentifierCallSyntaxAST
                        .builder(this.getSyntaxAnalyzer())
                        .build()
                        .parseAST(parentAST);
            default:
                return this.addError(
                        this,
                        this.getSyntaxAnalyzer().getCompilerClass(),
                        currentToken,
                        "Couldn't parse the operable because it isn't supported."
                );
        }
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) { }
    
}
