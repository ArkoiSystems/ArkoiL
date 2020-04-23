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
import com.arkoisystems.arkoicompiler.api.ISyntaxParser;
import com.arkoisystems.arkoicompiler.api.IToken;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.OperatorType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiASTNode;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.ExpressionAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.StatementAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.BlockType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.marker.ArkoiMarker;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.marker.MarkerFactory;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parsers.BlockParser;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlockAST extends ArkoiASTNode
{
    
    public static BlockParser BLOCK_PARSER = new BlockParser();
    
    
    private static final ISyntaxParser[] BLOCK_PARSERS = new ISyntaxParser[] {
            StatementAST.STATEMENT_PARSER,
            BlockAST.BLOCK_PARSER,
    };
    
    
    @Getter
    @NotNull
    private final List<IASTNode> astNodes;
    
    
    @Getter
    @Nullable
    private BlockType blockType;
    
    
    @Builder
    private BlockAST(
            @Nullable final SyntaxAnalyzer syntaxAnalyzer,
            @Nullable final BlockType blockType,
            @Nullable final IToken startToken,
            @Nullable final IToken endToken
    ) {
        super(null, syntaxAnalyzer, ASTType.BLOCK, startToken, endToken);
        
        this.astNodes = new ArrayList<>();
        this.blockType = blockType;
        
        this.setMarkerFactory(new MarkerFactory<>(new ArkoiMarker<>(this.getAstType()), this));
    }
    
    
    @NotNull
    @Override
    public BlockAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        
        this.startAST(this.getSyntaxAnalyzer().currentToken());
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_BRACE) != null) {
            this.blockType = BlockType.BLOCK;
            this.getSyntaxAnalyzer().nextToken();
            
            main_loop:
            while (this.getSyntaxAnalyzer().getPosition() < this.getSyntaxAnalyzer().getTokens().length) {
                if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACE) != null)
                    break;
    
                for (final ISyntaxParser parser : BLOCK_PARSERS) {
                    if (!parser.canParse(this, this.getSyntaxAnalyzer()))
                        continue;
    
                    final IASTNode astNode = parser.parse(this, this.getSyntaxAnalyzer());
                    this.getMarkerFactory().addFactory(astNode.getMarkerFactory());
    
                    if (astNode.isFailed()) {
                        this.skipToNextValidToken();
                        continue main_loop;
                    }
    
                    this.getAstNodes().add(astNode);
                    this.getSyntaxAnalyzer().nextToken();
                    continue main_loop;
                }
    
                this.addError(
                        this,
                        this.getSyntaxAnalyzer().getCompilerClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        SyntaxErrorType.BLOCK_NO_PARSER_FOUND
                );
                this.skipToNextValidToken();
            }
        } else if (this.getSyntaxAnalyzer().matchesCurrentToken(OperatorType.EQUALS) != null) {
            this.blockType = BlockType.INLINE;
            this.getSyntaxAnalyzer().nextToken();
            
            if (!ExpressionAST.EXPRESSION_PARSER.canParse(this, this.getSyntaxAnalyzer()))
                return this.addError(
                        this,
                        this.getSyntaxAnalyzer().getCompilerClass(),
                        this.getSyntaxAnalyzer().currentToken(),
        
                        SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                        "Block", "<expression>", this.getSyntaxAnalyzer().currentToken().getTokenContent()
                );
            
            final OperableAST operableAST = ExpressionAST.EXPRESSION_PARSER.parse(this, this.getSyntaxAnalyzer());
            this.getMarkerFactory().addFactory(operableAST.getMarkerFactory());
            
            if (operableAST.isFailed()) {
                this.failed();
                return this;
            }
            
            this.getAstNodes().add(operableAST);
        } else return this.addError(
                this,
                this.getSyntaxAnalyzer().getCompilerClass(),
                this.getSyntaxAnalyzer().currentToken(),
        
                SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                "Parameter", "'{' or '='", this.getSyntaxAnalyzer().currentToken().getTokenContent()
        );
        
        this.endAST(this.getSyntaxAnalyzer().currentToken());
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
    
}
