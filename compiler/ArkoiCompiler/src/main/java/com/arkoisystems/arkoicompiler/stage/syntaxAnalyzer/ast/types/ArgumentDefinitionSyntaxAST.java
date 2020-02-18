/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.types.ArgumentDefinitionParser;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ArgumentDefinitionSyntaxAST extends AbstractSyntaxAST
{
    
    public static ArgumentDefinitionParser ARGUMENT_DEFINITION_PARSER = new ArgumentDefinitionParser();
    
    
    @Expose
    private IdentifierToken argumentName;
    
    
    @Expose
    private TypeSyntaxAST argumentType;
    
    
    /**
     * This constructor will initialize the statement with the AST-Type
     * "ARGUMENT_DEFINITION". This will help to debug problems or check the AST for
     * correct syntax.
     */
    public ArgumentDefinitionSyntaxAST() {
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
    public ArgumentDefinitionSyntaxAST parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the argument definition because the parsing doesn't start with an identifier as name."));
            return null;
        } else {
            this.argumentName = (IdentifierToken) syntaxAnalyzer.currentToken();
            this.setStart(syntaxAnalyzer.currentToken().getStart());
        }
    
        if (syntaxAnalyzer.matchesNextToken(SymbolToken.SymbolType.COLON) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the argument definition because the argument name isn't followed by a colon."));
            return null;
        } else syntaxAnalyzer.nextToken();
        
        if (!TypeSyntaxAST.TYPE_PARSER.canParse(parentAST, syntaxAnalyzer)) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the argument definition because the colon isn't followed by a valid type."));
            return null;
        }
    
        if ((this.argumentType = TypeSyntaxAST.TYPE_PARSER.parse(this, syntaxAnalyzer)) == null) {
            syntaxAnalyzer.errorHandler().addError(new ParserError<>(TypeSyntaxAST.TYPE_PARSER, this.getStart(), syntaxAnalyzer.currentToken().getEnd(), "Couldn't parse the argument definition because an eror occurred during the parsing of the type."));
            return null;
        } else this.setEnd(syntaxAnalyzer.currentToken().getEnd());
        return this;
    }
    
    
    @Override
    public void printAST(final PrintStream printStream, final String indents) {
        printStream.println(indents + "├── name: " + this.getArgumentName().getTokenContent());
        printStream.println(indents + "└── type: " + this.getArgumentType().getTypeKind().getName() + (this.getArgumentType().isArray() ? "[]" : ""));
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
    public static List<ArgumentDefinitionSyntaxAST> parseArguments(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
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
    public static List<ArgumentDefinitionSyntaxAST> parseArguments(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer, final List<ArgumentDefinitionSyntaxAST> argumentASTs) {
        if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.OPENING_PARENTHESIS) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the arguments because parsing doesn't start with an opening parenthesis."));
            return null;
        } else syntaxAnalyzer.nextToken();
    
        while (syntaxAnalyzer.getPosition() < syntaxAnalyzer.getTokens().length) {
            if (!ArgumentDefinitionSyntaxAST.ARGUMENT_DEFINITION_PARSER.canParse(parentAST, syntaxAnalyzer))
                break;
        
            final ArgumentDefinitionSyntaxAST argumentDefinitionSyntaxAST = ArgumentDefinitionSyntaxAST.ARGUMENT_DEFINITION_PARSER.parse(parentAST, syntaxAnalyzer);
            if (argumentDefinitionSyntaxAST == null) {
                syntaxAnalyzer.errorHandler().addError(new ParserError<>(ArgumentDefinitionSyntaxAST.ARGUMENT_DEFINITION_PARSER, syntaxAnalyzer.currentToken(), "Couldn't parse the arguments because an error occurred during the parsing of an argument."));
                return null;
            } else argumentASTs.add(argumentDefinitionSyntaxAST);
    
            if (syntaxAnalyzer.matchesNextToken(SymbolToken.SymbolType.COMMA) == null)
                break;
            else syntaxAnalyzer.nextToken();
        }
    
        if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.CLOSING_PARENTHESIS) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the arguments because the parsing doesn't end with a closing parenthesis."));
            return null;
        }
        return argumentASTs;
    }
    
}
