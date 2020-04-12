/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiASTNode;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.ExpressionAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ImportAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ReturnAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parsers.StatementParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;

public class StatementAST extends ArkoiASTNode
{
    
    public static StatementParser STATEMENT_PARSER = new StatementParser();
    
    
    public StatementAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer, @NotNull final ASTType astType) {
        super(syntaxAnalyzer, astType);
    }
    
    
    @NotNull
    @Override
    public IASTNode parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
    
        final AbstractToken currentToken = this.getSyntaxAnalyzer().currentToken();
        if (parentAST instanceof ExpressionAST) {
            switch (currentToken.getTokenContent()) {
                case "var":
                case "fun":
                case "import":
                case "return":
                    return this.addError(
                            this,
                            this.getSyntaxAnalyzer().getCompilerClass(),
                            currentToken,
        
                            SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Statement", "<identifier call>", this.getSyntaxAnalyzer().currentToken().getTokenContent()
                    );
                default:
                    return IdentifierCallAST.builder(this.getSyntaxAnalyzer())
                            .build()
                            .parseAST(parentAST);
            }
        } else {
            switch (currentToken.getTokenContent()) {
                case "var":
                    return VariableAST.builder(this.getSyntaxAnalyzer())
                            .build()
                            .parseAST(parentAST);
                case "import":
                    return ImportAST.builder(this.getSyntaxAnalyzer())
                            .build()
                            .parseAST(parentAST);
                case "fun":
                    return FunctionAST.builder(this.getSyntaxAnalyzer())
                            .build()
                            .parseAST(parentAST);
                case "return":
                    return ReturnAST.builder(this.getSyntaxAnalyzer())
                            .build()
                            .parseAST(parentAST);
                default:
                    return IdentifierCallAST.builder(this.getSyntaxAnalyzer())
                            .build()
                            .parseAST(parentAST);
            }
        }
    }
    
    
    @Override
    public void accept(@NotNull final IVisitor visitor) { }
    
}
