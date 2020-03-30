/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.TypeKeywordToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.BlockType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FunctionDefinitionSyntaxAST extends AbstractStatementSyntaxAST
{
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private List<AnnotationSyntaxAST> functionAnnotations = new ArrayList<>();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private IdentifierToken functionName = IdentifierToken
            .builder()
            .content("Undefined identifier for \"functionName\"")
            .build();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private TypeSyntaxAST functionReturnType;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private List<ParameterSyntaxAST> functionParameters = new ArrayList<>();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private BlockSyntaxAST functionBlock;
    
    
    protected FunctionDefinitionSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.FUNCTION_DEFINITION);
    }
    
    
    @Override
    public Optional<FunctionDefinitionSyntaxAST> parseAST(@NotNull final AbstractSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
    
        if (!(parentAST instanceof RootSyntaxAST)) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_PARENT
            );
            return Optional.empty();
        }
    
        if (this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.FUN) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_START
            );
            return Optional.empty();
        }
    
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
    
        if (this.getSyntaxAnalyzer().matchesNextToken(TokenType.IDENTIFIER) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_NO_NAME
            );
            return Optional.empty();
        }
        this.functionName = (IdentifierToken) this.getSyntaxAnalyzer().currentToken();
    
        if (this.getSyntaxAnalyzer().matchesNextToken(SymbolType.OPENING_ARROW) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_RETURN_TYPE_START
            );
            return Optional.empty();
        }
        this.getSyntaxAnalyzer().nextToken();
    
        if (TypeSyntaxAST.TYPE_PARSER.canParse(this, this.getSyntaxAnalyzer())) {
            final Optional<TypeSyntaxAST> optionalTypeSyntaxAST = TypeSyntaxAST.TYPE_PARSER.parse(this, this.getSyntaxAnalyzer());
            if (optionalTypeSyntaxAST.isEmpty())
                return Optional.empty();
        
            this.getMarkerFactory().addFactory(optionalTypeSyntaxAST.get().getMarkerFactory());
            this.functionReturnType = optionalTypeSyntaxAST.get();
            this.getSyntaxAnalyzer().nextToken();
        } else this.functionReturnType = TypeSyntaxAST
                .builder(this.getSyntaxAnalyzer())
                .type(TypeKeywordToken
                        .builder()
                        .type(TypeKeywordType.VOID)
                        .build())
                .array(false)
                .build();
    
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_ARROW) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_RETURN_TYPE_ENDING
            );
            return Optional.empty();
        }
    
        if (this.getSyntaxAnalyzer().matchesNextToken(SymbolType.OPENING_PARENTHESIS) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_ARGUMENTS_START
            );
            return Optional.empty();
        }
    
        final Optional<List<ParameterSyntaxAST>> parameters = ParameterSyntaxAST.parseParameters(this, this.getSyntaxAnalyzer());
        if (parameters.isEmpty())
            return Optional.empty();
        
        parameters.get().forEach(parameterSyntaxAST -> this.getMarkerFactory().addFactory(parameterSyntaxAST.getMarkerFactory()));
        this.functionParameters = parameters.get();
    
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_PARENTHESIS) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_ARGUMENTS_ENDING
            );
            return Optional.empty();
        }
        this.getSyntaxAnalyzer().nextToken();
    
        if (this.hasAnnotation("native")) {
            this.functionBlock = BlockSyntaxAST
                    .builder(this.getSyntaxAnalyzer())
                    .type(BlockType.NATIVE)
                    .build();
            return Optional.of(this);
        }
    
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_BRACE) == null && this.getSyntaxAnalyzer().matchesCurrentToken(OperatorType.EQUALS) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_BLOCK_START
            );
            return Optional.empty();
        }
    
        if (!BlockSyntaxAST.BLOCK_PARSER.canParse(this, this.getSyntaxAnalyzer())) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_NO_VALID_BLOCK
            );
            return Optional.empty();
        }
        
        final Optional<BlockSyntaxAST> optionalBlockSyntaxAST = BlockSyntaxAST.BLOCK_PARSER.parse(this, this.getSyntaxAnalyzer());
        if (optionalBlockSyntaxAST.isEmpty())
            return Optional.empty();
        
        this.getMarkerFactory().addFactory(optionalBlockSyntaxAST.get().getMarkerFactory());
        this.functionBlock = optionalBlockSyntaxAST.get();
    
        if (this.functionBlock.getBlockType() == BlockType.BLOCK && this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACE) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_BLOCK_ENDING
            );
            return Optional.empty();
        }
        
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        return Optional.of(this);
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "├── annotations: " + (this.getFunctionAnnotations().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getFunctionAnnotations().size(); index++) {
            final AnnotationSyntaxAST abstractSyntaxAST = this.getFunctionAnnotations().get(index);
            if (index == this.getFunctionAnnotations().size() - 1) {
                printStream.println(indents + "│   └── " + abstractSyntaxAST.getClass().getSimpleName());
                abstractSyntaxAST.printSyntaxAST(printStream, indents + "│       ");
            } else {
                printStream.println(indents + "│   ├── " + abstractSyntaxAST.getClass().getSimpleName());
                abstractSyntaxAST.printSyntaxAST(printStream, indents + "│   │   ");
                printStream.println(indents + "│   │   ");
            }
        }
        printStream.println(indents + "│");
        printStream.println(indents + "├── name: " + this.getFunctionName().getTokenContent());
        printStream.println(indents + "├── type: " + (this.getFunctionReturnType() != null ? this.getFunctionReturnType().getTypeKeywordToken().getKeywordType() + (this.getFunctionReturnType().isArray() ? "[]" : "") : null));
        printStream.println(indents + "│");
        printStream.println(indents + "├── arguments: " + (this.getFunctionParameters().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getFunctionParameters().size(); index++) {
            final ParameterSyntaxAST abstractSyntaxAST = this.getFunctionParameters().get(index);
            if (index == this.getFunctionParameters().size() - 1) {
                printStream.println(indents + "│   └── " + abstractSyntaxAST.getClass().getSimpleName());
                abstractSyntaxAST.printSyntaxAST(printStream, indents + "│       ");
            } else {
                printStream.println(indents + "│   ├── " + abstractSyntaxAST.getClass().getSimpleName());
                abstractSyntaxAST.printSyntaxAST(printStream, indents + "│   │   ");
                printStream.println(indents + "│   │   ");
            }
        }
        printStream.println(indents + "│");
        printStream.println(indents + "└── block: " + (this.getFunctionBlock() == null ? null : ""));
        if (this.getFunctionBlock() != null)
            this.getFunctionBlock().printSyntaxAST(printStream, indents + "     ");
    }
    
    
    public boolean hasAnnotation(@NotNull final String annotationName) {
        for (final AnnotationSyntaxAST annotationSyntaxAST : this.functionAnnotations) {
            if(annotationSyntaxAST.getAnnotationName() == null)
                continue;
            if (annotationSyntaxAST.getAnnotationName().getTokenContent().equals(annotationName))
                return true;
        }
        return false;
    }
    
    
    public static FunctionDefinitionSyntaxASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new FunctionDefinitionSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static FunctionDefinitionSyntaxASTBuilder builder() {
        return new FunctionDefinitionSyntaxASTBuilder();
    }
    
    
    public static class FunctionDefinitionSyntaxASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private List<AnnotationSyntaxAST> functionAnnotations;
        
        
        @Nullable
        private IdentifierToken functionName;
        
        
        @Nullable
        private TypeSyntaxAST functionReturnType;
        
        
        @Nullable
        private List<ParameterSyntaxAST> functionParameters;
        
        
        @Nullable
        private BlockSyntaxAST functionBlock;
        
        
        private AbstractToken startToken, endToken;
        
        
        public FunctionDefinitionSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public FunctionDefinitionSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public FunctionDefinitionSyntaxASTBuilder annotations(final List<AnnotationSyntaxAST> functionAnnotations) {
            this.functionAnnotations = functionAnnotations;
            return this;
        }
        
        
        public FunctionDefinitionSyntaxASTBuilder name(final IdentifierToken functionName) {
            this.functionName = functionName;
            return this;
        }
        
        
        public FunctionDefinitionSyntaxASTBuilder returnType(final TypeSyntaxAST functionReturnType) {
            this.functionReturnType = functionReturnType;
            return this;
        }
        
        
        public FunctionDefinitionSyntaxASTBuilder parameters(final List<ParameterSyntaxAST> functionParameters) {
            this.functionParameters = functionParameters;
            return this;
        }
        
        
        public FunctionDefinitionSyntaxASTBuilder block(final BlockSyntaxAST functionBlock) {
            this.functionBlock = functionBlock;
            return this;
        }
        
        
        public FunctionDefinitionSyntaxASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public FunctionDefinitionSyntaxASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public FunctionDefinitionSyntaxAST build() {
            final FunctionDefinitionSyntaxAST functionDefinitionSyntaxAST = new FunctionDefinitionSyntaxAST(this.syntaxAnalyzer);
            if (this.functionAnnotations != null)
                functionDefinitionSyntaxAST.setFunctionAnnotations(this.functionAnnotations);
            if (this.functionName != null)
                functionDefinitionSyntaxAST.setFunctionName(this.functionName);
            if (this.functionReturnType != null)
                functionDefinitionSyntaxAST.setFunctionReturnType(this.functionReturnType);
            if (this.functionParameters != null)
                functionDefinitionSyntaxAST.setFunctionParameters(this.functionParameters);
            if (this.functionBlock != null)
                functionDefinitionSyntaxAST.setFunctionBlock(this.functionBlock);
            functionDefinitionSyntaxAST.setStartToken(this.startToken);
            functionDefinitionSyntaxAST.setEndToken(this.endToken);
            return functionDefinitionSyntaxAST;
        }
        
    }
    
}
