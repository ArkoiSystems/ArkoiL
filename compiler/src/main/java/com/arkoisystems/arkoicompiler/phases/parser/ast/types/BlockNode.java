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
package com.arkoisystems.arkoicompiler.phases.parser.ast.types;

import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.phases.lexer.token.LexerToken;
import com.arkoisystems.arkoicompiler.phases.lexer.token.enums.OperatorType;
import com.arkoisystems.arkoicompiler.phases.lexer.token.enums.SymbolType;
import com.arkoisystems.arkoicompiler.phases.parser.Parser;
import com.arkoisystems.arkoicompiler.phases.parser.ParserErrorType;
import com.arkoisystems.arkoicompiler.phases.parser.ast.ParserNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.enums.BlockType;
import com.arkoisystems.arkoicompiler.phases.parser.ast.enums.TypeKind;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.OperableNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.IdentifierNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.expression.ExpressionNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.statement.types.FunctionNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.statement.types.ImportNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.statement.types.ReturnNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.statement.types.VariableNode;
import com.arkoisystems.arkoicompiler.phases.parser.SymbolTable;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter
public class BlockNode extends ParserNode
{
    
    public static BlockNode GLOBAL_NODE = new BlockNode(null, null, null, null, null);
    
    @Printable(name = "nodes")
    @NotNull
    private final List<ParserNode> nodes;
    
    @Printable(name = "type")
    @Nullable
    private BlockType blockType;
    
    @Builder
    protected BlockNode(
            final @Nullable Parser parser,
            final @Nullable SymbolTable currentScope,
            final @Nullable BlockType blockType,
            final @Nullable LexerToken startToken,
            final @Nullable LexerToken endToken
    ) {
        super(parser, currentScope, startToken, endToken);
        
        this.nodes = new ArrayList<>();
        this.blockType = blockType;
    }
    
    @NotNull
    @Override
    public BlockNode parseAST(final @NotNull ParserNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        this.startAST(this.getParser().currentToken());
        
        if (this.getParser().matchesCurrentToken(SymbolType.OPENING_BRACE) != null) {
            this.parseBlock();
        } else if (this.getParser().matchesCurrentToken(OperatorType.EQUALS) != null) {
            this.parseInlinedBlock();
        } else {
            final LexerToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Parameter",
                            "'{' or '='",
                            currentToken != null ? currentToken.getTokenContent() : "nothing"
                    )
            );
        }
        
        this.endAST(this.getParser().currentToken());
        return this;
    }
    
    @SneakyThrows
    private void parseBlock() {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        this.blockType = BlockType.BLOCK;
        this.getParser().nextToken();
        
        while (this.getParser().getPosition() < this.getParser().getTokens().length) {
            if (this.getParser().matchesCurrentToken(SymbolType.CLOSING_BRACE) != null)
                break;
            
            final ParserNode foundNode = this.getValidNode(
                    VariableNode.GLOBAL_NODE,
                    IdentifierNode.GLOBAL_NODE,
                    ReturnNode.GLOBAL_NODE,
                    BlockNode.GLOBAL_NODE
            );
    
            if (foundNode == null) {
                this.addError(
                        null,
                        this.getParser().getCompilerClass(),
                        this.getParser().currentToken(),
                        ParserErrorType.BLOCK_NO_PARSER_FOUND
                );
                this.findValidToken();
                continue;
            }
    
            ParserNode astNode = foundNode.clone();
            if (astNode instanceof BlockNode)
                astNode.setCurrentScope(new SymbolTable(this.getCurrentScope()));
            else astNode.setCurrentScope(this.getCurrentScope());
            astNode.setParser(this.getParser());
            astNode = astNode.parseAST(this);
    
            if (astNode.isFailed()) {
                this.setFailed(true);
                this.findValidToken();
                continue;
            }
    
            this.getNodes().add(astNode);
            this.getParser().nextToken();
        }
    }
    
    private void parseInlinedBlock() {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
    
        this.blockType = BlockType.INLINE;
    
        if (!ExpressionNode.GLOBAL_NODE.canParse(this.getParser(), 1)) {
            final LexerToken peekedToken = this.getParser().peekToken(1);
            this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    peekedToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Inline block",
                            "<expression>",
                            peekedToken != null ? peekedToken.getTokenContent() : "nothing"
                    )
            );
            return;
        }
    
        this.getParser().nextToken();
    
        final OperableNode operableNode = ExpressionNode.expressionBuilder()
                .currentScope(this.getCurrentScope())
                .parser(this.getParser())
                .build()
                .parseAST(this);
        if (operableNode.isFailed()) {
            this.setFailed(true);
            return;
        }
    
        this.getNodes().add(operableNode);
    }
    
    @Override
    public boolean canParse(final @NotNull Parser parser, final int offset) {
        return parser.matchesPeekToken(offset, OperatorType.EQUALS) != null ||
                parser.matchesPeekToken(offset, SymbolType.OPENING_BRACE) != null;
    }
    
    @Override
    public void accept(final @NotNull IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @Override
    @NotNull
    public TypeKind getTypeKind() {
        if (this.getBlockType() == BlockType.INLINE) {
            return this.getNodes().get(0).getTypeKind();
        } else if (this.getBlockType() == BlockType.BLOCK) {
            final HashMap<TypeKind, Integer> kinds = new HashMap<>();
            this.getNodes().stream()
                    .filter(node -> node instanceof ReturnNode)
                    .forEach(statement -> {
                        final int count = kinds.getOrDefault(statement.getTypeKind(), 0);
                        kinds.put(statement.getTypeKind(), count + 1);
                    });
        
            if (kinds.size() == 0)
                return TypeKind.VOID;
        
            return Collections.max(kinds.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
        }
        return TypeKind.UNDEFINED;
    }
    
    private void findValidToken() {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        while (this.getParser().getPosition() < this.getParser().getTokens().length) {
            final ParserNode foundNode = this.getValidNode(
                    VariableNode.GLOBAL_NODE,
                    IdentifierNode.GLOBAL_NODE,
                    ReturnNode.GLOBAL_NODE,
                    BlockNode.GLOBAL_NODE
            );
            
            if(foundNode != null || this.getParser().matchesCurrentToken(SymbolType.CLOSING_BRACE) != null)
                break;
            
            this.getParser().nextToken();
        }
    }
    
}
