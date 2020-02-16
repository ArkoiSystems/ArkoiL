/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.google.gson.annotations.Expose;
import lombok.Getter;

/**
 * This class is used to define a {@link AbstractParser} for a specific {@link
 * AbstractSyntaxAST}. So you can easily create an AST without any problems. Also this
 * class is capable to test the current {@link AbstractToken} if it could be parsed by
 * this AST.
 *
 * @param <T>
 *         the {@link AbstractSyntaxAST} which should define this class.
 */
@Getter
public abstract class AbstractParser<T extends AbstractSyntaxAST>
{
    
    /**
     * The parameter name of the current {@link AbstractParser} which is defined through
     * the class parameter.
     */
    @Expose
    private final String parameterName;
    
    
    /**
     * This will construct a {@link AbstractParser} and initializes it's parameter name
     * with the generic superclass.
     */
    public AbstractParser() {
        final String genericTypeName = this.getClass().getGenericSuperclass().getTypeName();
        final String typeName = genericTypeName.substring(genericTypeName.indexOf("<") + 1, genericTypeName.length() - 1);
        final String[] separatedPath = typeName.split("\\.");
        this.parameterName = separatedPath[separatedPath.length - 1];
    }
    
    
    /**
     * This method is used to parse the specified AST. Also it will pass some arguments to
     * it like the parentAST ({@link AbstractSyntaxAST}) or the {@link SyntaxAnalyzer}. If
     * this method returns null it indicates that an error occurred.
     *
     * @param parentAST
     *         the {@link AbstractSyntaxAST} in which this AST is getting parsed.
     * @param syntaxAnalyzer
     *         the {@link SyntaxAnalyzer} which is used to call methods like {@link
     *         SyntaxAnalyzer#matchesCurrentToken(TokenType)} etc.
     *
     * @return {@code null} if an error occurred or the specified {@link
     *         AbstractSyntaxAST}.
     */
    public abstract T parse(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer);
    
    
    /**
     * This method is used to check if the current {@link AbstractToken} is capable to
     * parse the specified AST. If not it will return {@code false} or {@code true} if it
     * would work.
     *
     * @param parentAST
     *         the {@link AbstractSyntaxAST} in which this AST is getting parsed.
     * @param syntaxAnalyzer
     *         the {@link SyntaxAnalyzer} which is used to call methods like {@link
     *         SyntaxAnalyzer#matchesCurrentToken(TokenType)} etc.
     *
     * @return {@code false} if it's not capable to parse the current {@link
     *         AbstractToken} or {@code true} if it does.
     */
    public abstract boolean canParse(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer);
    
}
