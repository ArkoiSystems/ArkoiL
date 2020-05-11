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
import com.arkoisystems.arkoicompiler.api.IToken;
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
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ImportAST extends StatementAST
{
    
    @Getter
    @Nullable
    private StringToken importFilePath;
    
    @Getter
    @Nullable
    private IdentifierToken importName;
    
    @Builder
    private ImportAST(
            @Nullable final SyntaxAnalyzer syntaxAnalyzer,
            @Nullable final StringToken importFilePath,
            @Nullable final IdentifierToken importName,
            @Nullable final IToken startToken,
            @Nullable final IToken endToken
    ) {
        super(syntaxAnalyzer, ASTType.IMPORT, startToken, endToken);
        
        this.importFilePath = importFilePath;
        this.importName = importName;
    }
    
    @NotNull
    @Override
    public ImportAST parseAST(@Nullable final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.IMPORT) == null) {
            final ArkoiToken currentToken = this.getSyntaxAnalyzer().currentToken();
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    currentToken,
                    
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Import", "'import'", currentToken != null ? currentToken.getTokenContent() : "nothing"
            );
        }
        
        this.startAST(this.getSyntaxAnalyzer().currentToken());
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.STRING_LITERAL) == null) {
            final ArkoiToken peekedToken = this.getSyntaxAnalyzer().peekToken(1);
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    peekedToken,
            
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Import", "<string>", peekedToken != null ? peekedToken.getTokenContent() : "nothing"
            );
        }
        
        this.importFilePath = (StringToken) this.getSyntaxAnalyzer().nextToken();
        
        if (this.getImportFilePath() != null && this.getImportFilePath().getTokenContent().endsWith(".ark"))
            this.getImportFilePath().setTokenContent(this.getImportFilePath().getTokenContent().substring(0, this.getImportFilePath().getTokenContent().length() - 4));
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, KeywordType.AS) != null) {
            this.getSyntaxAnalyzer().nextToken();
    
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER) == null) {
                final ArkoiToken peekedToken = this.getSyntaxAnalyzer().peekToken(1);
                return this.addError(
                        this,
                        this.getSyntaxAnalyzer().getCompilerClass(),
                        peekedToken,
            
                        SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                        "Import", "<identifier>", peekedToken != null ? peekedToken.getTokenContent() : "nothing"
                );
            }
    
            this.importName = (IdentifierToken) this.getSyntaxAnalyzer().nextToken();
        } else if (this.getImportFilePath() != null) {
            final String[] split = this.getImportFilePath().getTokenContent().split("/");
            this.importName = IdentifierToken.builder()
                    .lexicalAnalyzer(this.getSyntaxAnalyzer().getCompilerClass().getLexicalAnalyzer())
                    .tokenContent(split[split.length - 1].replace(".ark", ""))
                    .build();
        }
        
        this.endAST(this.getSyntaxAnalyzer().currentToken());
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
    
}
