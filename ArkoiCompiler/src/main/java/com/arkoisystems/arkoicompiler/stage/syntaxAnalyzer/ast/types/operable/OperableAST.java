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
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.IToken;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiASTNode;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.CollectionAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.NumberAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.StringAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.StatementAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.marker.MarkerFactory;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class OperableAST extends ArkoiASTNode
{
    
    
    @Builder(builderMethodName = "operableBuilder")
    public OperableAST(
            @Nullable final MarkerFactory<? extends IASTNode, IToken, IToken> markerFactory,
            @Nullable final SyntaxAnalyzer syntaxAnalyzer,
            @NotNull final ASTType astType,
            @Nullable final IToken startToken,
            @Nullable final IToken endToken
    ) {
        super(markerFactory, syntaxAnalyzer, astType, startToken, endToken);
    }
    
    
    @NotNull
    @Override
    public OperableAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        
        final ArkoiToken currentToken = this.getSyntaxAnalyzer().currentToken();
        switch (currentToken.getTokenType()) {
            case STRING_LITERAL:
                return StringAST.builder()
                        .syntaxAnalyzer(this.getSyntaxAnalyzer())
                        .build()
                        .parseAST(parentAST);
            case NUMBER_LITERAL:
                return NumberAST.builder()
                        .syntaxAnalyzer(this.getSyntaxAnalyzer())
                        .build()
                        .parseAST(parentAST);
            case SYMBOL:
                if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_BRACKET) == null) {
                    return this.addError(
                            this,
                            this.getSyntaxAnalyzer().getCompilerClass(),
                            currentToken,
        
                            SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Operable", "'['", this.getSyntaxAnalyzer().currentToken().getTokenContent()
                    );
                }
                return CollectionAST.builder()
                        .syntaxAnalyzer(this.getSyntaxAnalyzer())
                        .build()
                        .parseAST(parentAST);
            case IDENTIFIER:
                if (!StatementAST.STATEMENT_PARSER.canParse(parentAST, this.getSyntaxAnalyzer())) {
                    return this.addError(
                            this,
                            this.getSyntaxAnalyzer().getCompilerClass(),
                            currentToken,
                
                            SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Operable", "<statement>", this.getSyntaxAnalyzer().currentToken().getTokenContent()
                    );
                }
    
                final IASTNode astNode = StatementAST.STATEMENT_PARSER.parse(parentAST, this.getSyntaxAnalyzer());
                this.getMarkerFactory().addFactory(astNode.getMarkerFactory());
    
                if (astNode instanceof IdentifierCallAST)
                    return (IdentifierCallAST) astNode;
    
                return this.addError(
                        this,
                        this.getSyntaxAnalyzer().getCompilerClass(),
                        astNode,
            
                        SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                        "Function", "<identifier call>", this.getSyntaxAnalyzer().currentToken().getTokenContent()
                );
            case KEYWORD:
                if (this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.THIS) == null)
                    return this.addError(
                            this,
                            this.getSyntaxAnalyzer().getCompilerClass(),
                            currentToken,
                            
                            SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Function", "'this'", this.getSyntaxAnalyzer().currentToken().getTokenContent()
                    );
                
                return IdentifierCallAST.builder()
                        .syntaxAnalyzer(this.getSyntaxAnalyzer())
                        .build()
                        .parseAST(parentAST);
            default:
                return this.addError(
                        this,
                        this.getSyntaxAnalyzer().getCompilerClass(),
                        currentToken,
        
                        SyntaxErrorType.OPERABLE_NOT_SUPPORTED
                );
        }
    }
    
    
    @Override
    public void accept(@NotNull final IVisitor<?> visitor) { }
    
    
    @Override
    public @NotNull TypeKind getTypeKind() {
        throw new NullPointerException(this.toString());
    }
    
}
