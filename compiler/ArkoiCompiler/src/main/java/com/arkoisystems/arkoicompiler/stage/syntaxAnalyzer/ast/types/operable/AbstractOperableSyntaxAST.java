/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
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
    
    public static OperableParser OPERABLE_PARSER = new OperableParser();
    
    
    @Getter
    @Setter
    private O operableObject;
    
    
    public AbstractOperableSyntaxAST(final ASTType astType) {
        super(astType);
    }
    
    
    @Override
    public AbstractOperableSyntaxAST<?> parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final AbstractToken currentToken = syntaxAnalyzer.currentToken();
        switch (currentToken.getTokenType()) {
            case STRING_LITERAL:
                return new StringOperableSyntaxAST().parseAST(parentAST, syntaxAnalyzer);
            case NUMBER_LITERAL:
                return new NumberOperableSyntaxAST().parseAST(parentAST, syntaxAnalyzer);
            case SYMBOL:
                if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.OPENING_BRACKET) != null)
                    return new CollectionOperableSyntaxAST().parseAST(parentAST, syntaxAnalyzer);
                else {
                    syntaxAnalyzer.errorHandler().addError(new TokenError(currentToken, "Couldn't parse the operable because the SymbolType isn't supported."));
                    return null;
                }
            case IDENTIFIER:
                if (!AbstractStatementSyntaxAST.STATEMENT_PARSER.canParse(parentAST, syntaxAnalyzer)) {
                    syntaxAnalyzer.errorHandler().addError(new ParserError<>(AbstractStatementSyntaxAST.STATEMENT_PARSER, parentAST.getStart(), syntaxAnalyzer.currentToken().getEnd(), "Couldn't parse the operable statement because it isn't parsable."));
                    return null;
                }
                
                final AbstractSyntaxAST abstractSyntaxAST = AbstractStatementSyntaxAST.STATEMENT_PARSER.parse(parentAST, syntaxAnalyzer);
                if (abstractSyntaxAST instanceof IdentifierCallOperableSyntaxAST)
                    return (IdentifierCallOperableSyntaxAST) abstractSyntaxAST;
                else if (abstractSyntaxAST instanceof FunctionInvokeOperableSyntaxAST)
                    return (FunctionInvokeOperableSyntaxAST) abstractSyntaxAST;
                else if (abstractSyntaxAST instanceof IdentifierInvokeOperableSyntaxAST)
                    return (IdentifierInvokeOperableSyntaxAST) abstractSyntaxAST;
                else if (abstractSyntaxAST != null) {
                    syntaxAnalyzer.errorHandler().addError(new SyntaxASTError<>(syntaxAnalyzer.getArkoiClass(), abstractSyntaxAST, "Couldn't parse the operable because it isn't a supported statement."));
                    return null;
                }
        }
        return null;
    }
    
    
    @Override
    public void printSyntaxAST(final PrintStream printStream, final String indents) { }
    
}
