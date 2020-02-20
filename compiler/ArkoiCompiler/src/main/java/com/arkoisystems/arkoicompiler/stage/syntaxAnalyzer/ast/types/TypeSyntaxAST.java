/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.types.TypeParser;
import lombok.Getter;

import java.io.PrintStream;

/**
 * Used if you want to create a new {@link TypeSyntaxAST}. But it is recommend to use the
 * {@link TypeSyntaxAST#TYPE_PARSER} to parse a new {@link TypeSyntaxAST} because with it
 * you can check if the current {@link AbstractToken} is capable to parse this AST.
 */
public class TypeSyntaxAST extends AbstractSyntaxAST
{
    
    /**
     * This variable is static because we just want a single instance of the {@link
     * TypeParser}.
     */
    public static TypeParser TYPE_PARSER = new TypeParser();
    
    
    /**
     * The {@link TypeKind} specifies the type of this {@link TypeSyntaxAST}. It is used
     * for later usage in the {@link SemanticAnalyzer}.
     */
    @Getter
    private TypeKind typeKind;
    
    
    /**
     * Defines if this {@link TypeSyntaxAST} is an array or not. Useful for later usage
     * when generating pseudo code etc.
     */
    @Getter
    private boolean isArray;
    
    
    /**
     * Constructs a new {@link TypeSyntaxAST} with the defines parameters. This
     * constructor is just used for in-build creation and not for parsing. So you can
     * create a {@link TypeSyntaxAST} without the need to parse it.
     *
     * @param typeKind
     *         the {@link TypeKind} which is used for later usage.
     * @param isArray
     *         defines if the {@link TypeSyntaxAST} should be an array or not.
     */
    public TypeSyntaxAST(final TypeKind typeKind, final boolean isArray) {
        super(ASTType.TYPE);
        
        this.typeKind = typeKind;
        this.isArray = isArray;
    }
    
    
    /**
     * Constructs a new {@link TypeSyntaxAST} without pre-defining any variables. You use
     * this constructor for parsing, so you also need to use the {@link
     * TypeSyntaxAST#parseAST(AbstractSyntaxAST, SyntaxAnalyzer)} method to initialize all
     * variables correctly.
     */
    public TypeSyntaxAST() {
        super(ASTType.TYPE);
    }
    
    
    /**
     * Parses a new {@link TypeSyntaxAST} with the given parameters, where the {@link
     * SyntaxAnalyzer} is used to check the syntax and the parent {@link
     * AbstractSyntaxAST} is used to see if this AST can be created inside the parent.
     * Also it will check if an array assignment is defined or not.
     *
     * @param parentAST
     *         the parent {@link AbstractSyntaxAST} which should get used to check if the
     *         {@link AbstractSyntaxAST} can be created inside it.
     * @param syntaxAnalyzer
     *         the {@link SyntaxAnalyzer} which is used for checking the syntax with
     *         methods like {@link SyntaxAnalyzer#matchesCurrentToken(TokenType)} or
     *         {@link SyntaxAnalyzer#matchesNextToken(SymbolToken.SymbolType)}.
     *
     * @return {@code null} if an error occurred or this {@link TypeSyntaxAST} if
     *         everything worked correctly.
     */
    @Override
    public TypeSyntaxAST parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the Type because the parsing doesn't start with an IdentifierToken."));
            return null;
        } else this.typeKind = TypeKind.getTypeKind(syntaxAnalyzer.currentToken());
        
        if (this.typeKind == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the Type because it isn't a valid type keyword."));
            return null;
        }
        
        this.setStart(syntaxAnalyzer.currentToken().getStart());
        this.setEnd(syntaxAnalyzer.currentToken().getEnd());
    
        // This will check if the next two Tokens are an opening and closing bracket aka. "[]". If it is, then skip these two Tokens and set the "isArray" boolean to true.
        if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.OPENING_BRACKET) != null && syntaxAnalyzer.matchesPeekToken(2, SymbolToken.SymbolType.CLOSING_BRACKET) != null) {
            syntaxAnalyzer.nextToken(2);
            this.isArray = true;
        }
        return this;
    }
    
    
    /**
     * This method will do nothing because it isn't relevant for the future.
     *
     * @param printStream
     *         the {@link PrintStream} which should get used for the output.
     * @param indents
     *         the {@code indents} which should get used when printing a new line.
     */
    @Override
    public void printSyntaxAST(final PrintStream printStream, final String indents) { }
    
}
