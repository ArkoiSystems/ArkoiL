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
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.StringToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class StringAST extends OperableAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private StringToken stringToken;
    
    
    protected StringAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.STRING);
    }
    
    
    @NotNull
    @Override
    public StringAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.STRING_LITERAL) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "String", "<string>", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
        this.setStringToken((StringToken) this.getSyntaxAnalyzer().currentToken());
    
        this.setStartToken(this.getStringToken());
        this.getMarkerFactory().mark(this.getStartToken());
    
        this.setEndToken(this.getStringToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
    }
    
    
    @Override
    public void accept(@NotNull final IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    
    @Override
    public @NotNull TypeKind getTypeKind() {
        return TypeKind.STRING;
    }
    
    
    public static StringASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new StringASTBuilder(syntaxAnalyzer);
    }
    
    
    public static StringASTBuilder builder() {
        return new StringASTBuilder();
    }
    
    
    public static class StringASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private StringToken stringToken;
        
        
        private ArkoiToken startToken, endToken;
        
        
        public StringASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public StringASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public StringASTBuilder literal(final StringToken stringToken) {
            this.stringToken = stringToken;
            return this;
        }
        
        
        public StringASTBuilder start(final ArkoiToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public StringASTBuilder end(final ArkoiToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public StringAST build() {
            final StringAST stringAST = new StringAST(this.syntaxAnalyzer);
            if (this.stringToken != null)
                stringAST.setStringToken(this.stringToken);
            stringAST.setStartToken(this.startToken);
            stringAST.getMarkerFactory().getCurrentMarker().setStart(stringAST.getStartToken());
            stringAST.setEndToken(this.endToken);
            stringAST.getMarkerFactory().getCurrentMarker().setEnd(stringAST.getEndToken());
            return stringAST;
        }
        
    }
    
}
