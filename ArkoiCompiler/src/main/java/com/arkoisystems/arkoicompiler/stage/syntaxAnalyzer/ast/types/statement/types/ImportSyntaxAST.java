/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.api.ICompilerSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.StringToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
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
import java.util.stream.Collectors;

public class ImportSyntaxAST extends AbstractStatementSyntaxAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private StringToken importFilePath;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private IdentifierToken importName;
    
    
    protected ImportSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.IMPORT);
    }
    
    
    @NotNull
    @Override
    public ImportSyntaxAST parseAST(@NotNull final ICompilerSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (!(parentAST instanceof RootSyntaxAST)) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    parentAST,
                    SyntaxErrorType.IMPORT_DEFINITION_WRONG_PARENT
            );
        }
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.IMPORT) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.IMPORT_DEFINITION_WRONG_START
            );
        }
        
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.STRING_LITERAL) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.IMPORT_DEFINITION_NO_FILE_PATH
            );
        } else this.getSyntaxAnalyzer().nextToken();
        
        this.importFilePath = (StringToken) this.getSyntaxAnalyzer().currentToken();
        if (this.importFilePath.getTokenContent().endsWith(".ark"))
            this.importFilePath.setTokenContent(this.importFilePath.getTokenContent().substring(0, this.importFilePath.getTokenContent().length() - 4));
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, KeywordType.AS) != null) {
            this.getSyntaxAnalyzer().nextToken();
            
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER) == null) {
                return this.addError(
                        this,
                        this.getSyntaxAnalyzer().getCompilerClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        SyntaxErrorType.IMPORT_DEFINITION_NOT_FOLLOWED_BY_NAME
                );
            } else this.getSyntaxAnalyzer().nextToken();
            
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
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getStart());
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getEnd());
        Objects.requireNonNull(this.getImportFilePath());
        Objects.requireNonNull(this.getImportName());
    
        printStream.println(indents + "├── factory:");
        printStream.println(indents + "│    ├── next: " + this.getMarkerFactory()
                .getNextMarkerFactories()
                .stream()
                .map(markerFactory -> markerFactory.getCurrentMarker().getAstType().name())
                .collect(Collectors.joining(", "))
        );
        printStream.println(indents + "│    ├── start: " + this.getMarkerFactory().getCurrentMarker().getStart().getStart());
        printStream.println(indents + "│    └── end: " + this.getMarkerFactory().getCurrentMarker().getEnd().getEnd());
        printStream.println(indents + "│");
        printStream.println(indents + "├── name: " + this.getImportName().getTokenContent());
        printStream.println(indents + "└── path: " + this.getImportFilePath().getTokenContent());
    }
    
    
    public static ImportSyntaxASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new ImportSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static ImportSyntaxASTBuilder builder() {
        return new ImportSyntaxASTBuilder();
    }
    
    
    public static class ImportSyntaxASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private StringToken importFilePath;
        
        
        @Nullable
        private IdentifierToken importName;
        
        
        private AbstractToken startToken, endToken;
        
        
        public ImportSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public ImportSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public ImportSyntaxASTBuilder name(final IdentifierToken importName) {
            this.importName = importName;
            return this;
        }
        
        
        public ImportSyntaxASTBuilder filePath(final StringToken importFilePath) {
            this.importFilePath = importFilePath;
            return this;
        }
        
        
        public ImportSyntaxASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public ImportSyntaxASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public ImportSyntaxAST build() {
            final ImportSyntaxAST importSyntaxAST = new ImportSyntaxAST(this.syntaxAnalyzer);
            if (this.importFilePath != null)
                importSyntaxAST.setImportFilePath(this.importFilePath);
            if (this.importName != null)
                importSyntaxAST.setImportName(this.importName);
            importSyntaxAST.setStartToken(this.startToken);
            importSyntaxAST.getMarkerFactory().getCurrentMarker().setStart(importSyntaxAST.getStartToken());
            importSyntaxAST.setEndToken(this.endToken);
            importSyntaxAST.getMarkerFactory().getCurrentMarker().setEnd(importSyntaxAST.getEndToken());
            return importSyntaxAST;
        }
        
    }
    
}
