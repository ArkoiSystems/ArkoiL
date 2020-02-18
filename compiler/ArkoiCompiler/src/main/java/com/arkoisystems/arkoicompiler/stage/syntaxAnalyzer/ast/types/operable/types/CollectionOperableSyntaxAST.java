/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.ArgumentDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.google.gson.annotations.Expose;
import lombok.Getter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CollectionOperableSyntaxAST extends AbstractOperableSyntaxAST<AbstractExpressionSyntaxAST[]>
{
    
    @Expose
    private final List<ExpressionSyntaxAST> collectionExpressions;
    
    
    public CollectionOperableSyntaxAST() {
        super(ASTType.COLLECTION_OPERABLE);
        
        this.collectionExpressions = new ArrayList<>();
    }
    
    
    @Override
    public AbstractOperableSyntaxAST<?> parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.OPENING_BRACKET) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the collection operable because the parsing doesn't start with an opening bracket."));
            return null;
        } else syntaxAnalyzer.nextToken();
        
        while (syntaxAnalyzer.getPosition() < syntaxAnalyzer.getTokens().length) {
            if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.CLOSING_BRACKET) != null)
                break;
            
            if (!AbstractExpressionSyntaxAST.EXPRESSION_PARSER.canParse(this, syntaxAnalyzer)) {
                syntaxAnalyzer.errorHandler().addError(new ParserError<>(AbstractExpressionSyntaxAST.EXPRESSION_PARSER, syntaxAnalyzer.currentToken(), "Couldn't parse the collection operable because there is an invalid expression inside."));
                return null;
            }
            
            final ExpressionSyntaxAST abstractExpressionAST = AbstractExpressionSyntaxAST.EXPRESSION_PARSER.parse(this, syntaxAnalyzer);
            if (abstractExpressionAST == null) {
                syntaxAnalyzer.errorHandler().addError(new SyntaxASTError<>(this, "Couldn't parse the collection operable because there occurred an error while parsing the expression inside it."));
                return null;
            } else this.collectionExpressions.add(abstractExpressionAST);
    
            if (syntaxAnalyzer.matchesNextToken(SymbolToken.SymbolType.COMMA) != null)
                syntaxAnalyzer.nextToken();
        }
        
        if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.CLOSING_BRACKET) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the collection operable because it doesn't end with an closing bracket."));
            return null;
        }
        return this;
    }
    
    
    @Override
    public void printAST(final PrintStream printStream, final String indents) {
        printStream.println(indents + "└── expressions: " + (this.getCollectionExpressions().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getCollectionExpressions().size(); index++) {
            final ExpressionSyntaxAST abstractSyntaxAST = this.getCollectionExpressions().get(index);
            if (index == this.getCollectionExpressions().size() - 1) {
                printStream.println(indents + "    │   ");
                printStream.println(indents + "    └── " + abstractSyntaxAST.getExpressionOperable().getClass().getSimpleName());
                abstractSyntaxAST.printAST(printStream, indents + "        ");
            } else {
                printStream.println(indents + "    │   ");
                printStream.println(indents + "    ├── " + abstractSyntaxAST.getExpressionOperable().getClass().getSimpleName());
                abstractSyntaxAST.printAST(printStream, indents + "    │   ");
            }
        }
    }
    
}

