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
package com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.SymbolType;
import com.arkoisystems.arkoicompiler.stage.parser.Parser;
import com.arkoisystems.arkoicompiler.stage.parser.ParserErrorType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.ArkoiNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.Operable;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.Expression;
import com.arkoisystems.arkoicompiler.stage.parser.ast.enums.ASTType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.enums.TypeKind;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class CollectionOperable extends Operable
{
    
    public static CollectionOperable GLOBAL_NODE = new CollectionOperable(null, null, null, null);
    
    @Printable(name = "expressions")
    @NotNull
    private final List<Operable> expressions;
    
    @Builder
    protected CollectionOperable(
            final @Nullable List<Operable> expressions,
            final @Nullable Parser parser,
            final @Nullable ArkoiToken startToken,
            final @Nullable ArkoiToken endToken
    ) {
        super(parser, ASTType.COLLECTION, startToken, endToken);
        
        this.expressions = expressions == null ? new ArrayList<>() : expressions;
    }
    
    @NotNull
    @Override
    public CollectionOperable parseAST(final @NotNull ArkoiNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        if (this.getParser().matchesCurrentToken(SymbolType.OPENING_BRACKET) == null) {
            final ArkoiToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
            
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Collection", "'['", currentToken != null ? currentToken.getTokenContent() : "nothing"
            );
        }
        
        this.startAST(this.getParser().currentToken());
        
        this.getParser().nextToken();
        
        while (this.getParser().getPosition() < this.getParser().getTokens().length) {
            if (this.getParser().matchesCurrentToken(SymbolType.CLOSING_BRACKET) != null)
                break;
            if(!Expression.GLOBAL_NODE.canParse(this.getParser(), 0))
                break;
            
            final Operable operableAST = Expression.expressionBuilder()
                    .parser(this.getParser())
                    .build()
                    .parseAST(this);
            if (operableAST.isFailed()) {
                this.setFailed(true);
                return this;
            }
            
            this.getExpressions().add(operableAST);
    
            if (this.getParser().matchesNextToken(SymbolType.COMMA) == null)
                break;
            this.getParser().nextToken(1);
        }
        
        if (this.getParser().matchesCurrentToken(SymbolType.CLOSING_BRACKET) == null) {
            final ArkoiToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
        
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Collection", "']'", currentToken != null ? currentToken.getTokenContent() : "nothing"
            );
        }
        
        this.endAST(this.getParser().currentToken());
        return this;
    }
    
    @Override
    public boolean canParse(final @NotNull Parser parser, final int offset) {
        return parser.matchesPeekToken(offset, SymbolType.OPENING_BRACKET) != null;
    }
    
    @Override
    public void accept(final @NotNull IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @Override
    protected @NotNull TypeKind initializeTypeKind() {
        return TypeKind.COLLECTION;
    }
    
}

