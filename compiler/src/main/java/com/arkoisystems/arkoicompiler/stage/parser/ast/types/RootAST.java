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

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.ISyntaxParser;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.parser.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.parser.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.ArkoiASTNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.StatementAST;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.TypeKind;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RootAST extends ArkoiASTNode
{
    
    public static ISyntaxParser[] ROOT_PARSERS = new ISyntaxParser[] {
            AnnotationAST.ANNOTATION_PARSER,
            StatementAST.STATEMENT_PARSER,
    };
    
    @Getter
    @NotNull
    private final List<IASTNode> astNodes;
    
    @Builder
    private RootAST(
            @Nullable final SyntaxAnalyzer syntaxAnalyzer,
            @Nullable final ArkoiToken startToken,
            @Nullable final ArkoiToken endToken
    ) {
        super(syntaxAnalyzer, ASTType.ROOT, startToken, endToken);
        
        this.astNodes = new ArrayList<>();
    }
    
    @NotNull
    @Override
    public RootAST parseAST(@NotNull IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        Objects.requireNonNull(this.getMarkerFactory(), "markerFactory must not be null.");
        
        this.startAST(this.getSyntaxAnalyzer().currentToken(false));
        
        main_loop:
        while (this.getSyntaxAnalyzer().getPosition() < this.getSyntaxAnalyzer().getTokens().length) {
            for (final ISyntaxParser parser : ROOT_PARSERS) {
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
                    null,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ROOT_NO_PARSER_FOUND
            );
            this.skipToNextValidToken();
        }
        
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
