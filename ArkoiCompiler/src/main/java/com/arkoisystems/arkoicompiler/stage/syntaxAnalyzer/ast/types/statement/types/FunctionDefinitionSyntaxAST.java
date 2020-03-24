/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
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
    @NotNull
    private TypeSyntaxAST functionReturnType;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private List<ParameterSyntaxAST> functionArguments = new ArrayList<>();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private BlockSyntaxAST functionBlock;
    
    
    public FunctionDefinitionSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer, @NotNull final List<AnnotationSyntaxAST> functionAnnotations) {
        super(syntaxAnalyzer, ASTType.FUNCTION_DEFINITION);
        
        this.functionAnnotations = functionAnnotations;
    }
    
    
    public FunctionDefinitionSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
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
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.IDENTIFIER) == null || !this.getSyntaxAnalyzer().currentToken().getTokenContent().equals("fun")) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_START
            );
            return Optional.empty();
        }
        this.setStart(this.getSyntaxAnalyzer().currentToken().getStart());
        
        if (this.getSyntaxAnalyzer().matchesNextToken(TokenType.IDENTIFIER) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_NO_NAME
            );
            return Optional.empty();
        }
        this.functionName = (IdentifierToken) this.getSyntaxAnalyzer().currentToken();
        
        if (this.getSyntaxAnalyzer().matchesNextToken(SymbolType.LESS_THAN_SIGN) == null) {
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
            
            this.functionReturnType = optionalTypeSyntaxAST.get();
            this.getSyntaxAnalyzer().nextToken();
        } else this.functionReturnType = TypeSyntaxAST
                .builder(this.getSyntaxAnalyzer())
                .typeKind(TypeKind.VOID)
                .array(false)
                .build();
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.GREATER_THAN_SIGN) == null) {
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
        
        final Optional<List<ParameterSyntaxAST>> arguments = ParameterSyntaxAST.parseParameters(this, this.getSyntaxAnalyzer());
        if (arguments.isEmpty())
            return Optional.empty();
        this.functionArguments = arguments.get();
        
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
            if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.SEMICOLON) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        SyntaxErrorType.FUNCTION_DEFINITION_WRONG_NATIVE_ENDING
                );
                return Optional.empty();
            }
            
            this.functionBlock = BlockSyntaxAST
                    .builder(this.getSyntaxAnalyzer())
                    .type(BlockType.NATIVE)
                    .build();
            return Optional.of(this);
        }
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_BRACE) == null && this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.EQUAL) == null) {
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
        this.functionBlock = optionalBlockSyntaxAST.get();
        
        if (this.functionBlock.getBlockType() == BlockType.INLINE && this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.SEMICOLON) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_INLINED_BLOCK_ENDING
            );
            return Optional.empty();
        } else if (this.functionBlock.getBlockType() == BlockType.BLOCK && this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACE) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_BLOCK_ENDING
            );
            return Optional.empty();
        }
        
        this.setEnd(this.getSyntaxAnalyzer().currentToken().getEnd());
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
        printStream.println(indents + "├── type: " + this.getFunctionReturnType().getTypeKind().getName() + (this.getFunctionReturnType().isArray() ? "[]" : ""));
        printStream.println(indents + "│");
        printStream.println(indents + "├── arguments: " + (this.getFunctionArguments().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getFunctionArguments().size(); index++) {
            final ParameterSyntaxAST abstractSyntaxAST = this.getFunctionArguments().get(index);
            if (index == this.getFunctionArguments().size() - 1) {
                printStream.println(indents + "│   └── " + abstractSyntaxAST.getClass().getSimpleName());
                abstractSyntaxAST.printSyntaxAST(printStream, indents + "│       ");
            } else {
                printStream.println(indents + "│   ├── " + abstractSyntaxAST.getClass().getSimpleName());
                abstractSyntaxAST.printSyntaxAST(printStream, indents + "│   │   ");
                printStream.println(indents + "│   │   ");
            }
        }
        printStream.println(indents + "│");
        printStream.println(indents + "└── block: ");
        this.getFunctionBlock().printSyntaxAST(printStream, indents + "     ");
    }
    
    
    public boolean hasAnnotation(@NotNull final String annotationName) {
        for (final AnnotationSyntaxAST annotationSyntaxAST : this.functionAnnotations)
            if (annotationSyntaxAST.getAnnotationName().getTokenContent().equals(annotationName))
                return true;
        return false;
    }
    
}
