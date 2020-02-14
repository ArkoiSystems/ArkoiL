package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright © 2019 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on the Sat Nov 09 2019 Author єхcsє#5543 aka Timo
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * <p>
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
@Getter
@Setter
public class FunctionDefinitionSyntaxAST extends AbstractStatementSyntaxAST
{
    
    @Expose
    private List<AnnotationSyntaxAST> functionAnnotations;
    
    @Expose
    private IdentifierToken functionName;
    
    @Expose
    private TypeSyntaxAST functionReturnType;
    
    @Expose
    private List<ArgumentDefinitionSyntaxAST> functionArguments;
    
    @Expose
    private BlockSyntaxAST functionBlock;
    
    /**
     * This constructor will initialize the statement with the AST-Type
     * "FUNCTION_DEFINITION". This will help to debug problems or check the AST for
     * correct syntax. Also it will pass the annotations which got parsed previously.
     *
     * @param functionAnnotations
     *         The annotation list which got parsed already.
     */
    public FunctionDefinitionSyntaxAST(final List<AnnotationSyntaxAST> functionAnnotations) {
        super(ASTType.FUNCTION_DEFINITION);
        
        this.functionAnnotations = functionAnnotations;
        this.functionArguments = new ArrayList<>();
    }
    
    /**
     * This constructor will initialize the statement with the AST-Type
     * "FUNCTION_DEFINITION". This will help to debug problems or check the AST for
     * correct syntax.
     */
    public FunctionDefinitionSyntaxAST() {
        super(ASTType.FUNCTION_DEFINITION);
        
        this.functionAnnotations = new ArrayList<>();
        this.functionArguments = new ArrayList<>();
    }
    
    /**
     * This method will parse the "function definition" statement and checks it for the
     * correct syntax. This statement can just be used in the RootAST. Also it can have
     * annotations which enables pre-defined features. For that the "native" annotation
     * gives the function the power to end directly with an semicolon after the argument
     * section.
     * <p>
     * An example for this statement:
     * <p>
     * &#64;native[default]
     * <p>
     * fun println<>(message: string);
     *
     * @param parentAST
     *         The parent of the AST. With it you can check for correct usage of the
     *         statement.
     * @param syntaxAnalyzer
     *         The given SyntaxAnalyzer is used for checking the syntax of the current
     *         Token list.
     *
     * @return It will return null if an error occurred or an FunctionDefinitionAST if it
     *         parsed until to the end.
     */
    @Override
    public FunctionDefinitionSyntaxAST parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (!(parentAST instanceof RootSyntaxAST)) {
            syntaxAnalyzer.errorHandler().addError(new SyntaxASTError<>(parentAST, "Couldn't parse the \"function definition\" statement because it isn't declared inside the root file."));
            return null;
        }
    
        if (syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER) == null || !syntaxAnalyzer.currentToken().getTokenContent().equals("fun")) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function definition\" statement because the parsing doesn't start with the \"fun\" keyword."));
            return null;
        } else this.setStart(syntaxAnalyzer.currentToken().getStart());
    
        if (syntaxAnalyzer.matchesNextToken(TokenType.IDENTIFIER) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function definition\" statement because the \"fun\" keyword isn't followed by a function name."));
            return null;
        } else this.functionName = (IdentifierToken) syntaxAnalyzer.currentToken();
    
        if (syntaxAnalyzer.matchesNextToken(SymbolToken.SymbolType.LESS_THAN_SIGN) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function definition\" statement because the function name isn't followed by an opening sign aka. \"<\"."));
            return null;
        } else syntaxAnalyzer.nextToken();
    
        if (TypeSyntaxAST.TYPE_PARSER.canParse(this, syntaxAnalyzer)) {
            final TypeSyntaxAST typeSyntaxAST = TypeSyntaxAST.TYPE_PARSER.parse(this, syntaxAnalyzer);
            if (typeSyntaxAST == null) {
                syntaxAnalyzer.errorHandler().addError(new ParserError<>(TypeSyntaxAST.TYPE_PARSER, this.getStart(), syntaxAnalyzer.currentToken().getEnd(), "Couldn't parse the \"function definition\" statement because an error occurred during the parsing of the return type."));
            } else {
                this.functionReturnType = typeSyntaxAST;
                syntaxAnalyzer.nextToken();
            }
        } else this.functionReturnType = new TypeSyntaxAST(TypeSyntaxAST.TypeKind.VOID, false);
    
        if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.GREATER_THAN_SIGN) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function definition\" statement because the return type section doesn't end with a closing sign aka. \">\"."));
            return null;
        }
    
        if (syntaxAnalyzer.matchesNextToken(SymbolToken.SymbolType.OPENING_PARENTHESIS) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function definition\" statement because the argument section doesn't start with an opening parenthesis."));
            return null;
        }
    
        ArgumentDefinitionSyntaxAST.parseArguments(this, syntaxAnalyzer, this.functionArguments);
        if (this.functionArguments == null) {
            syntaxAnalyzer.errorHandler().addError(new ParserError<>(ArgumentDefinitionSyntaxAST.ARGUMENT_DEFINITION_PARSER, this.getStart(), syntaxAnalyzer.currentToken().getEnd(), "Couldn't parse the \"function definition\" statement because an error occurred during the parsing of the arguments."));
            return null;
        }
    
        if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.CLOSING_PARENTHESIS) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function definition\" statement because the argument section doesn't end with a closing parenthesis."));
            return null;
        } else syntaxAnalyzer.nextToken();
    
        if (this.hasAnnotation("native")) {
            if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.SEMICOLON) == null) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function definition\" statement because a native function needs to end direclty with an semicolon after the argument section."));
                return null;
            } else {
                this.functionBlock = new BlockSyntaxAST();
                this.functionBlock.setBlockType(BlockSyntaxAST.BlockType.NATIVE);
            }
        } else {
            if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.OPENING_BRACE) == null && syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.EQUAL) == null) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function definition\" statement because after the argument section no opening brace or equal sign was declared. You need one of them to declare if this function uses a block or is inlined."));
                return null;
            }
        
            if (!BlockSyntaxAST.BLOCK_PARSER.canParse(this, syntaxAnalyzer)) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function definition\" statement because the block separator isn't followed by a valid block."));
                return null;
            }
        
            if ((this.functionBlock = BlockSyntaxAST.BLOCK_PARSER.parse(this, syntaxAnalyzer)) == null) {
                syntaxAnalyzer.errorHandler().addError(new ParserError<>(BlockSyntaxAST.BLOCK_PARSER, this.getStart(), syntaxAnalyzer.currentToken().getEnd(), "Couldn't parse the \"function definition\" statement because an error occurred during parsing of the block/inlined block."));
                return null;
            }
        
            if (this.functionBlock.getBlockType() == BlockSyntaxAST.BlockType.INLINE && syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.SEMICOLON) == null) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function definition\" statement because an inlined function needs to end with a semicolon."));
                return null;
            } else if (this.functionBlock.getBlockType() == BlockSyntaxAST.BlockType.BLOCK && syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.CLOSING_BRACE) == null) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function definition\" statement because a block needs to end with a closing brace aka. \"}\"."));
                return null;
            }
        }
        
        this.setEnd(syntaxAnalyzer.currentToken().getEnd());
        return this;
    }
    
    /**
     * This method loops through all annotation and returns true if it found an annotation
     * with the defined name.
     *
     * @param annotationName
     *         The name which is used to search if the annotation is present.
     *
     * @return It will return false if the list is empty or it doesn't found anything.
     *         Otherwise it will just return true.
     */
    public boolean hasAnnotation(final String annotationName) {
        for (final AnnotationSyntaxAST annotationSyntaxAST : this.functionAnnotations)
            if (annotationSyntaxAST.getAnnotationName().getTokenContent().equals(annotationName))
                return true;
        return false;
    }
    
}
