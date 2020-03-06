/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.types.ArgumentDefinitionParser;
import lombok.Getter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ArgumentDefinitionSyntaxAST extends AbstractSyntaxAST
{
    
    /**
     * This variable is static because we just want a single instance of the {@link
     * ArgumentDefinitionParser}.
     */
    public static ArgumentDefinitionParser ARGUMENT_DEFINITION_PARSER = new ArgumentDefinitionParser();
    
    
    /**
     * Defines the argument name with an {@link IdentifierToken} which is used to check
     * semantic errors in later stages. E.g. if the stage wants to check if a function
     * already has an {@link ArgumentDefinitionSyntaxAST} with the same name as the
     * other.
     */
    @Getter
    private IdentifierToken argumentName;
    
    
    /**
     * Defines the {@link TypeSyntaxAST} of this {@link ArgumentDefinitionSyntaxAST} which
     * is useful for checking the semantic in later stages.
     */
    @Getter
    private TypeSyntaxAST argumentType;
    
    
    /**
     * Constructs a new {@link ArgumentDefinitionSyntaxAST} with the {@link
     * SyntaxAnalyzer} as a parameter which is used to check for correct syntax with *
     * methods like {@link SyntaxAnalyzer#matchesNextToken(SymbolType)} * or {@link *
     * SyntaxAnalyzer#nextToken()}.
     *
     * @param syntaxAnalyzer
     *         the {@link SyntaxAnalyzer} which is used to check for correct syntax with
     *         methods like {@link SyntaxAnalyzer#matchesNextToken(SymbolType)} or {@link
     *         * SyntaxAnalyzer#nextToken()}.
     */
    public ArgumentDefinitionSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.ARGUMENT_DEFINITION);
    }
    
    
    @Override
    public ArgumentDefinitionSyntaxAST parseAST(final AbstractSyntaxAST parentAST) {
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ARGUMENT_WRONG_START
            );
            return null;
        }
        
        this.argumentName = (IdentifierToken) this.getSyntaxAnalyzer().currentToken();
        this.setStart(this.getSyntaxAnalyzer().currentToken().getStart());
        
        if (this.getSyntaxAnalyzer().matchesNextToken(SymbolType.COLON) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ARGUMENT_NO_SEPARATOR
            );
            return null;
        }
        this.getSyntaxAnalyzer().nextToken();
        
        if (!TypeSyntaxAST.TYPE_PARSER.canParse(parentAST, this.getSyntaxAnalyzer())) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ARGUMENT_NO_VALID_TYPE
            );
            return null;
        }
        this.argumentType = TypeSyntaxAST.TYPE_PARSER.parse(this, this.getSyntaxAnalyzer());
        if (this.argumentType == null)
            return null;
        
        this.setEnd(this.getSyntaxAnalyzer().currentToken().getEnd());
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(final PrintStream printStream, final String indents) {
        printStream.println(indents + "├── name: " + this.getArgumentName().getTokenContent());
        printStream.println(indents + "└── type: " + this.getArgumentType().getTypeKind().getName() + (this.getArgumentType().isArray() ? "[]" : ""));
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
     *
     * @return It will return null if an error occurred or the given "argumentsASTs" list
     *         if it parsed until to the end.
     */
    public static List<ArgumentDefinitionSyntaxAST> parseArguments(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final List<ArgumentDefinitionSyntaxAST> argumentASTs = new ArrayList<>();
        if (syntaxAnalyzer.matchesCurrentToken(SymbolType.OPENING_PARENTHESIS) == null) {
            parentAST.addError(
                    syntaxAnalyzer.getArkoiClass(),
                    syntaxAnalyzer.currentToken(),
                    "Couldn't parse the arguments because parsing doesn't start with an opening parenthesis."
            );
            return null;
        } else syntaxAnalyzer.nextToken();
    
        while (syntaxAnalyzer.getPosition() < syntaxAnalyzer.getTokens().length) {
            if (!ArgumentDefinitionSyntaxAST.ARGUMENT_DEFINITION_PARSER.canParse(parentAST, syntaxAnalyzer))
                break;
        
            final ArgumentDefinitionSyntaxAST argumentDefinitionSyntaxAST = ArgumentDefinitionSyntaxAST.ARGUMENT_DEFINITION_PARSER.parse(parentAST, syntaxAnalyzer);
            if (argumentDefinitionSyntaxAST == null)
                return null;
             else argumentASTs.add(argumentDefinitionSyntaxAST);
        
            if (syntaxAnalyzer.matchesNextToken(SymbolType.COMMA) == null)
                break;
            else syntaxAnalyzer.nextToken();
        }
    
        if (syntaxAnalyzer.matchesCurrentToken(SymbolType.CLOSING_PARENTHESIS) == null) {
            parentAST.addError(
                    syntaxAnalyzer.getArkoiClass(),
                    syntaxAnalyzer.currentToken(),
                    "Couldn't parse the arguments because the parsing doesn't end with a closing parenthesis."
            );
            return null;
        }
        return argumentASTs;
    }
    
}
