/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.KeywordType;
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
    @Setter
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
    
    
    @Getter
    @Setter
    @Nullable
    private IdentifierCallOperableSyntaxAST nextIdentifierCall;
    
    
    protected IdentifierCallOperableSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
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
    
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.THIS) != null) {
            this.isFileLocal = true;
            
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PERIOD) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        SyntaxErrorType.IDENTIFIER_THIS_NO_DOT
                );
                return Optional.empty();
            } else this.getSyntaxAnalyzer().nextToken();
            
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        SyntaxErrorType.IDENTIFIER_CALL_NO_IDENTIFIER
                );
                return Optional.empty();
            } else this.getSyntaxAnalyzer().nextToken();
        } else if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.IDENTIFIER_CALL_NO_IDENTIFIER
            );
            return Optional.empty();
        }
        
        this.calledIdentifier = (IdentifierToken) this.getSyntaxAnalyzer().currentToken();
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.OPENING_PARENTHESIS) != null) {
            this.getSyntaxAnalyzer().nextToken();
            
            final Optional<FunctionCallPartSyntaxAST> optionalFunctionCallPartSyntaxAST = new FunctionCallPartSyntaxAST(this.getSyntaxAnalyzer()).parseAST(this);
            if (optionalFunctionCallPartSyntaxAST.isEmpty()) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        "1"
                );
                return Optional.empty();
            }
            
            this.getMarkerFactory().addFactory(optionalFunctionCallPartSyntaxAST.get().getMarkerFactory());
            this.calledFunctionPart = optionalFunctionCallPartSyntaxAST.get();
        }
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PERIOD) != null) {
            final AbstractToken periodToken = this.getSyntaxAnalyzer().nextToken();
            
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        periodToken.getStart(),
                        this.getSyntaxAnalyzer().currentToken().getEnd(),
                        SyntaxErrorType.IDENTIFIER_CALL_WRONG_CALL_APPEND
                );
                return Optional.empty();
            } else this.getSyntaxAnalyzer().nextToken();
            
            final Optional<IdentifierCallOperableSyntaxAST> optionalIdentifierCallOperableSyntaxAST =
                    new IdentifierCallOperableSyntaxAST(this.getSyntaxAnalyzer()).parseAST(this);
            if (optionalIdentifierCallOperableSyntaxAST.isEmpty()) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        periodToken.getStart(),
                        this.getSyntaxAnalyzer().currentToken().getEnd(),
                        SyntaxErrorType.IDENTIFIER_CALL_WRONG_CALL_APPEND
                );
                return Optional.empty();
            }
            
            this.getMarkerFactory().addFactory(optionalIdentifierCallOperableSyntaxAST.get().getMarkerFactory());
            this.nextIdentifierCall = optionalIdentifierCallOperableSyntaxAST.get();
        }
        
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        return Optional.of(this);
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "├── fileLocal: " + this.isFileLocal());
        printStream.println(indents + "└── identifier: " + this.getCalledIdentifier().getTokenContent());
    }
    
    
    public static IdentifierCallOperableSyntaxASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new IdentifierCallOperableSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static IdentifierCallOperableSyntaxASTBuilder builder() {
        return new IdentifierCallOperableSyntaxASTBuilder();
    }
    
    
    public static class IdentifierCallOperableSyntaxASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        private boolean isFileLocal;
        
        
        @Nullable
        private IdentifierToken calledIdentifier;
        
        
        @Nullable
        private FunctionCallPartSyntaxAST calledFunctionPart;
        
        
        @Nullable
        private IdentifierCallOperableSyntaxAST nextIdentifierCall;
        
        
        private AbstractToken startToken, endToken;
        
        
        public IdentifierCallOperableSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public IdentifierCallOperableSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public IdentifierCallOperableSyntaxASTBuilder fileLocal(final boolean isFileLocal) {
            this.isFileLocal = isFileLocal;
            return this;
        }
        
        
        public IdentifierCallOperableSyntaxASTBuilder called(final IdentifierToken calledIdentifier) {
            this.calledIdentifier = calledIdentifier;
            return this;
        }
        
        
        public IdentifierCallOperableSyntaxASTBuilder functionPart(final FunctionCallPartSyntaxAST calledFunctionPart) {
            this.calledFunctionPart = calledFunctionPart;
            return this;
        }
        
        
        public IdentifierCallOperableSyntaxASTBuilder nextCall(final IdentifierCallOperableSyntaxAST nextIdentifierCall) {
            this.nextIdentifierCall = nextIdentifierCall;
            return this;
        }
        
        
        public IdentifierCallOperableSyntaxASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public IdentifierCallOperableSyntaxASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public IdentifierCallOperableSyntaxAST build() {
            final IdentifierCallOperableSyntaxAST identifierCallOperableSyntaxAST = new IdentifierCallOperableSyntaxAST(this.syntaxAnalyzer);
            identifierCallOperableSyntaxAST.setFileLocal(this.isFileLocal);
            if (this.calledIdentifier != null)
                identifierCallOperableSyntaxAST.setCalledIdentifier(this.calledIdentifier);
            if (this.calledFunctionPart != null)
                identifierCallOperableSyntaxAST.setCalledFunctionPart(this.calledFunctionPart);
            if (this.nextIdentifierCall != null)
                identifierCallOperableSyntaxAST.setNextIdentifierCall(this.nextIdentifierCall);
            identifierCallOperableSyntaxAST.setStartToken(this.startToken);
            identifierCallOperableSyntaxAST.setEndToken(this.endToken);
            return identifierCallOperableSyntaxAST;
        }
        
    }
    
}
