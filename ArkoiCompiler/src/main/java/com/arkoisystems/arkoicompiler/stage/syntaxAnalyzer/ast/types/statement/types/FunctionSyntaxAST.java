/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.api.ICompilerSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.BadToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.TypeKeywordToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.OperatorType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.BlockType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FunctionSyntaxAST extends AbstractStatementSyntaxAST
{
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private List<AnnotationSyntaxAST> functionAnnotations = new ArrayList<>();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private IdentifierToken functionName;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private TypeSyntaxAST functionReturnType;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private ParameterListSyntaxAST functionParameters;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private BlockSyntaxAST functionBlock;
    
    
    protected FunctionSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.FUNCTION);
    }
    
    
    @NotNull
    @Override
    public FunctionSyntaxAST parseAST(@NotNull final ICompilerSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (!(parentAST instanceof RootSyntaxAST)) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_PARENT
            );
        }
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.FUN) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_START
            );
        }
        
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_NO_NAME
            );
        } else this.functionName = (IdentifierToken) this.getSyntaxAnalyzer().nextToken();
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.OPENING_ARROW) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_RETURN_TYPE_START
            );
        } else this.getSyntaxAnalyzer().nextToken(2);
        
        if (TypeSyntaxAST.TYPE_PARSER.canParse(this, this.getSyntaxAnalyzer())) {
            final TypeSyntaxAST typeSyntaxAST = TypeSyntaxAST.TYPE_PARSER.parse(this, this.getSyntaxAnalyzer());
            this.getMarkerFactory().addFactory(typeSyntaxAST.getMarkerFactory());
            
            if (typeSyntaxAST.isFailed()) {
                this.failed();
                return this;
            } else this.functionReturnType = typeSyntaxAST;
            
            this.getSyntaxAnalyzer().nextToken();
        } else this.functionReturnType = TypeSyntaxAST
                .builder(this.getSyntaxAnalyzer())
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
                .build();
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_ARROW) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_RETURN_TYPE_ENDING
            );
        }
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.OPENING_PARENTHESIS) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_ARGUMENTS_START
            );
        } else this.getSyntaxAnalyzer().nextToken();
        
        final ParameterListSyntaxAST parameterListSyntaxAST = ParameterListSyntaxAST.builder(this.getSyntaxAnalyzer())
                .build()
                .parseAST(this);
        this.getMarkerFactory().addFactory(parameterListSyntaxAST.getMarkerFactory());
        
        if (parameterListSyntaxAST.isFailed()) {
            this.failed();
            return this;
        } else this.functionParameters = parameterListSyntaxAST;
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_PARENTHESIS) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_ARGUMENTS_ENDING
            );
        }
        this.getSyntaxAnalyzer().nextToken();
        
        if (this.hasAnnotation("native")) {
            this.setEndToken(this.getSyntaxAnalyzer().currentToken());
            this.getMarkerFactory().done(this.getEndToken());
    
            this.functionBlock = BlockSyntaxAST
                    .builder(this.getSyntaxAnalyzer())
                    .type(BlockType.NATIVE)
                    .start(this.getStartToken())
                    .end(this.getEndToken())
                    .build();
            return this;
        }
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_BRACE) == null && this.getSyntaxAnalyzer().matchesCurrentToken(OperatorType.EQUALS) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_BLOCK_START
            );
        }
        
        if (!BlockSyntaxAST.BLOCK_PARSER.canParse(this, this.getSyntaxAnalyzer())) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_NO_VALID_BLOCK
            );
        }
        
        final BlockSyntaxAST blockSyntaxAST = BlockSyntaxAST.BLOCK_PARSER.parse(this, this.getSyntaxAnalyzer());
        this.getMarkerFactory().addFactory(blockSyntaxAST.getMarkerFactory());
        
        if (blockSyntaxAST.isFailed()) {
            this.failed();
            return this;
        } else this.functionBlock = blockSyntaxAST;
        
        if (this.functionBlock.getBlockType() == BlockType.BLOCK && this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACE) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_BLOCK_ENDING
            );
        }
        
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getStart());
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getEnd());
        Objects.requireNonNull(this.getFunctionParameters());
        Objects.requireNonNull(this.getFunctionReturnType());
        Objects.requireNonNull(this.getFunctionName());
    
        printStream.println(indents + "├── factory:");
        printStream.println(indents + "│    ├── next: " + this.getMarkerFactory()
                .getNextMarkerFactories()
                .stream()
                .map(markerFactory -> markerFactory.getCurrentMarker().getAstType().name())
                .collect(Collectors.joining(", "))
        );
        printStream.println(indents + "│    ├── start: " + this.getMarkerFactory().getCurrentMarker().getStart().getStart());
        printStream.println(indents + "│    └── end: " + this.getMarkerFactory().getCurrentMarker().getEnd().getEnd());
        printStream.println(indents + "│");
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
        printStream.println(indents + "├── type: ");
        this.getFunctionReturnType().printSyntaxAST(printStream, indents + "│       ");
        printStream.println(indents + "│");
        printStream.println(indents + "├── parameters: " + (this.getFunctionParameters().getParameters().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getFunctionParameters().getParameters().size(); index++) {
            final ParameterSyntaxAST abstractSyntaxAST = this.getFunctionParameters().getParameters().get(index);
            if (index == this.getFunctionParameters().getParameters().size() - 1) {
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
            if (annotationSyntaxAST.getAnnotationName() == null)
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
        private ParameterListSyntaxAST functionParameterList;
        
        
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
    
    
        public FunctionDefinitionSyntaxASTBuilder parameters(final ParameterListSyntaxAST functionParameterList) {
            this.functionParameterList = functionParameterList;
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
    
    
        public FunctionSyntaxAST build() {
            final FunctionSyntaxAST functionSyntaxAST = new FunctionSyntaxAST(this.syntaxAnalyzer);
            if (this.functionAnnotations != null)
                functionSyntaxAST.setFunctionAnnotations(this.functionAnnotations);
            if (this.functionName != null)
                functionSyntaxAST.setFunctionName(this.functionName);
            if (this.functionReturnType != null)
                functionSyntaxAST.setFunctionReturnType(this.functionReturnType);
            if (this.functionParameterList != null)
                functionSyntaxAST.setFunctionParameters(this.functionParameterList);
            if (this.functionBlock != null)
                functionSyntaxAST.setFunctionBlock(this.functionBlock);
            functionSyntaxAST.setStartToken(this.startToken);
            functionSyntaxAST.getMarkerFactory().getCurrentMarker().setStart(functionSyntaxAST.getStartToken());
            functionSyntaxAST.setEndToken(this.endToken);
            functionSyntaxAST.getMarkerFactory().getCurrentMarker().setEnd(functionSyntaxAST.getEndToken());
            return functionSyntaxAST;
        }
        
    }
    
}
