/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.BlockSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.utils.FunctionInvocation;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTAccess;
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

public class FunctionInvokeOperableSyntaxAST extends AbstractOperableSyntaxAST<TypeKind>
{
    
    @Getter
    @Setter
    private ASTAccess functionAccess;
    
    
    @Getter
    private IdentifierToken invokedFunctionName;
    
    
    @Getter
    private final List<ExpressionSyntaxAST> invokedExpressions;
    
    
    @Getter
    private final FunctionInvocation invocationType;
    
    
    public FunctionInvokeOperableSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer, final FunctionInvocation invocationType) {
        super(syntaxAnalyzer, ASTType.FUNCTION_INVOKE_OPERABLE);
        
        this.invocationType = invocationType;
        
        this.functionAccess = ASTAccess.GLOBAL_ACCESS;
        this.invokedExpressions = new ArrayList<>();
    }
    
    
    public FunctionInvokeOperableSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.FUNCTION_INVOKE_OPERABLE);
        
        this.invocationType = FunctionInvocation.BLOCK_INVOCATION;
        this.functionAccess = ASTAccess.GLOBAL_ACCESS;
        this.invokedExpressions = new ArrayList<>();
    }
    
    
    @Override
    public Optional<FunctionInvokeOperableSyntaxAST> parseAST(@NonNull final AbstractSyntaxAST parentAST) {
        if (!(parentAST instanceof BlockSyntaxAST) && !(parentAST instanceof AbstractExpressionSyntaxAST) && !(parentAST instanceof IdentifierInvokeOperableSyntaxAST)) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_INVOKE_WRONG_PARENT
            );
            return Optional.empty();
        }
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_INVOKE_WRONG_START
            );
            return Optional.empty();
        }
        
        this.invokedFunctionName = (IdentifierToken) this.getSyntaxAnalyzer().currentToken();
        this.setStart(this.invokedFunctionName.getStart());
        
        if (this.getSyntaxAnalyzer().matchesNextToken(SymbolType.OPENING_PARENTHESIS) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_INVOKE_WRONG_ARGUMENTS_START
            );
            return Optional.empty();
        }
        this.getSyntaxAnalyzer().nextToken();
        
        while (this.getSyntaxAnalyzer().getPosition() < this.getSyntaxAnalyzer().getTokens().length) {
            if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_PARENTHESIS) != null)
                break;
            
            if (!AbstractExpressionSyntaxAST.EXPRESSION_PARSER.canParse(this, this.getSyntaxAnalyzer())) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        SyntaxErrorType.FUNCTION_INVOKE_ARGUMENT_NOT_PARSEABLE
                );
                return Optional.empty();
            }
            
            final Optional<ExpressionSyntaxAST> optionalExpressionSyntaxAST = AbstractExpressionSyntaxAST.EXPRESSION_PARSER.parse(this, this.getSyntaxAnalyzer());
            if (optionalExpressionSyntaxAST.isEmpty())
                return Optional.empty();
            this.invokedExpressions.add(optionalExpressionSyntaxAST.get());
            
            if (this.getSyntaxAnalyzer().matchesNextToken(SymbolType.CLOSING_PARENTHESIS) != null)
                break;
            else if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.COMMA) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        SyntaxErrorType.FUNCTION_INVOKE_NO_SEPARATOR
                );
                return Optional.empty();
            } else this.getSyntaxAnalyzer().nextToken();
        }
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_PARENTHESIS) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_INVOKE_WRONG_ARGUMENTS_ENDING
            );
            return Optional.empty();
        }
        
        if (this.invocationType.equals(FunctionInvocation.BLOCK_INVOCATION) && this.getSyntaxAnalyzer().matchesNextToken(SymbolType.SEMICOLON) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_INVOKE_WRONG_ENDING
            );
            return Optional.empty();
        }
        
        this.setEnd(this.getSyntaxAnalyzer().currentToken().getEnd());
        return Optional.of(this);
    }
    
    
    @Override
    public void printSyntaxAST(@NonNull final PrintStream printStream, @NonNull final String indents) {
        printStream.println(indents + "├── access: " + this.getFunctionAccess());
        printStream.println(indents + "├── identifier: " + this.getInvokedFunctionName().getTokenContent());
        printStream.println(indents + "└── expressions: " + (this.getInvokedExpressions().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getInvokedExpressions().size(); index++) {
            final ExpressionSyntaxAST abstractSyntaxAST = this.getInvokedExpressions().get(index);
            if (index == this.getInvokedExpressions().size() - 1) {
                printStream.println(indents + "    └── " + abstractSyntaxAST.getExpressionOperable().getClass().getSimpleName());
                abstractSyntaxAST.printSyntaxAST(printStream, indents + "        ");
            } else {
                printStream.println(indents + "    ├── " + abstractSyntaxAST.getExpressionOperable().getClass().getSimpleName());
                abstractSyntaxAST.printSyntaxAST(printStream, indents + "    │   ");
            }
        }
    }
    
}
