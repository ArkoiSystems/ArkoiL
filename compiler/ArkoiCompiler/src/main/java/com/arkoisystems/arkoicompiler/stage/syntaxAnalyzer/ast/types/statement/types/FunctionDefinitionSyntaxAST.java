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
import lombok.Getter;
import lombok.Setter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class FunctionDefinitionSyntaxAST extends AbstractStatementSyntaxAST
{
    
    
    @Getter
    @Setter
    private List<AnnotationSyntaxAST> functionAnnotations;
    
    
    @Getter
    private IdentifierToken functionName;
    
    
    @Getter
    private TypeSyntaxAST functionReturnType;
    
    
    @Getter
    private List<ArgumentDefinitionSyntaxAST> functionArguments;
    
    
    @Getter
    private BlockSyntaxAST functionBlock;
    
    
    public FunctionDefinitionSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer, final List<AnnotationSyntaxAST> functionAnnotations) {
        super(syntaxAnalyzer, ASTType.FUNCTION_DEFINITION);
        
        this.functionAnnotations = functionAnnotations;
        this.functionArguments = new ArrayList<>();
    }
    
    
    public FunctionDefinitionSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.FUNCTION_DEFINITION);
        
        this.functionAnnotations = new ArrayList<>();
        this.functionArguments = new ArrayList<>();
    }
    
    
    @Override
    public FunctionDefinitionSyntaxAST parseAST(final AbstractSyntaxAST parentAST) {
        if (!(parentAST instanceof RootSyntaxAST)) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_PARENT
            );
            return null;
        }
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.IDENTIFIER) == null || !this.getSyntaxAnalyzer().currentToken().getTokenContent().equals("fun")) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_START
            );
            return null;
        }
        this.setStart(this.getSyntaxAnalyzer().currentToken().getStart());
        
        if (this.getSyntaxAnalyzer().matchesNextToken(TokenType.IDENTIFIER) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_NO_NAME
            );
            return null;
        }
        this.functionName = (IdentifierToken) this.getSyntaxAnalyzer().currentToken();
        
        if (this.getSyntaxAnalyzer().matchesNextToken(SymbolType.LESS_THAN_SIGN) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_RETURN_TYPE_START
            );
            return null;
        }
        this.getSyntaxAnalyzer().nextToken();
        
        if (TypeSyntaxAST.TYPE_PARSER.canParse(this, this.getSyntaxAnalyzer())) {
            final TypeSyntaxAST typeSyntaxAST = TypeSyntaxAST.TYPE_PARSER.parse(this, this.getSyntaxAnalyzer());
            if (typeSyntaxAST == null)
                return null;
            
            this.functionReturnType = typeSyntaxAST;
            this.getSyntaxAnalyzer().nextToken();
        }
        this.functionReturnType = new TypeSyntaxAST(this.getSyntaxAnalyzer(), TypeKind.VOID, false);
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.GREATER_THAN_SIGN) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_RETURN_TYPE_ENDING
            );
            return null;
        }
        
        if (this.getSyntaxAnalyzer().matchesNextToken(SymbolType.OPENING_PARENTHESIS) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_ARGUMENTS_START
            );
            return null;
        }
        
        this.functionArguments = ArgumentDefinitionSyntaxAST.parseArguments(this, this.getSyntaxAnalyzer());
        if (this.functionArguments == null)
            return null;
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_PARENTHESIS) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_ARGUMENTS_ENDING
            );
            return null;
        } else this.getSyntaxAnalyzer().nextToken();
        
        if (this.hasAnnotation("native")) {
            if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.SEMICOLON) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        SyntaxErrorType.FUNCTION_DEFINITION_WRONG_NATIVE_ENDING
                );
                return null;
            }
            
            this.functionBlock = new BlockSyntaxAST(this.getSyntaxAnalyzer());
            this.functionBlock.setBlockType(BlockType.NATIVE);
            return this;
        }
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_BRACE) == null && this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.EQUAL) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_BLOCK_START
            );
            return null;
        }
        
        if (!BlockSyntaxAST.BLOCK_PARSER.canParse(this, this.getSyntaxAnalyzer())) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_NO_VALID_BLOCK
            );
            return null;
        }
        
        this.functionBlock = BlockSyntaxAST.BLOCK_PARSER.parse(this, this.getSyntaxAnalyzer());
        if (this.functionBlock == null)
            return null;
        
        if (this.functionBlock.getBlockType() == BlockType.INLINE && this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.SEMICOLON) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_INLINED_BLOCK_ENDING
            );
            return null;
        } else if (this.functionBlock.getBlockType() == BlockType.BLOCK && this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACE) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.FUNCTION_DEFINITION_WRONG_BLOCK_ENDING
            );
            return null;
        }
        
        this.setEnd(this.getSyntaxAnalyzer().currentToken().getEnd());
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(final PrintStream printStream, final String indents) {
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
            final ArgumentDefinitionSyntaxAST abstractSyntaxAST = this.getFunctionArguments().get(index);
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
    
    
    public boolean hasAnnotation(final String annotationName) {
        for (final AnnotationSyntaxAST annotationSyntaxAST : this.functionAnnotations)
            if (annotationSyntaxAST.getAnnotationName().getTokenContent().equals(annotationName))
                return true;
        return false;
    }
    
}
