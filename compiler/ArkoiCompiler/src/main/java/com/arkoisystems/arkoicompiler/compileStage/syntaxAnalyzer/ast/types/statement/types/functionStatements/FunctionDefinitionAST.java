package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.functionStatements;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ASTError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SeparatorToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.operators.types.AssignmentOperatorToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.*;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.AbstractStatementAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.FunctionStatementAST;
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
public class FunctionDefinitionAST extends FunctionStatementAST
{
    
    private ArkoiClass arkoiClass;
    
    @Expose
    private List<AnnotationAST> functionAnnotationASTs;
    
    @Expose
    private IdentifierToken functionNameToken;
    
    @Expose
    private TypeAST functionReturnTypeAST;
    
    @Expose
    private List<ArgumentDefinitionAST> functionArgumentASTs;
    
    @Expose
    private BlockAST blockAST;
    
    
    public FunctionDefinitionAST(final List<AnnotationAST> functionAnnotationASTs) {
        super(ASTType.FUNCTION_DEFINITION);
        
        this.functionAnnotationASTs = functionAnnotationASTs;
    }
    
    public FunctionDefinitionAST() {
        super(ASTType.FUNCTION_DEFINITION);
        
        this.functionAnnotationASTs = new ArrayList<>();
    }
    
    @Override
    public FunctionDefinitionAST parseAST(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (!(parentAST instanceof RootAST)) {
            syntaxAnalyzer.errorHandler().addError(new ASTError(parentAST, "Couldn't parse the function because it wasn't declared inside the Root block."));
            return null;
        } else this.arkoiClass = syntaxAnalyzer.getArkoiClass();
        
        final AbstractToken functionIdentifierToken = syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER);
        if (functionIdentifierToken == null || !functionIdentifierToken.getTokenContent().equals("fun")) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the function because the parsing doesn't start with an IdentifierToken with the name \"fun\"."));
            return null;
        } else this.setStart(functionIdentifierToken.getStart());
        
        final AbstractToken functionNameIdentifierToken = syntaxAnalyzer.matchesNextToken(TokenType.IDENTIFIER);
        if (functionNameIdentifierToken == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the function because there is no IdentifierToken for the name of the function."));
            return null;
        } else
            this.functionNameToken = (IdentifierToken) functionNameIdentifierToken;
        
        if (syntaxAnalyzer.matchesNextToken(SeparatorToken.SeparatorType.LESS_THAN_SIGN) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the function because it doesn't has a opening sign aka. \"<\" which declares the return type section."));
            return null;
        } else syntaxAnalyzer.nextToken();
        
        if (TypeAST.TYPE_PARSER.canParse(this, syntaxAnalyzer)) {
            final TypeAST typeAST = TypeAST.TYPE_PARSER.parse(this, syntaxAnalyzer);
            if (typeAST == null) {
                syntaxAnalyzer.errorHandler().addError(new ASTError(this, "Couldn't parse the function because the return type couldn't be parsed. Please check the stacktrace."));
                return null;
            } else this.functionReturnTypeAST = typeAST;
            syntaxAnalyzer.nextToken();
        } else this.functionReturnTypeAST = new TypeAST(TypeAST.TypeKind.VOID, false);
        
        if (syntaxAnalyzer.matchesCurrentToken(SeparatorToken.SeparatorType.GREATER_THAN_SIGN) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the function because it doesn't has a closing sign aka. \">\" for the return type section."));
            return null;
        }
        
        if (syntaxAnalyzer.matchesNextToken(SeparatorToken.SeparatorType.OPENING_PARENTHESIS) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the function because it doesn't has a open parenthesis which declares the arguments section."));
            return null;
        }
        
        final List<ArgumentDefinitionAST> arguments = ArgumentDefinitionAST.parseArguments(this, syntaxAnalyzer);
        if (arguments == null) {
            syntaxAnalyzer.errorHandler().addError(new ASTError(this, "Couldn't parse the function because there was an error while parsing the arguments."));
            return null;
        } else this.functionArgumentASTs = arguments;
        
        if (syntaxAnalyzer.matchesCurrentToken(SeparatorToken.SeparatorType.CLOSING_PARENTHESIS) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the function because it doesn't has a closing parenthesis for the arguments section."));
            return null;
        } else syntaxAnalyzer.nextToken();
        
        if (this.hasNativeAnnotation()) {
            if (syntaxAnalyzer.matchesCurrentToken(SeparatorToken.SeparatorType.SEMICOLON) == null) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the function because a native function just can have an empty body. Please add a semicolon after the arguments section."));
                return null;
            } else return parentAST.addAST(this, syntaxAnalyzer);
        } else {
            if (syntaxAnalyzer.matchesCurrentToken(SeparatorToken.SeparatorType.OPENING_BRACE) == null && syntaxAnalyzer.matchesCurrentToken(AssignmentOperatorToken.AssignmentOperatorType.ASSIGNMENT) == null) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the function because after the arguments an equal sign or opening brace is need if the method isn't native."));
                return null;
            }
            
            final BlockAST blockAST = new BlockAST(AbstractStatementAST.STATEMENT_PARSER).parseAST(this, syntaxAnalyzer);
            if (blockAST == null) {
                syntaxAnalyzer.errorHandler().addError(new ASTError(this, "Couldn't parse the FunctionAST because an error occurred. Please check the stacktrace."));
                return null;
            } else this.blockAST = blockAST;
            
            if (this.blockAST.getBlockType() == BlockAST.BlockType.INLINE && syntaxAnalyzer.matchesCurrentToken(SeparatorToken.SeparatorType.SEMICOLON) == null) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the function because an inlined function needs to end with a semicolon."));
                return null;
            } else if (this.blockAST.getBlockType() == BlockAST.BlockType.BLOCK && syntaxAnalyzer.matchesCurrentToken(SeparatorToken.SeparatorType.CLOSING_BRACE) == null) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the function because a function with a block needs to end with a closing brace aka \"}\"."));
                return null;
            }
            
            return parentAST.addAST(this, syntaxAnalyzer);
        }
    }
    
    @Override
    public boolean initialize(final SyntaxAnalyzer syntaxAnalyzer) {
        if(this.hasNativeAnnotation() && this.blockAST == null)
            return true;
        
        if(this.blockAST == null) {
            syntaxAnalyzer.errorHandler().addError(new ASTError(this, "Couldn't initialize the function because the BlockAST is null."));
            return false;
        }
        
        return this.blockAST.initialize(syntaxAnalyzer);
    }
    
    private boolean hasNativeAnnotation() {
        for (final AnnotationAST annotationAST : this.functionAnnotationASTs)
            if (annotationAST.getAnnotationName().getTokenContent().equals("native"))
                return true;
        return false;
    }
    
}
