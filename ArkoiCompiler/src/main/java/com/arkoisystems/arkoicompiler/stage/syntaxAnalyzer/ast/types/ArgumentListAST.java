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
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiASTNode;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArgumentListAST extends ArkoiASTNode
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private List<ArgumentAST> arguments = new ArrayList<>();
    
    
    public ArgumentListAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.ARGUMENT_LIST);
    }
    
    
    @NotNull
    @Override
    public ArgumentListAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_BRACKET) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
        
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Argument list", "'['", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        this.getSyntaxAnalyzer().nextToken();
        
        while (this.getSyntaxAnalyzer().getPosition() < this.getSyntaxAnalyzer().getTokens().length) {
            if (!ArgumentAST.ARGUMENT_DEFINITION_PARSER.canParse(parentAST, this.getSyntaxAnalyzer()))
                break;
            
            final ArgumentAST argumentAST = ArgumentAST.ARGUMENT_DEFINITION_PARSER.parse(parentAST, this.getSyntaxAnalyzer());
            this.getMarkerFactory().addFactory(argumentAST.getMarkerFactory());
            
            if (argumentAST.isFailed()) {
                this.failed();
                return this;
            }
            
            this.getArguments().add(argumentAST);
            
            if (this.getSyntaxAnalyzer().matchesNextToken(SymbolType.COMMA) == null)
                break;
            else this.getSyntaxAnalyzer().nextToken();
        }
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACKET) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
        
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Argument list", "']'", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
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
    
    
    public static ArgumentListASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new ArgumentListASTBuilder(syntaxAnalyzer);
    }
    
    
    public static ArgumentListASTBuilder builder() {
        return new ArgumentListASTBuilder();
    }
    
    
    public static class ArgumentListASTBuilder
    {
        
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private List<ArgumentAST> arguments;
        
        
        private ArkoiToken startToken, endToken;
        
        
        public ArgumentListASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public ArgumentListASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public ArgumentListASTBuilder parameters(final List<ArgumentAST> arguments) {
            this.arguments = arguments;
            return this;
        }
        
        
        public ArgumentListASTBuilder start(final ArkoiToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public ArgumentListASTBuilder end(final ArkoiToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public ArgumentListAST build() {
            final ArgumentListAST argumentListAST = new ArgumentListAST(this.syntaxAnalyzer);
            if (this.arguments != null)
                argumentListAST.setArguments(this.arguments);
            argumentListAST.setStartToken(this.startToken);
            argumentListAST.getMarkerFactory().getCurrentMarker().setStart(argumentListAST.getStartToken());
            argumentListAST.setEndToken(this.endToken);
            argumentListAST.getMarkerFactory().getCurrentMarker().setEnd(argumentListAST.getEndToken());
            return argumentListAST;
        }
        
    }
    
}
