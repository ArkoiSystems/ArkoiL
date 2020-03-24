/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.StringToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.RootSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.Optional;

public class ImportDefinitionSyntaxAST extends AbstractStatementSyntaxAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private StringToken importFilePath = StringToken
            .builder()
            .content("Undefined string for \"importFilePath\"")
            .crash()
            .build();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private IdentifierToken importName = IdentifierToken
            .builder()
            .content("Undefined identifier for \"importName\"")
            .crash()
            .build();
    
    
    public ImportDefinitionSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.IMPORT_DEFINITION);
    }
    
    
    @Override
    public Optional<ImportDefinitionSyntaxAST> parseAST(@NotNull final AbstractSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (!(parentAST instanceof RootSyntaxAST)) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    parentAST,
                    SyntaxErrorType.IMPORT_DEFINITION_WRONG_PARENT
            );
            return Optional.empty();
        }
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.IDENTIFIER) == null || !this.getSyntaxAnalyzer().currentToken().getTokenContent().equals("import")) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.IMPORT_DEFINITION_WRONG_START
            );
            return Optional.empty();
        }
        this.setStart(this.getSyntaxAnalyzer().currentToken().getStart());
        
        if (this.getSyntaxAnalyzer().matchesNextToken(TokenType.STRING_LITERAL) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.IMPORT_DEFINITION_NO_FILEPATH
            );
            return Optional.empty();
        }
        
        this.importFilePath = (StringToken) this.getSyntaxAnalyzer().currentToken();
        if (this.importFilePath.getTokenContent().endsWith(".ark"))
            this.importFilePath.setTokenContent(this.importFilePath.getTokenContent().substring(0, this.importFilePath.getTokenContent().length() - 4));
        this.getSyntaxAnalyzer().nextToken();
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.IDENTIFIER) != null && this.getSyntaxAnalyzer().currentToken().getTokenContent().equals("as")) {
            if (this.getSyntaxAnalyzer().matchesNextToken(TokenType.IDENTIFIER) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        SyntaxErrorType.IMPORT_DEFINITION_NOT_FOLLOWED_BY_NAME
                );
                return Optional.empty();
            }
            
            this.importName = (IdentifierToken) this.getSyntaxAnalyzer().currentToken();
            this.getSyntaxAnalyzer().nextToken();
        } else {
            final String[] split = this.importFilePath.getTokenContent().split("/");
            this.importName = IdentifierToken
                    .builder()
                    .content(split[split.length - 1].replace(".ark", ""))
                    .build();
        }
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.SEMICOLON) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.IMPORT_DEFINITION_WRONG_ENDING
            );
            return Optional.empty();
        }
        
        this.setEnd(this.getSyntaxAnalyzer().currentToken().getEnd());
        return Optional.of(this);
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "├── name: " + this.getImportName().getTokenContent());
        printStream.println(indents + "└── path: " + this.getImportFilePath().getTokenContent());
    }
    
}
