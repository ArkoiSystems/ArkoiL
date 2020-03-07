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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.operators.PrefixOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.PrintStream;
import java.util.Optional;

public class PrefixExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    @Getter
    private final PrefixOperatorType prefixOperatorType;
    
    
    @Getter
    @Setter
    private AbstractOperableSyntaxAST<?> rightSideOperable;
    
    
    public PrefixExpressionSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer, final PrefixOperatorType prefixOperatorType) {
        super(syntaxAnalyzer, ASTType.PREFIX_EXPRESSION);
        
        this.prefixOperatorType = prefixOperatorType;
    }
    
    @Override
    public Optional<? extends AbstractOperableSyntaxAST<?>> parseAST(@NonNull AbstractSyntaxAST parentAST) {
        if(this.getPrefixOperatorType() == PrefixOperatorType.PREFIX_SUB || this.getPrefixOperatorType() == PrefixOperatorType.NEGATE) {
            if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.MINUS) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        "Couldn't parse the %s expression because it doesn't start with a minus.",
                        this.getPrefixOperatorType() == PrefixOperatorType.PREFIX_SUB ? "pre-sub" : "negate"
                );
                return Optional.empty();
            }
        } else if(this.getPrefixOperatorType() == PrefixOperatorType.PREFIX_ADD || this.getPrefixOperatorType() == PrefixOperatorType.AFFIRM) {
            if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.PLUS) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        "Couldn't parse the pre-add expression because it doesn't start with a minus.",
                        this.getPrefixOperatorType() == PrefixOperatorType.PREFIX_ADD ? "pre-add" : "affirm"
                );
                return Optional.empty();
            }
        }
        this.setStart(this.getSyntaxAnalyzer().currentToken().getStart());
        
        if(this.getPrefixOperatorType() == PrefixOperatorType.PREFIX_SUB) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.MINUS, false) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getStart(),
                        this.getSyntaxAnalyzer().currentToken().getEnd(),
                        "Couldn't parse the pre-sub expression because it the first minus isn't directly followed by a next minus."
                );
                return Optional.empty();
            }
            this.getSyntaxAnalyzer().nextToken(2);
        } else if(this.getPrefixOperatorType() == PrefixOperatorType.PREFIX_ADD) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PLUS, false) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getStart(),
                        this.getSyntaxAnalyzer().currentToken().getEnd(),
                        "Couldn't parse the pre-add expression because it the first minus isn't directly followed by a next minus."
                );
                return Optional.empty();
            }
            this.getSyntaxAnalyzer().nextToken(2);
        } else this.getSyntaxAnalyzer().nextToken();
        
        final Optional<? extends AbstractOperableSyntaxAST<?>> optionalRightSideAST = this.parseOperable(parentAST);
        if (optionalRightSideAST.isEmpty())
            return Optional.empty();
        this.rightSideOperable = optionalRightSideAST.get();
        return Optional.of(this);
    }
    
    @Override
    public void printSyntaxAST(@NonNull final PrintStream printStream, @NonNull final String indents) {
        printStream.println(indents + "├── operator: " + this.getPrefixOperatorType());
        printStream.println(indents + "└── right:");
        printStream.println(indents + "    └── " + this.getRightSideOperable().getClass().getSimpleName());
        this.getRightSideOperable().printSyntaxAST(printStream, indents + "        ");
    }
    
}
