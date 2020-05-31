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
package com.arkoisystems.arkoicompiler.stages.parser.ast.types;

import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stages.lexer.token.LexerToken;
import com.arkoisystems.arkoicompiler.stages.parser.Parser;
import com.arkoisystems.arkoicompiler.stages.parser.ParserErrorType;
import com.arkoisystems.arkoicompiler.stages.parser.ast.ParserNode;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.statement.types.FunctionStatement;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.statement.types.ImportStatement;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.statement.types.VariableStatement;
import com.arkoisystems.arkoicompiler.stages.parser.ast.enums.ASTType;
import com.arkoisystems.arkoicompiler.stages.parser.ast.enums.TypeKind;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class Root extends ParserNode
{
    
    @Printable(name = "nodes")
    @NotNull
    private final List<ParserNode> nodes;
    
    @Builder
    protected Root(
            final @Nullable Parser parser,
            final @Nullable LexerToken startToken,
            final @Nullable LexerToken endToken
    ) {
        super(parser, ASTType.ROOT, startToken, endToken);
        
        this.nodes = new ArrayList<>();
    }
    
    @SneakyThrows
    @NotNull
    @Override
    public Root parseAST(@NotNull ParserNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        this.startAST(this.getParser().currentToken(false));
        while (this.getParser().getPosition() < this.getParser().getTokens().length) {
            final ParserNode foundNode = this.getValidNode(
                    FunctionStatement.GLOBAL_NODE,
                    ImportStatement.GLOBAL_NODE,
                    VariableStatement.GLOBAL_NODE
            );
            
            if (foundNode == null) {
                this.addError(
                        null,
                        this.getParser().getCompilerClass(),
                        this.getParser().currentToken(),
                        ParserErrorType.ROOT_NO_PARSER_FOUND
                );
                this.skipToNextValidToken();
                continue;
            }
            
            ParserNode astNode = foundNode.clone();
            astNode.setParser(this.getParser());
            astNode = astNode.parseAST(this);
            
            if (astNode.isFailed()) {
                this.skipToNextValidToken();
                continue;
            }
            
            this.getNodes().add(astNode);
            this.getParser().nextToken();
        }
        
        this.endAST(this.getParser().currentToken());
        return this;
    }
    
    @Override
    public void accept(final @NotNull IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @Override
    @NotNull
    public TypeKind getTypeKind() {
        return TypeKind.ERROR;
    }
    
}
