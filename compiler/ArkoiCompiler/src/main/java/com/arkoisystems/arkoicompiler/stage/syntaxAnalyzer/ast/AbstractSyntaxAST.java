/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast;

import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.utils.Variables;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractSyntaxAST
{
    
    @Expose
    private ASTType astType;
    
    @Expose
    private int start, end;
    
    /**
     * This constructor will provide the capability to set the AST-Type. This will help to
     * debug problems or check the AST for correct syntax.
     *
     * @param astType
     *         The AST-Type which should get set to this class.
     */
    public AbstractSyntaxAST(final ASTType astType) {
        this.astType = astType;
    }
    
    /**
     * This method is just a boilerplate code which should get used if it didn't got
     * overwritten.
     *
     * @param parentAST
     *         The parent of the AST. With it you can check for correct usage of the
     *         statement.
     * @param syntaxAnalyzer
     *         The given SyntaxAnalyzer is used for checking the syntax of the current
     *         Token list.
     *
     * @return It just returns null because you need to overwrite it.
     */
    public abstract AbstractSyntaxAST parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer);
    
    @Override
    public String toString() {
        return Variables.GSON.toJson(this);
    }
    
}
