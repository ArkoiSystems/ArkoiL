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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.BinaryOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.PrintStream;
import java.util.Optional;

@Getter
public class BinaryExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    @Getter
    private final AbstractOperableSyntaxAST<?> leftSideOperable;
    
    
    @Getter
    private final BinaryOperatorType binaryOperatorType;
    
    
    @Getter
    @Setter
    private AbstractOperableSyntaxAST<?> rightSideOperable;
    
    
    public BinaryExpressionSyntaxAST(@NonNull final SyntaxAnalyzer syntaxAnalyzer, @NonNull final AbstractOperableSyntaxAST<?> leftSideOperable, @NonNull final BinaryOperatorType binaryOperatorType) {
        super(syntaxAnalyzer, ASTType.BINARY_EXPRESSION);
        
        this.binaryOperatorType = binaryOperatorType;
        this.leftSideOperable = leftSideOperable;
        
        this.setStart(this.leftSideOperable.getStart());
    }
    
    
    @Override
    public Optional<? extends AbstractOperableSyntaxAST<?>> parseAST(@NonNull final AbstractSyntaxAST parentAST) {
        if (this.getBinaryOperatorType() == BinaryOperatorType.ADDITION) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PLUS) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken().getStart(),
                        this.getSyntaxAnalyzer().peekToken(1).getEnd(),
                        "Couldn't parse the binary add because it doesn't start with a plus."
                );
                return Optional.empty();
            }
        } else if (this.getBinaryOperatorType() == BinaryOperatorType.SUBTRACTION) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.MINUS) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken().getStart(),
                        this.getSyntaxAnalyzer().peekToken(1).getEnd(),
                        "Couldn't parse the binary sub because it doesn't start with a minus."
                );
                return Optional.empty();
            }
        } else if (this.getBinaryOperatorType() == BinaryOperatorType.MULTIPLICATION) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.ASTERISK) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken().getStart(),
                        this.getSyntaxAnalyzer().peekToken(1).getEnd(),
                        "Couldn't parse the binary mul because it doesn't start with an asterisk."
                );
                return Optional.empty();
            }
        } else if (this.getBinaryOperatorType() == BinaryOperatorType.DIVISION) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.SLASH) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken().getStart(),
                        this.getSyntaxAnalyzer().peekToken(1).getEnd(),
                        "Couldn't parse the binary div because it doesn't start with a slash."
                );
                return Optional.empty();
            }
        } else if (this.getBinaryOperatorType() == BinaryOperatorType.MODULO) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PERCENT) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken().getStart(),
                        this.getSyntaxAnalyzer().peekToken(1).getEnd(),
                        "Couldn't parse the binary mod because it doesn't start with a percent."
                );
                return Optional.empty();
            }
        } else if (this.getBinaryOperatorType() == BinaryOperatorType.EXPONENTIAL) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.ASTERISK) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken().getStart(),
                        this.getSyntaxAnalyzer().peekToken(1).getEnd(),
                        "Couldn't parse the exponential expression because it doesn't start with an asterisk."
                );
                return Optional.empty();
            }
        }
        this.getSyntaxAnalyzer().nextToken();
    
        if(this.getBinaryOperatorType() == BinaryOperatorType.EXPONENTIAL) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.ASTERISK, false) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken().getStart(),
                        this.getSyntaxAnalyzer().peekToken(1).getEnd(),
                        "Couldn't parse the exponential expression because the operator isn't followed by an asterisk."
                );
                return Optional.empty();
            }
            this.getSyntaxAnalyzer().nextToken(2);
        } else {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.EQUAL, false) != null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken().getStart(),
                        this.getSyntaxAnalyzer().peekToken(1, false).getEnd(),
                        "Couldn't parse the binary expression because the operator isn't followed by an equal sign."
                );
                return Optional.empty();
            }
            this.getSyntaxAnalyzer().nextToken(1);
        }
        
        final Optional<? extends AbstractOperableSyntaxAST<?>> optionalRightSideAST = this.parseMultiplicative(parentAST);
        if (optionalRightSideAST.isEmpty())
            return Optional.empty();
        this.rightSideOperable = optionalRightSideAST.get();
        this.setEnd(this.rightSideOperable.getEnd());
        return Optional.of(this);
    }
    
    
    @Override
    public void printSyntaxAST(@NonNull final PrintStream printStream, @NonNull final String indents) {
        printStream.println(indents + "├── left:");
        printStream.println(indents + "│   └── " + this.getLeftSideOperable().getClass().getSimpleName());
        this.getLeftSideOperable().printSyntaxAST(printStream, indents + "│        ");
        printStream.println(indents + "├── operator: " + this.getBinaryOperatorType());
        printStream.println(indents + "└── right:");
        printStream.println(indents + "    └── " + this.getRightSideOperable().getClass().getSimpleName());
        this.getRightSideOperable().printSyntaxAST(printStream, indents + "        ");
    }
    
}
