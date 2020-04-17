/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author єхcsє#5543 aka timo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.StringToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.StatementAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ImportAST extends StatementAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private StringToken importFilePath;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private IdentifierToken importName;
    
    
    protected ImportAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.IMPORT);
    }
    
    
    @NotNull
    @Override
    public ImportAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.IMPORT) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Import", "'import'", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        }
        
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.STRING_LITERAL) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Import", "<string>", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
        this.setImportFilePath((StringToken) this.getSyntaxAnalyzer().nextToken());
        
        if (this.getImportFilePath() != null && this.getImportFilePath().getTokenContent().endsWith(".ark"))
            this.getImportFilePath().setTokenContent(this.getImportFilePath().getTokenContent().substring(0, this.getImportFilePath().getTokenContent().length() - 4));
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, KeywordType.AS) != null) {
            this.getSyntaxAnalyzer().nextToken();
            
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER) == null)
                return this.addError(
                        this,
                        this.getSyntaxAnalyzer().getCompilerClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        
                        SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                        "Import", "<identifier>", this.getSyntaxAnalyzer().currentToken().getTokenContent()
                );
            
            this.setImportName((IdentifierToken) this.getSyntaxAnalyzer().nextToken());
        } else if (this.getImportFilePath() != null) {
            final String[] split = this.getImportFilePath().getTokenContent().split("/");
            this.setImportName(IdentifierToken.builder()
                    .content(split[split.length - 1].replace(".ark", ""))
                    .build()
            );
        }
        
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
    }
    
    
    @Override
    public void accept(@NotNull final IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    
    @Override
    public @NotNull TypeKind getTypeKind() {
        return TypeKind.UNDEFINED;
    }
    
    
    public static ImportASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new ImportASTBuilder(syntaxAnalyzer);
    }
    
    
    public static ImportASTBuilder builder() {
        return new ImportASTBuilder();
    }
    
    
    public static class ImportASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private StringToken importFilePath;
        
        
        @Nullable
        private IdentifierToken importName;
        
        
        private ArkoiToken startToken, endToken;
        
        
        public ImportASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public ImportASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public ImportASTBuilder name(final IdentifierToken importName) {
            this.importName = importName;
            return this;
        }
        
        
        public ImportASTBuilder filePath(final StringToken importFilePath) {
            this.importFilePath = importFilePath;
            return this;
        }
        
        
        public ImportASTBuilder start(final ArkoiToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public ImportASTBuilder end(final ArkoiToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public ImportAST build() {
            final ImportAST importAST = new ImportAST(this.syntaxAnalyzer);
            if (this.importFilePath != null)
                importAST.setImportFilePath(this.importFilePath);
            if (this.importName != null)
                importAST.setImportName(this.importName);
            importAST.setStartToken(this.startToken);
            importAST.getMarkerFactory().getCurrentMarker().setStart(importAST.getStartToken());
            importAST.setEndToken(this.endToken);
            importAST.getMarkerFactory().getCurrentMarker().setEnd(importAST.getEndToken());
            return importAST;
        }
        
    }
    
}
