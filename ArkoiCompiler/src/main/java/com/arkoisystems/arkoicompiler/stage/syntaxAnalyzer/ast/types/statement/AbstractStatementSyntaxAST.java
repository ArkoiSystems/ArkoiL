/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement;

import com.arkoisystems.arkoicompiler.api.ICompilerSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ImportSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ReturnSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parsers.StatementParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;

public class AbstractStatementSyntaxAST extends ArkoiSyntaxAST
{
    
    public static StatementParser STATEMENT_PARSER = new StatementParser();
    
    
    public AbstractStatementSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer, @NotNull final ASTType astType) {
        super(syntaxAnalyzer, astType);
    }
    
    
    @NotNull
    @Override
    public ICompilerSyntaxAST parseAST(@NotNull final ICompilerSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
    
        final AbstractToken currentToken = this.getSyntaxAnalyzer().currentToken();
        if (parentAST instanceof ExpressionSyntaxAST) {
            switch (currentToken.getTokenContent()) {
                case "var":
                case "fun":
                case "import":
                case "return":
                    return this.addError(
                            this,
                            this.getSyntaxAnalyzer().getCompilerClass(),
                            currentToken,
                            "10"
                    );
                default:
                    return IdentifierCallSyntaxAST
                            .builder(this.getSyntaxAnalyzer())
                            .build()
                            .parseAST(parentAST);
            }
        } else {
            switch (currentToken.getTokenContent()) {
                case "var":
                    return VariableSyntaxAST.builder(this.getSyntaxAnalyzer())
                            .build()
                            .parseAST(parentAST);
                case "import":
                    return ImportSyntaxAST.builder(this.getSyntaxAnalyzer())
                            .build()
                            .parseAST(parentAST);
                case "fun":
                    return FunctionSyntaxAST
                            .builder(this.getSyntaxAnalyzer())
                            .build()
                            .parseAST(parentAST);
                case "return":
                    return ReturnSyntaxAST
                            .builder(this.getSyntaxAnalyzer())
                            .build()
                            .parseAST(parentAST);
                default:
                    return IdentifierCallSyntaxAST
                            .builder(this.getSyntaxAnalyzer())
                            .build()
                            .parseAST(parentAST);
            }
        }
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) { }
    
}
