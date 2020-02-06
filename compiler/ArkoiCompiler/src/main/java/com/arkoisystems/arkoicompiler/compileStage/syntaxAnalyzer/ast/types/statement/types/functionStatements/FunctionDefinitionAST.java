package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.functionStatements;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ASTError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.types.FunctionDefinitionSemantic;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.*;
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
public class FunctionDefinitionAST extends FunctionStatementAST<FunctionDefinitionSemantic>
{
    
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
    
    /**
     * This constructor will initialize the statement with the AST-Type
     * "FUNCTION_DEFINITION". This will help to debug problems or check the AST for
     * correct syntax. Also it will pass the annotations which got parsed previously.
     *
     * @param functionAnnotationASTs
     *         The annotation list which got parsed already.
     */
    public FunctionDefinitionAST(final List<AnnotationAST> functionAnnotationASTs) {
        super(ASTType.FUNCTION_DEFINITION);
        
        this.functionAnnotationASTs = functionAnnotationASTs;
        this.functionArgumentASTs = new ArrayList<>();
    }
    
    /**
     * This constructor will initialize the statement with the AST-Type
     * "FUNCTION_DEFINITION". This will help to debug problems or check the AST for
     * correct syntax.
     */
    public FunctionDefinitionAST() {
        super(ASTType.FUNCTION_DEFINITION);
        
        this.functionAnnotationASTs = new ArrayList<>();
        this.functionArgumentASTs = new ArrayList<>();
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
    public FunctionDefinitionAST parseAST(final AbstractAST<?> parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (!(parentAST instanceof RootAST)) {
            syntaxAnalyzer.errorHandler().addError(new ASTError(parentAST, "Couldn't parse the \"function definition\" statement because it isn't declared inside the root file."));
            return null;
        }
    
        if (syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER) == null || !syntaxAnalyzer.currentToken().getTokenContent().equals("fun")) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function definition\" statement because the parsing doesn't start with the \"fun\" keyword."));
            return null;
        } else this.setStart(syntaxAnalyzer.currentToken().getStart());
    
        if (syntaxAnalyzer.matchesNextToken(TokenType.IDENTIFIER) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function definition\" statement because the \"fun\" keyword isn't followed by a function name."));
            return null;
        } else this.functionNameToken = (IdentifierToken) syntaxAnalyzer.currentToken();
    
        if (syntaxAnalyzer.matchesNextToken(SymbolToken.SymbolType.LESS_THAN_SIGN) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function definition\" statement because the function name isn't followed by an opening sign aka. \"<\"."));
            return null;
        } else syntaxAnalyzer.nextToken();
    
        if (TypeAST.TYPE_PARSER.canParse(this, syntaxAnalyzer)) {
            final TypeAST typeAST = TypeAST.TYPE_PARSER.parse(this, syntaxAnalyzer);
            if (typeAST == null) {
                syntaxAnalyzer.errorHandler().addError(new ParserError(TypeAST.TYPE_PARSER, this.getStart(), syntaxAnalyzer.currentToken().getEnd(), "Couldn't parse the \"function definition\" statement because an error occurred during the parsing of the return type."));
            } else {
                this.functionReturnTypeAST = typeAST;
                syntaxAnalyzer.nextToken();
            }
        } else this.functionReturnTypeAST = new TypeAST(TypeAST.TypeKind.VOID, false);
    
        if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.GREATER_THAN_SIGN) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function definition\" statement because the return type section doesn't end with a closing sign aka. \">\"."));
            return null;
        }
    
        if (syntaxAnalyzer.matchesNextToken(SymbolToken.SymbolType.OPENING_PARENTHESIS) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function definition\" statement because the argument section doesn't start with an opening parenthesis."));
            return null;
        }
    
        ArgumentDefinitionAST.parseArguments(this, syntaxAnalyzer, this.functionArgumentASTs);
        if (this.functionArgumentASTs == null) {
            syntaxAnalyzer.errorHandler().addError(new ParserError(ArgumentDefinitionAST.ARGUMENT_DEFINITION_PARSER, this.getStart(), syntaxAnalyzer.currentToken().getEnd(), "Couldn't parse the \"function definition\" statement because an error occurred during the parsing of the arguments."));
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
            }
        } else {
            if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.OPENING_BRACE) == null && syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.EQUAL) == null) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function definition\" statement because after the argument section no opening brace or equal sign was declared. You need one of them to declare if this function uses a block or is inlined."));
                return null;
            }
        
            if (!BlockAST.BLOCK_PARSER.canParse(this, syntaxAnalyzer)) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function definition\" statement because the block separator isn't followed by a valid block."));
                return null;
            }
        
            if ((this.blockAST = BlockAST.BLOCK_PARSER.parse(this, syntaxAnalyzer)) == null) {
                syntaxAnalyzer.errorHandler().addError(new ParserError(BlockAST.BLOCK_PARSER, this.getStart(), syntaxAnalyzer.currentToken().getEnd(), "Couldn't parse the \"function definition\" statement because an error occurred during parsing of the block/inlined block."));
                return null;
            }
        
            if (this.blockAST.getBlockType() == BlockAST.BlockType.INLINE && syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.SEMICOLON) == null) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function definition\" statement because an inlined function needs to end with a semicolon."));
                return null;
            } else if (this.blockAST.getBlockType() == BlockAST.BlockType.BLOCK && syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.CLOSING_BRACE) == null) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function definition\" statement because a block needs to end with a closing brace aka. \"}\"."));
                return null;
            }
        }
    
        this.setEnd(syntaxAnalyzer.currentToken().getEnd());
        return parentAST.addAST(this, syntaxAnalyzer);
    }
    
    /**
     * This method is just overwritten to prevent default code execution. So it will just
     * return the input and doesn't check anything.
     *
     * @param toAddAST
     *         The AST which should get added to the "FunctionDefinitionAST".
     * @param syntaxAnalyzer
     *         The SyntaxAnalyzer which should get used if you want to compare Tokens.
     * @param <T>
     *         The Type of the AST which should be added to the "FunctionDefinitionAST".
     *
     * @return It will just return the input "toAddAST" because you can't add ASTs to a
     *         FunctionDefinitionAST.
     */
    @Override
    public <T extends AbstractAST<?>> T addAST(final T toAddAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return toAddAST;
    }
    
    @Override
    public Class<FunctionDefinitionSemantic> semanticClass() {
        return FunctionDefinitionSemantic.class;
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
    private boolean hasAnnotation(final String annotationName) {
        for (final AnnotationAST annotationAST : this.functionAnnotationASTs)
            if (annotationAST.getAnnotationName().getTokenContent().equals(annotationName))
                return true;
        return false;
    }
    
}
