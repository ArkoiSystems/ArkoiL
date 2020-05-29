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

import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.OperatorType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.SymbolType;
import com.arkoisystems.arkoicompiler.stage.parser.Parser;
import com.arkoisystems.arkoicompiler.stage.parser.ParserErrorType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.ArkoiNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.enums.ASTType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.enums.BlockType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.enums.TypeKind;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.Operable;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.IdentifierOperable;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.Expression;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.ReturnStatement;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.VariableStatement;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter
public class Block extends ArkoiNode
{
    
    public static Block GLOBAL_NODE = new Block(null, null, null, null);
    
    @Printable(name = "nodes")
    @NotNull
    private final List<ArkoiNode> nodes;
    
    @Printable(name = "type")
    @Nullable
    private BlockType blockType;
    
    @Builder
    protected Block(
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
    public Block parseAST(final @NotNull ArkoiNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        this.startAST(this.getParser().currentToken());
        
        if (this.getParser().matchesCurrentToken(SymbolType.OPENING_BRACE) != null) {
            this.parseBlock();
        } else if (this.getParser().matchesCurrentToken(OperatorType.EQUALS) != null) {
            this.parseInlinedBlock();
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
    private void parseBlock() {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        this.blockType = BlockType.BLOCK;
        this.getParser().nextToken();
        
        while (this.getParser().getPosition() < this.getParser().getTokens().length) {
            if (this.getParser().matchesCurrentToken(SymbolType.CLOSING_BRACE) != null)
                break;
            
            final ArkoiNode foundNode = this.getValidNode(
                    VariableStatement.GLOBAL_NODE,
                    IdentifierOperable.GLOBAL_NODE,
                    ReturnStatement.GLOBAL_NODE
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
    }
    
    private void parseInlinedBlock() {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        this.blockType = BlockType.INLINE;
        
        if(!Expression.GLOBAL_NODE.canParse(this.getParser(), 1)) {
            final ArkoiToken peekedToken = this.getParser().peekToken(1);
            this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    peekedToken,
            
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Inline block", "<expression>", peekedToken != null ? peekedToken.getTokenContent() : "nothing"
            );
            return;
        }
        
        this.getParser().nextToken();
        
        final Operable operableAST = Expression.expressionBuilder()
                .parser(this.getParser())
                .build()
                .parseAST(this);
        if (operableAST.isFailed()) {
            this.setFailed(true);
            return;
        }
        
        this.getNodes().add(operableAST);
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
    protected @NotNull TypeKind initializeTypeKind() {
        if (this.getBlockType() == BlockType.INLINE) {
            return this.getNodes().get(0).getTypeKind();
        } else if (this.getBlockType() == BlockType.BLOCK) {
            final HashMap<TypeKind, Integer> kinds = new HashMap<>();
            this.getNodes().stream()
                    .filter(node -> node instanceof ReturnStatement)
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
    
}
