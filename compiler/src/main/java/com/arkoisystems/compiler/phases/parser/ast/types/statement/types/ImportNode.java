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
package com.arkoisystems.compiler.phases.parser.ast.types.statement.types;

import com.arkoisystems.compiler.Compiler;
import com.arkoisystems.compiler.CompilerClass;
import com.arkoisystems.compiler.phases.lexer.token.LexerToken;
import com.arkoisystems.compiler.phases.lexer.token.enums.KeywordType;
import com.arkoisystems.compiler.phases.lexer.token.enums.TokenType;
import com.arkoisystems.compiler.phases.lexer.token.types.IdentifierToken;
import com.arkoisystems.compiler.phases.lexer.token.types.StringToken;
import com.arkoisystems.compiler.phases.parser.Parser;
import com.arkoisystems.compiler.phases.parser.ParserErrorType;
import com.arkoisystems.compiler.phases.parser.SymbolTable;
import com.arkoisystems.compiler.phases.parser.ast.ParserNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.StatementNode;
import com.arkoisystems.compiler.visitor.IVisitor;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Objects;

@Getter
public class ImportNode extends StatementNode
{
    
    public static ImportNode GLOBAL_NODE = new ImportNode(null, null, null, null, null, null, null);
    
    @Printable(name = "file path")
    @Nullable
    private StringToken filePath;
    
    @Printable(name = "name")
    @Nullable
    private IdentifierToken name;
    
    @Builder
    protected ImportNode(
            @Nullable final Parser parser,
            @Nullable final ParserNode parentNode,
            @Nullable final SymbolTable currentScope,
            @Nullable final StringToken filePath,
            @Nullable final IdentifierToken name,
            @Nullable final LexerToken startToken,
            @Nullable final LexerToken endToken
    ) {
        super(parser, parentNode, currentScope, startToken, endToken);
        
        this.filePath = filePath;
        this.name = name;
    }
    
    @NotNull
    @Override
    public ImportNode parse() {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        if (this.getParser().matchesCurrentToken(KeywordType.IMPORT) == null) {
            final LexerToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Import",
                            "'import'",
                            currentToken != null ? currentToken.getTokenContent() : "nothing"
                    )
            );
        }
        
        this.startAST(this.getParser().currentToken());
        
        if (this.getParser().matchesPeekToken(1, TokenType.STRING) == null) {
            final LexerToken nextToken = this.getParser().nextToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    nextToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Import",
                            "<string>",
                            nextToken != null ? nextToken.getTokenContent() : "nothing"
                    )
            );
        }
        
        this.filePath = (StringToken) this.getParser().nextToken();
        Objects.requireNonNull(this.getFilePath(), "filePath must not be null.");
        if (this.getFilePath().getTokenContent().endsWith(".ark"))
            this.getFilePath().setTokenContent(this.getFilePath().getTokenContent().substring(0, this.getFilePath().getTokenContent().length() - 4));
    
        this.resolveClass();
        
        if (this.getParser().matchesPeekToken(1, KeywordType.AS) != null) {
            this.getParser().nextToken();
            
            if (this.getParser().matchesPeekToken(1, TokenType.IDENTIFIER) == null) {
                final LexerToken nextToken = this.getParser().nextToken();
                return this.addError(
                        this,
                        this.getParser().getCompilerClass(),
                        nextToken,
                        String.format(
                                ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                                "Import",
                                "<identifier>",
                                nextToken != null ? nextToken.getTokenContent() : "nothing"
                        )
                );
            }
            
            this.name = (IdentifierToken) this.getParser().nextToken();
            
            Objects.requireNonNull(this.getCurrentScope(), "currentScope must not be null.");
            Objects.requireNonNull(this.getName(), "name must not be null.");
            this.getCurrentScope().insert(this.getName().getTokenContent(), this);
        }
        
        this.endAST(this.getParser().currentToken());
        return this;
    }
    
    @Override
    public boolean canParse(@NotNull final Parser parser, final int offset) {
        return parser.matchesPeekToken(offset, KeywordType.IMPORT) != null;
    }
    
    @Override
    public void accept(@NotNull final IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @SneakyThrows
    @Nullable
    public CompilerClass resolveClass() {
        Objects.requireNonNull(this.getFilePath(), "filePath must not be null.");
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        File targetFile = new File(this.getFilePath().getTokenContent() + ".ark");
        if (!targetFile.isAbsolute()) {
            targetFile = new File(
                    new File(this.getParser().getCompilerClass().getFilePath()).getParent(),
                    this.getFilePath().getTokenContent() + ".ark"
            );
            
            if (!targetFile.exists()) {
                for (final File libraryDirectory : this.getParser().getCompilerClass().getCompiler().getLibraryPaths()) {
                    final File file = new File(
                            libraryDirectory.getPath(),
                            this.getFilePath().getTokenContent() + ".ark"
                    );
                    if (!file.exists())
                        continue;
                    
                    targetFile = file;
                    break;
                }
            }
        }
        
        if (!targetFile.exists())
            return null;
    
        final Compiler compiler = this.getParser().getCompilerClass().getCompiler();
        for (final CompilerClass compilerClass : compiler.getClasses()) {
            if (compilerClass.getFilePath().equals(targetFile.getCanonicalPath()))
                return compilerClass;
        }
    
        final CompilerClass compilerClass = new CompilerClass(compiler, targetFile);
        compiler.getClasses().add(compilerClass);
    
        if (!compilerClass.getLexer().processStage())
            return null;
        if (!compilerClass.getParser().processStage())
            return null;
        return compilerClass;
    }
    
}
