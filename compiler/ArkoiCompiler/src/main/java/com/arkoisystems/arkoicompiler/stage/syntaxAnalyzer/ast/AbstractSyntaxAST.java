/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.ArkoiCompiler;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.RootSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.utils.Variables;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.io.PrintStream;

/**
 * This class is used if you want to create an AST. With the {@link
 * AbstractSyntaxAST#parseAST(AbstractSyntaxAST, SyntaxAnalyzer)} method you can check the
 * syntax and initialize all variables which are needed for later usage. Also you can
 * print out this class as a JSON based {@link String} with the {@link
 * AbstractSyntaxAST#toString()} method.
 */
public abstract class AbstractSyntaxAST
{
    
    /**
     * The {@link ASTType} is used to differentiate it from other {@link
     * AbstractSyntaxAST}'s. Also it is useful for debugging if you use the output of the
     * {@link AbstractSyntaxAST#toString()} method.
     */
    @Getter
    private final ASTType astType;
    
    
    /**
     * The start and end index of the AST as char positions from the input source declared
     * in {@link ArkoiClass} which you can get through the {@link SyntaxAnalyzer}.
     */
    @Getter
    @Setter
    private int start, end;
    
    
    /**
     * Just constructs a new {@link AbstractSyntaxAST} with the defined {@link ASTType}.
     *
     * @param astType
     *         the {@link ASTType} which should get used to identify this specific {@link
     *         AbstractSyntaxAST}.
     */
    public AbstractSyntaxAST(final ASTType astType) {
        this.astType = astType;
    }
    
    
    /**
     * This method will be overwritten by the classes which extends {@link
     * AbstractSyntaxAST}. It will return a {@link AbstractSyntaxAST} if everything worked
     * correctly or {@code null} if an error occurred. The parent {@link
     * AbstractSyntaxAST} is used to check if the {@link AbstractSyntaxAST} can be created
     * inside it and the {@link SyntaxAnalyzer} is just used to check the syntax of
     * current/next and peeked {@link AbstractToken}.
     *
     * @param parentAST
     *         the parent {@link AbstractSyntaxAST} which should get used to check if the
     *         {@link AbstractSyntaxAST} can be created inside it.
     * @param syntaxAnalyzer
     *         the {@link SyntaxAnalyzer} which is used for checking the syntax with
     *         methods like {@link SyntaxAnalyzer#matchesCurrentToken(TokenType)} or
     *         {@link SyntaxAnalyzer#matchesNextToken(SymbolToken.SymbolType)} )}.
     *
     * @return {@code null} if an error occurred or the parsed {@link AbstractSyntaxAST}
     *         if everything worked correctly.
     */
    public abstract AbstractSyntaxAST parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer);
    
    
    /**
     * This method will be overwritten by the classes which extends {@link
     * AbstractSyntaxAST}. It will print with help of the {@link PrintStream} and {@code
     * indents} a tree which is used for debugging. To print all {@link RootSyntaxAST}'s
     * you just need to call this method {@link ArkoiCompiler#printSyntaxTree(PrintStream)}.
     *
     * @param printStream
     *         the {@link PrintStream} which should get used for the output.
     * @param indents
     *         the {@code indents} which will make the AST look like a Tree.
     */
    public abstract void printSyntaxAST(final PrintStream printStream, final String indents);
    
}
