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
package com.arkoisystems.arkoicompiler.stages.parser.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stages.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stages.lexer.token.enums.KeywordType;
import com.arkoisystems.arkoicompiler.stages.parser.Parser;
import com.arkoisystems.arkoicompiler.stages.parser.ParserErrorType;
import com.arkoisystems.arkoicompiler.stages.parser.ast.ArkoiNode;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.Operable;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.types.expression.Expression;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.statement.Statement;
import com.arkoisystems.arkoicompiler.stages.parser.ast.enums.ASTType;
import com.arkoisystems.arkoicompiler.stages.parser.ast.enums.TypeKind;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class ReturnStatement extends Statement
{
    
    public static ReturnStatement GLOBAL_NODE = new ReturnStatement(null, null, null, null);
    
    @Printable(name = "expression")
    @Nullable
    private Operable expression;
    
    @Builder
    protected ReturnStatement(
            final @Nullable Parser parser,
            final @Nullable Operable expression,
            final @Nullable ArkoiToken startToken,
            final @Nullable ArkoiToken endToken
    ) {
        super(parser, ASTType.RETURN, startToken, endToken);
        
        this.expression = expression;
    }
    
    @NotNull
    @Override
    public ReturnStatement parseAST(final @Nullable ArkoiNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        if (this.getParser().matchesCurrentToken(KeywordType.RETURN) == null) {
            final ArkoiToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Return", "'return'", currentToken != null ? currentToken.getTokenContent() : "nothing"
            );
        }
        
        this.startAST(this.getParser().currentToken());
        
        if(Expression.GLOBAL_NODE.canParse(this.getParser(), 1)) {
            this.getParser().nextToken();
    
            final Operable operable = Expression.expressionBuilder()
                    .parser(this.getParser())
                    .build()
                    .parseAST(this);
            if (operable.isFailed()) {
                this.setFailed(true);
                return this;
            }
    
            this.expression = operable;
        }
        
        this.endAST(this.getParser().currentToken());
        return this;
    }
    
    @Override
    public boolean canParse(final @NotNull Parser parser, final int offset) {
        return parser.matchesPeekToken(offset, KeywordType.RETURN) != null;
    }
    
    @Override
    public void accept(final @NotNull IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @Override
    @NotNull
    public TypeKind getTypeKind() {
        if(this.getExpression() != null) {
            Objects.requireNonNull(this.getExpression(), "expression must not be null.");
            return this.getExpression().getTypeKind();
        }
        return TypeKind.VOID;
    }
    
}
