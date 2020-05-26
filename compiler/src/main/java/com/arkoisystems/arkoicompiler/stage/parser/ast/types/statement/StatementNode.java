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
package com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement;

import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.parser.Parser;
import com.arkoisystems.arkoicompiler.stage.parser.ast.ArkoiNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.ASTType;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StatementNode extends ArkoiNode
{
    
    @Builder(builderMethodName = "statementBuilder")
    protected StatementNode(
            final @Nullable Parser parser,
            final @NotNull ASTType astType,
            final @Nullable ArkoiToken startToken,
            final @Nullable ArkoiToken endToken
    ) {
        super(parser, astType, startToken, endToken);
    }
    
    //    @NotNull
    //    @Override
    //    public ArkoiNode parseAST(final @NotNull ArkoiNode parentAST) {
    //        //        Objects.requireNonNull(this.getParser(), "parser must not be null.");
    //        //
    //        //        final ArkoiToken currentToken = this.getParser().currentToken();
    //        //        if (currentToken == null) {
    //        //            return this.addError(
    //        //                    this,
    //        //                    this.getParser().getCompilerClass(),
    //        //                    (ArkoiToken) null,
    //        //
    //        //                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
    //        //                    "Statement", "<token>", "nothing"
    //        //            );
    //        //        }
    //        //
    //        //        if (parentAST instanceof ExpressionNode) {
    //        //            switch (currentToken.getTokenContent()) {
    //        //                case "var":
    //        //                case "fun":
    //        //                case "import":
    //        //                case "return":
    //        //                    return this.addError(
    //        //                            this,
    //        //                            this.getParser().getCompilerClass(),
    //        //                            currentToken,
    //        //
    //        //                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
    //        //                            "Statement", "<identifier call>", currentToken.getTokenContent()
    //        //                    );
    //        //                default:
    //        //                    return IdentifierCallNode.builder()
    //        //                            .parser(this.getParser())
    //        //                            .build()
    //        //                            .parseAST(parentAST);
    //        //            }
    //        //        } else {
    //        //            switch (currentToken.getTokenContent()) {
    //        //                case "var":
    //        //                    return VariableNode.builder()
    //        //                            .parser(this.getParser())
    //        //                            .build()
    //        //                            .parseAST(parentAST);
    //        //                case "import":
    //        //                    return ImportNode.builder()
    //        //                            .parser(this.getParser())
    //        //                            .build()
    //        //                            .parseAST(parentAST);
    //        //                case "fun":
    //        //                    return FunctionNode.builder()
    //        //                            .parser(this.getParser())
    //        //                            .build()
    //        //                            .parseAST(parentAST);
    //        //                case "return":
    //        //                    return ReturnNode.builder()
    //        //                            .parser(this.getParser())
    //        //                            .build()
    //        //                            .parseAST(parentAST);
    //        //                default:
    //        //                    return IdentifierCallNode.builder()
    //        //                            .parser(this.getParser())
    //        //                            .build()
    //        //                            .parseAST(parentAST);
    //        //            }
    //        //        }
    //        return this;
    //    }
    
}
