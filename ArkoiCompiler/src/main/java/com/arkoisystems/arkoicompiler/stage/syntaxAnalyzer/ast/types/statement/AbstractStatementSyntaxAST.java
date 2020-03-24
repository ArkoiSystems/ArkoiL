/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.FunctionInvokeOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierInvokeOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.utils.FunctionInvocation;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ImportDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ReturnStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.types.StatementParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.Optional;

public class AbstractStatementSyntaxAST extends AbstractSyntaxAST
{
    
    public static StatementParser STATEMENT_PARSER = new StatementParser();
    
    
    public AbstractStatementSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer, @NotNull final ASTType astType) {
        super(syntaxAnalyzer, astType);
    }
    
    
    @Override
    public Optional<? extends AbstractSyntaxAST> parseAST(@NotNull final AbstractSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        final AbstractToken currentToken = this.getSyntaxAnalyzer().currentToken();
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    currentToken,
                    SyntaxErrorType.STATEMENT_WRONG_START
            );
            return Optional.empty();
        }
        
        if (parentAST instanceof ThisStatementSyntaxAST) {
            switch (currentToken.getTokenContent()) {
                case "var":
                case "fun":
                case "import":
                case "this":
                case "return":
                    this.addError(
                            this.getSyntaxAnalyzer().getArkoiClass(),
                            this.getSyntaxAnalyzer().currentToken(),
                            SyntaxErrorType.STATEMENT_WRONG_THIS_STATEMENT_USAGE
                    );
                    return Optional.empty();
                default:
                    if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.OPENING_PARENTHESIS) != null)
                        return new FunctionInvokeOperableSyntaxAST(this.getSyntaxAnalyzer()).parseAST(parentAST);
                    if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PERIOD) != null)
                        return new IdentifierInvokeOperableSyntaxAST(this.getSyntaxAnalyzer()).parseAST(parentAST);
                    return new IdentifierCallOperableSyntaxAST(this.getSyntaxAnalyzer()).parseAST(parentAST);
            }
        } else if (parentAST instanceof AbstractExpressionSyntaxAST) {
            switch (currentToken.getTokenContent()) {
                case "var":
                case "fun":
                case "import":
                case "return":
                    return Optional.empty();
                case "this":
                    return new ThisStatementSyntaxAST(this.getSyntaxAnalyzer()).parseAST(parentAST);
                default:
                    if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.OPENING_PARENTHESIS) != null)
                        return new FunctionInvokeOperableSyntaxAST(this.getSyntaxAnalyzer(), FunctionInvocation.EXPRESSION_INVOCATION).parseAST(parentAST);
                    if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PERIOD) != null)
                        return new IdentifierInvokeOperableSyntaxAST(this.getSyntaxAnalyzer()).parseAST(parentAST);
                    return new IdentifierCallOperableSyntaxAST(this.getSyntaxAnalyzer()).parseAST(parentAST);
            }
        } else if (parentAST instanceof IdentifierInvokeOperableSyntaxAST) {
            switch (currentToken.getTokenContent()) {
                case "var":
                case "fun":
                case "import":
                case "return":
                case "this":
                    return Optional.empty();
                default:
                    if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.OPENING_PARENTHESIS) != null)
                        return new FunctionInvokeOperableSyntaxAST(this.getSyntaxAnalyzer(), FunctionInvocation.EXPRESSION_INVOCATION).parseAST(parentAST);
                    if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PERIOD) != null)
                        return new IdentifierInvokeOperableSyntaxAST(this.getSyntaxAnalyzer()).parseAST(parentAST);
                    return new IdentifierCallOperableSyntaxAST(this.getSyntaxAnalyzer()).parseAST(parentAST);
            }
        } else {
            switch (currentToken.getTokenContent()) {
                case "var":
                    return new VariableDefinitionSyntaxAST(this.getSyntaxAnalyzer()).parseAST(parentAST);
                case "this":
                    return new ThisStatementSyntaxAST(this.getSyntaxAnalyzer()).parseAST(parentAST);
                case "import":
                    return new ImportDefinitionSyntaxAST(this.getSyntaxAnalyzer()).parseAST(parentAST);
                case "fun":
                    return new FunctionDefinitionSyntaxAST(this.getSyntaxAnalyzer()).parseAST(parentAST);
                case "return":
                    return ReturnStatementSyntaxAST
                            .builder(this.getSyntaxAnalyzer())
                            .build()
                            .parseAST(parentAST);
                default:
                    if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.OPENING_PARENTHESIS) != null)
                        return new FunctionInvokeOperableSyntaxAST(this.getSyntaxAnalyzer()).parseAST(parentAST);
                    if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PERIOD) != null)
                        return new IdentifierInvokeOperableSyntaxAST(this.getSyntaxAnalyzer()).parseAST(parentAST);
                    return new IdentifierCallOperableSyntaxAST(this.getSyntaxAnalyzer()).parseAST(parentAST);
            }
        }
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) { }
    
}
