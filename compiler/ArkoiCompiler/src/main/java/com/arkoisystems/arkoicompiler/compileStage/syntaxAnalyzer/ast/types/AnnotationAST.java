package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ASTError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.functionStatements.FunctionDefinitionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.variableStatements.VariableDefinitionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.types.AnnotationParser;
import com.google.gson.annotations.Expose;
import lombok.Getter;

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
public class AnnotationAST extends AbstractAST
{
    
    public static AnnotationParser ANNOTATION_PARSER = new AnnotationParser();
    
    
    private final List<AnnotationAST> annotationStorage;
    
    
    @Expose
    private IdentifierToken annotationName;
    
    @Expose
    private List<IdentifierToken> annotationArguments;
    
    /**
     * This constructor will initialize the AnnotationAST with the AST-Type "ANNOTATION".
     * This will help to debug problems or check the AST for correct syntax. Also it will
     * pass the AnnotationStorage through the constructor which is used if the parent AST
     * was an AnnotationAST too. So it just created one array for the entire Annotation
     * chain.
     *
     * @param annotationStorage
     *         The storage of the parent AST (AnnotationAST) which should get used to save
     *         a bit of memory during parsing.
     */
    public AnnotationAST(final List<AnnotationAST> annotationStorage) {
        super(ASTType.ANNOTATION);
        
        this.annotationStorage = annotationStorage;
        
        this.annotationArguments = new ArrayList<>();
        this.annotationStorage.add(this);
    }
    
    /**
     * This constructor will initialize the AnnotationAST with the AST-Type "ANNOTATION".
     * This will help to debug problems or check the AST for correct syntax. It wont pass
     * variables through the constructor, but initializes default variables like the
     * AnnotationStorage.
     */
    public AnnotationAST() {
        super(ASTType.ANNOTATION);
        
        this.annotationArguments = new ArrayList<>();
        this.annotationStorage = new ArrayList<>();
        this.annotationStorage.add(this);
    }
    
    /**
     * This method will parse the AnnotationAST and checks it for the correct syntax. This
     * AST can just be used in the RootAST but needs to be followed by an other
     * AnnotationAST or an FunctionDefinitionAST/VariableDefinitionAST. An Annotation has
     * a name and arguments. You can leave the arguments empty but the name must be
     * present every time.
     * <p>
     * An example for a AnnotationAST:
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
     * @return It will return null if an error occurred or an AnnotationAST if it parsed
     *         until to the end.
     */
    @Override
    public AnnotationAST parseAST(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (!(parentAST instanceof RootAST)) {
            syntaxAnalyzer.errorHandler().addError(new ASTError(parentAST, "Couldn't parse the Annotation because it isn't declared inside the root file."));
            return null;
        }
        
        if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.AT_SIGN) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the Annotation because the parsing doesn't start with an at sign aka. \"@\"."));
            return null;
        } else this.setStart(syntaxAnalyzer.currentToken().getStart());
        
        if (syntaxAnalyzer.matchesNextToken(TokenType.IDENTIFIER) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the Annotation because the at sign isn't followed by an name for the annotation."));
            return null;
        } else this.annotationName = (IdentifierToken) syntaxAnalyzer.currentToken();
        
        if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.OPENING_BRACKET) != null) {
            syntaxAnalyzer.nextToken(2);
            
            while (syntaxAnalyzer.getPosition() < syntaxAnalyzer.getTokens().length) {
                if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.CLOSING_BRACKET) != null)
                    break;
                
                if (syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER) == null) {
                    syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the Annotation because you can't define a non IdentifierToken inside the arguments section beside a comma after an argument if it should get followed by an extra one."));
                    return null;
                } else {
                    this.annotationArguments.add((IdentifierToken) syntaxAnalyzer.currentToken());
                    syntaxAnalyzer.nextToken();
                }
                
                if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.COMMA) != null)
                    syntaxAnalyzer.nextToken();
                else if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.CLOSING_BRACKET) != null)
                    break;
                else {
                    syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the Annotation because you can't declare something else then a closing bracket and a comma after an argument."));
                    return null;
                }
            }
            
            if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.CLOSING_BRACKET) == null) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the Annotation because the arguments section doesn't end with a closing bracket."));
                return null;
            }
        }
        
        this.setEnd(syntaxAnalyzer.currentToken().getEnd());
        syntaxAnalyzer.nextToken();
        
        if (ANNOTATION_PARSER.canParse(parentAST, syntaxAnalyzer))
            return new AnnotationAST(this.annotationStorage).parseAST(parentAST, syntaxAnalyzer);
        
        if (FunctionDefinitionAST.STATEMENT_PARSER.canParse(parentAST, syntaxAnalyzer)) {
            final FunctionDefinitionAST functionDefinitionAST = new FunctionDefinitionAST(this.annotationStorage).parseAST(parentAST, syntaxAnalyzer);
            if (functionDefinitionAST == null) {
                syntaxAnalyzer.errorHandler().addError(new ParserError(FunctionDefinitionAST.STATEMENT_PARSER, this, "Couldn't parse the Annotation because an error occurred during the parsing of the function definition."));
                return null;
            }
            
            return parentAST.addAST(this, syntaxAnalyzer);
        }
        
        if (VariableDefinitionAST.STATEMENT_PARSER.canParse(parentAST, syntaxAnalyzer)) {
            final VariableDefinitionAST variableDefinitionAST = new VariableDefinitionAST(this.annotationStorage).parseAST(parentAST, syntaxAnalyzer);
            if (variableDefinitionAST == null) {
                syntaxAnalyzer.errorHandler().addError(new ParserError(FunctionDefinitionAST.STATEMENT_PARSER, this, "Couldn't parse the Annotation because an error occurred during the parsing of the variable definition."));
                return null;
            }
            
            return parentAST.addAST(this, syntaxAnalyzer);
        }
        
        syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the Annotation because it isn't followed by an function or variable definition."));
        return null;
    }
    
    
    /**
     * This method is just overwritten because this class extends the AbstractAST class.
     * It will just return the input and doesn't check anything.
     *
     * @param toAddAST
     *         The AST which should get add to this class.
     * @param syntaxAnalyzer
     *         The given SyntaxAnalyzer is needed for checking and modification of the
     *         current Token list/order.
     * @param <T>
     *         The Type of the AST which should be added to the AnnotationAST.
     *
     * @return It will just return the input "toAddAST" because you can't add ASTs to a
     *         AnnotationAST.
     */
    @Override
    public <T extends AbstractAST> T addAST(final T toAddAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return toAddAST;
    }
    
}
