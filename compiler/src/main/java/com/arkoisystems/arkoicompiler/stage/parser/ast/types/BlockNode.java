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
package com.arkoisystems.arkoicompiler.stage.parser.ast.types;

import com.arkoisystems.arkoicompiler.api.ISyntaxParser;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.OperatorType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.SymbolType;
import com.arkoisystems.arkoicompiler.stage.parser.Parser;
import com.arkoisystems.arkoicompiler.stage.parser.ParserErrorType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.ArkoiNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.OperableNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.IdentifierCallNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.ExpressionNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.FunctionNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.ImportNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.ReturnNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.VariableNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.BlockType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.TypeKind;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class BlockNode extends ArkoiNode
{
    
    public static BlockNode GLOBAL_NODE = new BlockNode(null, null, null, null);
    
    private static final ISyntaxParser[] BLOCK_PARSERS = new ISyntaxParser[] {
            //            StatementNode.STATEMENT_PARSER,
            //            BlockNode.BLOCK_PARSER,
    };
    
    @Printable(name = "nodes")
    @NotNull
    private final List<ArkoiNode> nodes;
    
    @Printable(name = "type")
    @Nullable
    private BlockType blockType;
    
    @Builder
    protected BlockNode(
            final @Nullable Parser parser,
            final @Nullable BlockType blockType,
            final @Nullable ArkoiToken startToken,
            final @Nullable ArkoiToken endToken
    ) {
        super(parser, ASTType.BLOCK, startToken, endToken);
        
        this.nodes = new ArrayList<>();
        this.blockType = blockType;
    }
    
    @NotNull
    @Override
    public BlockNode parseAST(final @NotNull ArkoiNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        this.startAST(this.getParser().currentToken());
        
        if (this.getParser().matchesCurrentToken(SymbolType.OPENING_BRACE) != null) {
            final BlockNode blockAST = this.parseBlock();
            if (blockAST != null)
                return blockAST;
        } else if (this.getParser().matchesCurrentToken(OperatorType.EQUALS) != null) {
            final BlockNode blockAST = this.parseInlinedBlock();
            if (blockAST != null)
                return blockAST;
        } else {
            final ArkoiToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Parameter", "'{' or '='", currentToken != null ? currentToken.getTokenContent() : "nothing"
            );
        }
        
        this.endAST(this.getParser().currentToken());
        return this;
    }
    
    @SneakyThrows
    @Nullable
    private BlockNode parseBlock() {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        this.blockType = BlockType.BLOCK;
        this.getParser().nextToken();
        
        while (this.getParser().getPosition() < this.getParser().getTokens().length) {
            if (this.getParser().matchesCurrentToken(SymbolType.CLOSING_BRACE) != null)
                break;
            
            final ArkoiNode foundNode = this.getValidNode(
                    VariableNode.GLOBAL_NODE,
                    IdentifierCallNode.GLOBAL_NODE,
                    ReturnNode.GLOBAL_NODE
            );
    
            if (foundNode == null) {
                this.addError(
                        null,
                        this.getParser().getCompilerClass(),
                        this.getParser().currentToken(),
                        ParserErrorType.BLOCK_NO_PARSER_FOUND
                );
                this.skipToNextValidToken();
                continue;
            }
    
            // TODO: 5/27/20 Think about a better solution
            ArkoiNode astNode = foundNode.clone();
            astNode.setParser(this.getParser());
            astNode = astNode.parseAST(this);
    
            if (astNode.isFailed()) {
                this.skipToNextValidToken();
                continue;
            }
    
            this.getNodes().add(astNode);
            this.getParser().nextToken();
        }
    
        return null;
    }
    
    @Nullable
    private BlockNode parseInlinedBlock() {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        this.blockType = BlockType.INLINE;
        this.getParser().nextToken();
        
//        if (!ExpressionNode.EXPRESSION_PARSER.canParse(this, this.getParser())) {
//            final ArkoiToken currentToken = this.getParser().currentToken();
//            return this.addError(
//                    this,
//                    this.getParser().getCompilerClass(),
//                    currentToken,
//
//                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
//                    "Block", "<expression>", currentToken != null ? currentToken.getTokenContent() : "nothing"
//            );
//        }
        
        final OperableNode operableAST = ExpressionNode.expressionBuilder()
                .parser(this.getParser())
                .build()
                .parseAST(this);
        if (operableAST.isFailed()) {
            this.setFailed(true);
            return this;
        }
        
        this.getNodes().add(operableAST);
        return null;
    }
    
    @Override
    public boolean canParse(final @NotNull Parser parser, final int offset) {
        return parser.matchesPeekToken(0, OperatorType.EQUALS) != null ||
                parser.matchesPeekToken(0, SymbolType.OPENING_BRACE) != null;
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
