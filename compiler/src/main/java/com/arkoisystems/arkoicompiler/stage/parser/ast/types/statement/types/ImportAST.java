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
package com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.TokenType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.TypeKind;
import com.arkoisystems.arkoicompiler.stage.parser.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.parser.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.StatementAST;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.ASTType;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ImportAST extends StatementAST
{
    
    @Getter
    @Nullable
    private ArkoiToken importFilePath;
    
    @Getter
    @Nullable
    private ArkoiToken importName;
    
    @Builder
    private ImportAST(
            @Nullable final SyntaxAnalyzer syntaxAnalyzer,
            @Nullable final ArkoiToken importFilePath,
            @Nullable final ArkoiToken importName,
            @Nullable final ArkoiToken startToken,
            @Nullable final ArkoiToken endToken
    ) {
        super(syntaxAnalyzer, startToken, endToken, ASTType.IMPORT);
        
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
                    "Import", "'import'", currentToken != null ? currentToken.getData() : "nothing"
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
                    "Import", "<string>", peekedToken != null ? peekedToken.getData() : "nothing"
            );
        }
    
        this.importFilePath = this.getSyntaxAnalyzer().nextToken();
    
        if (this.getImportFilePath() != null && this.getImportFilePath().getData().endsWith(".ark"))
            this.getImportFilePath().setData(this.getImportFilePath().getData().substring(0, this.getImportFilePath().getData().length() - 4));
    
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, KeywordType.AS) != null) {
            this.getSyntaxAnalyzer().nextToken();
        
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER) == null) {
                final ArkoiToken peekedToken = this.getSyntaxAnalyzer().peekToken(1);
                return this.addError(
                        this,
                        this.getSyntaxAnalyzer().getCompilerClass(),
                        peekedToken,
                    
                        SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                        "Import", "<identifier>", peekedToken != null ? peekedToken.getData() : "nothing"
                );
            }
        
            this.importName = this.getSyntaxAnalyzer().nextToken();
        } else if (this.getImportFilePath() != null) {
            final String[] split = this.getImportFilePath().getData().split("/");
            this.importName = new ArkoiToken(
                    this.getSyntaxAnalyzer().getCompilerClass().getLanguageTools().getLexer(),
                    TokenType.IDENTIFIER
            );
            this.importName.setData(split[split.length - 1].replace(".ark", ""));
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
