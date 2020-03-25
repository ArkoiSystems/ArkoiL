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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.Optional;

public class IdentifierCallOperableSyntaxAST extends AbstractOperableSyntaxAST<TypeKind>
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private boolean isFileLocal;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private IdentifierToken calledIdentifier = IdentifierToken
            .builder()
            .content("Unresolved identifier for \"calledIdentifier\"")
            .crash()
            .build();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private FunctionCallPartSyntaxAST calledFunctionPart;
    
    
    public IdentifierCallOperableSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.IDENTIFIER_CALL_OPERABLE);
    }
    
    
    /**
     * This method will parse the "identifier call" statement and checks it for correct
     * syntax. This statement can be used everywhere it is needed but especially in
     * AbstractExpressionAST.
     *
     * @param parentAST
     *         The parent of the AST. With it you can check for correct usage of the
     *         statement.
     *
     * @return It will return null if an error occurred or an IdentifierCallStatementAST
     *         if it parsed until to the end.
     */
    @Override
    public Optional<IdentifierCallOperableSyntaxAST> parseAST(@NotNull final AbstractSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
    
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.IDENTIFIER_CALL_NO_IDENTIFIER
            );
            return Optional.empty();
        }
    
        if (this.getSyntaxAnalyzer().currentToken().getTokenContent().equals("this")) {
            this.isFileLocal = true;
        
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PERIOD) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        SyntaxErrorType.IDENTIFIER_THIS_NO_DOT
                );
                return Optional.empty();
            } else this.getSyntaxAnalyzer().nextToken();
            
            if(this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        SyntaxErrorType.IDENTIFIER_CALL_NO_IDENTIFIER
                );
                return Optional.empty();
            } else this.getSyntaxAnalyzer().nextToken();
        }
    
        this.calledIdentifier = (IdentifierToken) this.getSyntaxAnalyzer().currentToken();
        this.setStart(this.calledIdentifier.getStart());
        this.setEnd(this.calledIdentifier.getEnd());
        return Optional.of(this);
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "├── fileLocal: " + this.isFileLocal());
        printStream.println(indents + "└── identifier: " + this.getCalledIdentifier().getTokenContent());
    }
    
}
