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
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.BadToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiASTNode;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.StatementAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parsers.AnnotationParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AnnotationAST extends ArkoiASTNode
{
    
    public static AnnotationParser ANNOTATION_PARSER = new AnnotationParser();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private List<AnnotationAST> annotationStorage = new ArrayList<>();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private IdentifierCallAST annotationCall;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private ArgumentListAST annotationArguments;
    
    
    protected AnnotationAST(final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.ANNOTATION);
    }
    
    
    @NotNull
    @Override
    public IASTNode parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.AT_SIGN) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Annotation", "'@'", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Annotation", "<identifier>", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
        this.getSyntaxAnalyzer().nextToken();
        
        final IdentifierCallAST identifierCallAST = IdentifierCallAST.builder(this.getSyntaxAnalyzer())
                .build()
                .parseAST(this);
        this.getMarkerFactory().addFactory(identifierCallAST.getMarkerFactory());
        
        if (identifierCallAST.isFailed()) {
            this.failed();
            return this;
        }
        
        this.setAnnotationCall(identifierCallAST);
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.OPENING_BRACKET) != null) {
            this.getSyntaxAnalyzer().nextToken();
            
            final ArgumentListAST arguments = ArgumentListAST.builder(this.getSyntaxAnalyzer())
                    .build()
                    .parseAST(this);
            this.getMarkerFactory().addFactory(arguments.getMarkerFactory());
            
            if (arguments.isFailed()) {
                this.failed();
                return this;
            }
            
            this.setAnnotationArguments(arguments);
        } else this.setAnnotationArguments(ArgumentListAST.builder()
                .start(BadToken.builder()
                        .start(-1)
                        .end(-1)
                        .build())
                .end(BadToken.builder()
                        .start(-1)
                        .end(-1)
                        .build())
                .build());
        
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        
        this.getSyntaxAnalyzer().nextToken();
        this.getAnnotationStorage().add(this);
        
        if (ANNOTATION_PARSER.canParse(parentAST, this.getSyntaxAnalyzer())) {
            final IASTNode astNode = AnnotationAST.builder(this.getSyntaxAnalyzer())
                    .annotations(this.annotationStorage)
                    .build()
                    .parseAST(parentAST);
    
            this.getMarkerFactory().addFactory(astNode.getMarkerFactory());
            if (!astNode.isFailed())
                return astNode;
    
            this.failed();
            return this;
        }
        
        if (!StatementAST.STATEMENT_PARSER.canParse(parentAST, this.getSyntaxAnalyzer()))
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Annotation", "<function>, <variable> or <annotation>", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.FUN) != null && this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.VAR) != null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
        
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Annotation", "<function>, <variable> or <annotation>", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        } else if (this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.FUN) != null) {
            final FunctionAST functionAST = FunctionAST.builder(this.getSyntaxAnalyzer())
                    .annotations(this.getAnnotationStorage())
                    .build()
                    .parseAST(parentAST);
    
            this.getMarkerFactory().addFactory(functionAST.getMarkerFactory());
            if (!functionAST.isFailed())
                return functionAST;
            
            this.failed();
            return this;
        } else {
            final VariableAST variableAST = VariableAST.builder(this.getSyntaxAnalyzer())
                    .annotations(this.getAnnotationStorage())
                    .build()
                    .parseAST(parentAST);
            
            this.getMarkerFactory().addFactory(variableAST.getMarkerFactory());
            if (!variableAST.isFailed())
                return variableAST;
            
            this.failed();
            return this;
        }
    }
    
    
    @Override
    public void accept(@NotNull final IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    
    @Override
    public @NotNull TypeKind getTypeKind() {
        return TypeKind.UNDEFINED;
    }
    
    
    @Nullable
    public IdentifierToken getAnnotationName() {
        if (this.getAnnotationCall() == null)
            return null;
    
        IdentifierCallAST identifierCallAST = this.getAnnotationCall();
        while (identifierCallAST.getNextIdentifierCall() != null)
            identifierCallAST = identifierCallAST.getNextIdentifierCall();
        return identifierCallAST.getCalledIdentifier();
    }
    
    
    public static AnnotationASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new AnnotationASTBuilder(syntaxAnalyzer);
    }
    
    
    public static AnnotationASTBuilder builder() {
        return new AnnotationASTBuilder();
    }
    
    
    public static class AnnotationASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private List<AnnotationAST> annotationStorage;
        
        
        @Nullable
        private ArgumentListAST annotationArguments;
        
        
        @Nullable
        private IdentifierCallAST annotationCall;
        
        
        private ArkoiToken startToken, endToken;
        
        
        public AnnotationASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public AnnotationASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public AnnotationASTBuilder annotations(final List<AnnotationAST> annotationStorage) {
            this.annotationStorage = annotationStorage;
            return this;
        }
        
        
        public AnnotationASTBuilder call(final IdentifierCallAST annotationCall) {
            this.annotationCall = annotationCall;
            return this;
        }
        
        
        public AnnotationASTBuilder arguments(final ArgumentListAST annotationArguments) {
            this.annotationArguments = annotationArguments;
            return this;
        }
        
        
        public AnnotationASTBuilder start(final ArkoiToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public AnnotationASTBuilder end(final ArkoiToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public AnnotationAST build() {
            final AnnotationAST annotationAST = new AnnotationAST(this.syntaxAnalyzer);
            if (this.annotationStorage != null)
                annotationAST.setAnnotationStorage(this.annotationStorage);
            if (this.annotationCall != null)
                annotationAST.setAnnotationCall(this.annotationCall);
            if (this.annotationArguments != null)
                annotationAST.setAnnotationArguments(this.annotationArguments);
            annotationAST.setStartToken(this.startToken);
            annotationAST.getMarkerFactory().getCurrentMarker().setStart(annotationAST.getStartToken());
            annotationAST.setEndToken(this.endToken);
            annotationAST.getMarkerFactory().getCurrentMarker().setEnd(annotationAST.getEndToken());
            return annotationAST;
        }
        
    }
    
}
