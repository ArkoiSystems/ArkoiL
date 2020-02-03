package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
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
    
    /**
     * This constructor will initialize the statement with the AST-Type
     * "ARGUMENT_DEFINITION". This will help to debug problems or check the AST for
     * correct syntax.
     */
    public ArgumentDefinitionAST() {
        super(ASTType.ARGUMENT_DEFINITION);
    }
    
    /**
     * This method will parse the ArgumentDefinitionAST and checks it for the correct
     * syntax. This AST can be used in every case but there is no available Parser for
     * this AST. You just can create an argument definition directly in code like in
     * FunctionDefinitionAST. An argument needs to have a name and type deceleration.
     * <p>
     * An example for this AST:
     * <p>
     * fun main<int>(args: string[]) = 0;
     *
     * @param parentAST
     *         The parent of the AST. With it you can check for correct usage of the
     *         statement.
     * @param syntaxAnalyzer
     *         The given SyntaxAnalyzer is used for checking the syntax of the current
     *         Token list.
     *
     * @return It will return null if an error occurred or an ArgumentDefinitionAST if it
     *         parsed until to the end.
     */
    @Override
    public ArgumentDefinitionAST parseAST(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the argument definition because the parsing doesn't start with an identifier as name."));
            return null;
        } else {
            this.argumentNameIdentifier = (IdentifierToken) syntaxAnalyzer.currentToken();
            this.setStart(syntaxAnalyzer.currentToken().getStart());
        }
        
        if (syntaxAnalyzer.matchesNextToken(SeparatorToken.SeparatorType.COLON) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the argument definition because the argument name isn't followed by a colon."));
            return null;
        } else syntaxAnalyzer.nextToken();
        
        if (!TypeAST.TYPE_PARSER.canParse(parentAST, syntaxAnalyzer)) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the argument definition because the colon isn't followed by a valid type."));
            return null;
        }
        
        if ((this.argumentType = TypeAST.TYPE_PARSER.parse(this, syntaxAnalyzer)) == null) {
            syntaxAnalyzer.errorHandler().addError(new ParserError(TypeAST.TYPE_PARSER, this.getStart(), syntaxAnalyzer.currentToken().getEnd(), "Couldn't parse the argument definition because an eror occurred during the parsing of the type."));
            return null;
        }
        return parentAST.addAST(this, syntaxAnalyzer);
    }
    
    /**
     * This method is just overwritten because this method extends the AbstractAST class.
     * It will just return the input and doesn't check anything.
     *
     * @param toAddAST
     *         The AST which should get add to this class.
     * @param syntaxAnalyzer
     *         The given SyntaxAnalyzer is needed for checking and modification of the
     *         current Token list/order.
     * @param <T>
     *         The Type of the AST which should be added to the ArgumentDefinitionAST.
     *
     * @return It will just return the input "toAddAST" because you can't add ASTs to a
     *         ArgumentDefinitionAST.
     */
    @Override
    public <T extends AbstractAST> T addAST(final T toAddAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return toAddAST;
    }
    
    /**
     * This method provides the ability to parse a list of arguments which is used by the
     * FunctionDefinitionAST for the function arguments. It will throw an error if the
     * arguments didn't get separated by a semicolon or if it doesn't start/end with a
     * parenthesis. Also this method creates an own ArrayList so you don't need to define
     * one before using this method.
     *
     * @param parentAST
     *         The ParentAST defines the AST in which the arguments should be getting
     *         parsed. This is useful to check if the AST is supported or to report
     *         errors.
     * @param syntaxAnalyzer
     *         The given SyntaxAnalyzer is used for checking the syntax of the current
     *         Token list.
     *
     * @return It will return null if an error occurred or the given "argumentsASTs" list
     *         if it parsed until to the end.
     */
    public static List<ArgumentDefinitionAST> parseArguments(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return parseArguments(parentAST, syntaxAnalyzer, new ArrayList<>());
    }
    
    /**
     * This method provides the ability to parse a list of arguments which is used by the
     * FunctionDefinitionAST for the function arguments. It will throw an error if the
     * arguments didn't get separated by a semicolon or if it doesn't start/end with a
     * parenthesis.
     *
     * @param parentAST
     *         The ParentAST defines the AST in which the arguments should be getting
     *         parsed. This is useful to check if the AST is supported or to report
     *         errors.
     * @param syntaxAnalyzer
     *         The given SyntaxAnalyzer is used for checking the syntax of the current
     *         Token list.
     * @param argumentASTs
     *         The List in which the new arguments should be stored.
     *
     * @return It will return null if an error occurred or the given "argumentsASTs" list
     *         if it parsed until to the end.
     */
    public static List<ArgumentDefinitionAST> parseArguments(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer, final List<ArgumentDefinitionAST> argumentASTs) {
        if (syntaxAnalyzer.matchesCurrentToken(SeparatorToken.SeparatorType.OPENING_PARENTHESIS) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the arguments because parsing doesn't start with an opening parenthesis."));
            return null;
        } else syntaxAnalyzer.nextToken();
        
        while (syntaxAnalyzer.getPosition() < syntaxAnalyzer.getTokens().length) {
            if (!ArgumentDefinitionAST.ARGUMENT_DEFINITION_PARSER.canParse(parentAST, syntaxAnalyzer))
                break;
            
            final ArgumentDefinitionAST argumentDefinitionAST = ArgumentDefinitionAST.ARGUMENT_DEFINITION_PARSER.parse(parentAST, syntaxAnalyzer);
            if (argumentDefinitionAST == null) {
                syntaxAnalyzer.errorHandler().addError(new ParserError(ArgumentDefinitionAST.ARGUMENT_DEFINITION_PARSER, syntaxAnalyzer.currentToken(), "Couldn't parse the arguments because an error occurred during the parsing of an argument."));
                return null;
            } else argumentASTs.add(argumentDefinitionAST);
            
            if (syntaxAnalyzer.matchesNextToken(SeparatorToken.SeparatorType.COMMA) == null)
                break;
        }
        
        if (syntaxAnalyzer.matchesCurrentToken(SeparatorToken.SeparatorType.CLOSING_PARENTHESIS) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the arguments because the parsing doesn't end with a closing parenthesis."));
            return null;
        }
        return argumentASTs;
    }
    
}
