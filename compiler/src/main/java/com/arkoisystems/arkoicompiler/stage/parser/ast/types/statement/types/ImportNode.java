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

import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.TokenType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.types.StringToken;
import com.arkoisystems.arkoicompiler.stage.parser.Parser;
import com.arkoisystems.arkoicompiler.stage.parser.ParserErrorType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.ArkoiNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.StatementNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.TypeKind;
import com.arkoisystems.utils.printer.annotations.Printable;
import com.sun.source.tree.ImportTree;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class ImportNode extends StatementNode
{
    
    public static ImportNode GLOBAL_NODE = new ImportNode(null, null, null, null, null);
 
    @Printable(name = "file path")
    @Nullable
    private StringToken filePath;
    
    @Printable(name = "name")
    @Nullable
    private IdentifierToken name;
    
    @Builder
    protected ImportNode(
            final @Nullable Parser parser,
            final @Nullable StringToken filePath,
            final @Nullable IdentifierToken name,
            final @Nullable ArkoiToken startToken,
            final @Nullable ArkoiToken endToken
    ) {
        super(parser, ASTType.IMPORT, startToken, endToken);
        
        this.filePath = filePath;
        this.name = name;
    }
    
    @NotNull
    @Override
    public ImportNode parseAST(final @Nullable ArkoiNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
    
        if (this.getParser().matchesCurrentToken(KeywordType.IMPORT) == null) {
            final ArkoiToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Import", "'import'", currentToken != null ? currentToken.getTokenContent() : "nothing"
            );
        }
    
        this.startAST(this.getParser().currentToken());
    
        if (this.getParser().matchesPeekToken(1, TokenType.STRING) == null) {
            final ArkoiToken peekedToken = this.getParser().peekToken(1);
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    peekedToken,
                
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Import", "<string>", peekedToken != null ? peekedToken.getTokenContent() : "nothing"
            );
        }
    
        this.filePath = (StringToken) this.getParser().nextToken();
    
        if (this.getFilePath() != null && this.getFilePath().getTokenContent().endsWith(".ark"))
            this.getFilePath().setTokenContent(this.getFilePath().getTokenContent().substring(0, this.getFilePath().getTokenContent().length() - 4));
    
        if (this.getParser().matchesPeekToken(1, KeywordType.AS) != null) {
            this.getParser().nextToken();
        
            if (this.getParser().matchesPeekToken(1, TokenType.IDENTIFIER) == null) {
                final ArkoiToken peekedToken = this.getParser().peekToken(1);
                return this.addError(
                        this,
                        this.getParser().getCompilerClass(),
                        peekedToken,
                    
                        ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                        "Import", "<identifier>", peekedToken != null ? peekedToken.getTokenContent() : "nothing"
                );
            }
        
            this.name = (IdentifierToken) this.getParser().nextToken();
        } else if (this.getFilePath() != null) {
            final String[] split = this.getFilePath().getTokenContent().split("/");
            this.name = IdentifierToken.builder()
                    .lexer(this.getParser().getCompilerClass().getLexer())
                    .build();
            this.name.setTokenContent(split[split.length - 1].replace(".ark", ""));
        }
    
        this.endAST(this.getParser().currentToken());
        return this;
    }
    
    @Override
    public boolean canParse(final @NotNull Parser parser, final int offset) {
        return parser.matchesPeekToken(offset, KeywordType.IMPORT) != null;
    }
    
    @Override
    public void accept(final @NotNull IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @Override
    public @NotNull TypeKind getTypeKind() {
        return TypeKind.UNDEFINED;
    }
    
}
