package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ASTError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SeparatorToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.AbstractStatementAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.functionStatements.FunctionDefinitionAST;
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
    
    public AnnotationAST(final List<AnnotationAST> annotationStorage) {
        super(ASTType.ANNOTATION);
        
        this.annotationStorage = annotationStorage;
        this.annotationStorage.add(this);
    }
    
    public AnnotationAST() {
        super(ASTType.ANNOTATION);
        
        this.annotationStorage = new ArrayList<>();
        this.annotationStorage.add(this);
    }
    
    @Override
    public AnnotationAST parseAST(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (syntaxAnalyzer.matchesCurrentToken(SeparatorToken.SeparatorType.AT_SIGN) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the annotation because the parsing doesn't begin with an at sign aka \"@\"."));
            return null;
        } else this.setStart(syntaxAnalyzer.currentToken().getStart());
        
        final AbstractToken annotationNameIdentifier = syntaxAnalyzer.matchesNextToken(TokenType.IDENTIFIER);
        if (annotationNameIdentifier == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the annotation because the at sign isn't followed by an IdentifierToken."));
            return null;
        } else this.annotationName = (IdentifierToken) annotationNameIdentifier;
        
        if (syntaxAnalyzer.matchesNextToken(SeparatorToken.SeparatorType.OPENING_BRACKET) != null) {
            this.annotationArguments = new ArrayList<>();
            while (syntaxAnalyzer.getPosition() < syntaxAnalyzer.getTokens().length) {
                if (syntaxAnalyzer.matchesNextToken(SeparatorToken.SeparatorType.COMMA) != null)
                    syntaxAnalyzer.nextToken();
                if (syntaxAnalyzer.currentToken().getTokenType() != TokenType.IDENTIFIER)
                    break;
                
                this.annotationArguments.add((IdentifierToken) syntaxAnalyzer.currentToken());
            }
            
            if (syntaxAnalyzer.matchesCurrentToken(SeparatorToken.SeparatorType.CLOSING_BRACKET) == null) {
                syntaxAnalyzer.errorHandler().addError(new ASTError(this, "Couldn't parse the annotation arguments because the parsing doesn't end with a closing bracket."));
                return null;
            } else syntaxAnalyzer.nextToken();
        }
        
        this.setEnd(syntaxAnalyzer.currentToken().getEnd());
        
        if (ANNOTATION_PARSER.canParse(parentAST, syntaxAnalyzer))
            if (new AnnotationAST(this.annotationStorage).parseAST(parentAST, syntaxAnalyzer) == null)
                return null;
            else
                return parentAST.addAST(this, syntaxAnalyzer);
        
        if (!FunctionDefinitionAST.STATEMENT_PARSER.canParse(parentAST, syntaxAnalyzer)) {
            syntaxAnalyzer.errorHandler().addError(new ParserError(ANNOTATION_PARSER, this.getStart(), syntaxAnalyzer.currentToken().getEnd(), "Couldn't parse the annotations because they are not followed by an function."));
            return null;
        }
        
        final AbstractStatementAST abstractStatementAST = new FunctionDefinitionAST(this.annotationStorage).parseAST(parentAST, syntaxAnalyzer);
        if (abstractStatementAST == null) {
            syntaxAnalyzer.errorHandler().addError(new ASTError(this, "Couldn't parse the annotations because during the parsing of the function an error occurred."));
            return null;
        }
        return parentAST.addAST(this, syntaxAnalyzer);
    }
    
    @Override
    public <T extends AbstractAST> T addAST(final T toAddAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return toAddAST;
    }
    
}
