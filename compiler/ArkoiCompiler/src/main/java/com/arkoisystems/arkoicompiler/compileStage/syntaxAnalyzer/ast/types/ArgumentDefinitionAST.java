package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ASTError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SeparatorToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.types.ArgumentDefinitionParser;
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
public class ArgumentDefinitionAST extends AbstractAST
{
    
    public static ArgumentDefinitionParser ARGUMENT_DEFINITION_PARSER = new ArgumentDefinitionParser();
    
    
    @Expose
    private IdentifierToken argumentNameIdentifier;
    
    @Expose
    private TypeAST argumentType;
    
    
    public ArgumentDefinitionAST() {
        super(ASTType.ARGUMENT_DEFINITION);
    }
    
    @Override
    public ArgumentDefinitionAST parseAST(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final AbstractToken currentNameIdentifierToken = syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER);
        if (currentNameIdentifierToken == null) {
            syntaxAnalyzer.errorHandler().addError(new ASTError(this, "Couldn't parse the argument definition because it doesn't start with an IdentifierToken for the name."));
            return null;
        } else this.argumentNameIdentifier = (IdentifierToken) currentNameIdentifierToken;
        
        if (syntaxAnalyzer.matchesNextToken(SeparatorToken.SeparatorType.COLON) == null) {
            syntaxAnalyzer.errorHandler().addError(new ASTError(this, "Couldn't parse the argument definition the name token isn't followed by an colon."));
            return null;
        } else syntaxAnalyzer.nextToken();
        
        if (!TypeAST.TYPE_PARSER.canParse(parentAST, syntaxAnalyzer)) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the argument because the TypeParser couldn't parse the current token."));
            return null;
        }
        
        final TypeAST typeAST = TypeAST.TYPE_PARSER.parse(this, syntaxAnalyzer);
        if (typeAST == null) {
            syntaxAnalyzer.errorHandler().addError(new ASTError(this, "Couldn't parse the argument because the TypeParser couldn't parse the TypeAST."));
            return null;
        } else this.argumentType = typeAST;
        return parentAST.addAST(this, syntaxAnalyzer);
    }
    
    @Override
    public <T extends AbstractAST> T addAST(final T toAddAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return toAddAST;
    }
    
    public static List<ArgumentDefinitionAST> parseArguments(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (syntaxAnalyzer.matchesCurrentToken(SeparatorToken.SeparatorType.OPENING_PARENTHESIS) == null) {
            syntaxAnalyzer.errorHandler().addError(new ASTError(parentAST, "Couldn't parse the arguments because there is no opening parenthesis at the beginning."));
            return null;
        }
        
        final List<ArgumentDefinitionAST> argumentATSs = new ArrayList<>();
        while (syntaxAnalyzer.getPosition() < syntaxAnalyzer.getTokens().length) {
            syntaxAnalyzer.nextToken();
            
            if (!ARGUMENT_DEFINITION_PARSER.canParse(parentAST, syntaxAnalyzer))
                break;
            
            final ArgumentDefinitionAST argumentDefinitionAST = ARGUMENT_DEFINITION_PARSER.parse(parentAST, syntaxAnalyzer);
            if (argumentDefinitionAST == null) {
                syntaxAnalyzer.errorHandler().addError(new ASTError(parentAST, "Couldn't parse the arguments because there is an invalid argument."));
                return null;
            } else argumentATSs.add(argumentDefinitionAST);
            
            if (syntaxAnalyzer.matchesNextToken(SeparatorToken.SeparatorType.COMMA) == null)
                break;
        }
        
        if (syntaxAnalyzer.matchesCurrentToken(SeparatorToken.SeparatorType.CLOSING_PARENTHESIS) == null) {
            syntaxAnalyzer.errorHandler().addError(new ASTError(parentAST, "Couldn't parse the arguments because there is no closing parenthesis at the ending."));
            return null;
        }
        return argumentATSs;
    }
    
}
