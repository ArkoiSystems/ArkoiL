/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.BadToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.TypeKeywordToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.StatementAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.BlockType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FunctionAST extends StatementAST
{
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private List<AnnotationAST> functionAnnotations = new ArrayList<>();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private IdentifierToken functionName;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private TypeAST functionReturnType;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private ParameterListAST functionParameters;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private BlockAST functionBlock;
    
    
    protected FunctionAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.FUNCTION);
    }
    
    
    @NotNull
    @Override
    public FunctionAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.FUN) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Function", "'fun'", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Function", "<identifier>", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
        this.setFunctionName((IdentifierToken) this.getSyntaxAnalyzer().nextToken());
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.OPENING_ARROW) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Function", "'<'", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
        this.getSyntaxAnalyzer().nextToken(2);
        
        if (TypeAST.TYPE_PARSER.canParse(this, this.getSyntaxAnalyzer())) {
            final TypeAST typeAST = TypeAST.TYPE_PARSER.parse(this, this.getSyntaxAnalyzer());
            this.getMarkerFactory().addFactory(typeAST.getMarkerFactory());
            
            if (typeAST.isFailed()) {
                this.failed();
                return this;
            }
            
            this.setFunctionReturnType(typeAST);
            this.getSyntaxAnalyzer().nextToken();
        } else this.setFunctionReturnType(TypeAST.builder(this.getSyntaxAnalyzer())
                .type(TypeKeywordToken.builder()
                        .type(TypeKind.VOID)
                        .build())
                .start(BadToken.builder()
                        .start(-1)
                        .end(-1)
                        .build())
                .end(BadToken.builder()
                        .start(-1)
                        .end(-1)
                        .build())
                .array(false)
                .build()
        );
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_ARROW) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Function", "'>'", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.OPENING_PARENTHESIS) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Function", "'('", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
        this.getSyntaxAnalyzer().nextToken();
        
        final ParameterListAST parameterListAST = ParameterListAST.builder(this.getSyntaxAnalyzer())
                .build()
                .parseAST(this);
        this.getMarkerFactory().addFactory(parameterListAST.getMarkerFactory());
        
        if (parameterListAST.isFailed()) {
            this.failed();
            return this;
        }
        
        this.setFunctionParameters(parameterListAST);
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_PARENTHESIS) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Function", "')'", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
        this.getSyntaxAnalyzer().nextToken();
        
        if (this.hasAnnotation("native")) {
            this.setEndToken(this.getSyntaxAnalyzer().currentToken());
            this.getMarkerFactory().done(this.getEndToken());
    
            this.setFunctionBlock(BlockAST.builder(this.getSyntaxAnalyzer())
                    .type(BlockType.NATIVE)
                    .start(this.getStartToken())
                    .end(this.getEndToken())
                    .build()
            );
            return this;
        }
        
        if (!BlockAST.BLOCK_PARSER.canParse(this, this.getSyntaxAnalyzer()))
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Function", "'block'", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
        final BlockAST blockAST = BlockAST.BLOCK_PARSER.parse(this, this.getSyntaxAnalyzer());
        this.getMarkerFactory().addFactory(blockAST.getMarkerFactory());
        
        if (blockAST.isFailed()) {
            this.failed();
            return this;
        }
        
        this.setFunctionBlock(blockAST);
        
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
    }
    
    
    @Override
    public void accept(@NotNull final IVisitor visitor) {
        visitor.visit(this);
    }
    
    
    public String getFunctionDescription() {
        Objects.requireNonNull(this.getFunctionName(), "functionName must not be null.");
        return this.getFunctionName().getTokenContent() + "()";
    }
    
    
    public boolean hasAnnotation(@NotNull final String annotationName) {
        for (final AnnotationAST annotationAST : this.functionAnnotations) {
            if (annotationAST.getAnnotationName() == null)
                continue;
            if (annotationAST.getAnnotationName().getTokenContent().equals(annotationName))
                return true;
        }
        return false;
    }
    
    
    public static FunctionASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new FunctionASTBuilder(syntaxAnalyzer);
    }
    
    
    public static FunctionASTBuilder builder() {
        return new FunctionASTBuilder();
    }
    
    
    public static class FunctionASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private List<AnnotationAST> functionAnnotations;
        
        
        @Nullable
        private IdentifierToken functionName;
        
        
        @Nullable
        private TypeAST functionReturnType;
        
        
        @Nullable
        private ParameterListAST functionParameterList;
        
        
        @Nullable
        private BlockAST functionBlock;
        
        
        private AbstractToken startToken, endToken;
        
        
        public FunctionASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public FunctionASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public FunctionASTBuilder annotations(final List<AnnotationAST> functionAnnotations) {
            this.functionAnnotations = functionAnnotations;
            return this;
        }
        
        
        public FunctionASTBuilder name(final IdentifierToken functionName) {
            this.functionName = functionName;
            return this;
        }
        
        
        public FunctionASTBuilder returnType(final TypeAST functionReturnType) {
            this.functionReturnType = functionReturnType;
            return this;
        }
        
        
        public FunctionASTBuilder parameters(final ParameterListAST functionParameterList) {
            this.functionParameterList = functionParameterList;
            return this;
        }
        
        
        public FunctionASTBuilder block(final BlockAST functionBlock) {
            this.functionBlock = functionBlock;
            return this;
        }
        
        
        public FunctionASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public FunctionASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public FunctionAST build() {
            final FunctionAST functionAST = new FunctionAST(this.syntaxAnalyzer);
            if (this.functionAnnotations != null)
                functionAST.setFunctionAnnotations(this.functionAnnotations);
            if (this.functionName != null)
                functionAST.setFunctionName(this.functionName);
            if (this.functionReturnType != null)
                functionAST.setFunctionReturnType(this.functionReturnType);
            if (this.functionParameterList != null)
                functionAST.setFunctionParameters(this.functionParameterList);
            if (this.functionBlock != null)
                functionAST.setFunctionBlock(this.functionBlock);
            functionAST.setStartToken(this.startToken);
            functionAST.getMarkerFactory().getCurrentMarker().setStart(functionAST.getStartToken());
            functionAST.setEndToken(this.endToken);
            functionAST.getMarkerFactory().getCurrentMarker().setEnd(functionAST.getEndToken());
            return functionAST;
        }
        
    }
    
}
