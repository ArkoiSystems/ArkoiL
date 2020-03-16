/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.PostfixOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.PrintStream;
import java.util.Optional;

public class PostfixExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    @Getter
    private final PostfixOperatorType postfixOperatorType;
    
    
    @Getter
    private final AbstractOperableSyntaxAST<?> leftSideOperable;
    
    
    public PostfixExpressionSyntaxAST(@NonNull final SyntaxAnalyzer syntaxAnalyzer, @NonNull final AbstractOperableSyntaxAST<?> leftSideOperable, @NonNull final PostfixOperatorType postfixOperatorType) {
        super(syntaxAnalyzer, ASTType.POSTFIX_EXPRESSION);
        
        this.postfixOperatorType = postfixOperatorType;
        this.leftSideOperable = leftSideOperable;
        
        this.setStart(this.leftSideOperable.getStart());
    }
    
    
    @Override
    public Optional<? extends AbstractOperableSyntaxAST<?>> parseAST(@NonNull final AbstractSyntaxAST parentAST) {
        if (this.getPostfixOperatorType() == PostfixOperatorType.POSTFIX_ADD) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PLUS) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        "Couldn't parse the postfix add expression because it doesn't start with a plus."
                );
                return Optional.empty();
            }
            this.getSyntaxAnalyzer().nextToken();
            
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PLUS, false) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        "Couldn't parse the postfix add expression because the first operator isn't followed by a plus."
                );
                return Optional.empty();
            }
            this.getSyntaxAnalyzer().nextToken();
            this.setEnd(this.getSyntaxAnalyzer().currentToken().getEnd());
        } else if (this.getPostfixOperatorType() == PostfixOperatorType.POSTFIX_SUB) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.MINUS) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        "Couldn't parse the postfix sub expression because it doesn't start with a plus."
                );
                return Optional.empty();
            }
            this.getSyntaxAnalyzer().nextToken();
            
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.MINUS, false) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        "Couldn't parse the postfix sub expression because the first operator isn't followed by a minus."
                );
                return Optional.empty();
            }
            this.getSyntaxAnalyzer().nextToken();
            this.setEnd(this.getSyntaxAnalyzer().currentToken().getEnd());
        }
        return Optional.of(this);
    }
    
    
    @Override
    public void printSyntaxAST(@NonNull final PrintStream printStream, @NonNull final String indents) {
        printStream.println(indents + "├── left:");
        printStream.println(indents + "│   └── " + this.getLeftSideOperable().getClass().getSimpleName());
        this.getLeftSideOperable().printSyntaxAST(printStream, indents + "│       ");
        printStream.println(indents + "└── operator: " + this.getPostfixOperatorType());
    }
    
}
