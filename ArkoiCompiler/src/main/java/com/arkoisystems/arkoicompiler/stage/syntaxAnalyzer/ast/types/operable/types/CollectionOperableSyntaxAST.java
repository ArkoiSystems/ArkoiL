/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ParenthesizedExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CollectionOperableSyntaxAST extends AbstractOperableSyntaxAST<TypeKind>
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private List<ExpressionSyntaxAST> collectionExpressions;
    
    
    protected CollectionOperableSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.COLLECTION_OPERABLE);
    }
    
    
    @Override
    public Optional<CollectionOperableSyntaxAST> parseAST(@NonNull final AbstractSyntaxAST parentAST) {
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_BRACKET) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.COLLECTION_OPERABLE_WRONG_START
            );
            return Optional.empty();
        }
        
        this.setStart(this.getSyntaxAnalyzer().currentToken().getStart());
        this.getSyntaxAnalyzer().nextToken();
        
        while (this.getSyntaxAnalyzer().getPosition() < this.getSyntaxAnalyzer().getTokens().length) {
            if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACKET) != null)
                break;
            
            if (!AbstractExpressionSyntaxAST.EXPRESSION_PARSER.canParse(this, this.getSyntaxAnalyzer())) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        SyntaxErrorType.COLLECTION_OPERABLE_INVALID_EXPRESSION
                );
                return Optional.empty();
            }
            
            final Optional<ExpressionSyntaxAST> optionalExpressionSyntaxAST = AbstractExpressionSyntaxAST.EXPRESSION_PARSER.parse(this, this.getSyntaxAnalyzer());
            if (optionalExpressionSyntaxAST.isEmpty())
                return Optional.empty();
            this.collectionExpressions.add(optionalExpressionSyntaxAST.get());
            
            if (this.getSyntaxAnalyzer().matchesNextToken(SymbolType.COMMA) != null)
                this.getSyntaxAnalyzer().nextToken();
        }
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACKET) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.COLLECTION_OPERABLE_WRONG_ENDING
            );
            return Optional.empty();
        }
        
        this.setEnd(this.getSyntaxAnalyzer().currentToken().getEnd());
        return Optional.of(this);
    }
    
    
    @Override
    public void printSyntaxAST(@NonNull final PrintStream printStream, @NonNull final String indents) {
        printStream.println(indents + "└── expressions: " + (this.getCollectionExpressions().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getCollectionExpressions().size(); index++) {
            final ExpressionSyntaxAST abstractSyntaxAST = this.getCollectionExpressions().get(index);
            if (index == this.getCollectionExpressions().size() - 1) {
                printStream.println(indents + "    └── " + abstractSyntaxAST.getExpressionOperable().getClass().getSimpleName());
                abstractSyntaxAST.printSyntaxAST(printStream, indents + "        ");
            } else {
                printStream.println(indents + "    ├── " + abstractSyntaxAST.getExpressionOperable().getClass().getSimpleName());
                abstractSyntaxAST.printSyntaxAST(printStream, indents + "    │   ");
                printStream.println(indents + "    │   ");
            }
        }
    }
    
    
    public static CollectionOperableSyntaxASTBuilder builder(final SyntaxAnalyzer syntaxAnalyzer) {
        return new CollectionOperableSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static CollectionOperableSyntaxASTBuilder builder() {
        return new CollectionOperableSyntaxASTBuilder();
    }
    
    
    public static class CollectionOperableSyntaxASTBuilder {
        
        private final SyntaxAnalyzer syntaxAnalyzer;
    
    
        private List<ExpressionSyntaxAST> collectionExpressions;
        
        
        private int start, end;
        
        
        public CollectionOperableSyntaxASTBuilder(SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
            
            this.collectionExpressions = new ArrayList<>();
        }
        
        
        public CollectionOperableSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
            
            this.collectionExpressions = new ArrayList<>();
        }
        
        
        public CollectionOperableSyntaxASTBuilder expressions(final List<ExpressionSyntaxAST> collectionExpressions) {
            this.collectionExpressions = collectionExpressions;
            return this;
        }
        
        
        public CollectionOperableSyntaxASTBuilder start(final int start) {
            this.start = start;
            return this;
        }
        
        
        public CollectionOperableSyntaxASTBuilder end(final int end) {
            this.end = end;
            return this;
        }
        
        
        public CollectionOperableSyntaxAST build() {
            final CollectionOperableSyntaxAST collectionOperableSyntaxAST = new CollectionOperableSyntaxAST(this.syntaxAnalyzer);
            collectionOperableSyntaxAST.setCollectionExpressions(this.collectionExpressions);
            collectionOperableSyntaxAST.setStart(this.start);
            collectionOperableSyntaxAST.setEnd(this.end);
            return collectionOperableSyntaxAST;
        }
        
    }
    
}

