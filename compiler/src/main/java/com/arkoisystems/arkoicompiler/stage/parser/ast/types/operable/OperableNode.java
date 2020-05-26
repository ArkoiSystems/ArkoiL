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
package com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable;

import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.parser.Parser;
import com.arkoisystems.arkoicompiler.stage.parser.ast.ArkoiNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.ASTType;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OperableNode extends ArkoiNode
{
    
    @Builder(builderMethodName = "operatorBuilder")
    protected OperableNode(
            final @Nullable Parser parser,
            final @NotNull ASTType astType,
            final @Nullable ArkoiToken startToken,
            final @Nullable ArkoiToken endToken
    ) {
        super(parser, astType, startToken, endToken);
    }
    
    @Override
    public OperableNode clone() throws CloneNotSupportedException {
        return (OperableNode) super.clone();
    }
    
    //    @NotNull
    //    @Override
    //    public OperableNode parseAST(final @NotNull ArkoiNode parentAST) {
    //        //        Objects.requireNonNull(this.getParser(), "parser must not be null.");
    //        //
    //        //        final ArkoiToken currentToken = this.getParser().currentToken();
    //        //        if (currentToken == null) {
    //        //            return this.addError(
    //        //                    this,
    //        //                    this.getParser().getCompilerClass(),
    //        //                    (@Nullable ArkoiToken) null,
    //        //
    //        //                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
    //        //                    "Operable", "<token>", "nothing"
    //        //            );
    //        //        }
    //        //
    //        //        switch (currentToken.getTokenType()) {
    //        //            case STRING:
    //        //                return StringNode.builder()
    //        //                        .parser(this.getParser())
    //        //                        .build()
    //        //                        .parseAST(parentAST);
    //        //            case NUMBER:
    //        //                return NumberNode.builder()
    //        //                        .parser(this.getParser())
    //        //                        .build()
    //        //                        .parseAST(parentAST);
    //        //            case SYMBOL:
    //        //                if (this.getParser().matchesCurrentToken(SymbolType.OPENING_BRACKET) == null)
    //        //                    return this.addError(
    //        //                            this,
    //        //                            this.getParser().getCompilerClass(),
    //        //                            currentToken,
    //        //
    //        //                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
    //        //                            "Operable", "'['", currentToken.getTokenContent()
    //        //                    );
    //        //
    //        //                return CollectionNode.builder()
    //        //                        .parser(this.getParser())
    //        //                        .build()
    //        //                        .parseAST(parentAST);
    //        //            case IDENTIFIER:
    //        //                if (!StatementNode.STATEMENT_PARSER.canParse(parentAST, this.getParser()))
    //        //                    return this.addError(
    //        //                            this,
    //        //                            this.getParser().getCompilerClass(),
    //        //                            currentToken,
    //        //
    //        //                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
    //        //                            "Operable", "<statement>", currentToken.getTokenContent()
    //        //                    );
    //        //
    //        //                final ArkoiNode astNode = StatementNode.STATEMENT_PARSER.parse(parentAST, this.getParser());
    //        //                this.getMarkerFactory().addFactory(astNode.getMarkerFactory());
    //        //
    //        //                if (astNode instanceof IdentifierCallNode)
    //        //                    return (IdentifierCallNode) astNode;
    //        //
    //        //                return this.addError(
    //        //                        this,
    //        //                        this.getParser().getCompilerClass(),
    //        //                        astNode,
    //        //
    //        //                        ParserErrorType.SYNTAX_ERROR_TEMPLATE,
    //        //                        "Function", "<identifier call>", currentToken.getTokenContent()
    //        //                );
    //        //            case KEYWORD:
    //        //                if (this.getParser().matchesCurrentToken(KeywordType.THIS) == null)
    //        //                    return this.addError(
    //        //                            this,
    //        //                            this.getParser().getCompilerClass(),
    //        //                            currentToken,
    //        //
    //        //                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
    //        //                            "Function", "'this'", currentToken.getTokenContent()
    //        //                    );
    //        //
    //        //                return IdentifierCallNode.builder()
    //        //                        .parser(this.getParser())
    //        //                        .build()
    //        //                        .parseAST(parentAST);
    //        //            default:
    //        //                return this.addError(
    //        //                        this,
    //        //                        this.getParser().getCompilerClass(),
    //        //                        currentToken,
    //        //
    //        //                        ParserErrorType.OPERABLE_NOT_SUPPORTED
    //        //                );
    //        //        }
    //        return this;
    //    }
    //
    //    @Override
    //    public void accept(final @NotNull IVisitor<?> visitor) { }
    
}
