/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.StringToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.KeywordType;
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
    @NotNull
    private StringToken importFilePath = StringToken
            .builder()
            .content("Undefined string for \"importFilePath\"")
            .crash()
            .build();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
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
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.IMPORT) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.IMPORT_DEFINITION_WRONG_START
            );
            return Optional.empty();
        }
        
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
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
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.AS) != null) {
            if (this.getSyntaxAnalyzer().matchesNextToken(TokenType.IDENTIFIER) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        SyntaxErrorType.IMPORT_DEFINITION_NOT_FOLLOWED_BY_NAME
                );
                return Optional.empty();
            }
            
            this.importName = (IdentifierToken) this.getSyntaxAnalyzer().currentToken();
        } else {
            final String[] split = this.importFilePath.getTokenContent().split("/");
            this.importName = IdentifierToken
                    .builder()
                    .content(split[split.length - 1].replace(".ark", ""))
                    .build();
        }
        
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        return Optional.of(this);
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "├── name: " + this.getImportName().getTokenContent());
        printStream.println(indents + "└── path: " + this.getImportFilePath().getTokenContent());
    }
    
}
